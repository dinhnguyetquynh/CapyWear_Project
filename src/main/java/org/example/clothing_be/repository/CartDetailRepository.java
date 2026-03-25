package org.example.clothing_be.repository;

import org.example.clothing_be.entity.CartDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail,Integer> {
    boolean existsByItemId(Integer itemId);
    Optional<CartDetail> findByCartIdAndItemId(int cartId, int itemId);
    List<CartDetail> findByCartId(int cartId);
    @Query("SELECT cd FROM CartDetail cd JOIN FETCH cd.item WHERE cd.cart.user.id = :userId")
    List<CartDetail> findAllByUserIdWithItem(@Param("userId") Long userId);
}
