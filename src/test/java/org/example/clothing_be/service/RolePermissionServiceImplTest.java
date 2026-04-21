package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.RolePermissionReq;
import org.example.clothing_be.dto.admin.res.RolePermissionRes;
import org.example.clothing_be.entity.Permission;
import org.example.clothing_be.entity.Role;
import org.example.clothing_be.entity.RolePermission;
import org.example.clothing_be.exception.ResourceNotFoundException;
import org.example.clothing_be.repository.PermissionRepository;
import org.example.clothing_be.repository.RolePermissionRepository;
import org.example.clothing_be.repository.RoleRepository;
import org.example.clothing_be.service.serviceImpl.RolePermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RolePermissionServiceImplTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;

    private RolePermissionReq req;
    private Role mockRole;
    private Permission mockPermission;

    @BeforeEach
    void setUp() {
        // Chuẩn bị dữ liệu mẫu dùng chung cho các test case
        req = new RolePermissionReq();
        req.setRoleId(1);
        req.setPermissionId(2L);

        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setRoleName("ROLE_ADMIN");

        mockPermission = new Permission();
        mockPermission.setId(2L);
        mockPermission.setName("USER_CREATE");
    }

    @Test
    void addNewRP_shouldReturnSuccess_whenRoleAndPermissionExist() {
        // Arrange
        when(roleRepository.findById(1)).thenReturn(Optional.of(mockRole));
        when(permissionRepository.findById(2L)).thenReturn(Optional.of(mockPermission));

        // Mock hành vi save: trả về object RolePermission đã gán role và permission
        when(rolePermissionRepository.save(any(RolePermission.class))).thenAnswer(invocation -> {
            RolePermission rp = invocation.getArgument(0);
            return rp;
        });

        // Act
        RolePermissionRes res = rolePermissionService.addNewRP(req);

        // Assert
        assertNotNull(res);
        assertEquals("ROLE_ADMIN", res.getRole());
        assertEquals("USER_CREATE", res.getPermission());

        // Verify: Kiểm tra xem các repo có được gọi đúng không
        verify(roleRepository, times(1)).findById(1);
        verify(permissionRepository, times(1)).findById(2L);
        verify(rolePermissionRepository, times(1)).save(any(RolePermission.class));
    }

    @Test
    void addNewRP_shouldThrowException_whenRoleNotFound() {
        // Arrange
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            rolePermissionService.addNewRP(req);
        });

        // Đảm bảo rằng nếu Role không thấy thì không bao giờ gọi đến PermissionRepo hay Save
        verify(permissionRepository, never()).findById(anyLong());
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void addNewRP_shouldThrowException_whenPermissionNotFound() {
        // Arrange
        when(roleRepository.findById(1)).thenReturn(Optional.of(mockRole));
        when(permissionRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            rolePermissionService.addNewRP(req);
        });

        // Đảm bảo Role đã tìm thấy nhưng Permission lỗi thì không bao giờ gọi hàm Save
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }
}
