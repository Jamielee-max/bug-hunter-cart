package com.qashowcase.cart;

/**
 * Represents a single line item in a shopping cart: one product,
 * its unit price, and how many of it have been added.
 */
public class CartItem {

    private final String productName;
    private final double unitPrice;
    private int quantity;
    private final String sku;
    private final String category;

    public CartItem(String productName, double unitPrice, int quantity) {
        this(productName, unitPrice, quantity, null, null);
    }

    public CartItem(String productName, double unitPrice, int quantity, String sku, String category) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.sku = sku;
        this.category = category;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return unitPrice * quantity;
    }

    public String getSku() {
        return sku;
    }

    public String getCategory() {
        return category;
    }
}