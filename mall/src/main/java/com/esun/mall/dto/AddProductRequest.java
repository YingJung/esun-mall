package com.esun.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequest {
    @NotBlank(message = "商品編號不能為空")
    private String productId;

    @NotBlank(message = "商品名稱不能為空")
    private String productName;

    @NotNull(message = "售價不能為空")
    @Min(value = 0, message = "售價不能小於 0")
    private BigDecimal price;

    @NotNull(message = "庫存不能為空")
    @Min(value = 0, message = "庫存不能小於 0")
    private Integer quantity;
}