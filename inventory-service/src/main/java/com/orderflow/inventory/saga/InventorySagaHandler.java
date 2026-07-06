package com.orderflow.inventory.saga;

import com.orderflow.inventory.service.StockService;
import com.orderflow.shared.events.OrderCreatedEvent;
import com.orderflow.shared.events.OrderLineItem;
import com.orderflow.shared.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

// Wraps the stock operations with the distributed lock. The lock is held across
// the whole transaction (StockService commits before we unlock), so two orders
// for the same product can never both reserve the last unit -> no overselling.
@Component
@Slf4j
@RequiredArgsConstructor
public class InventorySagaHandler {

    private static final String LOCK_PREFIX = "inventory:product:";

    private final RedissonClient redisson;
    private final StockService stockService;

    // order.created -> reserve stock (locking every SKU involved)
    public void onOrderCreated(OrderCreatedEvent event) {
        List<String> skus = event.getItems().stream()
                .map(OrderLineItem::skuCode)
                .distinct()
                .sorted()   // consistent order -> no deadlocks between concurrent orders
                .toList();

        RLock lock = lockFor(skus);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
            if (!locked) {
                // couldn't get the lock in time -> throw so the message is retried
                throw new IllegalStateException("Timed out acquiring inventory lock for order " + event.getOrderId());
            }
            stockService.reserveForOrder(event);   // commits before we reach finally
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted acquiring inventory lock", e);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    // payment.failed -> release stock. Releasing can't oversell, so no lock is
    // needed here; optimistic @Version still protects against concurrent writes.
    public void onPaymentFailed(PaymentFailedEvent event) {
        stockService.releaseForOrder(event);
    }

    private RLock lockFor(List<String> skus) {
        RLock[] locks = skus.stream()
                .map(sku -> redisson.getLock(LOCK_PREFIX + sku))
                .toArray(RLock[]::new);
        // one product -> a single lock; several -> a multi-lock acquired atomically
        return locks.length == 1 ? locks[0] : redisson.getMultiLock(locks);
    }
}
