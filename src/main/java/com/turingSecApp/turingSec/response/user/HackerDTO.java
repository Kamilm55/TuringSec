package com.turingSecApp.turingSec.response.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HackerDTO {
    private Long id;
    private String username;
    private String first_name;
    private String last_name;
    private String country;
    private String website;
    private String bio;
    private String linkedin;
    private String twitter;
    private String github;
    private String city;
    private String userId;

    private Long backgroundImageId;
    private Long imageId;
}
