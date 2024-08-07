//package com.turingSecApp.turingSec.service.socket;
//
//import com.corundumstudio.socketio.SocketIOClient;
//import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.listener.ConnectListener;
//import com.corundumstudio.socketio.listener.DataListener;
//import com.corundumstudio.socketio.listener.DisconnectListener;
//import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
//import com.turingSecApp.turingSec.exception.custom.UnauthorizedException;
//import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
//import com.turingSecApp.turingSec.filter.JwtUtil;
//import com.turingSecApp.turingSec.model.entities.message.BaseMessageInReport;
//import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
//import com.turingSecApp.turingSec.model.entities.program.Program;
//import com.turingSecApp.turingSec.model.entities.report.Report;
//import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
//import com.turingSecApp.turingSec.model.entities.user.UserEntity;
//import com.turingSecApp.turingSec.model.repository.CompanyRepository;
//import com.turingSecApp.turingSec.model.repository.UserRepository;
//import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
//import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
//import com.turingSecApp.turingSec.model.repository.reportMessage.BaseMessageInReportRepository;
//import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
//import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
//import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
//import com.turingSecApp.turingSec.service.MockDataService;
//import com.turingSecApp.turingSec.service.socket.exceptionHandling.SocketExceptionHandler;
//import com.turingSecApp.turingSec.service.user.UserDetailsServiceImpl;
//import com.turingSecApp.turingSec.util.UtilService;
//import com.turingSecApp.turingSec.util.mapper.StringMessageInReportMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@Slf4j
//public class SocketService {
//    private final ReportRepository reportRepository;
//    private final SocketExceptionHandler socketExceptionHandler;
//    private final ProgramRepository programRepository;
//    private final CompanyRepository companyRepository;
//    private final MockDataService mockDataService;
//    private final JwtUtil jwtTokenProvider;
//    private final ISocketEntityHelper socketEntityHelper;
//    private final UserDetailsServiceImpl userDetailsService;
//    private final UserRepository userRepository;
//    private final UtilService utilService;
//    private final BaseMessageInReportRepository baseMessageInReportRepository;
//    private final StringMessageInReportRepository stringMessageInReportRepository;
//
//    private SocketIOServer socketIOServer;
//
//    public SocketService(ReportRepository reportRepository, SocketExceptionHandler socketExceptionHandler, ProgramRepository programRepository, CompanyRepository companyRepository, MockDataService mockDataService, JwtUtil jwtTokenProvider, ISocketEntityHelper socketEntityHelper, UserDetailsServiceImpl userDetailsService, UserRepository userRepository, UtilService utilService, BaseMessageInReportRepository baseMessageInReportRepository, StringMessageInReportRepository stringMessageInReportRepository, SocketIOServer socketIOServer) {
//        // Inject other class
//        this.reportRepository = reportRepository;
//        this.socketExceptionHandler = socketExceptionHandler;
//        this.programRepository = programRepository;
//        this.companyRepository = companyRepository;
//        this.mockDataService = mockDataService;
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.socketEntityHelper = socketEntityHelper;
//        this.userDetailsService = userDetailsService;
//        this.userRepository = userRepository;
//        this.utilService = utilService;
//        this.baseMessageInReportRepository = baseMessageInReportRepository;
//        this.stringMessageInReportRepository = stringMessageInReportRepository;
//
//        // Inject socketIOServer
//        this.socketIOServer = socketIOServer;
//        socketIOServer.addConnectListener(onConnected());
//        socketIOServer.addDisconnectListener(onDisconnected());
//
//
//        socketIOServer.addEventListener("send_message_str", StringMessageInReportPayload.class, onStrMessageReceived()); // base/parent class Message
//        //socketIOServer.addEventListener("send_message_file", FileMessageInReport.class, onFileMessageReceived());
//    }
//
//
//
//    @Transactional
//    public DataListener<StringMessageInReportPayload> onStrMessageReceived() {
//        return (socketIOClient, dataPayload, ackSender) -> {
////            socketExceptionHandler.executeWithExceptionHandling(() -> {
////                String authorizationHeader = socketIOClient.getHandshakeData().getSingleUrlParam("Authorization");
////                log.info("Authorization Header of request: " + authorizationHeader);
////                log.info(String.format("Data from client StringMessageInReportPayload -> (payload): %s", dataPayload));
////
////                // Room from path query (urlParam) and get Report from room
////                String room = socketIOClient.getHandshakeData().getSingleUrlParam("room");
////                Report reportOfMessage = reportRepository.findByRoom(room).orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));
////
////
////                // Is it user or company if authorized
////                Object authenticatedUser = getAuthenticatedUser();
////                log.info("User/Company info: " + authenticatedUser);
////
////                // Validate the hacker/company and set Hacker
////                checkUserOrCompanyReport(authenticatedUser, reportOfMessage.getId());
////
////                // Create message from payload
////                StringMessageInReport strMessage = createStringMessageInReport(dataPayload,authenticatedUser,reportOfMessage);
////
////                // Save created strMessage obj
////                StringMessageInReport savedMsg = stringMessageInReportRepository.save(strMessage);
////                log.info(String.format("Data after save -> StringMessageInReport (entity): %s", savedMsg));
////
////                // Send message to the report's room
////                sendMessageToRoom(socketIOClient, savedMsg, room);
////
////                // Sample response to the client
////                ackSender.sendAckData("Message received successfully");
////
////            });
//        };
//    }
//
//
//    private StringMessageInReport createStringMessageInReport(StringMessageInReportPayload data,Object authenticatedUser,Report reportOfMessage) {
//        StringMessageInReport strMessage = new StringMessageInReport();
//        strMessage.setContent(data.getContent());
//        strMessage.setEdited(false);
//        strMessage.setCreatedAt(LocalDateTime.now());
//        strMessage.setReplied(data.isReplied());
//
//        // Set "BaseMessage" (replyTo) if it is not null
//        if (data.getReplyToMessageId() != null) {
//            Optional<BaseMessageInReport> repliedMessageInReport = baseMessageInReportRepository.findById(data.getReplyToMessageId());
//            strMessage.setReplyTo(repliedMessageInReport.orElse(null));
//        }
//
//        // Set "Report"
//        strMessage.setReport(reportOfMessage);
//
//        // Set "isHacker"
//        setHackerFlag(authenticatedUser, strMessage);
//
//        log.info("Created StringMessageInReport object before save: " + strMessage);
//
//        return strMessage;
//    }
//
//    private Object getAuthenticatedUser() {
//        try {
//            return utilService.getAuthenticatedHackerWithHTTP();
//        } catch (UserNotFoundException e) {
//            // if exception occurs it is not Hacker
//            log.warn("It is not Hacker entity!");
//            return utilService.getAuthenticatedCompanyWithHTTP();
//        }
//    }
//
//    private void sendMessageToRoom(SocketIOClient socketIOClient, StringMessageInReport savedMsg, String room) {
//
//        log.info(String.format("Room uuid: %s", room));
//
//        socketIOClient.getNamespace().getRoomOperations(room).getClients().forEach(x -> {
//            if (!x.getSessionId().equals(socketIOClient.getSessionId())) {
//                StringMessageInReportDTO msgDTO = toStringMessageInReportDTO(savedMsg);
//                x.sendEvent("get_message", msgDTO);
//
//                log.info(String.format("In get_message event savedMsg -> StringMessageInReport (entity): %s, Socket's client session id: %s", savedMsg,socketIOClient.getSessionId().toString()));
//                log.info(String.format("Data after mapping to DTO -> StringMessageInReportDTO (DTO): %s , Socket's client session id: %s", msgDTO,socketIOClient.getSessionId().toString()));
//            }
//        });
//    }
//
//    private StringMessageInReportDTO toStringMessageInReportDTO(StringMessageInReport savedMsg) {
//        StringMessageInReportDTO dtoEagerFields = StringMessageInReportMapper.INSTANCE.toDTOEagerFields(savedMsg);
//
//        // Fetch with query to avoid lazyInit exception
//        Program programEntityForCompany = programRepository.findByReportsContains(savedMsg.getReport())
//                .orElseThrow(() -> new ResourceNotFoundException("Program not found with this report:" + savedMsg.getReport()));
//        CompanyEntity companyEntityForProgram = companyRepository.findByBugBountyProgramsContains(programEntityForCompany)
//                .orElseThrow(() -> new ResourceNotFoundException("Company not found with this program:" + programEntityForCompany));
//
//        // Set programId to DTO
//        dtoEagerFields.setProgramId(programEntityForCompany.getId());
//
//        // Set companyId to DTO
//        dtoEagerFields.setCompanyId(companyEntityForProgram.getId());
//
//        return dtoEagerFields;
//    }
//
//
//
//
//    //refactorThis
//    private void checkUserOrCompanyReport(Object authenticatedUser, Long reportId) {
//        if (authenticatedUser instanceof UserEntity) {
//            socketEntityHelper.checkUserReport(authenticatedUser, reportId);
//        } else if (authenticatedUser instanceof CompanyEntity) {
//            socketEntityHelper.checkCompanyReport(authenticatedUser, reportId);
//        } else {
//            throw new UnauthorizedException("User is neither Hacker nor Company!");
//        }
//    }
//    private void setHackerFlag(Object authenticatedUser, StringMessageInReport strMessage) {
//        if (authenticatedUser instanceof UserEntity) {
//            strMessage.setHacker(true);
//        } else if (authenticatedUser instanceof CompanyEntity) {
//            strMessage.setHacker(false);
//        } else {
//            throw new UnauthorizedException("User is neither Hacker nor Company!");
//        }
//    }
//
//
//    //
//    private ConnectListener onConnected() {
//        return socketIOClient -> {
////            socketExceptionHandler.executeWithExceptionHandling(()->{
////                String authorizationHeader = socketIOClient.getHandshakeData().getSingleUrlParam("Authorization");
////                log.info("Authorization Header of request: " + authorizationHeader);
////                // If user not authorized throw exception
//////                setUserIfAuthorized(socketIOClient);
////
////                // Is it user or company if authorized
//////                Object authenticatedUser = getAuthenticatedUser();
//////                log.info("User/Company info: " + authenticatedUser);
////
////                String room = socketIOClient.getHandshakeData().getSingleUrlParam("room");
////                Report reportOfMessage = reportRepository.findByRoom(room).orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));
////
////                // Is it user or company if authorized
//////                checkUserOrCompanyReport(authenticatedUser,reportOfMessage.getId());
////
////                socketIOClient.joinRoom(room);
////                log.info(String.format("SocketID: %s connected!", socketIOClient.getSessionId().toString()));
////                log.info(String.format("Connected to the room: %s",room));
////            });
//        };
//    }
//
//    private void setUserIfAuthorized(SocketIOClient socketIOClient) throws UnauthorizedException {
//            String authorizationHeader = socketIOClient.getHandshakeData().getHttpHeaders().get("Authorization");
//            log.info("Authorization Header of request: " + authorizationHeader);
//            if(authorizationHeader == null){
//                throw new UnauthorizedException("Authorization Header of request: null, You are unauthorized person");
//            }
//
//            String token = jwtTokenProvider.validateBearerToken(authorizationHeader);
//
//
//            if (token != null && jwtTokenProvider.validateToken(token)) {
//                String username = jwtTokenProvider.getUsernameFromToken(token);
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//                if (userDetails != null/* && userService.findUserByUsername(username).isActivated()*/) { // todo:fix this for only user not company
//                    System.out.println("UserDetails of current user: " + userDetails);
//                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            }
//    }
//
//    private DisconnectListener onDisconnected() {
//        return socketIOClient -> {
//          log.info(String.format("SocketID: %s disconnected!", socketIOClient.getSessionId().toString()));
//        };
//    }
//
//
//    // Util methods
//
//}
