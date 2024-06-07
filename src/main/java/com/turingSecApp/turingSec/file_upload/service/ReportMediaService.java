package com.turingSecApp.turingSec.file_upload.service;

import com.turingSecApp.turingSec.model.entities.report.Media;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.file_upload.exception.FileNotFoundException;
import com.turingSecApp.turingSec.file_upload.repository.MediaRepository;
import com.turingSecApp.turingSec.file_upload.response.FileResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.turingSecApp.turingSec.util.GlobalConstants.ROOT_LINK;

@Service
@RequiredArgsConstructor
public class ReportMediaService implements IFileService,IMultiFileService{
    private final MediaRepository mediaRepository;
    private final ReportsRepository reportsRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<FileResponse> saveFiles(List<MultipartFile> fileList, Long reportId) throws IOException {
        List<FileResponse> fileResponseList = new ArrayList<>();

        fileList.forEach(file -> {
            try {
                FileResponse fileResponse = saveVideoOrImg(file, reportId);
                fileResponseList.add(fileResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return fileResponseList;
    }
    @Override
    public FileResponse saveVideoOrImg(MultipartFile multipartFile, Long reportId) throws IOException {
        Report report = reportsRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id:" + reportId));

        Media newFile =  new Media();

        Media updatedFile = updateFileInfo(newFile, multipartFile, report,report.getUser().getHacker().getId());


        FileResponse fileResponse = mapToFileResponse(mediaRepository.save(updatedFile));

        // Add attachment link
        String media_url = ROOT_LINK + "/api/report-media/download/";
        report.addAttachment(media_url + fileResponse.getId());

        reportsRepository.save(report);

        return fileResponse;
    }

    private FileResponse mapToFileResponse(Media file) {
        return modelMapper.map(file, FileResponse.class);
    }

    private Media updateFileInfo(Media existingFile, MultipartFile multipartFile, Report report, Long hackerId) throws IOException {
        existingFile.setName(multipartFile.getOriginalFilename());
        existingFile.setContentType(multipartFile.getContentType());
        existingFile.setFileData(multipartFile.getBytes());
        existingFile.setHackerId(hackerId);

        // set report
        existingFile.setReport(report);

        return existingFile;
    }

    @Override
    public Media getMediaById(Long mediaId) throws FileNotFoundException {
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new FileNotFoundException("Video/image file not found for media id: " + mediaId));
    }


}
