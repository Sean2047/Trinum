# WorkStatus
Last updated: 2026-06-30 | Current Sprint: 4

## Current Status
Next Task: TASK-008 — Table Copy Cell (TD-003 resolution)
Blockers: None
Note: Sprint 1 records (TASK-001/002/003 progress, file registry, review history) archived to WorkStatus_Archive.md (P9 compliance, 2026-06-30)

## Task Progress
| ID | Description | Status | Created | Updated |
| ---- | ------------- | -------- | --------- | --------- |
| TASK-004 | Table Calculator Feature | Done | 2026-06-28 | 2026-06-29 |
| TASK-005 | Navigation Integration + Sprint Review | Done | 2026-06-28 | 2026-06-29 |
| TASK-006 | Table Grid Headers | Done | 2026-06-30 | 2026-06-30 |
| TASK-007 | Calculator Restore-From-History | Done | 2026-06-30 | 2026-06-30 |
| TASK-008 | Table Copy Cell (TD-003) | Pending | 2026-06-30 | 2026-06-30 |

## File Registry (Sprint 2+ active — TASK-004+)
| Path | Owning Task | Status | Date |
| ------ | ------------- | -------- | ------ |
| docs/tasks/TASK-004-table-calculator.md | TASK-004 | Created | 2026-06-29 |
| docs/tasks/TASK-008-table-copy-cell.md | TASK-008 | Created | 2026-06-30 |
| app/src/main/java/dev/trinum/app/feature/table/EvaluateTableUseCase.kt | TASK-004 | Created | 2026-06-29 |
| app/src/main/java/dev/trinum/app/feature/table/TableViewModel.kt | TASK-004 | Created | 2026-06-29 |
| app/src/main/java/dev/trinum/app/feature/table/ui/TableScreen.kt | TASK-004 | Created | 2026-06-29 |
| app/src/test/java/dev/trinum/app/feature/table/EvaluateTableUseCaseTest.kt | TASK-004 | Created | 2026-06-29 |
| app/src/test/java/dev/trinum/app/feature/table/TableViewModelTest.kt | TASK-004 | Created | 2026-06-29 |
| app/src/androidTest/java/dev/trinum/app/feature/table/ui/TableScreenTest.kt | TASK-004 | Created | 2026-06-29 |
| docs/reviews/REVIEW-TASK-004.md | TASK-004 | Created | 2026-06-29 |
| docs/reviews/REVIEW-TASK-005.md | TASK-005 | Created | 2026-06-29 |
| docs/tasks/TASK-005-navigation-sprint-review.md | TASK-005 | Created | 2026-06-29 |
| app/src/main/java/dev/trinum/app/navigation/MainScreen.kt | TASK-005 | Created | 2026-06-29 |
| app/src/androidTest/java/dev/trinum/app/navigation/MainScreenTest.kt | TASK-005 | Created | 2026-06-29 |
| app/src/main/java/dev/trinum/app/MainActivity.kt | TASK-005 | Modified | 2026-06-29 |
| gradle/libs.versions.toml | TASK-005 | Modified | 2026-06-29 |
| app/build.gradle.kts | TASK-005 | Modified | 2026-06-29 |
| app/src/main/java/dev/trinum/app/feature/table/ui/TableScreen.kt | TASK-006 | Modified | 2026-06-30 |
| app/src/androidTest/java/dev/trinum/app/feature/table/ui/TableScreenTest.kt | TASK-006 | Modified | 2026-06-30 |
| app/src/main/java/dev/trinum/app/feature/calculator/ui/CalculatorUiContracts.kt | TASK-007 | Modified | 2026-06-30 |
| app/src/main/java/dev/trinum/app/feature/calculator/CalculatorViewModel.kt | TASK-007 | Modified | 2026-06-30 |
| app/src/main/java/dev/trinum/app/feature/calculator/ui/CalculatorScreen.kt | TASK-007 | Modified | 2026-06-30 |
| app/src/test/java/dev/trinum/app/feature/calculator/CalculatorViewModelTest.kt | TASK-007 | Modified | 2026-06-30 |

