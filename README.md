# Axon Saga CQRS E-commerce

Este projeto demonstra a implementação de um sistema de e-commerce usando **Axon Framework** com padrões **CQRS** (Command Query Responsibility Segregation) e **Saga Pattern** para gerenciar transações distribuídas.

## 📋 Visão Geral

O projeto simula um processo de checkout de e-commerce que envolve múltiplas etapas:
1. Criação do checkout
2. Reserva de inventário
3. Finalização ou cancelamento do checkout

## 🏗️ Arquitetura

### CQRS (Command Query Responsibility Segregation)
- **Commands**: Operações que modificam o estado (`CreateCheckoutCommand`, `ReserveInventoryCommand`, etc.)
- **Events**: Representam mudanças de estado que já ocorreram (`CheckoutCreatedEvent`, `InventoryReservedEvent`, etc.)
- **Aggregates**: Entidades que encapsulam a lógica de negócio (`CheckoutAggregate`)

### Saga Pattern
- **Saga**: Coordena transações distribuídas de longa duração (`CheckoutSaga`)
- Garante consistência eventual entre diferentes bounded contexts
- Gerencia compensação em caso de falhas

## 📦 Estrutura do Projeto

```
src/main/java/com/example/ecommerce/
├── AxonSagaCqrsEcommerceApplication.java  # Classe principal
├── api/
│   └── CheckoutController.java            # Controller REST
├── aggregate/
│   ├── CheckoutAggregate.java             # Agregado principal
│   └── InventoryAggregate.java            # Agregado de inventário
├── command/                               # Comandos (Write Side)
│   ├── CreateCheckoutCommand.java
│   ├── CompleteCheckoutCommand.java
│   ├── CancelCheckoutCommand.java
│   └── ReserveInventoryCommand.java
├── event/                                 # Eventos (Estado persistido)
│   ├── CheckoutCreatedEvent.java
│   ├── CheckoutCompletedEvent.java
│   ├── CheckoutCancelledEvent.java
│   └── InventoryReservedEvent.java
└── saga/
    └── CheckoutSaga.java                  # Orquestrador de transações
```

## 🔄 Fluxo de Execução Passo a Passo

### 1. Inicialização do Checkout
```java
CreateCheckoutCommand → CheckoutAggregate → CheckoutCreatedEvent
```
- Um comando `CreateCheckoutCommand` é enviado
- O `CheckoutAggregate` processa o comando
- Um evento `CheckoutCreatedEvent` é publicado

### 2. Início da Saga
```java
CheckoutCreatedEvent → CheckoutSaga.on(CheckoutCreatedEvent)
```
- A `CheckoutSaga` é iniciada pelo evento `CheckoutCreatedEvent`
- A anotação `@StartSaga` marca este como o ponto de entrada da saga

### 3. Reserva de Inventário
```java
CheckoutSaga → ReserveInventoryCommand → InventoryAggregate → InventoryReservedEvent
```
- A saga envia um comando `ReserveInventoryCommand` com ID único para o inventário
- O `InventoryAggregate` processa o comando e reserva o produto
- Um evento `InventoryReservedEvent` é publicado

### 4. Finalização do Checkout
```java
InventoryReservedEvent → CheckoutSaga → CompleteCheckoutCommand → CheckoutCompletedEvent
```
- A saga recebe o evento `InventoryReservedEvent`
- Envia um comando `CompleteCheckoutCommand`
- O checkout é finalizado com `CheckoutCompletedEvent`

### 5. Finalização da Saga
```java
CheckoutCompletedEvent → CheckoutSaga.on(CheckoutCompletedEvent) → SagaLifecycle.end()
```
- A saga recebe o evento de finalização
- Chama `SagaLifecycle.end()` para encerrar a saga

## 🎯 Componentes Principais

### CheckoutController
- **Responsabilidade**: Expor endpoints REST para iniciar o processo de checkout
- **Endpoint**:
  - `POST /checkout?userId={userId}`: Inicia um novo checkout
- **Funcionalidade**: Converte requisições HTTP em comandos do Axon

