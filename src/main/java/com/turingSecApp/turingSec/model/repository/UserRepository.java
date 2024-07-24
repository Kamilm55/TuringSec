package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.user.UserEntityI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntityI, Long> {
    Optional<UserEntityI> findByUsername(String username);
    List<UserEntityI> findAllByActivated(boolean activated);

    UserEntityI findByActivationToken(String token);

    UserEntityI findByEmail(String email);

}
