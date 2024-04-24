package com.turingSecApp.turingSec.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String username;
}
