package com.turingSecApp.turingSec.model.entities.payment;

import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "card")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String city;
    private LocalDate dateOfBirth;
    private String iban;
    private Currency currency;
    private String nameAccountHolder;
    private String bankAccountCountry;

    @ManyToOne
    private HackerEntity hacker;
}