### CheckoutAggregate
- **Responsabilidade**: Gerenciar o estado do checkout
- **Comandos que processa**:
  - `CreateCheckoutCommand`: Cria um novo checkout
  - `CompleteCheckoutCommand`: Finaliza o checkout
  - `CancelCheckoutCommand`: Cancela o checkout
- **Eventos que produz**:
  - `CheckoutCreatedEvent`
  - `CheckoutCompletedEvent`
  - `CheckoutCancelledEvent`

### InventoryAggregate
- **Responsabilidade**: Gerenciar o estado do inventário de produtos
- **Comandos que processa**:
  - `ReserveInventoryCommand`: Reserva produtos para um checkout específico
- **Eventos que produz**:
  - `InventoryReservedEvent`
- **ID Único**: Usa formato `inventory-{productId}-{checkoutId}` para evitar conflitos

### CheckoutSaga
- **Responsabilidade**: Orquestrar o fluxo completo do checkout
- **Pontos de entrada**:
  - `@StartSaga` em `CheckoutCreatedEvent`
- **Fluxo de coordenação**:
  1. Recebe `CheckoutCreatedEvent` → envia `ReserveInventoryCommand`
  2. Recebe `InventoryReservedEvent` → envia `CompleteCheckoutCommand`
  3. Recebe `CheckoutCompletedEvent` → finaliza a saga

### Commands (Comandos)
- **CreateCheckoutCommand**: Inicia um novo processo de checkout
- **ReserveInventoryCommand**: Solicita reserva de inventário
- **CompleteCheckoutCommand**: Finaliza o checkout
- **CancelCheckoutCommand**: Cancela o checkout

### Events (Eventos)
- **CheckoutCreatedEvent**: Checkout foi criado
- **InventoryReservedEvent**: Inventário foi reservado
- **CheckoutCompletedEvent**: Checkout foi finalizado
- **CheckoutCancelledEvent**: Checkout foi cancelado

## 🔧 Tecnologias Utilizadas

- **Spring Boot 3.4.3**: Framework base
- **Axon Framework 4.9.3**: CQRS e Event Sourcing
- **H2 Database**: Banco de dados em memória
- **Java 17**: Linguagem de programação

## 📊 Como o Saga Funciona

### Características do Saga Pattern:
1. **Transações de Longa Duração**: Gerencia processos que podem levar tempo
2. **Consistência Eventual**: Garante que o sistema chegue a um estado consistente
3. **Compensação**: Em caso de falha, pode reverter operações já realizadas
4. **Coordenação**: Orquestra múltiplos serviços/agregados

### Vantagens:
- ✅ **Resiliência**: Lida com falhas de serviços individuais
- ✅ **Escalabilidade**: Permite processamento assíncrono
- ✅ **Desacoplamento**: Serviços não precisam se conhecer diretamente
- ✅ **Auditoria**: Histórico completo de eventos

### Cenários de Compensação:
Se algo falhar durante o processo, a saga pode:
1. Cancelar o checkout (`CancelCheckoutCommand`)
2. Liberar o inventário reservado
3. Notificar o usuário sobre a falha

## 🚀 Como Executar

### Pré-requisitos:
- Java 17 ou superior
- Maven 3.6 ou superior

### Execução:
```bash
# Clone o repositório
git clone <repository-url>

# Navegue até o diretório
cd axon-saga-cqrs-ecommerce

# Execute o projeto
./mvnw spring-boot:run
```

### Configurações:
- **Porta**: 8082 (configurável em `application.properties`)
- **Banco H2**: Console disponível em `http://localhost:8082/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuário**: `sa` / **Senha**: `password`

### Testando o Fluxo:
1. A aplicação inicia na porta 8082
2. O Axon Framework configura automaticamente:
   - Event Store (H2 database)
   - Command Bus
   - Event Bus
   - Saga Manager

### Testando via HTTP:
```bash
# Criar um novo checkout
curl -X POST "http://localhost:8082/checkout?userId=user123"

