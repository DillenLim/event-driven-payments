package com.example.payments.common.repository;

import com.example.payments.common.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ProcessedEvent entity.
 * Shared infrastructure for idempotency tracking across all services.
 */
@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
