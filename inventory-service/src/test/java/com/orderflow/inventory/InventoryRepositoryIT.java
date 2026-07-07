package com.orderflow.inventory;

import com.orderflow.inventory.model.Inventory;
import com.orderflow.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Repository test against a real MySQL (Testcontainers). Verifies the derived query
// and the reserved/available accounting round-trips through the database.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class InventoryRepositoryIT {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.4"))
            .withDatabaseName("inventory_service");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired
    InventoryRepository repository;

    @Test
    void savesAndFindsBySkuCode_withReservedAccounting() {
        Inventory inventory = new Inventory("R-SKU", 5);
        inventory.reserve(2);
        repository.save(inventory);

        Optional<Inventory> found = repository.findBySkuCode("R-SKU");

        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(5);
        assertThat(found.get().getReservedQuantity()).isEqualTo(2);
        assertThat(found.get().availableQuantity()).isEqualTo(3);
    }
}
