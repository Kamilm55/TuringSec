package com.turingSecApp.turingSec.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StrictPayload {

    @NotBlank(message = "Prohibit Added is required")
    private String prohibitAdded;
}
