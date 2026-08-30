package com.esun.mall.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "會員編號不能為空")
    private Integer memberId;

    @NotEmpty(message = "訂單商品不能為空")
    @Valid
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品編號不能為空")
        private String productId;

        @NotNull(message = "購買數量不能為空")
        @Min(value = 1, message = "購買數量至少為 1")
        private Integer quantity;

        @NotNull(message = "商品單價不能為空")
        private BigDecimal standPrice;
    }
}