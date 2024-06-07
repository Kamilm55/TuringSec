package com.turingSecApp.turingSec.model.entities.program;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = "bugBountyProgramForStrict")
@ToString(exclude = "bugBountyProgramForStrict")
@Entity
@Table(name = "strict_prohibits")
public class StrictEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prohibit_added")
    private String prohibitAdded;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    @JsonIgnore
    private Program bugBountyProgramForStrict;

    // Getters and setters
}
