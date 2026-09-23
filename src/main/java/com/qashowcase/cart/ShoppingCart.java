package com.qashowcase.cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple shopping cart with stock checking, a "new customer" discount,
 * tax, and free shipping over a threshold.
 *
 * Stock checks are handled by {@link Inventory} and pricing by
 * {@link PricingCalculator}, so this class just manages what's in the cart.
 */
public class ShoppingCart {

    private final List<CartItem> insertionOrder = new ArrayList<>();
    private final Map<String, CartItem> items = new LinkedHashMap<>();
    private final Inventory inventory;
    private final PricingCalculator pricingCalculator = new PricingCalculator();

    /** @param stockLevels available stock per product name */
    public ShoppingCart(Map<String, Integer> stockLevels) {
        this.inventory = new Inventory(stockLevels);
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
     * Adds an item to the cart, reserving stock. If the product is
     * already in the cart, increases its quantity instead of duplicating it.
     *
     * @throws IllegalStateException if there isn't enough stock for the requested quantity
     */
    public void addItem(String productName, double unitPrice, int quantity, String sku, String category) {
        inventory.reserve(productName, quantity);

        if (items.containsKey(productName)) {
            items.get(productName).addQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(productName, unitPrice, quantity, sku, category);
            items.put(productName, newItem);
            insertionOrder.add(newItem);
        }
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
        return pricingCalculator.getSubtotal(insertionOrder);
    }

    /** @return 15% tax on the discounted subtotal */
    public double getTax() {
        return pricingCalculator.getTax(insertionOrder);
    }

    /** @return $0 if subtotal is at or above the free-shipping threshold, otherwise the flat rate */
    public double getShippingCost() {
        return pricingCalculator.getShippingCost(insertionOrder);
    }

    /** @return subtotal + tax + shipping */
    public double getTotal() {
        return pricingCalculator.getTotal(insertionOrder);
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