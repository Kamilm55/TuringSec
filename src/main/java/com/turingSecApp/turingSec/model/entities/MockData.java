package com.turingSecApp.turingSec.model.entities;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "mock_data")
public class MockData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int insertedMockNumber; // default 0  , when inserts add 1 , it can be one or zero

}
