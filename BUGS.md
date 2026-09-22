## Bug Log

Fill in one row per bug as you find it. Keep descriptions short — the test
itself is the real evidence.

| # | Bug | Test that caught it | Fix |
|---|-----|----------------------|-----|
| 1 | New-customer discount applied to 3 items instead of 2 (off-by-one) | `newCustomerDiscount_appliesToFirstTwoItemsOnly` | Changed loop condition `i <= NEW_CUSTOMER_DISCOUNT_ITEM_COUNT` to `i < NEW_CUSTOMER_DISCOUNT_ITEM_COUNT` |
| 2 | Free shipping only triggered above $50, not at exactly $50 (wrong boundary operator) | `shipping_isFreeWhenSubtotalIsExactlyThreshold` | Changed `getSubtotal() > FREE_SHIPPING_THRESHOLD` to `getSubtotal() >= FREE_SHIPPING_THRESHOLD` |
| 3 | Tax calculation drifted from expected value due to casting through `float` instead of using `double` throughout | `tax_isPreciseToTheCent` | Removed the `float` casts in `getTax()`; now computes `getSubtotal() * TAX_RATE` directly in `double` |
| 4 | `getMostExpensiveItemName()` threw an unchecked NullPointerException on an empty cart, with no explanation | `getMostExpensiveItemName_throwsClearExceptionWhenCartIsEmpty` | Added an explicit empty-cart check that throws `IllegalStateException("Cannot find most expensive item: cart is empty")` |
| 5 | `insertionOrder` was declared `static`, so it was shared across every `ShoppingCart` instance — a second cart's items leaked into the first cart's discount calculation | `newCustomerDiscount_isIndependentPerCartInstance` | Changed `insertionOrder` from a `static` field to an instance field, so each cart tracks its own insertion order |

## Reflection

**1. Which bug was hardest to catch, and why?**

Bug #5 (the shared `static` field) was the hardest to catch. The other four
bugs show up the moment you write a test that exercises the specific
behaviour they break — off-by-one, boundary, precision, null-check. But #5
only breaks when you have *two separate cart instances* running side by
side; a single-cart test suite would pass every time even with the bug
present. You have to think to test cross-instance isolation before it
even becomes visible, and that's not an obvious thing to reach for unless
you already suspect shared state.

**2. Did writing the test first change how you thought about the "correct"
behaviour compared to just reading the code?**

Yes and no. For the straightforward bugs (off-by-one, boundary, precision),
writing the test first didn't change much — the correct behaviour was
already clear from the feature description, and the test just formalised
it. But for bug #5, writing the test first mattered a lot: reading
`ShoppingCart.java` in isolation, the `static` field looks like ordinary
code, nothing flags it as wrong. It was only by writing a test that
described the expected behaviour ("two carts shouldn't affect each
other") that the bug became visible at all — reading the code alone
wouldn't have surfaced it.

**3. If you were reviewing someone else's PR with these bugs, what would
have tipped you off before even running the tests?**

Mostly the errors in isolation, once I saw them: a `static` field being
used to track per-instance state is the biggest red flag on a code
read — that keyword should jump out in review regardless of tests. The
boundary (`>` vs `>=`) and off-by-one (`<=` vs `<`) bugs are the kind of
thing a careful line-by-line read catches, but they're easy to skim past
if you're not specifically checking edge conditions. The `float` cast
inside a `double`-based calculation is the one I'd be least confident
about catching just by reading — that one really needed the test to
surface the actual drift.