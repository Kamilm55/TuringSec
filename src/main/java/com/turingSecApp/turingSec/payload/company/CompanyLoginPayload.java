package com.turingSecApp.turingSec.payload.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyLoginPayload {
    @Schema(example = "string@gmail.com")
    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = "\\S+", message = "Email cannot contain spaces")
    private String email;

    @Schema(example = "companyPass")
    @NotBlank(message = "Password is required")
    private String password;
}
