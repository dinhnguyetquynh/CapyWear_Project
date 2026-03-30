package org.example.clothing_be.dto.admin.res;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolePermissionRes {
    @NotBlank
    private String role;
    @NotBlank
    private String permission;
}
