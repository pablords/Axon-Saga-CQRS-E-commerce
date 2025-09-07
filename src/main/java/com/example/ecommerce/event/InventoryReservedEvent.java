package com.example.ecommerce.event;

public class InventoryReservedEvent {
    public final String checkoutId;
    public final String productId;
    public final int quantity;

    public InventoryReservedEvent(String checkoutId, String productId, int quantity) {
        this.checkoutId = checkoutId;
        this.productId = productId;
        this.quantity = quantity;
    }
}