package com.orderflow.shared.common.correlation;

import org.slf4j.MDC;

// Constants + helpers for the correlation id that follows a request/event
// through every service. Stored in the SLF4J MDC so it appears in every log line.
public final class CorrelationId {

    private CorrelationId() {
    }

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    // Correlation id of the work currently being handled (or null if none set).
    public static String current() {
        return MDC.get(MDC_KEY);
    }
}
