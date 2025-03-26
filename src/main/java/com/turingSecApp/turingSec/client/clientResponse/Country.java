package com.turingSecApp.turingSec.client.clientResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    private Long id;
    private String name;
    private String iso2;

}