package com.turingSecApp.turingSec.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
    @Schema(example = "Username")
    @NotBlank(message = "Email/Username is mandatory")
    private String usernameOrEmail;

    @Schema(example = "userPass")
    @NotBlank(message = "Password is required")
    private String password;
}
