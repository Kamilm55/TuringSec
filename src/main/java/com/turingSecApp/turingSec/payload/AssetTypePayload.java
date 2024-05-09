package com.turingSecApp.turingSec.payload;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssetTypePayload {
    @NotBlank(message = "Level is required")
    private String level;

    @NotBlank(message = "Asset type is required")
    private String assetType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be number(double) and greater than 0")
//    @Digits(integer = 10, fraction = 2, message = "Price must be a valid number with up to 10 digits in total, and 2 digits after the decimal point")
    private Double price;
}
