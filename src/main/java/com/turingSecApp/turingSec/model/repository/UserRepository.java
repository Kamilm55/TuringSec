package com.turingSecApp.turingSec.model.repository;

import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAllByActivated(boolean activated);

    UserEntity findByActivationToken(String token);

    UserEntity findByEmail(String email);

}
