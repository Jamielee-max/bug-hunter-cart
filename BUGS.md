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

1. Which bug was hardest to catch, and why?

Bug 5 (the static field) was the hardest one for me. The other four bugs
are obvious once you write a test for the specific behaviour they break,
like an off-by-one or a boundary check. But bug 5 only shows up if you
have two carts running at the same time. If you only ever test with one
cart, you'd never notice anything was wrong. I had to actually think
about testing two instances against each other before it clicked that
this was even a thing to check.

2. Did writing the test first change how you thought about the "correct"
behaviour compared to just reading the code?

Kind of, depends on the bug. For the simple ones (off-by-one, the
boundary, the tax rounding) it didn't really change much, I already knew
what the right answer should be from reading the feature description. But
for bug 5 it mattered a lot more. If I'd just read ShoppingCart.java
without writing a test first, the static field doesn't look wrong at
all, it just looks like a normal field. Writing the test forced me to
think about what "correct" actually meant for two carts, and that's the
only way the bug became visible.

3. If you were reviewing someone else's PR with these bugs, what would
have tipped you off before even running the tests?

Honestly the static field is the one I'd catch just by reading the code,
that keyword should jump out immediately if you're paying attention. The
`>` vs `>=` and `<=` vs `<` bugs are easy to miss on a skim though,
you'd need to actually slow down and check the edge cases on purpose. The
float/double bug is the one I wouldn't trust myself to catch just by
reading, that one really needs a test to actually show the numbers
drifting.