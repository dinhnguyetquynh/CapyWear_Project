package org.example.clothing_be.repository;

import org.example.clothing_be.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail,Integer> {
    boolean existsByItemId(Integer itemId);
}
