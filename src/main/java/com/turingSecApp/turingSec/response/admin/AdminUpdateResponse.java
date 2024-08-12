package com.turingSecApp.turingSec.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUpdateResponse {

    private String first_name;
    private String last_name;
    private String username;

}
