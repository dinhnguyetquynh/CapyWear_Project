package org.example.clothing_be.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class RolePermissionId implements Serializable {
    private Long roleId;
    private Long permissionId;
}
