package com.turingSecApp.turingSec.Request;

import lombok.*;

@Data
//@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    private String firstName;
    private String lastName;

    // Add other user properties as needed
}
