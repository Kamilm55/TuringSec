package com.turingSecApp.turingSec.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeEmailRequest {
    @NotBlank(message = "New email is mandatory")
    private String newEmail;
    @NotBlank(message = "Password is required")
    private String password;

}
