package org.example.clothing_be.dto.admin.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.clothing_be.dto.users.respone.OrderDetailDTO;
import org.example.clothing_be.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPendingRes {
    private int orderId;
    private String userEmail;
    private LocalDateTime orderDate;
    private double totalOrder;
    private String status;
    private List<OrderDetailDTO> details;
}
