package com.turingSecApp.turingSec.model.entities.user;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.turingSecApp.turingSec.exception.custom.InvalidUUIDFormatException;
import com.turingSecApp.turingSec.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type") // Discriminator column to distinguish between types
@Table(name = "base_users")
@Slf4j
public class BaseUser {
    // We cannot use hibernate custom generator -> we cannot set uuid explicitly for mock data
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
//    @GenericGenerator(
//            name = "sequenceGenerator",
//            type = SequenceStyleGenerator.class
//    )
//    @Column(updatable = false, nullable = false)
    @Id
    private UUID id;
    private String first_name;
    private String last_name;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean activated;
    private String country;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "base_user_id")) // Note: Use `user_id` or appropriate column
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        log.info("OnCreate - @PrePersist works in BaseUser");
        if (this.id == null) { // If UUID is not set explicitly -> If I haven't specified uuid explicitly, it should generate
            try {
                NameBasedGenerator generator = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_DNS);
                String uniqueName = "baseUserID-" + UUID.randomUUID() +
                        (this.email != null ? this.email : "") +
                        (this.password != null ? this.password : "") +
                        (this.country != null ? this.country : "") +
                        (this.first_name != null ? this.first_name : "") +
                        (this.last_name != null ? this.last_name : "") +
                        System.currentTimeMillis();
                UUID uuid = generator.generate(uniqueName);
                System.out.println("Generated UUIDv5 for base user id: " + uuid.toString());
                this.id = uuid;
            } catch (Exception e) {
                System.err.println("Error generating UUID: " + e.getMessage());
            }
        }
    }


    public String getId() {
        if (id != null) {
            return id.toString();
        }
        return null;
//        else {
//            throw new RuntimeException("Base user id is null");
//        }
    }

    public void setId(String id) {
        try {
            this.id = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDFormatException("Invalid UUID format for ID: " + id, e);
        }
    }
}