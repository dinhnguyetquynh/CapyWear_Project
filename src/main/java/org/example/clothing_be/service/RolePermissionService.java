package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.RolePermissionReq;
import org.example.clothing_be.dto.admin.res.RolePermissionRes;

public interface RolePermissionService {
    RolePermissionRes addNewRP(RolePermissionReq req);
}
