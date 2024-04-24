package com.turingSecApp.turingSec.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HackerDTO {
    private Long id;
    private String first_name;
    private String last_name;
    private String country;
    private String website;
    private String bio;
    private String linkedin;
    private String twitter;
    private String github;
    private String city;
    private Long userId;

    private Long backgroundImageId;
    private Long imageId;
}
