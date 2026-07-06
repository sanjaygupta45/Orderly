package com.orderflow.inventory.repository;

import com.orderflow.inventory.model.ReservationStatus;
import com.orderflow.inventory.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByOrderIdAndStatus(String orderId, ReservationStatus status);
}
