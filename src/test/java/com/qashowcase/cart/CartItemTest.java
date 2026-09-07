package com.qashowcase.cart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void cartItem_storesSkuAndCategory() {
        CartItem item = new CartItem("Widget", 9.99, 2, "SKU-1234", "Hardware");

        assertEquals("SKU-1234", item.getSku());
        assertEquals("Hardware", item.getCategory());
    }

    @Test
    void cartItem_originalConstructorStillWorks() {
        // Backward compatibility check: existing 3-arg constructor
        // (used throughout ShoppingCart) should still behave the same.
        CartItem item = new CartItem("Widget", 9.99, 2);

        assertEquals("Widget", item.getProductName());
        assertEquals(9.99, item.getUnitPrice(), 0.001);
        assertEquals(2, item.getQuantity());
        assertNull(item.getSku());
        assertNull(item.getCategory());
    }
}