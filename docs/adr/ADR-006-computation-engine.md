# ADR-006: Computation Engine Strategy
Status: Accepted
Date: 2026-06-28
Supersedes: none
Superseded-by: none

## Decision
Use exp4j 0.4.8 for arithmetic expression evaluation (Calculator feature) and a custom
cell-reference resolver for spreadsheet formula evaluation (Table feature). There is no
network layer; Retrofit and OkHttp are absent from this project entirely.

## Context
Standard Android project templates include a Network ADR for Retrofit. Trinum has no
network requirement — all computation is local. This ADR replaces the Network ADR slot
to record the computation strategy decisions that are equally fundamental to this project.

## Consequences

**Calculator feature (exp4j):**
- `EvaluateExpressionUseCase` wraps exp4j's `ExpressionBuilder`
- Supported operations: arithmetic (`+`, `-`, `*`, `/`, `^`), standard math functions
  (`sin`, `cos`, `tan`, `sqrt`, `log`, `abs`, `ceil`, `floor`)
- Invalid expression → use case returns a typed `Result.Failure` — never throws to ViewModel
- Expression evaluation must complete in < 50ms (NFR); exp4j is synchronous, no coroutine needed
- exp4j version pinned to `0.4.8` in `libs.versions.toml`; ProGuard rule required (reflection-based function lookup)

**Table feature (custom resolver):**
- Formulas identified by `=` prefix stored in `TableCellEntity.content` (INV-004)
- `CellFormulaEvaluator` resolves A1-style cell references to numeric values before delegating
  to exp4j for final arithmetic evaluation
- Supported aggregate functions: `SUM(A1:A5)`, `AVERAGE(B1:B3)`, `MIN`, `MAX`
- Circular reference detection is required: cycles → `#REF!` error displayed in cell
- Formula evaluation must complete in < 200ms for ≤ 100 cells (NFR)

**Unit Converter feature:**
- No external library. Conversion is a pure multiplication/division with hardcoded ratios
- `ConvertUnitUseCase` takes `(value: Double, from: UnitDefinition, to: UnitDefinition)`
  and converts through a base unit (e.g., metres for length)
- All `UnitDefinition` objects are compile-time constants; no runtime loading

**Network prohibition:**
- Zero network calls are ever permitted (INV-002)
- No Retrofit, OkHttp, or any HTTP client in any module
- Any import of `okhttp3.*`, `retrofit2.*`, or `java.net.URL` in a non-test file = critical violation

## Rationale
exp4j is chosen over a custom recursive-descent parser because:
1. It is a mature, tested library that handles operator precedence and standard math functions correctly
2. Writing a custom parser would produce a high-complexity class that likely violates P11.3's
   cyclomatic complexity threshold (≤ 10), requiring decomposition effort that is not the
   project's primary goal
3. The custom cell-reference resolver is necessary regardless — it handles the A1 syntax
   that exp4j does not support — and it delegates final arithmetic to exp4j, keeping each
   component's responsibility narrow
