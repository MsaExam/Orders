package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.entity.OrderProductId;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPutRequestDto {
    private Long productId;

    public OrderProductId toEntity() {
        return OrderProductId.builder()
                .productId(productId)
                .build();
    }
}
