package com.orderflow.payment.simulation;

import com.orderflow.payment.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentSimulatorTest {

    @Test
    void alwaysSuccessMode_returnsSuccess() {
        assertEquals(PaymentStatus.SUCCESS,
                new PaymentSimulator(PaymentMode.ALWAYS_SUCCESS, 0.8).decide(BigDecimal.TEN));
    }

    @Test
    void alwaysFailMode_returnsFailed() {
        assertEquals(PaymentStatus.FAILED,
                new PaymentSimulator(PaymentMode.ALWAYS_FAIL, 0.8).decide(BigDecimal.TEN));
    }

    @Test
    void randomMode_fullSuccessRate_returnsSuccess() {
        assertEquals(PaymentStatus.SUCCESS,
                new PaymentSimulator(PaymentMode.RANDOM, 1.0).decide(BigDecimal.TEN));
    }

    @Test
    void randomMode_zeroSuccessRate_returnsFailureOrTimeout() {
        PaymentStatus status = new PaymentSimulator(PaymentMode.RANDOM, 0.0).decide(BigDecimal.TEN);
        assertTrue(status == PaymentStatus.FAILED || status == PaymentStatus.TIMEOUT);
    }
}
