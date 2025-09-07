package com.example.ecommerce.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ReserveInventoryCommand {
    @TargetAggregateIdentifier
    public final String inventoryId; // ID único para o agregado de inventário
    public final String checkoutId;
    public final String productId;
    public final int quantity;

    public ReserveInventoryCommand(String checkoutId, String productId, int quantity) {
        this.inventoryId = UUID.randomUUID().toString();
        this.checkoutId = checkoutId;
        this.productId = productId;
        this.quantity = quantity;
    }
}