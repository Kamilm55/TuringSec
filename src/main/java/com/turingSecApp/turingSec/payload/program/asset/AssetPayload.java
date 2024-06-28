package com.turingSecApp.turingSec.payload.program.asset;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetPayload {
    @NotBlank(message = "Asset type is required field")
    private String type;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be number(double) and greater than 0 or equal to 0")
    private Double price;

    private Set<String> names = new HashSet<>();
}
