package com.example.ecommerce.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CompleteCheckoutCommand {
    @TargetAggregateIdentifier
    public final String checkoutId;

    public CompleteCheckoutCommand(String checkoutId) {
        this.checkoutId = checkoutId;
    }
}