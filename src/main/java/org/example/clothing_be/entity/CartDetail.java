package org.example.clothing_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_detail")
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    private double purchasePrice;
    private int quantity;
    private double totalItem;
    private LocalDate dateAdd;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