# Resposta esperada:
# Checkout iniciado: <uuid-gerado>
```

## 📊 Logs e Monitoramento

O projeto inclui logs detalhados para acompanhar o fluxo da saga:

### Exemplo de Logs durante um Checkout:
```
🌐 [CONTROLLER] Recebida requisição de checkout | UserId: user123 | CheckoutId: abc-123
📝 [AGGREGATE] Processando comando CreateCheckout | CheckoutId: abc-123 | UserId: user123
📤 [AGGREGATE] Evento CheckoutCreated aplicado | CheckoutId: abc-123
💾 [AGGREGATE] Aplicando evento CheckoutCreated | CheckoutId: abc-123
📨 [CONTROLLER] Comando CreateCheckout enviado | CheckoutId: abc-123
🚀 [SAGA] Checkout criado para usuário: user123 | CheckoutId: abc-123
📦 [SAGA] Enviando comando para reservar inventário...
📦 [INVENTORY] Processando comando ReserveInventory | InventoryId: inventory-product-123-abc-123
📤 [INVENTORY] Evento InventoryReserved aplicado | CheckoutId: abc-123 | Produto: product-123
💾 [INVENTORY] Aplicando evento InventoryReserved | CheckoutId: abc-123
✅ [SAGA] Inventário reservado! Produto: product-123 | Quantidade: 2 | CheckoutId: abc-123
🎯 [SAGA] Enviando comando para finalizar checkout...
✅ [AGGREGATE] Processando comando CompleteCheckout | CheckoutId: abc-123
📤 [AGGREGATE] Evento CheckoutCompleted aplicado | CheckoutId: abc-123
🎉 [SAGA] Checkout finalizado com sucesso! CheckoutId: abc-123
🏁 [SAGA] Encerrando saga...
```

### Configurações de Log:
- **Logs SQL**: Desabilitados para reduzir ruído
- **Logs da aplicação**: Nível INFO com emojis para facilitar identificação
- **Logs por componente**: Controller, Aggregate, Saga e Inventory

## 📝 Exemplo de Uso

O projeto já inclui um controller REST funcional:

```java
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
```

### Componentes Implementados:

#### CheckoutSaga com Logs:
```java
@Saga
public class CheckoutSaga {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "checkoutId")
    public void on(CheckoutCreatedEvent event) {
        logger.info("🚀 [SAGA] Checkout criado para usuário: {} | CheckoutId: {}", 
                   event.userId, event.checkoutId);
        logger.info("📦 [SAGA] Enviando comando para reservar inventário...");
        
        commandGateway.send(new ReserveInventoryCommand(event.checkoutId, "product-123", 2));
    }

    @SagaEventHandler(associationProperty = "checkoutId")
    public void on(InventoryReservedEvent event) {
        logger.info("✅ [SAGA] Inventário reservado! Produto: {} | Quantidade: {} | CheckoutId: {}", 
                   event.productId, event.quantity, event.checkoutId);
        logger.info("🎯 [SAGA] Enviando comando para finalizar checkout...");
        
        commandGateway.send(new CompleteCheckoutCommand(event.checkoutId));
    }

    @SagaEventHandler(associationProperty = "checkoutId")
    public void on(CheckoutCompletedEvent event) {
        logger.info("🎉 [SAGA] Checkout finalizado com sucesso! CheckoutId: {}", event.checkoutId);
        logger.info("🏁 [SAGA] Encerrando saga...");
        
        SagaLifecycle.end();
    }
}
```

#### InventoryAggregate:
```java
@Aggregate
public class InventoryAggregate {
    
    @AggregateIdentifier
    private String inventoryId; // ID único: inventory-{productId}-{checkoutId}
    
