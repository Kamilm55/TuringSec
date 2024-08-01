package com.turingSecApp.turingSec.payload.message;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
//@ToString(callSuper = true)
public class StringMessageInReportPayload extends BaseMessageInReportPayload{

    @NotBlank
    private String content;

    public String toString() {
        return "StringMessageInReportPayload(isReplied=" + super.isReplied()+ ", replyToMessageId=" + super.getReplyToMessageId() + ", content=" + this.getContent() + ")";
    }

}
