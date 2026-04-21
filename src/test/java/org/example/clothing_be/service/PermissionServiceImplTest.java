package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.PermissionReq;
import org.example.clothing_be.dto.admin.res.PermissionRes;
import org.example.clothing_be.entity.Permission;
import org.example.clothing_be.repository.PermissionRepository;
import org.example.clothing_be.service.serviceImpl.PermissionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceImplTest {
    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    void addPermission_shouldReturnPermissionRes_whenDescriptionIsProvided() {
        // Arrange
        PermissionReq req = new PermissionReq();
        req.setName("VIEW_DASHBOARD");
        req.setDescription("Allow user to view dashboard");
        req.setAction("READ");
        req.setResource("DASHBOARD");


        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
            Permission p = invocation.getArgument(0);
            p.setId(1L); // Giả lập database tự sinh ID
            return p;
        });

        // Act
        PermissionRes res = permissionService.addPermission(req);

        // Assert
        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("VIEW_DASHBOARD", res.getName());
        assertEquals("Allow user to view dashboard", res.getDescription());
        assertEquals("READ", res.getAction());
        assertEquals("DASHBOARD", res.getResource());

        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    void addPermission_shouldReturnPermissionRes_whenDescriptionIsNull() {
        // Arrange
        PermissionReq req = new PermissionReq();
        req.setName("DELETE_USER");
        req.setDescription(null); // Trường hợp description là null
        req.setAction("DELETE");
        req.setResource("USER");

        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
            Permission p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        // Act
        PermissionRes res = permissionService.addPermission(req);

        // Assert
        assertNotNull(res);
        assertEquals(2L, res.getId());
        assertNull(res.getDescription()); // Kiểm tra xem logic if(description != null) có chạy đúng không
        assertEquals("DELETE_USER", res.getName());

        verify(permissionRepository, times(1)).save(any(Permission.class));
    }
}
