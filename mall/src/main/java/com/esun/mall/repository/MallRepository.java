package com.esun.mall.repository;

import com.esun.mall.entity.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class MallRepository {

    private final JdbcTemplate jdbcTemplate;

    public MallRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 呼叫 SP 新增商品
    public void addProduct(String id, String name, BigDecimal price, Integer quantity) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_AddProduct");
        jdbcCall.execute(new MapSqlParameterSource()
                .addValue("p_ProductID", id)
                .addValue("p_ProductName", name)
                .addValue("p_Price", price)
                .addValue("p_Quantity", quantity));
    }

    // 2. 查詢庫存量大於零的商品清單
    public List<Product> getAvailableProducts() {
        String sql = "SELECT product_id, product_name, price, quantity FROM product WHERE quantity > 0";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Product p = new Product();
            p.setProductId(rs.getString("product_id"));
            p.setProductName(rs.getString("product_name"));
            p.setPrice(rs.getBigDecimal("price"));
            p.setQuantity(rs.getInt("quantity"));
            return p;
        });
    }

    // 3. 呼叫 SP 建立主訂單
    public void createOrder(String orderId, Integer memberId, BigDecimal price, Integer payStatus) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_CreateOrder");
        jdbcCall.execute(new MapSqlParameterSource()
                .addValue("p_OrderID", orderId)
                .addValue("p_MemberID", memberId)
                .addValue("p_Price", price)
                .addValue("p_PayStatus", payStatus));
    }

    // 4. 呼叫 SP 新增訂單明細並扣減庫存
    public void addOrderDetailAndUpdateStock(String orderId, String productId, Integer quantity, BigDecimal standPrice, BigDecimal itemPrice) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_AddOrderDetailAndUpdateStock");
        jdbcCall.execute(new MapSqlParameterSource()
                .addValue("p_OrderID", orderId)
                .addValue("p_ProductID", productId)
                .addValue("p_Quantity", quantity)
                .addValue("p_StandPrice", standPrice)
                .addValue("p_ItemPrice", itemPrice));
    }
}