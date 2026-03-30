package org.example.clothing_be.repository;

import org.example.clothing_be.dto.admin.req.PermissionReq;
import org.example.clothing_be.dto.admin.res.PermissionRes;
import org.example.clothing_be.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {

}
