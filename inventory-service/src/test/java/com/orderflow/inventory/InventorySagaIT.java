package com.orderflow.inventory;

import com.orderflow.inventory.model.Inventory;
import com.orderflow.inventory.repository.InventoryRepository;
import com.orderflow.shared.events.Exchanges;
import com.orderflow.shared.events.OrderCreatedEvent;
import com.orderflow.shared.events.OrderLineItem;
import com.orderflow.shared.events.RoutingKeys;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

// Full-stack slice: real MySQL + RabbitMQ + Redis via Testcontainers.
// Publishing an OrderCreatedEvent should make inventory-service reserve stock
// (through its real @RabbitListener, the Redisson lock, and MySQL).
@SpringBootTest
@Testcontainers
class InventorySagaIT {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.4"))
            .withDatabaseName("inventory_service");

    // Mount a config that disables the guest loopback restriction so the app can
    // authenticate over the mapped port.
    @Container
    static final RabbitMQContainer RABBIT = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management"))
            .withCopyFileToContainer(MountableFile.forClasspathResource("rabbitmq-loopback.conf"),
                    "/etc/rabbitmq/rabbitmq.conf");

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.rabbitmq.host", RABBIT::getHost);
        registry.add("spring.rabbitmq.port", RABBIT::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT::getAdminPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> "");
    }

    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void orderCreatedEvent_reservesStock() {
        inventoryRepository.save(new Inventory("IT-SKU", 10));

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId("it-order-1")
                .userId(1L)
                .totalAmount(new BigDecimal("150"))
                .items(List.of(new OrderLineItem("IT-SKU", 3, new BigDecimal("50"))))
                .build();

        // publish like the outbox relay would; the shared converter stamps the type header
        rabbitTemplate.convertAndSend(Exchanges.ORDER_EXCHANGE, RoutingKeys.ORDER_CREATED, event);

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            Inventory reloaded = inventoryRepository.findBySkuCode("IT-SKU").orElseThrow();
            assertThat(reloaded.getReservedQuantity()).isEqualTo(3);
            assertThat(reloaded.availableQuantity()).isEqualTo(7);
        });
    }
}
