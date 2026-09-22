package com.qashowcase.cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple shopping cart with stock checking, a "new customer" discount,
 * tax, and free shipping over a threshold.
 *
 * <p>NOTE: this class currently mixes inventory tracking, pricing rules,
 * and cart-contents management in one place — see project notes on
 * splitting these into {@code Inventory} / {@code PricingCalculator}
 * for a cleaner separation of concerns.</p>
 */
public class ShoppingCart {

    private static final double TAX_RATE = 0.15;
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final double FLAT_SHIPPING_COST = 5.99;
    private static final double NEW_CUSTOMER_DISCOUNT_RATE = 0.20;
    private static final int NEW_CUSTOMER_DISCOUNT_ITEM_COUNT = 2;

    private final List<CartItem> insertionOrder = new ArrayList<>();
    private final Map<String, CartItem> items = new LinkedHashMap<>();
    private final Map<String, Integer> stock;

    /**
     * @param stockLevels available stock per product name. Note: this cart
     *                    mutates the map it's given as items are added.
     */
    public ShoppingCart(Map<String, Integer> stockLevels) {
        this.stock = stockLevels;
    }

    /**
     * Adds an item without SKU/category.
     *
     * @throws IllegalStateException if there isn't enough stock
     */
    public void addItem(String productName, double unitPrice, int quantity) {
        addItem(productName, unitPrice, quantity, null, null);
    }

    /**
     * Adds an item to the cart, decrementing stock. If the product is
     * already in the cart, increases its quantity instead of duplicating it.
     *
     * @throws IllegalStateException if there isn't enough stock for the requested quantity
     */
    public void addItem(String productName, double unitPrice, int quantity, String sku, String category) {
        Integer available = stock.get(productName);
        if (available == null || available < quantity) {
            throw new IllegalStateException("Not enough stock for " + productName);
        }

        if (items.containsKey(productName)) {
            items.get(productName).addQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(productName, unitPrice, quantity, sku, category);
            items.put(productName, newItem);
            insertionOrder.add(newItem);
        }

        stock.put(productName, available - quantity);
    }

    /** @return the cart item for this product, or {@code null} if not present */
    public CartItem getItem(String productName) {
        return items.get(productName);
    }

    /** Removes a product from the cart entirely. */
    public void removeItem(String productName) {
        items.remove(productName);
    }

    /** @return number of distinct products in the cart */
    public int getItemCount() {
        return items.size();
    }

    /** @return {@code true} if the cart has no items */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** @return subtotal after the new-customer discount, before tax/shipping */
    public double getSubtotal() {
        double rawSubtotal = 0.0;
        for (CartItem item : items.values()) {
            rawSubtotal += item.getLineTotal();
        }
        return rawSubtotal - newCustomerDiscountAmount();
    }

    /** @return 20% off the unit price of the first two items added to the cart */
    private double newCustomerDiscountAmount() {
        double discount = 0.0;
        for (int i = 0; i < NEW_CUSTOMER_DISCOUNT_ITEM_COUNT && i < insertionOrder.size(); i++) {
            CartItem item = insertionOrder.get(i);
            discount += item.getUnitPrice() * NEW_CUSTOMER_DISCOUNT_RATE;
        }
        return discount;
    }

    /** @return 15% tax on the discounted subtotal */
    public double getTax() {
        return getSubtotal() * TAX_RATE;
    }

    /** @return $0 if subtotal is at or above the free-shipping threshold, otherwise the flat rate */
    public double getShippingCost() {
        if (getSubtotal() >= FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return FLAT_SHIPPING_COST;
    }

    /** @return subtotal + tax + shipping */
    public double getTotal() {
        return getSubtotal() + getTax() + getShippingCost();
    }

    /**
     * @return the name of the highest unit-price item in the cart
     * @throws IllegalStateException if the cart is empty
     */
    public String getMostExpensiveItemName() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot find most expensive item: cart is empty");
        }

        CartItem mostExpensive = null;
        for (CartItem item : items.values()) {
            if (mostExpensive == null || item.getUnitPrice() > mostExpensive.getUnitPrice()) {
                mostExpensive = item;
            }
        }
        return mostExpensive.getProductName();
    }
}