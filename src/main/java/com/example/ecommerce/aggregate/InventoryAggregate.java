package com.example.ecommerce.aggregate;

import com.example.ecommerce.command.ReserveInventoryCommand;
import com.example.ecommerce.event.InventoryReservedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class InventoryAggregate {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryAggregate.class);

    @AggregateIdentifier
    private String inventoryId; // Usar ID próprio para o inventário
    
    private String checkoutId;
    private String productId;
    private int reservedQuantity;

    public InventoryAggregate() {}

    @CommandHandler
    public InventoryAggregate(ReserveInventoryCommand cmd) {
        logger.info("[INVENTORY] Processando comando ReserveInventory | InventoryId: {} | CheckoutId: {} | Produto: {} | Quantidade: {}", 
                   cmd.inventoryId, cmd.checkoutId, cmd.productId, cmd.quantity);
        
        // Simula verificação de estoque - sempre aceita por simplicidade
        apply(new InventoryReservedEvent(cmd.checkoutId, cmd.productId, cmd.quantity, cmd.inventoryId));
        
        logger.info("[INVENTORY] Evento InventoryReserved aplicado | InventoryId: {} | CheckoutId: {} | Produto: {}", 
                   cmd.inventoryId, cmd.checkoutId, cmd.productId);
    }

    @EventSourcingHandler
    public void on(InventoryReservedEvent event) {
        logger.info("[INVENTORY] Aplicando evento InventoryReserved | CheckoutId: {} | Produto: {}", 
                   event.checkoutId, event.productId);
        this.checkoutId = event.checkoutId;
        this.productId = event.productId;
        this.reservedQuantity = event.quantity;
        this.inventoryId = event.inventoryId;
    }
}
