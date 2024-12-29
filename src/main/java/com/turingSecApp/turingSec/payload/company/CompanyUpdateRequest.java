package com.turingSecApp.turingSec.payload.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUpdateRequest {
    @NotBlank(message = "Company name is required")
    private String company_name;

    @NotBlank(message = "Country is required")
    private String country;

    @Pattern(
            regexp = "^(https?:\\/\\/)?(www\\.)?linkedin\\.com\\/in\\/[^\\/]+(\\/)?$|^$",
            message = "Linkedin should be in the format linkedin.com/in/<username> or empty"
    )
    private String linkedin;

    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?x\\.com\\/\\w*$|^$",
            message = "Twitter should be in format x3.com/<username> or empty")
    private String twitter;

    private String website;
    private String bio;
}
