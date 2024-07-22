package com.turingSecApp.turingSec.service.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.filter.JwtUtil;
import com.turingSecApp.turingSec.model.entities.message.BaseMessageInReport;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import com.turingSecApp.turingSec.model.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.BaseMessageInReportRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.MockDataService;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.ISocketEntityHelper;
import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketExceptionHandler;
import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.StringMessageInReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class SocketService {
    private final ReportsRepository reportsRepository;
    private final SocketExceptionHandler socketExceptionHandler;
    private final JwtUtil jwtTokenProvider;
    private final ISocketEntityHelper socketEntityHelper;
    private final UserDetailsServiceImpl userDetailsService;
    private final UtilService utilService;
    private final BaseMessageInReportRepository baseMessageInReportRepository;
    private final StringMessageInReportRepository stringMessageInReportRepository;

    private SocketIOServer socketIOServer;

    public SocketService(ReportsRepository reportsRepository, SocketExceptionHandler socketExceptionHandler, JwtUtil jwtTokenProvider, ISocketEntityHelper socketEntityHelper, UserDetailsServiceImpl userDetailsService, UtilService utilService, BaseMessageInReportRepository baseMessageInReportRepository, StringMessageInReportRepository stringMessageInReportRepository, SocketIOServer socketIOServer) {
        // Inject other class
        this.reportsRepository = reportsRepository;
        this.socketExceptionHandler = socketExceptionHandler;
        this.jwtTokenProvider = jwtTokenProvider;
        this.socketEntityHelper = socketEntityHelper;
        this.userDetailsService = userDetailsService;
        this.utilService = utilService;
        this.baseMessageInReportRepository = baseMessageInReportRepository;
        this.stringMessageInReportRepository = stringMessageInReportRepository;

        // Inject socketIOServer
        this.socketIOServer = socketIOServer;
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());


        socketIOServer.addEventListener("send_message_str", StringMessageInReportPayload.class, onStrMessageReceived()); // base/parent class Message
        //socketIOServer.addEventListener("send_message_file", FileMessageInReport.class, onFileMessageReceived());
    }


    //  2. Payloadda access token alinmalidi, ve mesaji gonderen Hacker yoxsa Company-di tapa bilerik token vasitesile , (en 1-ci unauthorized olmadigini yoxlamalyiq, eks halda exception) +
    //  1. Exceptionlar error event name ile atilmalidi ,  hem log hem sendEvent
    //  3. Payloaddan entity-e kecirerken eger token ile tapdigimiz user hackerdisa -> isHacker=true , company-dirse -> isHacker=false edib save edirik
    //  6. Token-den extract etdiyimiz user(hacker) hemin reportun useri ile eyni olmalidi , deyilse exception ("Message of Hacker must be same with report Hacker") -> (hem log hem de sendEvent ile error eventinde ex-mesaji gondermek)
    //  7. Token-den extract etdiyimiz company hemin reportun company-si ile eyni olmalidi , deyilse exception
    //   autheticatedUser hemin reportun hackeri ve ya company-sidirmi? eks halda exception
    //    hacker ve ya company reporta aid deyilse mesaj gondere bilmir ama gore bilir (otaga connect ede bilir, mesj atanda disconnect olur) , room-a girmeye icaze verme
    // TODO: TASKS
    //      exceptionlar sadece logda gorunur event kimi send olmur
    //    dto-ya cevirerken her iki id-ni set et ,  reportdan gotur , isHackeride set et entityden
    //  4. Reportun yalniz bir user(hacker) ve bir company id-si ola biler , isHacker true ve ya false ferq etmir
    //      hansi hal olursa olsun her iki id DTO-da gorunmelidi entity-de yox(report-dan goture bilerik bu iki value-nu)  ,
    //      frontendler isHacker-a gore mesaji gonderenin company yoxsa hacker oldugunu mueyyenlesdirib lazim olan id-ni isledecekler
    //  5. DTO-da company ve hacker-in img,background img gosterilmelidi
    //  8. BaseMessageInReportPayload-dan private Long reportId; - bu fieldi cixartmaq lazimdi path-de biz room deye birsey aliriq(uuid) ->
    //     bu room reportun fieldi-dir, ona gore bu room fieldi ile reportu tapa bilerik -> String room = socketIOClient.getHandshakeData().getSingleUrlParam("room"); // room as query ?room= uuid of msg's report
    //   (*Reportu room ile , user ve company-de report ile tapa bilerik)
    //    * Postmanda "error" event-i de listen olunmalidir, "get_message" ile yanasi
    //    * Butun log-lardaki melumatlar dogru olmalidi, payload->entity->dto hamisi duzgun sekilde yaradilmalidi
    //   9. Report-da get program islemir lazy fetch olduguna gore error verir
    //    * Event icinde LocalDateTime tipinde mesaji gondermek olmur, serialize ede bilmir deye DTO-da string formatinda gotururuk
    //   10. *** Mesaj eger reply-dirsa isReplied true,repliedTo - da id-si verilir, amma DTO-da reply olunan mesajin contenti yoxdu ***
    //   11. Message payload validationlar edilmelidi
    //   12. getMessage ile evvelki gonderilen mesajlar gorunmelidi ama list<DTO> seklinde
    //      Adminler reportId ile istenilen report altindaki, butun mesajlari gore biler list<DTO> seklinde
    //      Silinmis mesajlar, editlenmis mesajlarida bu 3 task - http-dir socket ile deyil
    //   13. Butun bu tasklar bitenden sonra -> editMessage, deleteMessage, deleteMessageList (every user only edit or delete own), edited or deleted messages must be tracked in logs and db, explore: "can we store logs?(not to use safe delete)"
    @Transactional
    public DataListener<StringMessageInReportPayload> onStrMessageReceived() {
        return (socketIOClient, data, ackSender) -> {
            socketExceptionHandler.executeWithExceptionHandling(() -> {
                log.info(String.format("Data from client StringMessageInReportPayload -> (payload): %s", data));

                // Create message from payload
                StringMessageInReport strMessage = createStringMessageInReport(data);

                // Room from path query (urlParam) and get Report from room
                String room = socketIOClient.getHandshakeData().getSingleUrlParam("room");
                Report reportOfMessage = reportsRepository.findByRoom(room).orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));

                // Is it user or company if authorized
                Object authenticatedUser = getAuthenticatedUser();
                log.info("User/Company info: " + authenticatedUser);

                // Validate the hacker/company
                validateHackerAndCompany(authenticatedUser, reportOfMessage.getId(), strMessage);

                // Save str message
                StringMessageInReport savedMsg = stringMessageInReportRepository.save(strMessage);
                log.info(String.format("Data after save -> StringMessageInReport (entity): %s", savedMsg));

                // Send message to the report's room
                sendMessageToRoom(socketIOClient, savedMsg, room);

                // Log important details of message
                logImportantDetails(savedMsg, data, socketIOClient);

            }, socketIOClient);
        };
    }


    private StringMessageInReport createStringMessageInReport(StringMessageInReportPayload data) {
        StringMessageInReport strMessage = new StringMessageInReport();
        strMessage.setContent(data.getContent());
        strMessage.setEdited(false);
        strMessage.setCreatedAt(LocalDateTime.now());
        strMessage.setReplied(data.isReplied());

        // Set "BaseMessage" (replyTo) if it is not null
        if (data.getReplyToMessageId() != null) {
            Optional<BaseMessageInReport> repliedMessageInReport = baseMessageInReportRepository.findById(data.getReplyToMessageId());
            strMessage.setReplyTo(repliedMessageInReport.orElse(null));
        }

        // Set "Report"
        strMessage.setReport(socketEntityHelper.findReportById(data.getReportId()));

        return strMessage;
    }

    private Object getAuthenticatedUser() {
        try {
            return utilService.getAuthenticatedHacker();
        } catch (UserNotFoundException e) {
            // if exception occurs it is not Hacker
            log.warn("It is not Hacker entity!");
            return utilService.getAuthenticatedCompany();
        }
    }

    private void sendMessageToRoom(SocketIOClient socketIOClient, StringMessageInReport savedMsg, String room) {

        log.info(String.format("Room uuid: %s", room));

        socketIOClient.getNamespace().getRoomOperations(room).getClients().forEach(x -> {
            if (!x.getSessionId().equals(socketIOClient.getSessionId())) {
                StringMessageInReportDTO msgDTO = StringMessageInReportMapper.INSTANCE.toDTO(savedMsg);
                log.info(String.format("In get_message event: %s", savedMsg));
                log.info(String.format("Data after mapping to DTO -> StringMessageInReportDTO (DTO): %s", msgDTO));
                x.sendEvent("get_message", msgDTO);
            }
        });
    }

    private void logImportantDetails(StringMessageInReport savedMsg, StringMessageInReportPayload data, SocketIOClient socketIOClient) {
        log.info(String.format("Sent message id: %d ,Report id: %d ,Report's user id: %d,Socket client id: %s -> msg: %s at %s",
                savedMsg.getId(),
                data.getReportId(),
                savedMsg.getReport().getUser().getId(),
                socketIOClient.getSessionId().toString(),
                data.getContent(),
                LocalDateTime.now()));
        // todo: get program and company must be logged
    }


    public void validateHackerAndCompany(Object authenticatedUser, Long reportId, StringMessageInReport strMessage) {
        setHackerFlag(authenticatedUser, strMessage);
        checkUserOrCompanyReport(authenticatedUser, reportId);
    }
    //refactorThis
    private void checkUserOrCompanyReport(Object authenticatedUser, Long reportId) {
        if (authenticatedUser instanceof UserEntity) {
            socketEntityHelper.checkUserReport(authenticatedUser, reportId);
        } else if (authenticatedUser instanceof CompanyEntity) {
            socketEntityHelper.checkCompanyReport(authenticatedUser, reportId);
        } else {
            throw new UnauthorizedException("User is neither Hacker nor Company!");
        }
    }
    private void setHackerFlag(Object authenticatedUser, StringMessageInReport strMessage) {
        if (authenticatedUser instanceof UserEntity) {
            strMessage.setHacker(true);
        } else if (authenticatedUser instanceof CompanyEntity) {
            strMessage.setHacker(false);
        } else {
            throw new UnauthorizedException("User is neither Hacker nor Company!");
        }
    }





    //
    private ConnectListener onConnected() {
        return socketIOClient -> {

            // refactorThis
            // If user authorized it sets or throw exception
            setUserIfAuthorized(socketIOClient);

            // Is it user or company if authorized
            Object authenticatedUser = getAuthenticatedUser();

            String room = socketIOClient.getHandshakeData().getSingleUrlParam("room");
            Report reportOfMessage = reportsRepository.findByRoom(room).orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));

            // Is it user or company if authorized
            checkUserOrCompanyReport(authenticatedUser,reportOfMessage.getId());

            socketIOClient.joinRoom(room);
            log.info(String.format("SocketID: %s connected!", socketIOClient.getSessionId().toString()));
            log.info(String.format("Connected to the room: %s",room));
        };
    }

    private void setUserIfAuthorized(SocketIOClient socketIOClient) {
            String authorizationHeader = socketIOClient.getHandshakeData().getHttpHeaders().get("Authorization");
            log.info("Authorization Header of request: " + authorizationHeader);
            if(authorizationHeader == null){
                throw new UnauthorizedException("Authorization Header of request: null, You are unauthorized person");
            }

            String token = jwtTokenProvider.validateBearerToken(authorizationHeader);


            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails != null/* && userService.findUserByUsername(username).isActivated()*/) { // todo:fix this for only user not company
                    System.out.println("UserDetails of current user: " + userDetails);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
    }

    private DisconnectListener onDisconnected() {
        return socketIOClient -> {
          log.info(String.format("SocketID: %s disconnected!", socketIOClient.getSessionId().toString()));
        };
    }

    // Util methods



}
