package com.turingSecApp.turingSec.payload.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Country is required")
    private String country;

    @Pattern(
            regexp = "^(https?:\\/\\/)?(www\\.)?linkedin\\.com\\/in\\/[^\\/]+(\\/)?$|^$",
            message = "Linkedin should be in the format linkedin.com/in/<username> or empty"
    )
    private String linkedin;


    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?github\\.com\\/\\w*$|^$",
            message = "GitHub should be in format github.com/<username> or empty")
    private String github;

    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?x\\.com\\/\\w*$|^$",
            message = "Twitter should be in format x3.com/<username> or empty")
    private String twitter;
    @NotBlank(message = "City is required")
    private String city;

    // Optional fields
    private String website;
    private String bio;
//    private boolean has_background_pic;
//    private boolean has_profile_pic;
}
