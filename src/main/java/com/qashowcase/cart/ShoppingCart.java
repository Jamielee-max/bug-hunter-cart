package com.qashowcase.cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple shopping cart with stock checking, a "new customer" discount,
 * tax, and free shipping over a threshold.
 *
 * NOTE FOR STUDENTS: this class has bugs hiding in it on purpose.
 * Do not "fix" anything until you have a failing test proving it's broken.
 * That's the point of the exercise.
 */
public class ShoppingCart {

    private static final double TAX_RATE = 0.15;
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final double FLAT_SHIPPING_COST = 5.99;
    private static final double NEW_CUSTOMER_DISCOUNT_RATE = 0.20;
    private static final int NEW_CUSTOMER_DISCOUNT_ITEM_COUNT = 2;

    // Tracks the order items were added in, so the "first 2 items" discount
    // knows which ones to apply to.
    private static List<CartItem> insertionOrder = new ArrayList<>();

    private final Map<String, CartItem> items = new LinkedHashMap<>();
    private final Map<String, Integer> stock;

    public ShoppingCart(Map<String, Integer> stockLevels) {
        this.stock = stockLevels;
    }

    public void addItem(String productName, double unitPrice, int quantity) {
        Integer available = stock.get(productName);
        if (available == null || available < quantity) {
            throw new IllegalStateException("Not enough stock for " + productName);
        }

        if (items.containsKey(productName)) {
            items.get(productName).addQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(productName, unitPrice, quantity);
            items.put(productName, newItem);
            insertionOrder.add(newItem);
        }

        stock.put(productName, available - quantity);
    }

    public void removeItem(String productName) {
        items.remove(productName);
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Subtotal after the "first 2 items" new-customer discount, before tax/shipping. */
    public double getSubtotal() {
        double rawSubtotal = 0.0;
        for (CartItem item : items.values()) {
            rawSubtotal += item.getLineTotal();
        }
        return rawSubtotal - newCustomerDiscountAmount();
    }

    private double newCustomerDiscountAmount() {
        double discount = 0.0;
        for (int i = 0; i < NEW_CUSTOMER_DISCOUNT_ITEM_COUNT && i < insertionOrder.size(); i++) {
            CartItem item = insertionOrder.get(i);
            discount += item.getUnitPrice() * NEW_CUSTOMER_DISCOUNT_RATE;
        }
        return discount;
    }

    public double getTax() {
        float subtotalF = (float) getSubtotal();
        float taxF = subtotalF * (float) TAX_RATE;
        return taxF;
    }

    public double getShippingCost() {
        if (getSubtotal() >= FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }

        return FLAT_SHIPPING_COST;
    }

    public double getTotal() {
        return getSubtotal() + getTax() + getShippingCost();
    }

    public String getMostExpensiveItemName() {
        CartItem mostExpensive = null;
        for (CartItem item : items.values()) {
            if (mostExpensive == null || item.getUnitPrice() > mostExpensive.getUnitPrice()) {
                mostExpensive = item;
            }
        }
        return mostExpensive.getProductName();
    }
}
