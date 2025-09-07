package com.example.ecommerce.saga;

import com.example.ecommerce.command.*;
import com.example.ecommerce.event.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Saga
public class CheckoutSaga {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "checkoutId")
    public void on(CheckoutCreatedEvent event) {
        logger.info("[SAGA] Checkout criado para usuário: {} | CheckoutId: {}", 
                   event.userId, event.checkoutId);
        logger.info("[SAGA] Enviando comando para reservar inventário...");
        
        commandGateway.send(new ReserveInventoryCommand(event.checkoutId, "product-123", 2));
    }

    @SagaEventHandler(associationProperty = "checkoutId")
    public void on(InventoryReservedEvent event) {
        logger.info("[SAGA] Inventário reservado! Produto: {} | Quantidade: {} | CheckoutId: {}", 
                   event.productId, event.quantity, event.checkoutId);
        logger.info("[SAGA] Enviando comando para finalizar checkout...");
        
        commandGateway.send(new CompleteCheckoutCommand(event.checkoutId));
    }

    @SagaEventHandler(associationProperty = "checkoutId")
    public void on(CheckoutCompletedEvent event) {
        logger.info("[SAGA] Checkout finalizado com sucesso! CheckoutId: {}", event.checkoutId);
        logger.info("[SAGA] Encerrando saga...");
        
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "checkoutId")
    public void on(CheckoutCancelledEvent event) {
        logger.warn("[SAGA] Checkout cancelado! CheckoutId: {}", event.checkoutId);
        logger.info("[SAGA] Encerrando saga...");
        
        SagaLifecycle.end();
    }
}