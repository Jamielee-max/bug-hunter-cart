# Bug Hunter: Shopping Cart

A small QA showcase project. `ShoppingCart` has **5 intentionally planted bugs**.
Your job is to find each one with a test-driven approach: write a test that
proves the bug exists (red), fix the code (green), then document it.

## Project structure

```
src/main/java/com/qashowcase/cart/
  CartItem.java        - line item model (no bugs here)
  ShoppingCart.java     - the class under test (bugs live here)
src/test/java/com/qashowcase/cart/
  ShoppingCartTest.java - skeleton test class with 5 TODOs, one worked example
BUGS.md                 - fill this in as you find each bug
```

## Features implemented

- Add items to the cart, respecting stock levels
- "New customer" discount: 20% off the first 2 items added
- 15% tax on the subtotal
- Free shipping on orders of $50 or more (otherwise a flat $5.99)
- Look up the most expensive item in the cart

## How to run

```bash
mvn test
```

This also generates a JaCoCo coverage report at
`target/site/jacoco/index.html` once you've run the tests — useful if you
want to show test coverage as part of your submission.

## Workflow for each bug

1. Read the relevant TODO in `ShoppingCartTest.java`.
2. Write a test expressing the *correct* behaviour — don't look at the
   implementation to reverse-engineer the bug, work from what the feature
   is supposed to do.
3. Run `mvn test` and confirm it fails.
4. Open `ShoppingCart.java`, find and fix the bug.
5. Run `mvn test` again and confirm it passes.
6. Add a row to `BUGS.md`.

## A note on the 5 bugs

They cover 5 different common bug categories on purpose:

1. An off-by-one error in a loop condition
2. A wrong comparison operator at a boundary
3. A floating-point precision issue
4. An unchecked null/empty case
5. Shared mutable state leaking between object instances

If you get stuck on any one for more than ~20 minutes, that's a good sign
you've found a "real" bug worth writing up carefully — that's often how it
goes in practice too.

## Optional extensions (if you want to go further)

- Add mutation testing with PIT (`org.pitest:pitest-maven`) to prove your
  tests actually catch the bugs, not just happen to pass
- Wire this into a CI pipeline (e.g. GitLab CI) that runs `mvn test` and
  fails the build on a coverage drop
- Add a couple of intentionally bad tests (e.g. one with no assertions) to
  show you can also spot weak tests, not just weak code
