package com.orderflow.shared.events;

import java.io.Serializable;
import java.math.BigDecimal;

// One line of an order: which product, how many, at what unit price.
public record OrderLineItem(String skuCode, int quantity, BigDecimal unitPrice) implements Serializable {
}
