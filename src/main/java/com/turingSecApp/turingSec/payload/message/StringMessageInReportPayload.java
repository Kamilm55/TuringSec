package com.turingSecApp.turingSec.payload.message;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class StringMessageInReportPayload extends BaseMessageInReportPayload{

    private String content;
}
