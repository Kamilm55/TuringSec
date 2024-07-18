package com.turingSecApp.turingSec.payload.message;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseMessageInReportPayload {

    private boolean isReplied;

    private Long replyToMessageId;

    private Long reportId;
    private boolean isHacker;
//    private boolean isCompany;
}
