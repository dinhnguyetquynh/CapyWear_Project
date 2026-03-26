package org.example.clothing_be.dto.users.respone;

import lombok.Getter;
import lombok.Setter;
import org.example.clothing_be.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private int orderId;
    private LocalDateTime orderDate;
    private double totalOrder;
    private OrderStatus status;
    private List<OrderDetailDTO> details;
}
