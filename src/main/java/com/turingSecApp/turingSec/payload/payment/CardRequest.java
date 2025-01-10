package com.turingSecApp.turingSec.payload.payment;

import com.turingSecApp.turingSec.model.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    @NotNull(message = "IBAN is required.")
    private String iban;

    @NotNull(message = "Currency is required.")
    private Currency currency;

    @NotNull(message = "Account holder name is required.")
    @Size(min = 2, max = 50, message = "Account holder name must be between 2 and 50 characters.")
    private String nameAccountHolder;

    @NotNull(message = "Bank account country is required.")
    private String bankAccountCountry;
}
