package com.sparta.msa_exam.order.common.client;

import java.util.List;
import java.util.Set;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/products")
    List<ProductResponseDto> getProducts(@RequestParam("ids") Set<Long> ids);

    @PatchMapping("/products/stocks")
    List<ProductResponseDto> decreaseProductStocks(@RequestBody Set<Long> orderProductRequestDtos);

    @PatchMapping("/products/stocks/rollback")
    List<ProductResponseDto> rollbackProductStocks(@RequestBody Set<Long> orderProductRequestDtos);
}