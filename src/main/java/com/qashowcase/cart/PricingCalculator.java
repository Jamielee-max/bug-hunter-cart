package com.qashowcase.cart;

import java.util.List;

/**
 * Computes cart pricing — the new-customer discount, tax, and shipping —
 * based on a cart's items in the order they were added.
 */
public class PricingCalculator {

    private static final double TAX_RATE = 0.15;
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final double FLAT_SHIPPING_COST = 5.99;
    private static final double NEW_CUSTOMER_DISCOUNT_RATE = 0.20;
    private static final int NEW_CUSTOMER_DISCOUNT_ITEM_COUNT = 2;

    /** @return subtotal after the new-customer discount, before tax/shipping */
    public double getSubtotal(List<CartItem> insertionOrder) {
        double rawSubtotal = 0.0;
        for (CartItem item : insertionOrder) {
            rawSubtotal += item.getLineTotal();
        }
        return rawSubtotal - newCustomerDiscountAmount(insertionOrder);
    }

    /** @return 20% off the unit price of the first two items added */
    private double newCustomerDiscountAmount(List<CartItem> insertionOrder) {
        double discount = 0.0;
        for (int i = 0; i < NEW_CUSTOMER_DISCOUNT_ITEM_COUNT && i < insertionOrder.size(); i++) {
            CartItem item = insertionOrder.get(i);
            discount += item.getUnitPrice() * NEW_CUSTOMER_DISCOUNT_RATE;
        }
        return discount;
    }

    /** @return 15% tax on the discounted subtotal */
    public double getTax(List<CartItem> insertionOrder) {
        return getSubtotal(insertionOrder) * TAX_RATE;
    }

    /** @return $0 if subtotal is at or above the free-shipping threshold, otherwise the flat rate */
    public double getShippingCost(List<CartItem> insertionOrder) {
        if (getSubtotal(insertionOrder) >= FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return FLAT_SHIPPING_COST;
    }

    /** @return subtotal + tax + shipping */
    public double getTotal(List<CartItem> insertionOrder) {
        return getSubtotal(insertionOrder) + getTax(insertionOrder) + getShippingCost(insertionOrder);
    }
}