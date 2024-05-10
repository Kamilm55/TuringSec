package com.turingSecApp.turingSec.response.admin;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminAuthResponse {
    String accessToken;
    AdminDTO userInfo;
}
