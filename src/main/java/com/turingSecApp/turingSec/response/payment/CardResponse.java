package com.turingSecApp.turingSec.response.payment;

import com.turingSecApp.turingSec.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String city;
    private LocalDate dateOfBirth;
    private String iban;
    private Currency currency;
    private String nameAccountHolder;
    private String bankAccountCountry;
}
