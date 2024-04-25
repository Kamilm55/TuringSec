package com.turingSecApp.turingSec.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StrictDTO {
    //private Long id;
    private String prohibitAdded;
    private Long programId; // New field for program ID

    // Getters and setters
}