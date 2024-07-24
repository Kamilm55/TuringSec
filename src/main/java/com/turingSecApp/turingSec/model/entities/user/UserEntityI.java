    package com.turingSecApp.turingSec.model.entities.user;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.turingSecApp.turingSec.model.entities.report.Report;
    import com.turingSecApp.turingSec.model.entities.role.Role;
    import jakarta.persistence.*;
    import lombok.*;

    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(exclude = {"hacker", "reports"}) // this is important for operations in db
    @ToString(exclude = {"hacker", "reports"})
    @Data
    @Entity
    @Table(name = "users")
    public class UserEntityI implements IBaseUser {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String first_name;
        private String last_name;
        private String email;
        private String password;
        private boolean activated;
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
        )
        @JsonIgnore
        private Set<Role> roles = new HashSet<>();;

        private String username;
        private String country;

        @Column(name = "activation_token")
        private String activationToken;



        @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Report> reports = new ArrayList<>();

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private HackerEntity hacker;
    }
