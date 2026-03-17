package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Cart;
import org.hibernate.type.descriptor.jdbc.TinyIntAsSmallIntJdbcType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
}
