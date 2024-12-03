package com.sparta.msa_exam.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id", nullable = false)
    private Long id;

    // 자식의 영속성을 관리하기 위해 CascadeType.ALL을 사용합니다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProductId> productIds = new ArrayList<>();

    @Builder
    private Order(List<OrderProductId> orderProductIds) {
        orderProductIds.forEach(orderProductId -> orderProductId.setOrder(this));
        this.productIds = orderProductIds;
    }

    public void addOrderProductId(OrderProductId orderProductId) {
        orderProductId.setOrder(this);
        this.productIds.add(orderProductId);
    }
}
