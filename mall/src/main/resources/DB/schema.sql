-- 1. 建立並使用資料庫
CREATE DATABASE IF NOT EXISTS esun_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE esun_mall;

-- 2. 清除舊有的資料表與 Stored Procedure
DROP TABLE IF EXISTS orderdetail;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS product;

DROP PROCEDURE IF EXISTS sp_AddProduct;
DROP PROCEDURE IF EXISTS sp_GetAvailableProducts;
DROP PROCEDURE IF EXISTS sp_CreateOrder;
DROP PROCEDURE IF EXISTS sp_AddOrderDetailAndUpdateStock;

-- 3. 建立 Product (商品表)
CREATE TABLE product (
                         product_id VARCHAR(50) PRIMARY KEY,
                         product_name VARCHAR(100) NOT NULL,
                         price DECIMAL(10, 2) NOT NULL,
                         quantity INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 建立 Order (主訂單表)
CREATE TABLE `order` (
                         order_id VARCHAR(50) PRIMARY KEY,
                         member_id INT NOT NULL,
                         price DECIMAL(10, 2) NOT NULL,
                         pay_status TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 建立 OrderDetail (訂單明細表)
CREATE TABLE orderdetail (
                             order_item_sn INT AUTO_INCREMENT PRIMARY KEY,
                             order_id VARCHAR(50) NOT NULL,
                             product_id VARCHAR(50) NOT NULL,
                             quantity INT NOT NULL,
                             stand_price DECIMAL(10, 2) NOT NULL,
                             item_price DECIMAL(10, 2) NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES product(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 建立 Stored Procedures
DELIMITER //
CREATE PROCEDURE sp_AddProduct(
    IN p_product_id VARCHAR(50),
    IN p_product_name VARCHAR(100),
    IN p_price DECIMAL(10, 2),
    IN p_quantity INT
)
BEGIN
    INSERT INTO product (product_id, product_name, price, quantity)
    VALUES (p_product_id, p_product_name, p_price, p_quantity);
END //

CREATE PROCEDURE sp_GetAvailableProducts()
BEGIN
    SELECT product_id, product_name, price, quantity
    FROM product
    WHERE quantity > 0;
END //

CREATE PROCEDURE sp_CreateOrder(
    IN p_order_id VARCHAR(50),
    IN p_member_id INT,
    IN p_price DECIMAL(10, 2),
    IN p_pay_status TINYINT
)
BEGIN
    INSERT INTO `order` (order_id, member_id, price, pay_status)
    VALUES (p_order_id, p_member_id, p_price, p_pay_status);
END //

CREATE PROCEDURE sp_AddOrderDetailAndUpdateStock(
    IN p_order_id VARCHAR(50),
    IN p_product_id VARCHAR(50),
    IN p_quantity INT,
    IN p_stand_price DECIMAL(10, 2),
    IN p_item_price DECIMAL(10, 2)
)
BEGIN
    DECLARE current_stock INT;

    SELECT quantity INTO current_stock FROM product WHERE product_id = p_product_id FOR UPDATE;

    IF current_stock IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '找不到該商品！';
    ELSEIF current_stock < p_quantity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '庫存不足，無法建立訂單！';
    ELSE
        INSERT INTO orderdetail (order_id, product_id, quantity, stand_price, item_price)
        VALUES (p_order_id, p_product_id, p_quantity, p_stand_price, p_item_price);

        UPDATE product
        SET quantity = quantity - p_quantity
        WHERE product_id = p_product_id;
    END IF;
END //
DELIMITER ;