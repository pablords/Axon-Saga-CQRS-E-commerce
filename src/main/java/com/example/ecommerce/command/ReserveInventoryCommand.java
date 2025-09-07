package com.example.ecommerce.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ReserveInventoryCommand {
    @TargetAggregateIdentifier
    public final String inventoryId; // ID único para o agregado de inventário
    public final String checkoutId;
    public final String productId;
    public final int quantity;

    public ReserveInventoryCommand(String checkoutId, String productId, int quantity) {
        this.inventoryId = "inventory-" + productId + "-" + checkoutId; // Gerar ID único
        this.checkoutId = checkoutId;
        this.productId = productId;
        this.quantity = quantity;
    }
}