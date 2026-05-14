package org.example.clothing_be.repository;

import org.example.clothing_be.entity.UserRole;
import org.example.clothing_be.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

}
