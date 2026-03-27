package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Integer> {
    boolean existsByName(String name);
    Page<Item> findAllByDeletedFalse(Pageable pageable);
    List<Item> findTop10ByNameStartingWithIgnoreCase(String name);
    @Query("SELECT i FROM Item i WHERE i.inventoryQty <= :qty AND i.deleted = false")
    List<Item> findLowStockItems(@Param("qty") int qty);
}
