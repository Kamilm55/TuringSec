package com.turingSecApp.turingSec.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
    @Schema(example = "Username")
    private String usernameOrEmail;
    @Schema(example = "userPass")
    private String password;
}
