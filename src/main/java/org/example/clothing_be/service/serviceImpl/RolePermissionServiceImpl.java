package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.RolePermissionReq;
import org.example.clothing_be.dto.admin.res.RolePermissionRes;

import org.example.clothing_be.entity.Permission;
import org.example.clothing_be.entity.Role;
import org.example.clothing_be.entity.RolePermission;
import org.example.clothing_be.exception.ResourceNotFoundException;
import org.example.clothing_be.repository.PermissionRepository;
import org.example.clothing_be.repository.RolePermissionRepository;
import org.example.clothing_be.repository.RoleRepository;
import org.example.clothing_be.service.RolePermissionService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public RolePermissionRes addNewRP(RolePermissionReq req) {
        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(()-> new ResourceNotFoundException(""));
        Permission permission = permissionRepository.findById(req.getPermissionId())
                .orElseThrow(()-> new ResourceNotFoundException(""));

        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setPermission(permission);

        RolePermission savedRP = rolePermissionRepository.save(rp);
        return toDTO(savedRP);
    }

    private RolePermissionRes toDTO(RolePermission req){
        RolePermissionRes res = new RolePermissionRes();
        res.setRole(req.getRole().getRoleName());
        res.setPermission(req.getPermission().getName());
        return res;
    }
}
