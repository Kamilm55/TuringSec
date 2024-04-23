package com.turingSecApp.turingSec.Request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyRequest {
    private String email;
    private String password;
}
