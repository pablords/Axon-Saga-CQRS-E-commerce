package com.example.ecommerce.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateCheckoutCommand {
    @TargetAggregateIdentifier
    public final String checkoutId;
    public final String userId;

    public CreateCheckoutCommand(String checkoutId, String userId) {
        this.checkoutId = checkoutId;
        this.userId = userId;
    }
}