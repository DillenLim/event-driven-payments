package com.example.payments.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public class TransactionRecordedEvent extends BaseEvent {
    private String transactionId;
    private String status;

    public TransactionRecordedEvent(String aggregateId, String transactionId, String status) {
        super(aggregateId, "TransactionRecordedEvent");
        this.transactionId = transactionId;
        this.status = status;
    }
}
