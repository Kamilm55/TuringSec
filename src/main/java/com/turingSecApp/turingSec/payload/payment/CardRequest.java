package com.turingSecApp.turingSec.payload.payment;

import com.turingSecApp.turingSec.model.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardRequest {
    @NotNull(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    String firstName;

    @NotNull(message = "Last name is required.")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    String lastName;

    @NotNull(message = "Country id is required.")
    Long countryId;

    @NotNull(message = "Country name is required")
    String countryName;

    @NotNull(message = "City id is required.")
    Long cityId;

    @NotNull(message = "City name is required")
    String cityName;

    @NotNull(message = "Address is required.")
    String address;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in past")
    LocalDate dateOfBirth;

    @NotNull(message = "Bank account country id is required.")
    Long bankAccountCountryId;

    @NotNull(message = "Bank account country name is required.")
    String bankAccountCountryName;

    @NotNull(message = "Currency is required.")
    Currency currency;

    @NotNull(message = "Card number is required.")
    String cardNumber;

    @NotNull(message = "Account holder name is required.")
    @Size(min = 2, max = 50, message = "Account holder name must be between 2 and 50 characters.")
    String nameAccountHolder;

}
