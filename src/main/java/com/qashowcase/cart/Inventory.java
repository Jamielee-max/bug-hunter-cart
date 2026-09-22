package com.qashowcase.cart;

import java.util.Map;

/**
 * Tracks how many units of each product are available, and reserves
 * stock as items are added to a cart.
 */
public class Inventory {

    private final Map<String, Integer> stock;

    /**
     * @param stockLevels available stock per product name. This inventory
     *                    mutates the map it's given as stock is reserved.
     */
    public Inventory(Map<String, Integer> stockLevels) {
        this.stock = stockLevels;
    }

    /** @return {@code true} if at least {@code quantity} units of the product are available */
    public boolean hasStock(String productName, int quantity) {
        Integer available = stock.get(productName);
        return available != null && available >= quantity;
    }

    /**
     * Reduces the available stock for a product by {@code quantity}.
     *
     * @throws IllegalStateException if there isn't enough stock
     */
    public void reserve(String productName, int quantity) {
        if (!hasStock(productName, quantity)) {
            throw new IllegalStateException("Not enough stock for " + productName);
        }
        stock.put(productName, stock.get(productName) - quantity);
    }
}