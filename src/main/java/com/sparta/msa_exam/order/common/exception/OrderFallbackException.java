package com.sparta.msa_exam.order.common.exception;

public class OrderFallbackException extends RuntimeException {
    public OrderFallbackException(String message) {
        super(message);
    }
}
