package com.turingSecApp.turingSec.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHackerDTO {
    private String username;
    private String first_name;
    private String last_name;
    private String country;
    private String website;
    private boolean has_background_pic;
    private boolean has_profile_pic;
    private String bio;
    private String linkedin;
    private String twitter;
    private String github;
    private String city;

    private String userId;
    private Long hackerId;

}
