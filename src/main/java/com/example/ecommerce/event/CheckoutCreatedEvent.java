package com.example.ecommerce.event;

public class CheckoutCreatedEvent {
    public final String checkoutId;
    public final String userId;

    public CheckoutCreatedEvent(String checkoutId, String userId) {
        this.checkoutId = checkoutId;
        this.userId = userId;
    }
}