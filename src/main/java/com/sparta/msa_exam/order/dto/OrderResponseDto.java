package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.entity.Order;
import com.sparta.msa_exam.order.entity.OrderProductId;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponseDto implements Serializable {
    Long orderId;
    List<Long> productIds;

    @Builder
    private OrderResponseDto(Long orderId, List<Long> productIds) {
        this.orderId = orderId;
        this.productIds = productIds;
    }

    public static OrderResponseDto of(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .productIds(order.getProductIds().stream()
                        .map(OrderProductId::getProductId)
                        .toList())
                .build();
    }
}