## DAO Addition Log
| Method Signature | DAO | Source Task | Date |
| ----------------- | --- | ------------ | ------ |
| observeRecent(): Flow<List<HistoryEntryEntity>> | HistoryEntryDao | TASK-001 | 2026-06-28 |
| insert(entry: HistoryEntryEntity) | HistoryEntryDao | TASK-001 | 2026-06-28 |
| deleteById(id: Long) | HistoryEntryDao | TASK-001 | 2026-06-28 |
| clearAll() | HistoryEntryDao | TASK-001 | 2026-06-28 |
| observeAll(): Flow<List<SavedTableEntity>> | SavedTableDao | TASK-001 | 2026-06-28 |
| getWithCells(tableId: Long): SavedTableWithCells? | SavedTableDao | TASK-001 | 2026-06-28 |
| insertTable(table: SavedTableEntity): Long | SavedTableDao | TASK-001 | 2026-06-28 |
| insertCells(cells: List<TableCellEntity>) | SavedTableDao | TASK-001 | 2026-06-28 |
| deleteCellsForTable(tableId: Long) | SavedTableDao | TASK-001 | 2026-06-28 |
| deleteTable(tableId: Long) | SavedTableDao | TASK-001 | 2026-06-28 |

## Decision Log (DEC)
| DEC-ID | Date | Change | Reason | Affected Files | Status |
| ------ | ---- | ------ | ------ | ---------------- | ------ |
| DEC-001 | 2026-06-28 | applicationId changed from com.paxoo.trinum to dev.trinum.app | ADR §1 specifies dev.trinum.app | app/build.gradle.kts, AndroidManifest.xml | Closed |
| DEC-002 | 2026-06-28 | Kept AGP 9.2.1, Kotlin 2.2.10, composeBom 2026.02.01 instead of Bootstrap spec | Project initialized with newer versions; downgrading destructive | gradle/libs.versions.toml | Closed |
| DEC-003 | 2026-06-28 | ProGuard rules in app/src/main/keepRules/rules.keep (AGP 9.x convention) | AGP 9.x uses keepRules/ directory | app/src/main/keepRules/rules.keep | Closed |
| DEC-004 | 2026-06-28 | kapt→KSP migration | AGP 9.x built-in Kotlin incompatible with kotlin.kapt plugin | all build.gradle.kts files | Closed |
| DEC-005 | 2026-06-28 | ProGuard path updated in AI_SESSION_GUIDE.md P12 | AGP 9.x auto-discovers keepRules/ | docs/AI_SESSION_GUIDE.md | Closed |
| DEC-006 | 2026-06-28 | Project identity: BOOTSTRAP uses "CalcSuite"; actual project is "Trinum" | BOOTSTRAP was a template document | BOOTSTRAP.md | Closed |
| DEC-007 | 2026-06-28 | kotlin.android plugin removed from ALL Android modules | AGP 9.x built-in Kotlin applies to all android.application/library modules | app/, data/, core/ui/ build.gradle.kts | Closed |
| DEC-008 | 2026-06-28 | Hilt bumped from 2.52 to 2.60 | AGP 9.x removed BaseExtension; Hilt 2.59+ required | gradle/libs.versions.toml | Closed |
| DEC-009 | 2026-06-28 | android.disallowKotlinSourceSets=false added to gradle.properties | KSP 2.2.10-2.0.2 registers generated sources via kotlin.sourceSets; official AGP workaround | gradle.properties | Closed |
| DEC-010 | 2026-06-28 | kotlinOptions{} removed from all Android build files; buildConfig=true added to :app | kotlinOptions DSL removed in AGP 9; BuildConfig requires explicit opt-in | app/, data/, core/ui/ build.gradle.kts | Closed |
| DEC-011 | 2026-06-28 | EvaluateExpressionUseCase placed in :app (not :domain) | exp4j already declared in :app; avoids new dependency to :domain | app/feature/calculator/EvaluateExpressionUseCase.kt | Closed |
| DEC-012 | 2026-06-28 | Foundation Task (DoD Update): Generate all subsequent Feature Task Specs in docs/tasks/ | Execution revealed Task Specs need to be created individually | docs/AI_SESSION_GUIDE.md | Closed |
| DEC-013 | 2026-06-28 | Establish docs/reviews/ directory and REVIEW-TASK-NNN.md | persistent review record convention | docs/reviews/ | Closed |
| DEC-014 | 2026-06-29 | TASK-003 CREATES=6 (one over P7 limit). Screen Feature Tasks inherently require 6 files | Any Task CREATEs-ing a Screen must include ScreenTest per TASK-002 Round 2 non-blocking note (Class E) | docs/tasks/TASK-003-unit-converter.md | Closed |
| DEC-015 | 2026-06-29 | Temperature conversion handled as special case in ConvertUnitUseCase (Kelvin intermediate), not via UnitDefinition.toBaseRatio | toBaseRatio only supports linear conversion; temperature needs affine (ratio+offset); modifying domain model requires Foundation Task authority | app/feature/converter/ConvertUnitUseCase.kt | Closed |
| DEC-016 | 2026-06-29 | ConverterViewModel uses compute-then-update pattern (single _state.update per action, no recalculate()) | Two-phase reset-then-compute emits intermediate result="" state; Turbine tests capture it; single-update eliminates race and is architecturally cleaner | app/feature/converter/ConverterViewModel.kt | Closed |
| DEC-017 | 2026-06-29 | material-icons-extended added to :app (BOM-managed, no version pin) | Bottom nav requires Calculate/SwapHoriz/GridOn icons not available in material-icons-core; P0.3 Upgrade Task waived — same-BOM first-party artifact with zero version conflict risk | gradle/libs.versions.toml, app/build.gradle.kts | Closed |
| DEC-018 | 2026-06-29 | KSP upgraded from 2.2.10-2.0.2 to 2.3.9; android.disallowKotlinSourceSets=false removed from gradle.properties | KSP 2.3.x decoupled versioning from Kotlin; no longer registers sources via kotlin.sourceSets; workaround no longer needed | gradle/libs.versions.toml, gradle.properties | Closed |

