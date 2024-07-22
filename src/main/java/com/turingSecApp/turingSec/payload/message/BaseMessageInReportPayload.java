package com.turingSecApp.turingSec.payload.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseMessageInReportPayload {
    @JsonProperty(value = "isReplied")
    private boolean isReplied;

    private Long replyToMessageId;
}
