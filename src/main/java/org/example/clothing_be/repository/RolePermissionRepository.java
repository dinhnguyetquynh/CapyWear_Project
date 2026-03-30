package org.example.clothing_be.repository;

import org.example.clothing_be.entity.RolePermission;
import org.example.clothing_be.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}