## Technical Debt
| TD-ID | Type | Description | Impact | Owner Task | Target Sprint | Status |
| ----- | ---- | ----------- | ------ | ---------- | ------------- | ------ |
| TD-001 | DEPRECATION | KSP 2.2.10-2.0.2 uses kotlin.sourceSets (banned by AGP 9); suppressed via gradle.properties. Upgrade to KSP 2.3.x to remove workaround | Build warning | TASK-001 | Sprint 2 | Closed 2026-06-29 |
| TD-002 | INVARIANT_TENSION | EvaluateAll in TableViewModel replaces formula content with numeric result while isFormula=true is preserved, creating INV-004 inconsistency in-memory; persisted if user saves after evaluation | Data integrity risk | TASK-004 | Sprint 2 | Closed 2026-06-29 |
| TD-003 | DEAD_CODE | TableUiEffect.CopyToClipboard is wired in TableScreen but TableViewModel never sends it; no CopyCell action exists | Dead UX path | TASK-004 | Sprint 4 | Open |

## Verified Assumptions
| Assumption | Status | Source | Verified Sprint |
| ---------- | ------ | ------ | --------------- |
| exp4j correctly evaluates standard arithmetic + math functions | Verified | TASK-002 tests | Sprint 1 |
| Room 2.7.0 is compatible with selected AGP version | Verified | CI PASS 2026-06-28 | Sprint 1 |

## Failure Mode Classes
| Class | Failure Mode | Prevention Rule | Tooling Mitigated |
| ----- | ------------ | --------------- | ------------------ |
| A | Assumption instead of reading — signatures inferred from memory | Read real source before coding (P13) | Direct repo access |
| B | Silent default overwrite | All user values explicit and logged | Process |
| C | Unowned field — multiple writers to the same state | Declare ownership in ADR/DEC first | Process |
| D | Platform side-effect blindness | Identify side effects before platform calls | Process |
| E | UI test omitted from CREATES count | Any Task CREATEs-ing a Screen must declare corresponding ScreenTest at planning time | Process |

