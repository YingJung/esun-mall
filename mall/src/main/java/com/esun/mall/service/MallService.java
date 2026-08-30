package com.esun.mall.service;

import com.esun.mall.dto.AddProductRequest;
import com.esun.mall.dto.CreateOrderRequest;
import com.esun.mall.entity.Product;
import com.esun.mall.repository.MallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MallService {

    private final MallRepository mallRepository;

    public MallService(MallRepository mallRepository) {
        this.mallRepository = mallRepository;
    }

    // 1. 新增商品
    public void addProduct(AddProductRequest request) {
        mallRepository.addProduct(
                request.getProductId(),
                request.getProductName(),
                request.getPrice(),
                request.getQuantity()
        );
    }

    // 2. 查詢庫存量大於零的商品清單
    public List<Product> getAvailableProducts() {
        return mallRepository.getAvailableProducts();
    }

    // 3. 建立訂單與明細並扣減庫存 (純 SQL 事務處理)
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(CreateOrderRequest request) {
        // --- 產生指定格式的 orderId (Ms + yyyyMMdd + 6位隨機數) ---
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String orderId = "Ms" + dateStr + randomNum;
        // -----------------------------------------------------

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 計算總金額
        for (CreateOrderRequest.OrderItemDTO item : request.getItems()) {
            BigDecimal itemTotal = item.getStandPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // A. 新增主訂單 (order 表)
        mallRepository.createOrder(orderId, request.getMemberId(), totalAmount, 1);

        // B. 逐筆新增明細並更新庫存 (orderdetail 表 + product 表)
        for (CreateOrderRequest.OrderItemDTO item : request.getItems()) {
            BigDecimal itemPrice = item.getStandPrice().multiply(new BigDecimal(item.getQuantity()));

            mallRepository.addOrderDetailAndUpdateStock(
                    orderId,
                    item.getProductId(),
                    item.getQuantity(),
                    item.getStandPrice(),
                    itemPrice
            );
        }

        return orderId;
    }
}