package com.example.ecommerce.event;

public class InventoryReservedEvent {
    public final String checkoutId;
    public final String productId;
    public final int quantity;
    public final String inventoryId; // Novo campo para ID do inventário

    public InventoryReservedEvent(String checkoutId, String productId, int quantity, String inventoryId) {
        this.checkoutId = checkoutId;
        this.productId = productId;
        this.quantity = quantity;
        this.inventoryId = inventoryId;
    }
}