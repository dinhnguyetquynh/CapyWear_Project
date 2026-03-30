package org.example.clothing_be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.PermissionReq;
import org.example.clothing_be.dto.admin.res.PermissionRes;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiRes<PermissionRes>> createPermission(@Valid @RequestBody PermissionReq req){
        PermissionRes permissionRes = permissionService.addPermission(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiRes.success(201,permissionRes,"Tạo Permission thành công"));
    }
}
