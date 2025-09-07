package com.example.ecommerce.api;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.ecommerce.command.CreateCheckoutCommand;

@RestController
public class CheckoutController {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    
    @Autowired
    private CommandGateway commandGateway;
    
    @PostMapping("/checkout")
    public String createCheckout(@RequestParam String userId) {
        String checkoutId = UUID.randomUUID().toString();
        
        logger.info("🌐 [CONTROLLER] Recebida requisição de checkout | UserId: {} | CheckoutId: {}", 
                   userId, checkoutId);
        
        commandGateway.send(new CreateCheckoutCommand(checkoutId, userId));
        
        logger.info("📨 [CONTROLLER] Comando CreateCheckout enviado | CheckoutId: {}", checkoutId);
        
        return "Checkout iniciado: " + checkoutId;
    }
}