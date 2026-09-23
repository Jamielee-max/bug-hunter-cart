.PHONY: all build test coverage ci clean

# coverage threshold %, override with: make ci COVERAGE_THRESHOLD=70
COVERAGE_THRESHOLD ?= 80

all: build

build:
	mvn -q compile

test:
	mvn test

coverage: test
	@bash scripts/ci.sh --coverage-only $(COVERAGE_THRESHOLD)

# runs everything and fails if tests break or coverage drops
ci: clean
	@bash scripts/ci.sh $(COVERAGE_THRESHOLD)

clean:
	mvn -q clean