package com.example.ecommerce.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CancelCheckoutCommand {
    @TargetAggregateIdentifier
    public final String checkoutId;

    public CancelCheckoutCommand(String checkoutId) {
        this.checkoutId = checkoutId;
    }
}