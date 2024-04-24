package com.turingSecApp.turingSec.response;

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
    private String background_pic;
    private String profile_pic;
    private String bio;
    private String linkedin;
    private String twitter;
    private String github;
    private String city;

    private Long userId;
    private Long hackerId;

}
