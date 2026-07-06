package com.orderflow.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Stock for one product (SKU). We track total on-hand quantity and how much is
// currently reserved for in-flight orders; available = quantity - reserved.
// @Version gives optimistic locking as a second guard against lost updates.
@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String skuCode;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    @Version
    private Long version;

    public Inventory(String skuCode, Integer quantity) {
        this.skuCode = skuCode;
        this.quantity = quantity;
        this.reservedQuantity = 0;
    }

    // how much can still be reserved right now
    public int availableQuantity() {
        return quantity - reservedQuantity;
    }

    public void reserve(int qty) {
        this.reservedQuantity += qty;
    }

    public void release(int qty) {
        this.reservedQuantity = Math.max(0, this.reservedQuantity - qty);
    }
}
