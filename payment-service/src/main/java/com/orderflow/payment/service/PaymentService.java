package com.orderflow.payment.service;

import com.orderflow.payment.dto.PaymentResponse;
import com.orderflow.payment.model.Payment;
import com.orderflow.payment.model.PaymentStatus;
import com.orderflow.payment.repository.PaymentRepository;
import com.orderflow.payment.simulation.PaymentSimulator;
import com.orderflow.shared.common.correlation.CorrelationId;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.InventoryReservedEvent;
import com.orderflow.shared.events.PaymentCompletedEvent;
import com.orderflow.shared.events.PaymentFailedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// Reacts to inventory.reserved: charges the customer (simulated) and emits
// payment.completed or payment.failed. Idempotent + transactional + outbox.
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyService idempotency;
    private final OutboxEventPublisher outbox;
    private final PaymentSimulator simulator;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void process(InventoryReservedEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "InventoryReserved")) {
                return;
            }

            Payment payment = new Payment();
            payment.setPaymentId(UUID.randomUUID().toString());
            payment.setOrderId(event.getOrderId());
            payment.setUserId(event.getUserId());
            payment.setAmount(event.getAmount());
            payment.setStatus(PaymentStatus.PENDING);

            PaymentStatus outcome = simulator.decide(event.getAmount());
            if (outcome == PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
                outbox.save(RoutingKeys.PAYMENT_COMPLETED, PaymentCompletedEvent.builder()
                        .orderId(event.getOrderId())
                        .userId(event.getUserId())
                        .paymentId(payment.getPaymentId())
                        .amount(event.getAmount())
                        .correlationId(event.getCorrelationId())
                        .build());
                meterRegistry.counter("orderflow.payment.success").increment();
                log.info("Payment {} SUCCESS for order {}", payment.getPaymentId(), event.getOrderId());
            } else {
                // FAILED or TIMEOUT - both mean the charge did not go through
                String reason = outcome == PaymentStatus.TIMEOUT ? "Payment gateway timeout" : "Payment declined";
                payment.setStatus(outcome);
                payment.setFailureReason(reason);
                paymentRepository.save(payment);
                outbox.save(RoutingKeys.PAYMENT_FAILED, PaymentFailedEvent.builder()
                        .orderId(event.getOrderId())
                        .userId(event.getUserId())
                        .reason(reason)
                        .correlationId(event.getCorrelationId())
                        .build());
                meterRegistry.counter("orderflow.payment.failed").increment();
                log.warn("Payment {} {} for order {}: {}", payment.getPaymentId(), outcome, event.getOrderId(), reason);
            }
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(p -> new PaymentResponse(p.getPaymentId(), p.getOrderId(), p.getAmount(),
                        p.getStatus().name(), p.getFailureReason(), p.getCreatedAt()))
                .toList();
    }
}
