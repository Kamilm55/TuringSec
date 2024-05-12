package com.turingSecApp.turingSec.file_upload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {

    private Long id;

    private String name;
    private String contentType;
    private Long hackerId;

    private byte[] fileData;

}