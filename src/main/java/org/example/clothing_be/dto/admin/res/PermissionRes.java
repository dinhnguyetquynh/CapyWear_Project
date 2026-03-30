package org.example.clothing_be.dto.admin.res;

import lombok.Data;

@Data
public class PermissionRes {
    private Long id;
    private String name;
    private String description;
    private String resource;
    private String action;
}
