package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderDetailDTO;
import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.entity.OrderDetail;
import org.example.clothing_be.entity.Orders;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.enums.OrderStatus;
import org.example.clothing_be.repository.ItemRepository;
import org.example.clothing_be.repository.OrdersRepository;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrdersRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Tìm User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Khởi tạo đối tượng Order
        Orders order = new Orders();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);

        double finalTotalOrder = 0;

        // 3. Duyệt qua danh sách item từ FE để tạo OrderDetail
        for (OrderRequest.ItemRequest itemReq : request.getItems()) {
            Item item = itemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemReq.getItemId()));

            OrderDetail detail = new OrderDetail();
            detail.setItem(item);
            detail.setQuantity(itemReq.getQuantity());
            detail.setPurchasedPrice(item.getPrice()); // Lấy giá hiện tại từ DB

            double totalItem = item.getPrice() * itemReq.getQuantity();
            detail.setTotalItem(totalItem);

            // Thiết lập mối quan hệ 2 chiều
            detail.setOrder(order);
            order.getOrderDetails().add(detail);

            finalTotalOrder += totalItem;
        }

        order.setTotalOrder(finalTotalOrder);

        // 4. Lưu vào Database (Nhờ CascadeType.ALL nên OrderDetail sẽ được lưu cùng)
        Orders savedOrder = orderRepository.save(order);

        // 5. Chuyển đổi sang Response DTO để trả về
        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Orders order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setOrderDate(LocalDateTime.now()); // Hoặc dùng order.getOrderDate()
        response.setTotalOrder(order.getTotalOrder());
        response.setStatus(order.getStatus());

        List<OrderDetailDTO> details = order.getOrderDetails().stream().map(d -> {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setItemName(d.getItem().getName());
            dto.setQuantity(d.getQuantity());
            dto.setPrice(d.getPurchasedPrice());
            dto.setTotal(d.getTotalItem());
            return dto;
        }).collect(Collectors.toList());

        response.setDetails(details);
        return response;
    }
}
