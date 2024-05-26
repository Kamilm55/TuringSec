package com.turingSecApp.turingSec.payload.program.asset;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetPayload {
    @NotBlank(message = "Asset type is required field")
    private String type;

    private Set<String> names = new HashSet<>();
}
