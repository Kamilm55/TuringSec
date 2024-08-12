package com.turingSecApp.turingSec.service.websocket.messageInReport;

import com.turingSecApp.turingSec.config.websocket.headerAccessorAdapter.SimpHeaderAccessorAdapter;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.model.repository.reportMessage.StringMessageInReportRepository;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import com.turingSecApp.turingSec.exception.websocket.exceptionHandling.SocketExceptionHandler;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StompMessageInReportService implements IStompMessageInReportService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ReportRepository reportRepository;
    private final StringMessageInReportRepository stringMessageInReportRepository;
    private final UtilService utilService;
    private final SocketExceptionHandler socketExceptionHandler;
    private final CommonMessageInReportService commonMessageInReportService;



    //  1. Exceptionlar error event name ile atilmalidi ,  hem log hem sendEvent +
    //  2. headerda access token alinmalidi, ve mesaji gonderen Hacker yoxsa Company-di tapa bilerik token vasitesile , (en 1-ci unauthorized olmadigini yoxlamalyiq, eks halda exception) +
    //  3. Payloaddan entity-e kecirerken eger token ile tapdigimiz user hackerdisa -> isHacker=true , company-dirse -> isHacker=false edib save edirik +
    //  6. Token-den extract etdiyimiz user(hacker) hemin reportun useri ile eyni olmalidi , deyilse exception ("Message of Hacker must be same with report Hacker") -> (hem log hem de sendEvent ile error eventinde ex-mesaji gondermek) +
    //  7. Token-den extract etdiyimiz company hemin reportun company-si ile eyni olmalidi , deyilse exception +
    //    hacker ve ya company reporta aid deyilse mesaj gondere bilmir (connect ola bilmir) , room-a girmeye icaze verme +
    //  8. bu room reportun fieldi-dir, ona gore bu room fieldi ile reportu tapa bilerik -> // room as query ?room= uuid of msg's report +
    //   (*Reportu room ile , user ve company-de report ile tapa bilerik)
    //    dto-ya cevirerken her iki id-ni set et ,  reportdan gotur , isHackeride set et entityden
    //  4. Reportun yalniz bir user(hacker) ve bir company id-si ola biler , isHacker true ve ya false ferq etmir
    //      hansi hal olursa olsun her iki id DTO-da gorunmelidi entity-de yox(report-dan goture bilerik bu iki value-nu)  ,
    //      frontendler isHacker-a gore mesaji gonderenin company yoxsa hacker oldugunu mueyyenlesdirib lazim olan id-ni isledecekler
    // TODO: TASKS
    //  11. Message payload validationlar edilmelidi, log ve event olaraq gonder -> customize (SimpAnnotationMethodMessageHandler (handleMatch)  ) which abstract class is AbstractMethodMessageHandler.handleMatch then add to config -> WebSocketAnnotationMethodMessageHandler burada catch olur
    // todo:  executeWithExceptionHandling for @Service (topic/baseUserId/error) , butun errorlari buna cevir
    //  5. DTO-da company ve hacker-in img,background img gosterilmelidi (baseUser-dan gelecek)
    //    * Butun log-lardaki melumatlar dogru olmalidi, payload->entity->dto hamisi duzgun sekilde yaradilmalidi
    //  14. reply etmemisden qabag hemin mesaj movcud deyilse exception
    //      Adminler reportId ile istenilen report altindaki, butun mesajlari gore biler list<DTO> seklinde
    //      Silinmis mesajlar, editlenmis mesajlarida bu 3 task - http-dir socket ile deyil
    //   12. getMessage ile evvelki gonderilen mesajlar gorunmelidi ama list<DTO> seklinde
    //   reporta da createdAt elave et
    //   13. Butun bu tasklar bitenden sonra -> editMessage, deleteMessage, deleteMessageList (every user only edit or delete own), edited or deleted messages must be tracked in logs and db, explore: "can we store logs?(not to use safe delete)"
    //   10. *** Mesaj eger reply-dirsa isReplied true,repliedTo - da id-si verilir, amma DTO-da reply olunan mesajin contenti yoxdu ***


    @Override
    public void sendTextMessageToReportRoom(String room, StringMessageInReportPayload strMessageInReportPayload, SimpMessageHeaderAccessor headerAccessor) {
        SimpHeaderAccessorAdapter accessorAdapter = new SimpHeaderAccessorAdapter(headerAccessor);

        if (strMessageInReportPayload.getContent() == null || strMessageInReportPayload.getContent().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty!");
        }

            Report reportOfMessage = reportRepository.findByRoom(room).orElseThrow(() -> new ResourceNotFoundException("Report not found with room: " + room));

            // Is it user or company if authorized
            Object authenticatedUser = utilService.getAuthenticatedBaseUserForWebsocket();
            log.info("User/Company info: " + authenticatedUser);
//
//        // Create message from payload
            StringMessageInReport strMessage = commonMessageInReportService.createStringMessageInReport(strMessageInReportPayload,authenticatedUser,reportOfMessage);

            // Save created strMessage obj
            StringMessageInReport savedMsg = stringMessageInReportRepository.save(strMessage);
            log.info(String.format("Data after save -> StringMessageInReport (entity): %s", savedMsg));

            // Convert to DTO with lazy fields' id
            StringMessageInReportDTO msgDTO = commonMessageInReportService.toStringMessageInReportDTO(savedMsg);
            log.info(String.format("Data after converting into DTO  -> StringMessageInReportDTO): %s", msgDTO));

            // Send message with socket
            messagingTemplate.convertAndSend(
                    String.format("/topic/%s/messagesInReport", room), //  stompClient.subscribe('/topic/{room}/messages'...
                    msgDTO
            );
    }


}
