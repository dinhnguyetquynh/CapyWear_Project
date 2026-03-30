package org.example.clothing_be.dto.admin.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RolePermissionReq {
    @NotBlank
    private Integer roleId;
    @NotBlank
    private Long permissionId;
}
