package com.sparta.msa_exam.order.common.client;

import lombok.Getter;

@Getter
public class ProductOrderAvailabilityResponseDto {
    private Long productId;
    private Boolean available;
}
