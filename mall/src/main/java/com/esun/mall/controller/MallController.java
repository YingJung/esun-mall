package com.esun.mall.controller;

import com.esun.mall.dto.AddProductRequest;
import com.esun.mall.dto.CreateOrderRequest;
import com.esun.mall.entity.Product;
import com.esun.mall.service.MallService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允許前端 Vue 跨域請求
public class MallController {

    private final MallService mallService;

    public MallController(MallService mallService) {
        this.mallService = mallService;
    }

    // 1. 新增商品 API
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductRequest request) {
        mallService.addProduct(request);
        return ResponseEntity.ok(Map.of("message", "商品新增成功！"));
    }

    // 2. 查詢庫存量大於零的商品清單 API (同時支援 /products 與 /products/available)
    @GetMapping({"/products", "/products/available"})
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(mallService.getAvailableProducts());
    }

    // 3. 建立訂單 API
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            String orderId = mallService.createOrder(request);
            return ResponseEntity.ok(Map.of("message", "訂單建立成功！", "orderId", orderId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}