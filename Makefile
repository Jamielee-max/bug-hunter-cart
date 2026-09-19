.PHONY: all build test coverage ci clean

# Default threshold for line coverage, as a whole number percentage.
# Override on the command line: make ci COVERAGE_THRESHOLD=70
COVERAGE_THRESHOLD ?= 80

all: build

## Compile the project without running tests.
build:
	mvn -q compile

## Run the JUnit test suite.
test:
	mvn test

## Run tests and print the JaCoCo line coverage percentage.
coverage: test
	@bash scripts/ci.sh --coverage-only $(COVERAGE_THRESHOLD)

## Full local CI run: clean, test, and enforce the coverage threshold.
## Fails (non-zero exit) if any test fails or coverage drops below the
## threshold -- same idea as a real CI pipeline, just run on your machine.
ci: clean
	@bash scripts/ci.sh $(COVERAGE_THRESHOLD)

## Remove build output.
clean:
	mvn -q clean