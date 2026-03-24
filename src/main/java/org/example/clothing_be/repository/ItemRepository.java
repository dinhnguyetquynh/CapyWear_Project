package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item,Integer> {
    boolean existsByName(String name);
}
