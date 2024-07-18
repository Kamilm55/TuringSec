package com.turingSecApp.turingSec.payload.message;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StringMessageInReportPayload extends BaseMessageInReportPayload{

    private String content;
}
