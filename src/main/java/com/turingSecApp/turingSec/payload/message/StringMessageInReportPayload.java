package com.turingSecApp.turingSec.payload.message;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class StringMessageInReportPayload extends BaseMessageInReportPayload{

    @NotBlank
    private String content;
}
