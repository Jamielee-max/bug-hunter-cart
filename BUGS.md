## Bug Log

Fill in one row per bug as you find it. Keep descriptions short — the test
itself is the real evidence.

| # | Bug | Test that caught it | Fix |
|---|-----|----------------------|-----|
| 1 | New-customer discount applied to 3 items instead of 2 (off-by-one) | `newCustomerDiscount_appliesToFirstTwoItemsOnly` | Changed loop condition `i <= NEW_CUSTOMER_DISCOUNT_ITEM_COUNT` to `i < NEW_CUSTOMER_DISCOUNT_ITEM_COUNT` |
| 2 | Free shipping only triggered above $50, not at exactly $50 (wrong boundary operator) | `shipping_isFreeWhenSubtotalIsExactlyThreshold` | Changed `getSubtotal() > FREE_SHIPPING_THRESHOLD` to `getSubtotal() >= FREE_SHIPPING_THRESHOLD` |
| 3 | Tax calculation drifted from expected value due to casting through `float` instead of using `double` throughout | `tax_isPreciseToTheCent` | Removed the `float` casts in `getTax()`; now computes `getSubtotal() * TAX_RATE` directly in `double` |
| 4 | `getMostExpensiveItemName()` threw an unchecked NullPointerException on an empty cart, with no explanation | `getMostExpensiveItemName_throwsClearExceptionWhenCartIsEmpty` | Added an explicit empty-cart check that throws `IllegalStateException("Cannot find most expensive item: cart is empty")` |
| 5 | | | |

## Reflection (optional but recommended for a report/submission)

- Which bug was hardest to catch, and why?
- Did writing the test first change how you thought about the "correct"
  behaviour compared to just reading the code?
- If you were reviewing someone else's PR with these bugs, what would have
  tipped you off before even running the tests?