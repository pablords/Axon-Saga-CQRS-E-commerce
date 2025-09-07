package com.example.ecommerce.event;

public class CheckoutCompletedEvent {
    public final String checkoutId;

    public CheckoutCompletedEvent(String checkoutId) {
        this.checkoutId = checkoutId;
    }
}