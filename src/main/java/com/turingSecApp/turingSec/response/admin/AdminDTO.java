package com.turingSecApp.turingSec.response.admin;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {
    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private String username;
}
