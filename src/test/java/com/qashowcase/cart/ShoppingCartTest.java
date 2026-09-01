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

        // Prices chosen to make the expected math easy to verify by hand.
        cart.addItem("Widget", 10.00, 1);   // 1st item added -> should be discounted
        cart.addItem("Gadget", 20.00, 1);   // 2nd item added -> should be discounted
        cart.addItem("Gizmo", 30.00, 1);    // 3rd item added -> should NOT be discounted

        double rawSubtotal = 10.00 + 20.00 + 30.00; // 60.00
        double expectedDiscount = (10.00 + 20.00) * 0.20; // only first 2 items, 20% off = 6.00
        double expectedSubtotal = rawSubtotal - expectedDiscount; // 54.00

        assertEquals(expectedSubtotal, cart.getSubtotal(), 0.001,
                "Discount should only apply to the first 2 items added, not the 3rd");
    }

    // TODO 2: Free shipping should kick in at exactly $50, not just
    // above it. Write a boundary test at precisely $50 subtotal.
    // (Hint: watch out for TODO 1's discount affecting your subtotal
    //  math while you set this test up.)

    @Test
    void shipping_isFreeWhenSubtotalIsExactlyThreshold() {
        ShoppingCart cart = new ShoppingCart(stock);

        // Add 3+ items so the new-customer discount (fixed in TODO 1) is
        // already "used up" and doesn't interfere with hitting $50 exactly.
        cart.addItem("Widget", 10.00, 1);   // discounted
        cart.addItem("Gadget", 20.00, 1);   // discounted
        cart.addItem("Gizmo", 10.00, 1);    // not discounted (3rd item)

        // rawSubtotal = 10 + 20 + 10 = 40.00
        // discount = (10 + 20) * 0.20 = 6.00
        // subtotal = 40.00 - 6.00 = 34.00 ... not quite $50, so add one more item

        cart.addItem("Doohickey", 16.00, 1); // not discounted either

        // rawSubtotal = 40 + 16 = 56.00
        // subtotal = 56.00 - 6.00 = 50.00 exactly

        assertEquals(50.00, cart.getSubtotal(), 0.001, "sanity check on subtotal math");
        assertEquals(0.0, cart.getShippingCost(), 0.001,
                "Shipping should be free at exactly $50, not just above it");
    }

    // TODO 3: Tax should be calculated precisely to the cent. Try a
    // subtotal that's likely to expose floating point drift, e.g.
    // something that doesn't divide evenly at 15% tax. Compare with
    // assertEquals(expected, actual, delta) vs. exact equality —
    // which one exposes the bug?

    @Test
    void tax_isPreciseToTheCent() {
        ShoppingCart cart = new ShoppingCart(stock);

        // A single item, no discount interference, chosen so 15% tax
        // doesn't land on a "clean" float value.
        cart.addItem("Widget", 19.99, 1);

        double expectedTax = 19.99 * 0.15; // 2.9985 -> should round/behave like 2.9985 in double precision

        // Using a tight delta here is the point: float math tends to drift
        // by more than this at the cent level, so this should catch it.
        assertEquals(expectedTax, cart.getTax(), 0.0001,
                "Tax should match double-precision arithmetic, not drift due to float rounding");
    }

    // TODO 4: getMostExpensiveItemName() on an empty cart should fail
    // gracefully (or return something sensible) instead of throwing an
    // unchecked NullPointerException. Decide what the *correct*
    // behaviour should be, then write a test for it.

    @Test
    void getMostExpensiveItemName_throwsClearExceptionWhenCartIsEmpty() {
        ShoppingCart cart = new ShoppingCart(stock);

        // Cart has no items added. Calling this should fail with a clear,
        // documented exception (IllegalStateException) explaining why --
        // not an unchecked NullPointerException with no context.
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                cart::getMostExpensiveItemName);

        assertTrue(thrown.getMessage().toLowerCase().contains("empty"),
                "Exception message should explain that the cart is empty");
    }

    // TODO 5: Two separate ShoppingCart instances should not affect
    // each other. Create cart A, add 2 items to it. Then create a
    // brand new cart B and add 1 item to it. Does cart B's subtotal
    // look right, or does it seem to "remember" cart A's items?

}