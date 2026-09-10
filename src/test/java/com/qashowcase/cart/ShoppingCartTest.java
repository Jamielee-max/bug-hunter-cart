package com.qashowcase.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD workflow for this file:
 *   1. Pick one TODO below.
 *   2. Write a test that expresses what SHOULD happen.
 *   3. Run `mvn test` and watch it fail (red).
 *   4. Open ShoppingCart.java, find the bug, fix it.
 *   5. Run `mvn test` again and watch it pass (green).
 *   6. Log the bug in BUGS.md before moving to the next one.
 *
 * There are 5 bugs planted in ShoppingCart.java. This file gives you a
 * starting point (stock setup + one worked example) — the rest is on you.
 */
class ShoppingCartTest {

    private Map<String, Integer> stock;

    @BeforeEach
    void setUp() {
        stock = new HashMap<>();
        stock.put("Widget", 10);
        stock.put("Gadget", 10);
        stock.put("Gizmo", 10);
        stock.put("Doohickey", 10);
    }

    // ---------------------------------------------------------------
    // WORKED EXAMPLE — stock check. This one is already correct code,
    // just here to show you the pattern. Run it to confirm the project
    // is wired up correctly before you start hunting bugs.
    // ---------------------------------------------------------------
    @Test
    void addItem_throwsWhenNotEnoughStock() {
        ShoppingCart cart = new ShoppingCart(stock);
        assertThrows(IllegalStateException.class,
                () -> cart.addItem("Widget", 9.99, 999));
    }


    // TODO 1: New-customer discount should apply to the first 2 items
    // added to the cart, not the first 3. Write a test that adds 3
    // different items and checks the subtotal reflects a discount on

    @Test
    void newCustomerDiscount_appliesToFirstTwoItemsOnly() {
        ShoppingCart cart = new ShoppingCart(stock);

        cart.addItem("Widget", 10.00, 1);
        cart.addItem("Gadget", 20.00, 1);
        cart.addItem("Gizmo", 30.00, 1);

        double rawSubtotal = 10.00 + 20.00 + 30.00;
        double expectedDiscount = (10.00 + 20.00) * 0.20;
        double expectedSubtotal = rawSubtotal - expectedDiscount;

        assertEquals(expectedSubtotal, cart.getSubtotal(), 0.001,
                "Discount should only apply to the first 2 items added, not the 3rd");
    }

    // TODO 2: Free shipping should kick in at exactly $50, not just
    // above it. Write a boundary test at precisely $50 subtotal.

    @Test
    void shipping_isFreeWhenSubtotalIsExactlyThreshold() {
        ShoppingCart cart = new ShoppingCart(stock);

        cart.addItem("Widget", 10.00, 1);
        cart.addItem("Gadget", 20.00, 1);
        cart.addItem("Gizmo", 10.00, 1);
        cart.addItem("Doohickey", 16.00, 1);

        assertEquals(50.00, cart.getSubtotal(), 0.001, "sanity check on subtotal math");
        assertEquals(0.0, cart.getShippingCost(), 0.001,
                "Shipping should be free at exactly $50, not just above it");
    }

    // TODO 3: Tax should be calculated precisely to the cent.

    @Test
    void tax_isPreciseToTheCent() {
        ShoppingCart cart = new ShoppingCart(stock);

        // Add two filler items first so they use up the "first 2 items"
        // new-customer discount, leaving Widget's price untouched as the
        // 3rd item -- otherwise this test's expected value doesn't
        // account for the discount and looks like a false failure.
        cart.addItem("Gadget", 5.00, 1);
        cart.addItem("Doohickey", 5.00, 1);
        cart.addItem("Widget", 19.99, 1);

        double discountedFillerTotal = (5.00 + 5.00) - (5.00 + 5.00) * 0.20; // 8.00
        double subtotal = discountedFillerTotal + 19.99; // 27.99
        double expectedTax = subtotal * 0.15;

        assertEquals(expectedTax, cart.getTax(), 0.0001,
                "Tax should match double-precision arithmetic, not drift due to float rounding");
    }

    // TODO 4: getMostExpensiveItemName() on an empty cart should fail
    // gracefully instead of throwing an unchecked NullPointerException.

    @Test
    void getMostExpensiveItemName_throwsClearExceptionWhenCartIsEmpty() {
        ShoppingCart cart = new ShoppingCart(stock);

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                cart::getMostExpensiveItemName);

        assertTrue(thrown.getMessage().toLowerCase().contains("empty"),
                "Exception message should explain that the cart is empty");
    }

    // TODO 5: Two separate ShoppingCart instances should not affect
    // each other.

    @Test
    void newCustomerDiscount_isIndependentPerCartInstance() {
        Map<String, Integer> stockA = new HashMap<>();
        stockA.put("Widget", 10);
        stockA.put("Gadget", 10);
        ShoppingCart cartA = new ShoppingCart(stockA);
        cartA.addItem("Widget", 10.00, 1);
        cartA.addItem("Gadget", 20.00, 1);

        Map<String, Integer> stockB = new HashMap<>();
        stockB.put("Gizmo", 10);
        ShoppingCart cartB = new ShoppingCart(stockB);
        cartB.addItem("Gizmo", 30.00, 1);

        double expectedDiscount = 30.00 * 0.20;
        double expectedSubtotal = 30.00 - expectedDiscount;

        assertEquals(expectedSubtotal, cartB.getSubtotal(), 0.001,
                "Cart B's discount should be based on its own items, not leftover state from cart A");
    }

    // ---------------------------------------------------------------
    // NEW FEATURE: wiring sku/category through to ShoppingCart.addItem().
    // Written test-first, same as the bugs above.
    // ---------------------------------------------------------------
    @Test
    void addItem_withSkuAndCategory_storesThemOnTheCartItem() {
        ShoppingCart cart = new ShoppingCart(stock);

        cart.addItem("Widget", 9.99, 1, "SKU-1234", "Hardware");

        CartItem stored = cart.getItem("Widget");
        assertNotNull(stored, "Expected to find the item we just added");
        assertEquals("SKU-1234", stored.getSku());
        assertEquals("Hardware", stored.getCategory());
    }

    @Test
    void addItem_withoutSkuAndCategory_stillWorksAsBefore() {
        ShoppingCart cart = new ShoppingCart(stock);

        cart.addItem("Widget", 9.99, 1);

        CartItem stored = cart.getItem("Widget");
        assertNotNull(stored);
        assertNull(stored.getSku());
        assertNull(stored.getCategory());
    }

}