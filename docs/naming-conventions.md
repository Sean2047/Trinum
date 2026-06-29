# Trinum — Naming Conventions
Source: Bootstrap STEP 3 / P0.1

## Standard Conventions (project-wide)

| Type | Pattern | Project examples |
|------|---------|-----------------|
| ViewModel | `{Feature}ViewModel` | `CalculatorViewModel`, `ConverterViewModel`, `TableViewModel` |
| Use Case | `{Verb}{Noun}UseCase` | `EvaluateExpressionUseCase`, `ConvertUnitUseCase`, `EvaluateCellFormulaUseCase` |
| Repository interface | `{Domain}Repository` | `HistoryRepository`, `TableRepository` |
| Repository impl | `{Domain}RepositoryImpl` | `HistoryRepositoryImpl`, `TableRepositoryImpl` |
| DAO | `{Entity}Dao` | `HistoryEntryDao`, `SavedTableDao` |
| Entity | `{Model}Entity` | `HistoryEntryEntity`, `SavedTableEntity`, `TableCellEntity` |
| UiState | `{Feature}UiState` | `CalculatorUiState`, `ConverterUiState`, `TableUiState` |
| UiEffect | `{Feature}UiEffect` | `CalculatorUiEffect`, `ConverterUiEffect`, `TableUiEffect` |
| UiAction | `{Feature}UiAction` | `CalculatorUiAction`, `ConverterUiAction`, `TableUiAction` |
| Screen composable | `{Feature}Screen` | `CalculatorScreen`, `ConverterScreen`, `TableScreen` |
| Resource IDs | `{type}_{feature}_{descriptor}` | `ic_calculator_clear`, `str_converter_category_length` |
| ADR files | `ADR-[NNN]-[slug].md` | `ADR-001-mvvm-architecture.md` |
| Task spec files | `TASK-[NNN]-[slug].md` | `TASK-001-foundation.md` |

## Project-Specific Extensions

| Type | Pattern | Example |
|------|---------|---------|
| Unit definition | `{Category}Units` (object) | `LengthUnits`, `TemperatureUnits` |
| Formula evaluator | `{Scope}FormulaEvaluator` | `CellFormulaEvaluator` |
| Expression parser wrapper | `ExpressionEvaluator` | (singleton-style object) |
