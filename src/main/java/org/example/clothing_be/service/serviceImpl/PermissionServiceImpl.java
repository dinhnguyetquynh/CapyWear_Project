package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.PermissionReq;
import org.example.clothing_be.dto.admin.res.PermissionRes;
import org.example.clothing_be.entity.Permission;
import org.example.clothing_be.repository.PermissionRepository;
import org.example.clothing_be.service.PermissionService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    @Override
    public PermissionRes addPermission(PermissionReq req) {
        Permission permission = new Permission();
        permission.setName(req.getName());
        if(req.getDescription()!=null){
            permission.setDescription(req.getDescription());
        }
        permission.setAction(req.getAction());
        permission.setResource(req.getResource());

        Permission savedPermission = permissionRepository.save(permission);
        return toPermissionDTO(savedPermission);
    }

    private PermissionRes toPermissionDTO(Permission permission){
        PermissionRes res = new PermissionRes();
        res.setId(permission.getId());
        res.setName(permission.getName());
        res.setDescription(permission.getDescription());
        res.setAction(permission.getAction());
        res.setResource(permission.getResource());
        return res;
    }
}
