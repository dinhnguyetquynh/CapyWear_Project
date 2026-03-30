package org.example.clothing_be.service;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.PermissionReq;
import org.example.clothing_be.dto.admin.res.PermissionRes;
import org.springframework.stereotype.Service;

public interface PermissionService {
    PermissionRes addPermission(PermissionReq req);
}
