package org.example.clothing_be.dto.admin.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionReq {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String resource;
    @NotBlank
    private String action;
}
