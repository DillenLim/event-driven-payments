package com.example.payments.payment.repository;

import com.example.payments.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * JPA Repository for Payment.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
