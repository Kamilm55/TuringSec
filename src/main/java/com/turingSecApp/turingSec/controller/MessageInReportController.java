package com.turingSecApp.turingSec.controller;


import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import com.turingSecApp.turingSec.service.interfaces.IMessageInReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messagesInReport")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class MessageInReportController {



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


}