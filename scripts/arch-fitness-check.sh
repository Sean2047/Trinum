#!/usr/bin/env bash
# Trinum Architecture Fitness Check (P15)
# Run at every Sprint Review.
# Usage: ./scripts/arch-fitness-check.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_ROOT"

echo "=== Trinum Architecture Fitness Check ==="
echo ""

echo "[1/4] Running ArchUnit zero-Android-import test on :domain..."
./gradlew :domain:test --tests "*ArchUnit*" || { echo "FAIL: :domain has Android imports"; exit 1; }
echo "PASS: :domain zero-Android-import"
echo ""

echo "[2/4] Running all unit tests..."
./gradlew test
echo "PASS: all unit tests"
echo ""

echo "[3/4] Running static analysis (detekt + ktlintCheck + lint)..."
./gradlew detekt ktlintCheck lint
echo "PASS: static analysis"
echo ""

echo "[4/4] Checking module dependency graph..."
./gradlew :app:dependencies --configuration releaseRuntimeClasspath | grep ":data\|:domain\|:core" || true
echo "INFO: review above for unexpected module edges"
echo ""

echo "=== Fitness Check Complete ==="
echo "P15 thresholds (manual review required):"
echo "  Module Fan-In <= 10"
echo "  Module Fan-Out <= 10"
echo "  StateFlow chain depth <= 3"
echo "  Composable nesting depth <= 5"
echo "  DAOs per RepositoryImpl = 1"
echo "  Package depth from module root <= 4"
echo "  Kotlin files per module <= 30"
echo "  Functions per ViewModel <= 15"
