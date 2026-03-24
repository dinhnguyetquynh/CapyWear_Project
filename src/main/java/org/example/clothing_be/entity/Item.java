package org.example.clothing_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String urlImg;
    private double price;
    private int inventoryQty;
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
