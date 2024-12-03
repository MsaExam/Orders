package com.sparta.msa_exam.order.common.client;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponseDto {
    private Long id;
    private String name;
    private Integer supplyPrice;
    private Integer stock;
}
