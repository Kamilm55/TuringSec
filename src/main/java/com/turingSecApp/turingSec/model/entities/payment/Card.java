package com.turingSecApp.turingSec.model.entities.payment;

import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.enums.Currency;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "card")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String firstName;
    String lastName;
    String countryName;
    Long countryId;
    String cityName;
    Long cityId;
    String address;
    LocalDate dateOfBirth;
    String cardNumber;
    Double balance;
    Currency currency;
    String nameAccountHolder;
    String bankAccountCountryName;
    Long bankAccountCountryId;

    @ManyToOne
    private HackerEntity hacker;
}
