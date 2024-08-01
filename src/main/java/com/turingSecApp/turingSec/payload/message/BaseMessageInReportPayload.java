package com.turingSecApp.turingSec.payload.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseMessageInReportPayload {
    @NotNull
    @JsonProperty(value = "isReplied")
    private boolean isReplied;

    private Long replyToMessageId;
}
