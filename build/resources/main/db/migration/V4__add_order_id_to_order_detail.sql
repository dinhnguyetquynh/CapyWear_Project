ALTER TABLE order_detail
    ADD COLUMN order_id INT;

ALTER TABLE order_detail
    ADD CONSTRAINT fk_order_detail_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE;