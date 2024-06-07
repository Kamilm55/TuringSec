package com.turingSecApp.turingSec.model.repository;


import com.turingSecApp.turingSec.model.entities.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
