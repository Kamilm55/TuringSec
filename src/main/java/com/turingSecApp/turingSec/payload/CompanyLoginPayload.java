package com.turingSecApp.turingSec.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyLoginPayload {
    @Schema(example = "string@gmail.com")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
