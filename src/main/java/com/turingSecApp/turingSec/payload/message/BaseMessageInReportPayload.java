package com.turingSecApp.turingSec.payload.message;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseMessageInReportPayload {
    @NotNull
    private boolean isReplied;

    private Long replyToMessageId;
}
