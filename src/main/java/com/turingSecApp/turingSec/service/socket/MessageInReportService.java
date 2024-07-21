package com.turingSecApp.turingSec.service.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
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
public class MessageInReportService {
    private final ReportsRepository reportsRepository;
    private final JwtUtil jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final UtilService utilService;
    private final BaseMessageInReportRepository baseMessageInReportRepository;
    private final StringMessageInReportRepository stringMessageInReportRepository;

    private SocketIOServer socketIOServer;

    public MessageInReportService(ReportsRepository reportsRepository, JwtUtil jwtTokenProvider, UserDetailsServiceImpl userDetailsService, UtilService utilService, BaseMessageInReportRepository baseMessageInReportRepository, StringMessageInReportRepository stringMessageInReportRepository, SocketIOServer socketIOServer) {
        // Inject other class
        this.reportsRepository = reportsRepository;
        this.jwtTokenProvider = jwtTokenProvider;
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
    // TODO: TASKS
    //  6. Token-den extract etdiyimiz user(hacker) hemin reportun useri ile eyni olmalidi , deyilse exception ("Message of Hacker must be same with report Hacker") -> (hem log hem de sendEvent ile error eventinde ex-mesaji gondermek)
    //  7. Token-den extract etdiyimiz company hemin reportun company-si ile eyni olmalidi , deyilse exception
    //   autheticatedUser hemin reportun hackeri ve ya company-sidirmi? eks halda exception
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
    @Transactional // for this anno we cannot set private
    public DataListener<StringMessageInReportPayload> onStrMessageReceived() {
        return (socketIOClient, data, ackSender) -> {

            log.info(String.format("Data from client StringMessageInReportPayload -> (payload): %s",data));

            StringMessageInReport strMessage = new StringMessageInReport();
            strMessage.setContent(data.getContent());
            strMessage.setEdited(false); // Initialize false, set true when change and set updatedAt
            strMessage.setCreatedAt(LocalDateTime.now());
            strMessage.setReplied(data.isReplied());

            // Set "BaseMessage" (replyTo) if it is not null
            if(data.getReplyToMessageId() != null){
                Optional<BaseMessageInReport> repliedMessageInReport = baseMessageInReportRepository.findById(data.getReplyToMessageId());
                strMessage.setReplyTo(repliedMessageInReport.get());
            }

            // Set "Report"
            Report reportOfMessage = findReportById(data.getReportId(),socketIOClient);
            strMessage.setReport(reportOfMessage);

            //////////
            // Set isHacker based on token, token comes in auth header there is no need in payload
            // refactorThis
            Object authenticatedUser;

            try{
                // if exception not occurs it is Hacker
                authenticatedUser = utilService.getAuthenticatedHacker();// returns UserEntity
                log.info("It is Hacker entity!");
            }catch (UnauthorizedException e){
                // if exception occurs it is not Hacker
                log.warn("It is not Hacker entity!");
                authenticatedUser = utilService.getAuthenticatedCompany();// returns CompanyEntity
            }

            log.info("User/Company info: " + authenticatedUser);

            if(authenticatedUser instanceof UserEntity){
                strMessage.setHacker(true);
            } else if (authenticatedUser instanceof CompanyEntity) {
                strMessage.setHacker(false);
            }else {
                log.error("User is neither Hacker nor Company!");
                throw new UnauthorizedException("User is neither Hacker nor Company!");
            }

            //////////

            // Save str message
            StringMessageInReport savedMsg = stringMessageInReportRepository.save(strMessage);
            log.info(String.format("Data after save -> StringMessageInReport (entity): %s",savedMsg));



            // Send message to the report's room
            String room = socketIOClient.getHandshakeData().getSingleUrlParam("room"); // room as query ?room= uuid of msg's report
            log.info(String.format("Room uuid: %s , report of room uuid: %s , must be equal",room, reportOfMessage.getRoom()));


            socketIOClient.getNamespace().getRoomOperations(room).getClients().forEach(
                    x -> {
                        if (!x.getSessionId().equals(socketIOClient.getSessionId())){
                            // Convert into dto, client only see dto (time format -> String)
                            StringMessageInReportDTO msgDTO = StringMessageInReportMapper.INSTANCE.toDTO(savedMsg);

                            log.info(String.format("In get_message event: %s",savedMsg));
                            log.info(String.format("Data after mapping to DTO -> StringMessageInReportDTO (DTO): %s",msgDTO));

                            x.sendEvent("get_message", msgDTO);
                        }
                    }
            );


            // Log important details of message
            log.info(String.format("Sent message id: %d ,Report id: %d ,Report's user id: %d,Socket client id: %s -> msg: %s at %s",
                    savedMsg.getId(),
                    data.getReportId(),
                    reportOfMessage.getUser().getId(),
//                        reportOfMessage.getBugBountyProgram().getCompany().getId(),// todo: not working
//                        reportOfMessage.getBugBountyProgram().getId(),// todo: not working
                    socketIOClient.getSessionId().toString(),
                    data.getContent(),
                    LocalDateTime.now()));
        };
    }

    private Report findReportById(Long reportId, SocketIOClient socketIOClient) {
        Report report = null;
        try {
            report = reportsRepository.findById(reportId)
                    .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
        } catch (ResourceNotFoundException e) {
            // Handle the exception here, possibly by logging it or sending an error response
            log.error("Resource not found: " + e.getMessage());
            socketIOClient.sendEvent("error","Resource not found: " + e.getMessage());
        }
        return report;
    }
//    private DataListener<FileMessageInReport> onFileMessageReceived() {
//        return null;
//    }

    //
    private ConnectListener onConnected() {
        return socketIOClient -> {

            // refactorThis
            String authorizationHeader = socketIOClient.getHandshakeData().getHttpHeaders().get("Authorization");
            log.info("Authorization Header of request: " + authorizationHeader);
            if(authorizationHeader == null){
                log.error("Auth header is null");
                socketIOClient.sendEvent("error","Authorization Header of request: null, You are unauthorized person");
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

            String room = socketIOClient.getHandshakeData().getSingleUrlParam("room");
            socketIOClient.joinRoom(room);
            log.info(String.format("SocketID: %s connected!", socketIOClient.getSessionId().toString()));
            log.info(String.format("Connected to the room: %s",room));
        };
    }

    private DisconnectListener onDisconnected() {
        return socketIOClient -> {
          log.info(String.format("SocketID: %s disconnected!", socketIOClient.getSessionId().toString()));
        };
    }


}
