package com.example.ecommerce.event;

public class CheckoutCancelledEvent {
    public final String checkoutId;

    public CheckoutCancelledEvent(String checkoutId) {
        this.checkoutId = checkoutId;
    }
}