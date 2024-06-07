package com.turingSecApp.turingSec.model.repository;


import com.turingSecApp.turingSec.model.entities.role.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

}
