package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.res.OrderPendingRes;
import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderDetailDTO;
import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.entity.OrderDetail;
import org.example.clothing_be.entity.Orders;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.enums.OrderStatus;
import org.example.clothing_be.exception.ResourceNotFoundException;
import org.example.clothing_be.repository.ItemRepository;
import org.example.clothing_be.repository.OrdersRepository;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.OrderService;
import org.hibernate.query.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders order = new Orders();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);

        double finalTotalOrder = 0;

        for (OrderRequest.ItemRequest itemReq : request.getItems()) {
            Item item = itemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemReq.getItemId()));

            OrderDetail detail = new OrderDetail();
            detail.setItem(item);
            detail.setQuantity(itemReq.getQuantity());
            detail.setPurchasedPrice(item.getPrice());

            double totalPriceItems = item.getPrice() * itemReq.getQuantity();
            detail.setTotalItem(totalPriceItems);

            detail.setOrder(order);
            order.getOrderDetails().add(detail);

            finalTotalOrder += totalPriceItems;
        }
        order.setTotalOrder(finalTotalOrder);
        Orders savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new ResourceNotFoundException(""));
        List<Orders> ordersList = orderRepository.getAllByUser_Id(user.getId());
        List<OrderResponse> orderResponsesList = new ArrayList<>();
        for(Orders order : ordersList){
            OrderResponse orderResponse = mapToResponse(order);
            orderResponsesList.add(orderResponse);
        }
        return orderResponsesList;
    }

    @Override
    public List<OrderPendingRes> getOrdersPending() {
        List<Orders> ordersList = orderRepository.findAllByStatus(OrderStatus.PENDING);
        List<OrderPendingRes> orderResponseList = new ArrayList<>();
        for(Orders o:ordersList){
            OrderPendingRes orderResponse = mapToDTO(o);
            orderResponseList.add(orderResponse);
        }
        return orderResponseList;
    }

    @Override
    public OrderPendingRes changeStatusOrder(Integer orderId) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException(""));

        orders.setStatus(OrderStatus.COMPLETE);
        Orders saveOrder = orderRepository.save(orders);

        return mapToDTO(saveOrder);
    }

    private OrderPendingRes mapToDTO(Orders order) {
        OrderPendingRes response = new OrderPendingRes();
        response.setOrderId(order.getId());
        response.setUserEmail(order.getUser().getEmail());
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
