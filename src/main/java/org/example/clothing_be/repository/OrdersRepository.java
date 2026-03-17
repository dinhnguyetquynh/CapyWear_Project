package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Integer> {
}
