# TASK-003: Unit Converter Feature
Sprint: 1
Status: In Progress

## Objective
Implement the Unit Converter screen: category selection, value input, unit-pair selection, swap, live conversion result, and copy-to-clipboard.

## READS
- app/src/main/java/dev/trinum/app/feature/converter/ui/ConverterUiContracts.kt — locked UI contracts (UiState/Effect/Action/Intent)
- domain/src/main/java/dev/trinum/app/domain/model/UnitCategory.kt — 8 categories
- domain/src/main/java/dev/trinum/app/domain/model/UnitDefinition.kt — unit model (id, displayName, toBaseRatio)
- app/src/main/java/dev/trinum/app/navigation/AppNavGraph.kt — placeholder to replace
- app/src/main/java/dev/trinum/app/feature/calculator/EvaluateExpressionUseCase.kt — UseCase pattern
- app/src/main/java/dev/trinum/app/feature/calculator/CalculatorViewModel.kt — ViewModel pattern
- app/src/main/java/dev/trinum/app/feature/calculator/ui/CalculatorScreen.kt — Screen/Content pattern
- app/src/test/java/dev/trinum/app/feature/calculator/CalculatorViewModelTest.kt — ViewModel test pattern

## CREATES
- app/src/main/java/dev/trinum/app/feature/converter/ConvertUnitUseCase.kt — conversion logic + in-memory unit catalog for all 8 categories
- app/src/main/java/dev/trinum/app/feature/converter/ConverterViewModel.kt — HiltViewModel; owns ConverterUiState StateFlow + UiEffect channel
- app/src/main/java/dev/trinum/app/feature/converter/ui/ConverterScreen.kt — ConverterScreen (public) + ConverterContent (internal stateless)
- app/src/test/java/dev/trinum/app/feature/converter/ConvertUnitUseCaseTest.kt — UseCase unit tests (linear + temperature special cases)
- app/src/test/java/dev/trinum/app/feature/converter/ConverterViewModelTest.kt — ViewModel Turbine tests for all UiActions
- app/src/androidTest/java/dev/trinum/app/feature/converter/ui/ConverterScreenTest.kt — Compose semantics tests (Class E requirement)

## MODIFIES
- app/src/main/java/dev/trinum/app/navigation/AppNavGraph.kt — replace Converter placeholder with ConverterScreen()

## DELETES
- none

## Evidence
- ADR-001: MVVM — one ViewModel per screen, no business logic in @Composable
- ADR-002: Jetpack Compose UI — no XML layouts
- ADR-003: Hilt DI — @HiltViewModel on ConverterViewModel
- ADR-004: Navigation Compose — wire via AppNavGraph
- DEC-011: UseCase stays in :app (same pattern as EvaluateExpressionUseCase)
- DEC-014: CREATES=6 accepted for Screen Feature Tasks (Class E precedent)
- DEC-015: Temperature handled as special case (Kelvin intermediate) in ConvertUnitUseCase

## Definition of Done
- [ ] All CREATES files exist
- [ ] Compile gate passes
- [ ] Tests written per P11
- [ ] WorkStatus updated
- [ ] Knowledge Alert output (P17)
- [ ] Handoff Package submitted for Reviewer
- [ ] Reviewer: APPROVED
- [ ] CI PASS
