package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.model.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.report.ReportManual;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.response.report.ReportsByUserDTO;
import com.turingSecApp.turingSec.response.report.ReportsByUserWithCompDTO;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.service.interfaces.IBugBountyReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bug-bounty-reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ReportController {

    private final IBugBountyReportService bugBountyReportService;

    //TODO
    // -> tekce Admine icaze ver
    // get all reports
    // get all reports by company id
    //  get all reports by user id
    // token istemeye ehtiyac yoxdu, payload hecne, dto -> BaseResponse<List<ReportDTO>> -> company id , program id , userId(hansi hackerdi) , username of user

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


    @GetMapping("/{id}")
    public BaseResponse<Report> getBugBountyReportById(@PathVariable Long id) {
        Report bugBountyReport = bugBountyReportService.getBugBountyReportById(id);
        return BaseResponse.success(bugBountyReport);
    }

    // Learn:
    //  @RequestPart
    //  Purpose: Used to bind a part of a "multipart/form-data" request to a method parameter.
    //  Typical Usage: When you need to handle file uploads along with other form fields or JSON data in a multipart request.

//    https://github.com/swagger-api/swagger-core/issues/3050
//ResponseEntity<Void> doSomething(MyMultipartRequest request) {...}

    // Handle big videos -> https://stackoverflow.com/questions/2689989/how-to-handle-maxuploadsizeexceededexception
    @PostMapping(value = "/manualReport",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<ReportManual> submitManualReport(
            @RequestPart(value = "files",required = false) @Parameter(description = "File to upload") @Schema(type = "string", format = "binary") List<MultipartFile> files,
            @RequestPart("reportPayload") @Valid ReportManualPayload reportPayload,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long bugBountyProgramId
    ) {

        ReportManual submittedBugBountyReport = null;
        try {
            submittedBugBountyReport = bugBountyReportService.submitManualReport(files,userDetails,reportPayload, bugBountyProgramId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return BaseResponse.success(
                submittedBugBountyReport,
                "Bug bounty report submitted successfully");
    }

//    @PutMapping("/manualReport/{id}")
//    public BaseResponse<ReportManual> updateManualReport(@PathVariable Long id,
//                                                         @RequestBody @Valid ReportManualPayload bugBountyReportUpdatePayload) {
//        ReportManual updatedReport = bugBountyReportService.updateManualReport(id, bugBountyReportUpdatePayload);
//        return BaseResponse.success(updatedReport);
//    }

    //
    @PostMapping(value = "/CVSSReport",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<ReportCVSS> submitCVSSReport(
            @RequestPart(value = "files",required = false) @Parameter(description = "File to upload") @Schema(type = "string", format = "binary") List<MultipartFile> files,
            @RequestPart("reportPayload") @Valid ReportCVSSPayload reportPayload,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long bugBountyProgramId) {
        ReportCVSS submittedBugBountyReport = null;
        try {
            submittedBugBountyReport = bugBountyReportService.submitCVSSReport(files,userDetails,reportPayload, bugBountyProgramId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return BaseResponse.success(submittedBugBountyReport,"Bug bounty report submitted successfully");
    }

//    @PutMapping("/CVSSReport/{id}")
//    public BaseResponse<ReportCVSS> updateManualReport(@PathVariable Long id,
//                                                         @RequestBody @Valid ReportCVSSPayload bugBountyReportUpdatePayload) {
//        ReportCVSS updatedReport = bugBountyReportService.updateCVSSReport(id, bugBountyReportUpdatePayload);
//        return BaseResponse.success(updatedReport);
//    }


    @DeleteMapping("/{id}")
    public BaseResponse<?> deleteBugBountyReport(@PathVariable Long id) {
        bugBountyReportService.deleteBugBountyReport(id);
        // refactorThis -> new ResponseEntity<>(HttpStatus.NO_CONTENT)
        return BaseResponse.success();
    }

    @GetMapping("/user")
    public BaseResponse<List<ReportsByUserWithCompDTO>> getAllBugBountyReportsByUser() {
        List<ReportsByUserWithCompDTO> userReports = bugBountyReportService.getAllBugBountyReportsByUser();
        return BaseResponse.success(userReports);
    }

    @GetMapping("/reports/company")
    public BaseResponse<List<ReportsByUserDTO>> getBugBountyReportsForCompanyPrograms() {
        return BaseResponse.success(bugBountyReportService.getBugBountyReportsForCompanyPrograms());
    }

    //    @GetMapping// No need , because every report belongs to specific hacker or company
//    public ResponseEntity<List<ReportsEntity>> getAllBugBountyReports() {
//        List<ReportsEntity> bugBountyReports = bugBountyReportService.getAllBugBountyReports();
//        return new ResponseEntity<>(bugBountyReports, HttpStatus.OK);
//    }
}
