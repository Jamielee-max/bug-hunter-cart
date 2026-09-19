#!/usr/bin/env bash
#
# Local "CI" runner: runs the test suite and enforces a minimum JaCoCo
# line-coverage percentage, failing the build (non-zero exit) if either
# the tests fail or coverage drops below the threshold.
#
# Usage:
#   scripts/ci.sh [THRESHOLD]                 # run mvn test, then check coverage
#   scripts/ci.sh --coverage-only [THRESHOLD] # skip running tests, just check
#                                              # the coverage report from the
#                                              # last test run
#
# THRESHOLD is a whole number percentage (default: 80).

set -euo pipefail

COVERAGE_ONLY=false
if [[ "${1:-}" == "--coverage-only" ]]; then
    COVERAGE_ONLY=true
    shift
fi

THRESHOLD="${1:-80}"
JACOCO_CSV="target/site/jacoco/jacoco.csv"

if [[ "$COVERAGE_ONLY" == false ]]; then
    echo "==> Running tests..."
    if ! mvn test; then
        echo "==> FAIL: tests did not pass."
        exit 1
    fi
fi

if [[ ! -f "$JACOCO_CSV" ]]; then
    echo "==> FAIL: coverage report not found at $JACOCO_CSV"
    echo "    (did the tests run and jacoco:report execute?)"
    exit 1
fi

# JaCoCo's CSV columns:
# GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,
# BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,
# COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED
# Line coverage is columns 8 (missed) and 9 (covered).
read -r LINE_MISSED LINE_COVERED <<< "$(awk -F, '
    NR > 1 { missed += $8; covered += $9 }
    END { print missed, covered }
' "$JACOCO_CSV")"

TOTAL_LINES=$((LINE_MISSED + LINE_COVERED))

if [[ "$TOTAL_LINES" -eq 0 ]]; then
    echo "==> No line data found in coverage report."
    exit 1
fi

# Percentage as a whole number, rounded down, using integer arithmetic
# only (no bc/python dependency).
COVERAGE_PCT=$(( LINE_COVERED * 100 / TOTAL_LINES ))

echo "==> Line coverage: ${COVERAGE_PCT}% (covered ${LINE_COVERED}/${TOTAL_LINES} lines)"
echo "==> Required threshold: ${THRESHOLD}%"

if [[ "$COVERAGE_PCT" -lt "$THRESHOLD" ]]; then
    echo "==> FAIL: coverage ${COVERAGE_PCT}% is below the ${THRESHOLD}% threshold."
    exit 1
fi

echo "==> PASS: coverage meets the threshold."