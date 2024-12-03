package com.sparta.msa_exam.order.controller;

import com.sparta.msa_exam.order.dto.OrderPostRequestDto;
import com.sparta.msa_exam.order.dto.OrderPutRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.common.exception.OrderFallbackException;
import com.sparta.msa_exam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping
    public OrderResponseDto crateOrder(@RequestParam(value = "fail", defaultValue = "false", required = false) boolean fail, @RequestBody OrderPostRequestDto requestDto) {
        if (fail) {
            return orderService.createOrderFail(requestDto);
        }
        return orderService.createOrder(requestDto);
    }

    @PutMapping("/{orderId}")
    public OrderResponseDto updateOrder(@PathVariable Long orderId, @RequestBody OrderPutRequestDto requestDto) {
        return orderService.updateOrder(orderId, requestDto);
    }

    @ExceptionHandler(OrderFallbackException.class)
    public ResponseEntity<String> handleOrderFallbackException(OrderFallbackException e) {
        log.info("Handling fallback exception: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        log.error("e: ", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
