package com.turingSecApp.turingSec.file_upload.response;

import lombok.Data;

@Data
public class ImageForCompanyResponse {
    private Long id;

    private String name;
    private String contentType;
    private String companyId;

    private byte[] fileData;
}
