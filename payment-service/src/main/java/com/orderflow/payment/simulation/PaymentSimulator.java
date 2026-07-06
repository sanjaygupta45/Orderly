package com.orderflow.payment.simulation;

import com.orderflow.payment.model.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

// Stand-in for a real payment gateway. Decides SUCCESS / FAILED / TIMEOUT based on
// the configured mode, so we can demo both the happy path and compensation.
@Component
@Slf4j
public class PaymentSimulator {

    private final PaymentMode mode;
    private final double successRate;
    private final Random random = new Random();

    public PaymentSimulator(
            @Value("${orderflow.payment.mode:RANDOM}") PaymentMode mode,
            @Value("${orderflow.payment.success-rate:0.8}") double successRate) {
        this.mode = mode;
        this.successRate = successRate;
        log.info("Payment simulator started: mode={}, successRate={}", mode, successRate);
    }

    public PaymentStatus decide(BigDecimal amount) {
        return switch (mode) {
            case ALWAYS_SUCCESS -> PaymentStatus.SUCCESS;
            case ALWAYS_FAIL -> PaymentStatus.FAILED;
            case RANDOM -> random.nextDouble() < successRate
                    ? PaymentStatus.SUCCESS
                    // split the failures between a plain decline and a gateway timeout
                    : (random.nextBoolean() ? PaymentStatus.TIMEOUT : PaymentStatus.FAILED);
        };
    }
}
