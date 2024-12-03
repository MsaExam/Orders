package com.sparta.msa_exam.order.service;

import com.sparta.msa_exam.order.common.client.ProductClient;
import com.sparta.msa_exam.order.common.client.ProductResponseDto;
import com.sparta.msa_exam.order.dto.OrderPostRequestDto;
import com.sparta.msa_exam.order.dto.OrderPutRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.entity.Order;
import com.sparta.msa_exam.order.entity.OrderProductId;
import com.sparta.msa_exam.order.common.exception.OrderException;
import com.sparta.msa_exam.order.common.exception.OrderFallbackException;
import com.sparta.msa_exam.order.repository.OrderProductIdRepository;
import com.sparta.msa_exam.order.repository.OrderRepository;
import feign.FeignException;
import feign.FeignException.FeignClientException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductIdRepository orderProductIdRepository;
    private final ProductClient productClient;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orderCache", key = "#orderId")
    public OrderResponseDto getOrderById(Long orderId) {
        return OrderResponseDto.of(orderRepository.findById(orderId).orElseThrow());
    }

    @Transactional
    @CachePut(cacheNames = "orderCache", key = "#result.orderId")
    public OrderResponseDto createOrder(OrderPostRequestDto requestDto) {
        try {
            List<ProductResponseDto> productResponseDtos = productClient.getProducts(requestDto.getProductIds());

            if(productResponseDtos.size() != requestDto.getProductIds().size())
                throw new OrderException("존재하지 않는 상품이 있습니다.");

            productResponseDtos.stream().filter(product -> product.getStock() <= 0)
                    .findFirst()
                    .ifPresent(product -> {
                        throw new OrderException("재고가 없는 상품이 있습니다.");
                    });

            productClient.decreaseProductStocks(requestDto.getProductIds());

            return OrderResponseDto.of(orderRepository.save(requestDto.toEntity()));
        } catch (OrderException e) {
            throw e;
        } catch (FeignException e) {
            throw new OrderException("상품 주문에 실패했습니다. : " + e.getMessage());
        } catch (Exception e) {
            rollbackOrder(requestDto.getProductIds());
            throw new OrderException("상품 주문에 실패했습니다. : " + e.getMessage());
        }
    }


    @Transactional
    @CachePut(cacheNames = "orderCache", key = "#result.orderId")
    @CircuitBreaker(name = "createOrderFail", fallbackMethod = "fallbackCreateOrderFail")
    public OrderResponseDto createOrderFail(OrderPostRequestDto requestDto) {
        try {
            productClient.decreaseProductStocks(requestDto.getProductIds());

            throw new RuntimeException("상품 주문에 실패했습니다. : 테스트용 실패");
        } catch (OrderException e) {
            throw e;
        }  catch (FeignClientException e) {
            throw new OrderException("상품 주문에 실패했습니다. : " + e.getMessage());
        } catch (Exception e) {
            rollbackOrder(requestDto.getProductIds());
            throw e;
        }
    }

    @Transactional
    @CachePut(cacheNames = "orderCache", key = "#result.orderId")
    public OrderResponseDto updateOrder(Long orderId, OrderPutRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("주문이 존재하지 않습니다."));

        if(orderProductIdRepository.existsByOrderAndProductId(order, requestDto.getProductId()))
            throw new OrderException("이미 주문한 상품입니다.");

        try {
            productClient.decreaseProductStocks(Set.of(requestDto.getProductId()));

            OrderProductId orderProductId = requestDto.toEntity();
            orderProductId.setOrder(order);
            order.addOrderProductId(orderProductId);

            return OrderResponseDto.of(orderRepository.save(order));
        } catch (FeignException e) {
            throw new OrderException("상품 주문에 실패했습니다. : " + e.getMessage());
        } catch (Exception e) {
            rollbackOrder(Set.of(requestDto.getProductId()));
            throw new OrderException("상품 주문에 실패했습니다. : " + e.getMessage());
        }
    }

    private void rollbackOrder(Set<Long> ids) {
        productClient.rollbackProductStocks(ids);
    }

    public OrderResponseDto fallbackCreateOrderFail(OrderPostRequestDto requestDto, Throwable throwable) {
        // 예외를 던짐
        throw new OrderFallbackException("잠시 후에 주문 추가를 요청 해주세요.");
    }
}
