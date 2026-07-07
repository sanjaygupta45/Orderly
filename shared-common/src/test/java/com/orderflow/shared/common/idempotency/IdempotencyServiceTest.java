package com.orderflow.shared.common.idempotency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    ProcessedMessageRepository repository;

    @InjectMocks
    IdempotencyService service;

    @Test
    void firstTimeEventIsNew_andRecorded() {
        when(repository.existsById("evt-1")).thenReturn(false);

        assertTrue(service.isNew("evt-1", "OrderCreated"));
        verify(repository).save(any(ProcessedMessage.class));
    }

    @Test
    void duplicateEventIsNotNew_andNotRecordedAgain() {
        when(repository.existsById("evt-1")).thenReturn(true);

        assertFalse(service.isNew("evt-1", "OrderCreated"));
        verify(repository, never()).save(any());
    }
}
