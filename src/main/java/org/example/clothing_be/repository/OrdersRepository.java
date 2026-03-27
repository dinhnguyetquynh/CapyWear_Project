package org.example.clothing_be.repository;

import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.entity.Orders;
import org.example.clothing_be.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Integer> {
    List<Orders> getAllByUser_Id(Long userId);
    List<Orders> findAllByStatus(OrderStatus status);
}
