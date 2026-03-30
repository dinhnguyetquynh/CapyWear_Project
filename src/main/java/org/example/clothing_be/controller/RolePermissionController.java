package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.RolePermissionReq;
import org.example.clothing_be.dto.admin.res.RolePermissionRes;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.service.RolePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rp")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @PostMapping
    public ResponseEntity<ApiRes<RolePermissionRes>> addNewRolePermission(@RequestBody RolePermissionReq req){
        RolePermissionRes res = rolePermissionService.addNewRP(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiRes.success(201,res,"Them permission cho role thanh cong"));
    }
}