    @CommandHandler
    public InventoryAggregate(ReserveInventoryCommand cmd) {
        logger.info("📦 [INVENTORY] Processando comando ReserveInventory | InventoryId: {} | CheckoutId: {} | Produto: {} | Quantidade: {}", 
                   cmd.inventoryId, cmd.checkoutId, cmd.productId, cmd.quantity);
        
        apply(new InventoryReservedEvent(cmd.checkoutId, cmd.productId, cmd.quantity));
    }
}
```

## 🎓 Conceitos Aprendidos

1. **Event Sourcing**: Estado é derivado de uma sequência de eventos
2. **CQRS**: Separação entre operações de escrita e leitura
3. **Saga Pattern**: Coordenação de transações distribuídas
4. **Axon Framework**: Framework para implementar esses padrões
5. **Aggregate Pattern**: Encapsulamento de lógica de negócio
6. **Command/Event Handling**: Processamento assíncrono de comandos e eventos

## 🔍 Próximos Passos

Para expandir este projeto, considere:
- [ ] Adicionar tratamento de erros e compensação na saga
- [ ] Implementar um serviço de inventário real com validação de estoque
- [ ] Adicionar mais endpoints REST (consultar checkout, cancelar, etc.)
- [ ] Implementar projeções para queries (CQRS read side)
- [ ] Adicionar testes unitários e de integração
- [ ] Configurar snapshots para otimização do Event Sourcing
- [ ] Implementar Dead Letter Queue para eventos falhos
- [ ] Adicionar métricas e monitoramento
- [ ] Implementar autenticação e autorização
- [ ] Adicionar validação de dados de entrada

## 🐛 Problemas Comuns e Soluções

### 1. Erro de Chave Única no Event Store
**Problema**: `Unique index or primary key violation` na tabela `DOMAIN_EVENT_ENTRY`
**Causa**: Dois agregados diferentes usando o mesmo ID
**Solução**: Cada agregado deve ter um ID único (implementado no projeto)

### 2. Saga não é instanciada
**Problema**: `NoSuchMethodException: CheckoutSaga.<init>()`
**Causa**: Falta de construtor padrão na Saga
**Solução**: Adicionar construtor padrão e usar `@Autowired` para dependências

### 3. CommandGateway não encontrado
**Problema**: Injeção de dependência falhando na Saga
**Solução**: Usar `@Autowired` com modificador `transient` em Sagas

## 🔧 Configurações Avançadas

### application.properties:
```properties
# Configuração do servidor
server.port=8082

# Configuração para rodar sem Axon Server (modo standalone)
axon.axonserver.enabled=false

# Configuração do H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Desabilitar logs SQL detalhados
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN
logging.level.org.hibernate=WARN

# Logging para nossa aplicação
logging.level.org.axonframework=INFO
logging.level.com.example.ecommerce=INFO
logging.level.root=INFO
```

## 🎓 Conceitos Aprendidos

1. **Event Sourcing**: Estado é derivado de uma sequência de eventos persistidos
2. **CQRS**: Separação entre operações de escrita (Commands) e leitura (Queries)
3. **Saga Pattern**: Coordenação de transações distribuídas de longa duração
4. **Axon Framework**: Framework para implementar CQRS e Event Sourcing
5. **Aggregate Pattern**: Encapsulamento de lógica de negócio e consistência
6. **Command/Event Handling**: Processamento assíncrono de comandos e eventos
7. **Event Store**: Armazenamento imutável de eventos para auditoria e replay
8. **Tracking Tokens**: Mecanismo para garantir processamento de eventos sem perdas
9. **Dependency Injection**: Injeção de dependências em componentes do Axon
10. **Logging Estruturado**: Implementação de logs para monitoramento de fluxos

## 🏆 Benefícios Demonstrados

### Observabilidade:
- **Logs detalhados** em cada etapa do processo
- **Rastreamento completo** do fluxo de checkout
- **Identificação visual** dos componentes com emojis

### Robustez:
- **Event Sourcing** garante auditoria completa
- **Transações distribuídas** coordenadas pela saga
- **IDs únicos** previnem conflitos entre agregados

### Escalabilidade:
- **Processamento assíncrono** de eventos
- **Desacoplamento** entre componentes
- **Capacidade de replay** de eventos

---

Este projeto serve como uma excelente base para entender como implementar sistemas distribuídos usando padrões modernos de arquitetura com o Axon Framework. Os logs implementados facilitam o debugging e o entendimento do fluxo completo da saga.
