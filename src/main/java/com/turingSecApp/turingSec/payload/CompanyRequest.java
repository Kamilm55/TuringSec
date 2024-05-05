package com.turingSecApp.turingSec.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyRequest {
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
