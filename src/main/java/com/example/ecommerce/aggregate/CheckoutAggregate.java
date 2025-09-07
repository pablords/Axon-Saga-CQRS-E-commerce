package com.example.ecommerce.aggregate;

import com.example.ecommerce.command.*;
import com.example.ecommerce.event.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class CheckoutAggregate {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutAggregate.class);

    @AggregateIdentifier
    private String checkoutId;

    public CheckoutAggregate() {}

    @CommandHandler
    public CheckoutAggregate(CreateCheckoutCommand cmd) {
        logger.info("📝 [AGGREGATE] Processando comando CreateCheckout | CheckoutId: {} | UserId: {}", 
                   cmd.checkoutId, cmd.userId);
        
        apply(new CheckoutCreatedEvent(cmd.checkoutId, cmd.userId));
        
        logger.info("📤 [AGGREGATE] Evento CheckoutCreated aplicado | CheckoutId: {}", cmd.checkoutId);
    }

    @EventSourcingHandler
    public void on(CheckoutCreatedEvent event) {
        logger.info("💾 [AGGREGATE] Aplicando evento CheckoutCreated | CheckoutId: {}", event.checkoutId);
        this.checkoutId = event.checkoutId;
    }

    @CommandHandler
    public void handle(CompleteCheckoutCommand cmd) {
        logger.info("✅ [AGGREGATE] Processando comando CompleteCheckout | CheckoutId: {}", cmd.checkoutId);
        
        apply(new CheckoutCompletedEvent(cmd.checkoutId));
        
        logger.info("📤 [AGGREGATE] Evento CheckoutCompleted aplicado | CheckoutId: {}", cmd.checkoutId);
    }

    @CommandHandler
    public void handle(CancelCheckoutCommand cmd) {
        logger.warn("❌ [AGGREGATE] Processando comando CancelCheckout | CheckoutId: {}", cmd.checkoutId);
        
        apply(new CheckoutCancelledEvent(cmd.checkoutId));
        
        logger.info("📤 [AGGREGATE] Evento CheckoutCancelled aplicado | CheckoutId: {}", cmd.checkoutId);
    }
}