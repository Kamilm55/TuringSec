package com.turingSecApp.turingSec.response.payment;

import com.turingSecApp.turingSec.model.enums.Currency;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    Long id;
    String firstName;
    String lastName;
    String countryName;
    Long countryId;
    String cityName;
    Long cityId;
    LocalDate dateOfBirth;
    String address;
    String cardNumber;
    Double balance;
    Currency currency;
    String nameAccountHolder;
    String bankAccountCountryName;
    Long bankAccountCountryId;
}
