package com.sparta.msa_exam.order.repository;

import com.sparta.msa_exam.order.entity.Order;
import com.sparta.msa_exam.order.entity.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductIdRepository extends JpaRepository<OrderProductId, Long> {
    void deleteAllByOrder(Order order);

    boolean existsByOrderAndProductId(Order order, Long productId);
}
