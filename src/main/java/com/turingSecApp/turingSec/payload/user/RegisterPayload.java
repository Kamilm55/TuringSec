package com.turingSecApp.turingSec.payload.user;

import com.turingSecApp.turingSec.exception.validation.NoSpaces;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterPayload {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "\\S+", message = "Username cannot contain spaces")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Country is required")
    private String country;

}
// Additional fields for hacker
//    private String website;
//    private String background_pic;
//    private String profile_pic;
//    private String bio;
//    private String linkedin;
//    private String twitter;
//    private String github;
//    private String city;