## Review Queue
| Task ID | Handoff Time | Developer Declaration | Status |
| ------- | ------------ | -------------------- | ------ |
| TASK-004 | 2026-06-29 | CI PASS — ./gradlew test detekt ktlintCheck lint | Done |
| TASK-005 | 2026-06-29 | CI PASS — ./gradlew test detekt ktlintCheck lint | Done |
| TASK-006 | 2026-06-30 | CI PASS — ./gradlew test detekt ktlintCheck lint | Done |
| TASK-007 | 2026-06-30 | CI PASS — ./gradlew test detekt ktlintCheck lint | Done |

## Review Log
| Task ID | Review Time | Result | Issues | Reviewer |
| ------- | ----------- | ------ | ------ | -------- |
| TASK-004 | 2026-06-29 | APPROVED | Non-blocking notes: P7 DEC entry for Class E instance; INV-005 currency process note | Reviewer Agent |
| TASK-005 | 2026-06-29 | APPROVED (Round 2) | R4 fixed: assertIsSelected() on merged semantics node | Reviewer Agent |
| TASK-006 | 2026-06-30 | APPROVED | Non-blocking: File Registry missing TASK-006 Modified entries (fixed) | Reviewer Agent |
| TASK-007 | 2026-06-30 | APPROVED | Non-blocking: File Registry missing TASK-007 Modified entries (fixed) | Reviewer Agent |

## Evidence Budget Log
| Task ID | Context Est. | Over Budget | Action |
| ------- | ------------ | ----------- | ------ |
| TASK-001 | ~30K | No (Foundation Task expanded budget) | None |
| TASK-002 | ~28K | No | None |
| TASK-003 | ~35K | No | None |

## Session Output
| Date | Task | Deliverables |
| ---- | ---- | ------------ |
| 2026-06-29 | TASK-003 | ConvertUnitUseCase, ConverterViewModel (single-update), ConverterScreen, 3 test files, AppNavGraph wired; CI PASS confirmed |
| 2026-06-29 | TASK-004 | EvaluateTableUseCase (A1-ref DFS resolver), TableViewModel (combine pattern, companion helpers), TableScreen (CellGrid+CellEditBar+SavedTablesList), 3 test files, AppNavGraph TableScreen wired; CI PASS confirmed |
| 2026-06-29 | TASK-005 | MainScreen (3-tab BottomNavBar, consumeWindowInsets), MainScreenTest (3 Compose tests), MainActivity wired; material-icons-extended added (DEC-017); TD-002 logged (INV-004 tension from EvaluateAll); Sprint 1 Review: INV-001–005 spot-checked, module DAG unchanged, Fan-In/Out within threshold, TD-001/002 noted |
| 2026-06-29 | TD-002 fix | evaluatedResults map added to LocalState+TableUiState; evaluateAll() no longer overwrites cell.content; CellGrid renders evaluatedResults??cell.content; TableViewModelTest corrected + stale-clear test added; INV-004 enforced |
| 2026-06-29 | TD-001 fix | KSP upgraded 2.2.10-2.0.2→2.3.9 (DEC-018); android.disallowKotlinSourceSets=false removed from gradle.properties; configure-phase warning eliminated |
| 2026-06-29 | Deprecation fix | LocalClipboardManager→LocalClipboard in CalculatorScreen, ConverterScreen, TableScreen; setText(AnnotatedString)→setClipEntry(ClipEntry(ClipData.newPlainText)); zero compiler warnings |
| 2026-06-30 | TASK-006 | HeaderCell composable added; CellGrid gains header row (A–E) + row index column (1–5); column_and_row_headers_are_displayed test added to TableScreenTest |
| 2026-06-30 | TASK-007 | RestoreHistoryEntry(id) action added to CalculatorUiAction; restoreEntry() in CalculatorViewModel reads from uiState.value.history; HistoryItem made clickable; restore test added to CalculatorViewModelTest; TD-003 logged |
