package com.turingSecApp.turingSec.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorWithIdPayload {
    @NotNull(message = "Id is required")
    private Long id;

    @NotBlank(message = "Hacker username is required")
    private String hackerUsername;

    @NotNull(message = "Collaboration percentage is required")
    private Double collaborationPercentage;
}
