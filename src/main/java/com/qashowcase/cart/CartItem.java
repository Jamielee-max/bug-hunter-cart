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

    /**
     * Creates a line item without SKU/category, for callers that don't track them.
     *
     * @param productName name of the product
     * @param unitPrice   price per unit
     * @param quantity    number of units being added
     */
    public CartItem(String productName, double unitPrice, int quantity) {
        this(productName, unitPrice, quantity, null, null);
    }

    /**
     * Creates a line item with full product metadata.
     *
     * @param productName name of the product
     * @param unitPrice   price per unit
     * @param quantity    number of units being added
     * @param sku         stock-keeping unit identifier, or {@code null} if unknown
     * @param category    product category, or {@code null} if unknown
     */
    public CartItem(String productName, double unitPrice, int quantity, String sku, String category) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.sku = sku;
        this.category = category;
    }

    /**
     * Increases the quantity of this line item (e.g. the same product added again).
     *
     * @param amount number of additional units
     */
    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    /** @return the product's name */
    public String getProductName() {
        return productName;
    }

    /** @return the price of a single unit */
    public double getUnitPrice() {
        return unitPrice;
    }

    /** @return how many units of this product are in the line item */
    public int getQuantity() {
        return quantity;
    }

    /** @return unit price multiplied by quantity */
    public double getLineTotal() {
        return unitPrice * quantity;
    }

    /** @return the SKU, or {@code null} if not set */
    public String getSku() {
        return sku;
    }

    /** @return the product category, or {@code null} if not set */
    public String getCategory() {
        return category;
    }
}
