package com.turingSecApp.turingSec.payload.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
    @Schema(example = "Username")
    @NotBlank(message = "Email/Username is mandatory")
    @Pattern(regexp = "\\S+", message = "Email/Username cannot contain spaces")
    private String usernameOrEmail;

    @Schema(example = "userPass")
    @NotBlank(message = "Password is required")
    private String password;
}
