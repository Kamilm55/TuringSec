package com.turingSecApp.turingSec.controller;


import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IMessageInReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageInReportController {


    // Socket service 116-dan baxa bilersen
    // room (Report, reportdan da mesaji) -> StringMessageInReport (entity) -> StringMessageInReportDTO
    //  TODO:
    //   1. Butun mesajlar get ile cekilmelidi (payload olaraq room) return -> List<child of BaseMessageInReportDTO> (BaseMessageInReport-un reposundan find edessen) +
    //   securityConfig-de path-i rollara gore icaze ver, Role company, ROLE Hacker hami accesi var, +
    //  bu mesajin aid oldugu reportun user-i ve ya reportun company-si ancaq accesible ola biler +
    //   Token-den extract etdiyimiz user(hacker) hemin reportun useri ile eyni olmalidi , deyilse exception ("Message of Hacker must be same with report Hacker") -> (hem log hem de sendEvent ile error eventinde ex-mesaji gondermek)
    //    Token-den extract etdiyimiz company hemin reportun company-si ile eyni olmalidi , deyilse exception
    //    getAll, getById
    //  2. yuxaridakinin admin ucun olani, security configde role admin -> getAll, getById
    //
    //   3. get all Deleted mesaj yeni entity yarat deletedAt,-> getAll, getById -> ancaq Admin
    //   4. edit ucun -> getAll, getById -> ancaq Admin

    private final IMessageInReportService reportService;

    @GetMapping("/messages")
    public BaseResponse<List<StringMessageInReportDTO>> getMessages(@RequestParam String room) {
        return BaseResponse.success(reportService.getMessagesByRoom(room));
    }

    @GetMapping("/message/{id}")
    public BaseResponse<StringMessageInReportDTO> getMessageById(@PathVariable Long id) {
        return BaseResponse.success(reportService.getMessageById(id));
    }


    @GetMapping("/admins/{reportId}")
    public BaseResponse<List<StringMessageInReportDTO>> getMessageByReportId(@PathVariable Long reportId){
        return BaseResponse.success(reportService.getMessageByReportId(reportId));
    }


    @GetMapping("/admin/{id1}")
    public BaseResponse<StringMessageInReportDTO> getMessageWithId(@PathVariable Long id1){
        return BaseResponse.success(reportService.getMessageWithId(id1));
    }


}