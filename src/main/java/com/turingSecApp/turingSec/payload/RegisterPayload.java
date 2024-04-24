package com.turingSecApp.turingSec.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterPayload {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String country;

    // Additional fields for hacker
//    private String website;
//    private String background_pic;
//    private String profile_pic;
//    private String bio;
//    private String linkedin;
//    private String twitter;
//    private String github;
//    private String city;
}
