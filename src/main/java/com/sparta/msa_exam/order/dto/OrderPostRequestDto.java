package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.entity.Order;
import com.sparta.msa_exam.order.entity.OrderProductId;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPostRequestDto {

    private Set<Long> productIds;

    public Order toEntity() {
        return Order.builder()
                .orderProductIds(productIds.stream()
                        .map(productId -> OrderProductId.builder()
                                .productId(productId)
                                .build())
                        .toList())
                .build();
    }
}
