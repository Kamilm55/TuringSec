package com.turingSecApp.turingSec.model.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@EqualsAndHashCode(exclude = "user") // this is important for operations in db
@ToString(exclude = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "hackers")
public class HackerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore

    private Long id;
    private String first_name;
    private String last_name;
    private String country;
    private String website;
    private boolean has_background_pic;
    private boolean has_profile_pic;
//    private Long background_pic_id;
//    private Long profile_pic_id;
    private String bio;
    private String linkedin;
    private String twitter;
    private String github;
    private String city;


    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private UserEntity user;


}
