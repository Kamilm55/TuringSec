package com.turingSecApp.turingSec.model.entities.user;

import com.turingSecApp.turingSec.model.entities.report.Report;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"hacker", "reports"},callSuper = true) // this is important for operations in db
@ToString(exclude = {"hacker", "reports"},callSuper = true)
@Data
@Entity
@DiscriminatorValue("USER")
@Table(name = "users")
public class  UserEntity extends BaseUser {
    private String username;

    @Column(name = "activation_token")
    private String activationToken;


    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private HackerEntity hacker;

}