package com.turingSecApp.turingSec.controller;


import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.payload.message.StringMessageInReportPayload;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.response.report.AllReportDTO;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IMessageInReportService;
import com.turingSecApp.turingSec.service.interfaces.IStompMessageInReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messagesInReport")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class MessageInReportController {


    //TODO
    // -> tekce Admine icaze ver
    // get all reports
    // get all reports by company id +
    //  get all reports by user id +
    // token istemeye ehtiyac yoxdu, payload hecne, dto -> BaseResponse<List<ReportDTO>> -> company id , program id , userId(hansi hackerdi) , username of user +

    //1. user report atannan sonra  ---> submitted - unreviewed
    // POST submitManualReport-da statusu submitted - unreviewed set et
    // POST submitCVSS-da statusu submitted - unreviewed set et

    // hacker hissesinde all( submitted underreview (accepted | rejected) -> assessed )
    // sirket hissesinde all(unreviewed,reviewed,assessed)

    // get all reports for hacker -> var -> getAllBugBountyReportsByUser
    // get submitted reports for hacker
    // get underreview reports for hacker
    // get accepted reports for hacker
    // get rejected reports for hacker
    // get assessed reports for hacker -> if accepted | rejected return

    // get all reports for company -> var -> getAllBugBountyReportsByCompany
    // get submitted reports for company
    // get unreviewed reports for company
    // get reviewed reports for company
    // get assessed reports for company

    // QUERY ILE ET -> EGER QUERY PARAM SEHVDISE ILLAGEAL ARGUMENT


    // Socket service 116-dan baxa bilersen
    // room (Report, reportdan da mesaji) -> StringMessageInReport (entity) -> StringMessageInReportDTO
    //  TODO:
    //   3. get all Deleted mesaj yeni entity yarat deletedAt,-> getAll, getById -> ancaq Admin
    //   4. edit ucun -> getAll, getById -> ancaq Admin

    private final IMessageInReportService messageInReportService;

    @GetMapping
    public BaseResponse<List<StringMessageInReportDTO>> getMessages(@RequestParam String room) {
        return BaseResponse.success(messageInReportService.getMessagesByRoom(room));
    }

    @GetMapping("/{id}")
    public BaseResponse<StringMessageInReportDTO> getMessageById(@PathVariable Long id) {
        return BaseResponse.success(messageInReportService.getMessageById(id));
    }


    @GetMapping("/report/{id}/admin")
    public BaseResponse<List<StringMessageInReportDTO>> getMessageByReportId(@PathVariable("id") Long reportId){
        return BaseResponse.success(messageInReportService.getMessageByReportId(reportId));
    }


    @GetMapping("/message/{id}/admin")
    public BaseResponse<StringMessageInReportDTO> getMessageWithId(@PathVariable Long id){
        return BaseResponse.success(messageInReportService.getMessageWithId(id));
    }


    @GetMapping("/company/{companyId}")
    public BaseResponse<List<Report>> getAllReportsByCompanyId(@PathVariable Long companyId) {
        return BaseResponse.success(messageInReportService.getReportsByCompanyId(companyId));

    }
    // Get all reports by user id
    @GetMapping("/user/{userId}")
    public BaseResponse<List<Report>> getAllReportsByUserId(@PathVariable Long userId) {
        return BaseResponse.success(messageInReportService. getReportsByUserId(userId));
    }

    @GetMapping("/report/all")
    public BaseResponse<List<AllReportDTO>> getAllReport(){
        return BaseResponse.success(messageInReportService.getAllReports());
    }
}