# Trinum — Project AI Session Guide
**Version 1.0 | 2026-06-28**
**Supersedes: android_ai_handbook_v2_2.md (for this project)**
**Supreme authority: Android AI Dev Design Principles v6.1 (overrides this document on conflict)**

> This is the sole operational reference for every AI session on the Trinum project.
> Load this guide + WorkStatus.md + current Task Spec at every session start.
> In REVIEWER MODE: load this guide + Handoff Package only.

---

## §0 — Mode Declaration

Declare your mode at session start before any other action.

| Mode | Trigger | Behavior |
|---|---|---|
| **DEVELOPER** | Normal feature/foundation work | Follow §2–§7 fully |
| **REVIEWER** | Human pastes Handoff Package | Jump to §6 REVIEWER MODE immediately; no code generation |

---

## §1 — Project Decisions (Embedded ADR Quick-Reference)

All decisions are **Accepted** and immutable. Changes require a new ADR preceded by a DEC entry.

### Architecture Decisions

| Domain | Decision | Constraint |
|---|---|---|
| Architecture pattern | **MVVM** | One ViewModel per screen; zero business logic in @Composable |
| UI framework | **Jetpack Compose** | No XML layouts anywhere in codebase |
| DI | **Hilt 2.52** | @HiltViewModel on every ViewModel; @HiltAndroidApp on Application |
| Navigation | **Navigation Compose + bottom nav** | 3 tabs: Calculator / Converter / Table |
| Persistence | **Room 2.7.0** | Calculator history + saved tables only; DAO signatures locked (see §1.3) |
| Network | **NONE** | Zero network calls; no Retrofit; no OkHttp. Violation = critical bug |
| Expression evaluation | **exp4j 0.4.8** (calculator) + **custom cell-ref resolver** (table) | exp4j handles arithmetic/math functions; resolver handles A1-style references |
| Coroutine contract | Repository → `Flow<T>` / `suspend fun` | ViewModel → `StateFlow` via `.stateIn(viewModelScope, WhileSubscribed(5000), initial)` |
| UI event taxonomy | `UiState`(StateFlow) / `UiEffect`(Channel) / `UiAction`(sealed) / `UiIntent`(UseCase param) | One set per feature; declared in Foundation Task |
| Logging | **Timber only** | `Log.*` is prohibited everywhere |
| Testing | 70% unit / 20% integration / 10% UI | Per P11; CI PASS = Done |
| Application ID | `dev.trinum.app` | Package root for all modules |
| Application class | `TrinumApplication` | `@HiltAndroidApp`; Timber.plant() here only |

### Module Structure (ADR-001 derived)

```
:app      → :domain, :data (runtime Hilt), :core:ui
:data     → :domain
:core:ui  → (Android/Compose only — no :domain or :data)
:domain   → (zero Android imports — enforced by ArchUnit)
```

### NFR Thresholds (P0.2 — frozen)

| Metric | Threshold |
|---|---|
| Cold start | < 2s |
| Expression evaluation | < 50ms |
| Unit conversion (incl. UI update) | < 100ms |
| Table formula (≤100 cells) | < 200ms |
| DB query (history load) | < 100ms |
| DB insert | < 50ms |
| Frame drop rate | < 1% |
| Offline | 100% — network calls = critical violation |

### Locked DAO Signatures (P6 — Feature Tasks fill bodies only; never add signatures)

**HistoryEntryDao**
```kotlin
fun observeRecent(): Flow<List<HistoryEntryEntity>>       // LIMIT 50 ORDER BY timestamp DESC
suspend fun insert(entry: HistoryEntryEntity)
suspend fun deleteById(id: Long)
suspend fun clearAll()
```

**SavedTableDao**
```kotlin
fun observeAll(): Flow<List<SavedTableEntity>>            // ORDER BY updated_at DESC
suspend fun getWithCells(tableId: Long): SavedTableWithCells?
suspend fun insertTable(table: SavedTableEntity): Long
suspend fun insertCells(cells: List<TableCellEntity>)
suspend fun deleteCellsForTable(tableId: Long)
suspend fun deleteTable(tableId: Long)
```

---

## §2 — Session Contract

### SESSION START — mandatory checklist (complete before any code)

- [ ] Declared mode: DEVELOPER or REVIEWER
- [ ] Loaded: this AI Session Guide
- [ ] Loaded: WorkStatus.md — read Current Status + Task Progress
- [ ] Loaded: current Task Spec (DEVELOPER mode) or Handoff Package (REVIEWER mode)
- [ ] Identified: current Task ID, status, READS list
- [ ] Read: all files in Task Spec READS list (P13 — evidence before generation)
- [ ] Checked: P18 context budget — total loaded context within budget (see §2.3)
- [ ] Confirmed: no ADR is in Draft status (check WorkStatus DEC log for pending decisions)

### SESSION END — mandatory checklist (complete before outputting Handoff Package)

- [ ] All declared CREATES files exist with correct signatures
- [ ] All declared MODIFIES files updated correctly
- [ ] WorkStatus.md updated: Task Progress, File Registry, DAO Addition Log (if any), DEC (if any), Session Output
- [ ] WorkStatus.md < 150 lines — if over, notify human to archive before proceeding
- [ ] All P11 self-checks passed (tests written, CI-ready)
- [ ] Knowledge Alert output if triggered (§5, P17)
- [ ] Handoff Package composed (§6.3) — do NOT mark Task Done yet
- [ ] Notified human: "Session complete. Activate Reviewer Agent with Handoff Package below."

### P18 — Context Budget (Trinum baseline)

| Context item | Est. tokens | Notes |
|---|---|---|
| This Session Guide | ~8K | Mandatory |
| WorkStatus.md | ≤3K | Mandatory; enforce 150-line limit |
| Task Spec | ~2K | Mandatory |
| READS files (feature tasks) | ≤8K target | Read only what task declares |
| **Total mandatory** | **~21K** | ~10% of 200K context — within budget |

**If approaching limit:** stop, list what you still need to read, ask human how to proceed. Never silently skip evidence.

---

## §3 — Evidence System

### P13.1 — Evidence Priority Hierarchy (conflict resolution order)

1. **ADR files** — immutable architecture law; overrides everything else
2. **Existing source code** — real current state; read before assuming
3. **This AI Session Guide** — project context and constraints
4. **WorkStatus.md** — project state, DEC log, decisions made mid-project
5. **libs.versions.toml** — dependency version truth

When two sources conflict: higher number loses. If ADR conflicts with existing code → code must be fixed to comply with ADR.

### P13 — Evidence Before Generation

**Never write code based on remembered or assumed API signatures.**

Before generating code that calls any external API (Room, Hilt, Navigation, exp4j, Android SDK):
- Read the real source file or interface definition
- Read the relevant ADR
- Read existing usage in the codebase (if any)

Class A Failure Mode: "I remember the signature" → write code → signature wrong → compile error.
Prevention: read first, always.

### P14 — Context Freshness Gate

Before editing any file:
- Re-read the current file content
- Do not edit from memory of a file seen earlier in the session
- Re-read especially after any other file has been edited (edits can shift line numbers and state)

### P9.3 — Failure Mode Classes (seed — add new classes as discovered)

| Class | Mode | Prevention |
|---|---|---|
| A | Assumption instead of reading — signature inferred from memory | Read real source before coding (P13) |
| B | Silent default overwrite — setting a field without surfacing the change | All value changes explicit and logged |
| C | Unowned field — multiple writers to the same state | Declare ownership in ADR/DEC first |
| D | Platform side-effect blindness — calling platform API without identifying side effects | Identify side effects before platform calls |

---

## §4 — Principles & Self-Checks

### Architecture Layer

**P0.1 — Naming Conventions**
All names must match the convention table (§1, Bootstrap). No abbreviations not in ProjectGlossary.md.
- [ ] Does this class/file name exactly match the convention pattern?
- [ ] Is this a new term? If so, register it in docs/ProjectGlossary.md.

**P0.2 — NFR Compliance**
Any code that could affect a measured NFR threshold must be justified.
- [ ] Could this code path affect cold start, frame rate, or DB latency?
- [ ] If yes, is the implementation within the thresholds in §1?

**P0.3 — Dependency Registry**
Feature Tasks: read-only access to libs.versions.toml. No version modifications.
- [ ] Am I adding a new dependency? → STOP. Log DEC. Create dedicated Upgrade Task.
- [ ] Am I using a library version not in libs.versions.toml? → STOP.

**P0.5 — ADR Lifecycle**
Accepted ADRs are immutable. Any architectural change → new ADR + DEC first.
- [ ] Does this implementation deviate from any ADR decision in §1?
- [ ] If deviation is needed: STOP. Log DEC. Create new ADR. Do not proceed without Accepted status.

**P1 — MVVM**
- One ViewModel per screen. ViewModel owns all state.
- @Composable functions are pure UI: observe state, emit UiActions, render nothing else.
- No `suspend fun` calls in @Composable scope. No direct repository access from UI.
- [ ] Is any business logic inside a @Composable? → Move to ViewModel or UseCase.
- [ ] Does any ViewModel have more than one screen as consumer? → Split.
- [ ] Is ViewModel injected via `hiltViewModel()`? (Not manually constructed.)

**P2 — Repository as Data Boundary**
- Repository is the ONLY path to any data source (Room, in-memory).
- Repository exposes: `Flow<T>` for reactive reads, `suspend fun` for writes.
- Repository does NOT create coroutine scopes. It does NOT collect its own Flows.
- DAOs are called ONLY from within RepositoryImpl. Never from ViewModel or UseCase.
- [ ] Is any DAO being called outside a RepositoryImpl? → Violation.
- [ ] Does Repository create `CoroutineScope(...)` anywhere? → Violation.
- [ ] Does Repository return `LiveData`? → Violation; must be `Flow`.

**P2.1 — Schema Evolution (simplified for Trinum)**
Trinum has minimal schema (2 entities + 1 relation). Any schema change:
1. Log a DEC entry (what changed, why)
2. Add a Room migration (never use `fallbackToDestructiveMigration` in production build)
3. Update `@Entity` and notify Foundation Task ownership in WorkStatus
- [ ] Am I modifying any @Entity class? → Log DEC first.
- [ ] Does any Room migration handle all existing versions gracefully?

**P3 — Layer Boundaries**
- `:domain` module: zero `android.*` or `androidx.*` imports (enforced by ArchUnit test in Foundation Task)
- `:app` feature screens: access data only via `:domain` interfaces injected by Hilt — never reference `*RepositoryImpl` or DAO directly
- `:core:ui`: no imports from `:domain` or `:data`
- [ ] Does `:domain` have any Android import? → Critical violation.
- [ ] Does any feature Screen reference `*RepositoryImpl` or `*Dao`? → Critical violation.
- [ ] Does `:core:ui` import from `:domain`? → Violation.

**P3.1 — Coroutine Contract**
```
Repository  → exposes Flow<T> / suspend fun  (never collects internally)
UseCase     → may return Flow<T> or suspend fun  (no scope creation)
ViewModel   → .stateIn(viewModelScope, WhileSubscribed(5000), InitialState)
              one StateFlow<UiState> exposed to UI
UI          → collectAsStateWithLifecycle() only
```
- [ ] Is any Flow collected inside Repository or UseCase? → Violation.
- [ ] Does ViewModel expose raw `Flow` instead of `StateFlow`? → Violation.
- [ ] Is `viewModelScope.launch` used for anything other than one-time side effects? → Review.

**P3.3 — UI Event Taxonomy**
```
UiState     StateFlow<FeatureUiState>   — current screen state; entire screen derived from this
UiEffect    Channel<FeatureUiEffect>    — one-time events: navigation, snackbar, clipboard
UiAction    sealed class               — user inputs into ViewModel: button taps, text changes
UiIntent    data class / value         — parameter to UseCase
```
- [ ] Does ViewModel expose multiple `StateFlow`s instead of one `UiState`? → Consolidate.
- [ ] Are navigation events going through `UiState` instead of `UiEffect`? → Move to Channel.
- [ ] Is UiEffect consumed only once (Channel, not StateFlow)? → Verify.

**P4 — Module DAG**
- No circular dependencies at any level.
- Fan-In ≤ 10; Fan-Out ≤ 10 per module (thresholds in §7).
- New module dependency: must be declared in a DEC before adding.
- [ ] Does this change add a dependency between modules? → Check §1 DAG; log DEC if new edge.
- [ ] Could this create a cycle? → Verify with `./gradlew :module:dependencies`.

---

### Task Execution

**P6 — Foundation Task Contract**
Foundation Task creates ALL interfaces, signatures, and contracts. Feature Tasks fill bodies only.
- [ ] Am I adding a new DAO method in a Feature Task? → STOP. Log DEC. Create micro-task.
- [ ] Am I adding a new Repository method in a Feature Task? → STOP. Log DEC. Create micro-task.
- [ ] Am I modifying `proguard-rules.pro` in a Feature Task? → STOP. Log DEC. Create micro-task.
- [ ] Am I modifying `libs.versions.toml` in a Feature Task? → STOP. See P0.3.

**P7 — Task Sizing (Feature Tasks)**
- CREATES: 3–5 new files maximum
- READS: ≤ 8 files
- If CREATES > 5 before coding starts: split the task. Do not proceed.
- [ ] How many files will I CREATES? If > 5 → split now, before any code.
- [ ] How many files in READS? If > 8 → trim to essential evidence.
- (Foundation Task exemption: larger CREATES list allowed per §9 of Bootstrap.)

**P8 — WorkStatus Maintenance**
WorkStatus.md is the project's only persistent memory across sessions. Keep it accurate.
Updated sections every session: Task Progress, File Registry, Session Output, DEC (if applicable).
- [ ] Have I registered every new file created in the File Registry?
- [ ] Have I logged every architectural/dependency decision made in the DEC?
- [ ] Is WorkStatus.md current as of this session?

**P9 — WorkStatus Size Gate**
WorkStatus.md must stay < 150 lines.
- [ ] Is WorkStatus.md approaching 150 lines? → Notify human to archive Done tasks before continuing.
- [ ] If > 150 lines: STOP all code work. Output warning. Do not proceed until human archives.

---

### Quality Gates

**P11 — Test Requirements (per Feature Task DoD)**
- ViewModel → all UiAction handlers tested via Turbine (StateFlow emissions asserted)
- UseCase → success path + at least one error/edge case (invalid expression, conversion of zero, empty table)
- New DAO method → in-memory Room test
- Screen → at minimum: result displays after input action (Compose semantics test)
- [ ] Are all ViewModel state transitions covered by Turbine tests?
- [ ] Are error paths tested (not just happy path)?

**P11.2 — CI Authority**
Local compile pass ≠ Done. CI PASS = Done.
Required CI: `./gradlew test detekt ktlintCheck lint`
When a Task is declared complete in a session: output "CI gate required before Done status."
- [ ] Have I confirmed CI passes (or instructed human to run CI)?
- [ ] Is Done status withheld until after CI + Reviewer approval?

**P11.3 — Static Analysis Gates**
- detekt `LongMethod`: 30 lines max
- detekt `CyclomaticComplexMethod`: 10 max
- Lint Error = Build Failure (never suppress without DEC)
- [ ] Does any function exceed 30 lines? → Extract.
- [ ] Cyclomatic complexity > 10 in expression evaluator or cell resolver? → Decompose.
- [ ] Any lint suppression added? → Log DEC with justification.

**P12 — ProGuard Ownership**
`app/src/main/keepRules/rules.keep` is owned by Foundation Task. Feature Tasks must not modify it.
(AGP 9.x uses `src/main/keepRules/` auto-discovery; `proguard-rules.pro` is NOT used — DEC-003)
If a new dependency needs ProGuard rules → log DEC → create dedicated micro-task.
- [ ] Am I adding a dependency that uses reflection? → Check if proguard rules needed. Log DEC.
- [ ] Am I modifying `rules.keep` in a Feature Task? → Stop. Create micro-task.

---

### Knowledge

**P16 — Invariant Registry**
When a new domain truth is discovered during implementation, add it to `docs/invariants.md`.
Format: `| INV-NNN | Scope | Statement | Source | Sprint |`
Existing invariants: INV-001 through INV-004 (see Bootstrap / docs/invariants.md).
- [ ] Did I discover a domain constraint that should always hold? → Add to invariants.md.

**P17 — Knowledge Alert**
Output a Knowledge Alert at session end if any of these occurred:
- An API behaved unexpectedly (e.g., exp4j edge case)
- A dependency version incompatibility was found
- A design decision proved more complex than ADR assumed
- A new domain invariant was discovered
- A Failure Mode Class not in the seed list was encountered

```
[KNOWLEDGE ALERT]
Type:                [ASSUMPTION_BUST | NEW_INVARIANT | VERSION_CONFLICT | API_SURPRISE | PROCESS_GAP]
Trigger:             [What happened that surfaced this]
Discovery:           [What was learned — be specific]
Action:              [Suggested next step: log DEC / update ADR / create TD / update invariants.md]
Decision Confidence: [HIGH | MEDIUM | LOW]
Owner:               [Developer | AI | Both]
```

---

## §5 — Format Specifications

### WorkStatus.md (complete format — maintain exactly)

```markdown
# WorkStatus
Last updated: [ISO date] | Current Sprint: [N]

## Current Status
Next Task: [TASK-ID] — [title]
Blockers: [none | description]

## Task Progress
| ID | Description | Status | Created | Updated |
|----|-------------|--------|---------|---------|
| TASK-001 | Foundation Task | [Pending|InProgress|InReview|Done] | date | date |

## File Registry
| Path | Owning Task | Status | Date |
|------|-------------|--------|------|

## DAO Addition Log
| Method Signature | DAO | Source Task | Date |
|-----------------|-----|-------------|------|

## Decision Log (DEC)
| DEC-ID | Date | Change | Reason | Affected Files | Status |
|--------|------|--------|--------|----------------|--------|

## Technical Debt
| TD-ID | Type | Description | Impact | Owner Task | Target Sprint | Status |
|-------|------|-------------|--------|------------|---------------|--------|

## Verified Assumptions
| Assumption | Status | Source | Verified Sprint |
|------------|--------|--------|-----------------|

## Failure Mode Classes
| Class | Failure Mode | Prevention Rule | Tooling Mitigated |
|-------|--------------|-----------------|-------------------|

## Review Queue
| Task ID | Handoff Time | Developer Declaration | Status |
|---------|-------------|----------------------|--------|

## Review Log
| Task ID | Review Time | Result | Issues | Reviewer |
|---------|-------------|--------|--------|----------|

## Evidence Budget Log
| Task ID | Context Est. | Over Budget | Action |
|---------|-------------|-------------|--------|

## Session Output
| Date | Task | Deliverables |
|------|------|--------------|
```

**Maintenance rules:** Update BEFORE session ends. Keep < 150 lines. Move Done tasks older than one Sprint to `WorkStatus_Archive.md` at each Sprint start.

---

### Task Spec Format

```markdown
# [TASK-ID]: [Task Title]
Sprint: [N]
Status: Pending | In Progress | In Review | Blocked | Done

## Objective
[One sentence: what this Task accomplishes when Done]

## READS
- path/to/file.kt  — [why this file is read]

## CREATES
- path/to/NewFile.kt  — [what it contains]

## MODIFIES
- path/to/ExistingFile.kt  — [what changes and why]

## DELETES
- none | path/to/file.kt

## Evidence
- ADR-[NNN]: [why this ADR applies]
- DEC-[NNN]: [if applicable]

## Definition of Done
- [ ] All CREATES files exist
- [ ] Compile gate passes
- [ ] Tests written per P11
- [ ] WorkStatus updated
- [ ] Knowledge Alert output (P17)
- [ ] Handoff Package submitted for Reviewer
- [ ] Reviewer: APPROVED
- [ ] CI PASS
```

---

### ADR File Format (`/docs/adr/ADR-[NNN]-[slug].md`)

```markdown
# ADR-[NNN]: [Title]
Status: Draft | Accepted | Superseded | Archived
Date: [ISO date]
Supersedes: [ADR-XXX | none]
Superseded-by: [ADR-XXX | none]

## Decision
[One-sentence statement of the decision.]

## Context
[Why this decision was needed.]

## Consequences
[What becomes true as a result.]

## Rationale
[Why this option over alternatives.]
```

---

### DEC Entry Format (in WorkStatus Decision Log)

```
DEC-ID:         DEC-[NNN]
Date:           [ISO date]
Change:         [What changed or was decided]
Reason:         [Why]
Affected Files: [comma-separated full paths, or "none"]
Status:         Open | Closed
Closed Date:    [ISO date | blank]
```

---

## §6 — Reviewer Agent Protocol

### Activation

Reviewer Agent is activated by the human after every Developer session that declares a Task ready for review.
The human opens a **new AI session** (separate from the Developer session), provides:
1. This AI Session Guide (§0–§7)
2. The Handoff Package from the Developer session

At session start, Reviewer Agent declares: **MODE: REVIEWER**

### REVIEWER MODE Rules

- Do NOT generate any code
- Do NOT modify any files
- Do NOT access the codebase beyond what is in the Handoff Package
- Assess the Handoff Package against R1–R7 gates
- Output a Review Report (format below)
- Issue exactly one verdict: **APPROVED** or **FAIL**

### R1–R7 Gate Definitions

| Gate | Criterion | FAIL condition |
|---|---|---|
| **R1 File Existence** | All CREATES files declared in Task Spec exist | Any CREATES file missing |
| **R2 Compile** | Developer declares compile passes; no obvious compile errors in code excerpts | Declared compile failure, or obvious syntax errors in provided code |
| **R3 Architecture** | All ADR decisions in §1 followed; no layer violations (P1, P2, P3, P4) | DAO called outside Repository; Android import in :domain; UiState not StateFlow; etc. |
| **R4 Test Coverage** | Tests written for ViewModel + UseCase per P11 requirements | Missing ViewModel tests; no error path tested; new DAO method has no in-memory test |
| **R5 Evidence** | No code appears to be generated from assumed/remembered signatures | Signature mismatch from locked DAO table §1.3; obvious assumption-based code patterns |
| **R6 Static Analysis** | No obvious detekt/ktlint/lint violations visible in code | Functions > 30 lines; suppression without DEC; naming convention violations |
| **R7 WorkStatus** | WorkStatus updated correctly; all new files registered; Knowledge Alert present if triggered | Missing file in File Registry; DEC not logged for architectural decision; no Knowledge Alert when trigger conditions met |

### Handoff Package Format (Developer Agent output)

```markdown
# Handoff Package — [TASK-ID]
Date: [ISO datetime]
Developer declares: session end checklist complete.

## Summary
[2-3 sentences: what was built, what decisions were made]

## CREATES
- path/to/file.kt — [brief: what it does]

## MODIFIES
- path/to/file.kt — [brief: what changed]

## DEC entries this session
[DEC entries or "none"]

## Knowledge Alerts
[Knowledge Alert(s) or "none"]

## Self-check declaration
- [x] P1 MVVM self-checks passed
- [x] P2 Repository boundary self-checks passed
- [x] P3 Layer boundary self-checks passed
- [x] P3.1 Coroutine contract self-checks passed
- [x] P3.3 UI event taxonomy self-checks passed
- [x] P6 Foundation contract self-checks passed (no new DAO/Repo signatures in Feature Tasks)
- [x] P7 Task sizing: CREATES = [N] files (≤5 for Feature Tasks)
- [x] P11 Tests written for all new ViewModel/UseCase/DAO
- [x] P17 Knowledge Alert: [output | not triggered]
- [x] WorkStatus.md updated and < 150 lines

## WorkStatus excerpt
[Paste current Task Progress + File Registry sections]

---
Ready for Reviewer Agent.
```

### Review Report Format (Reviewer Agent output)

```markdown
# Review Report — [TASK-ID]
Date: [ISO datetime]
Reviewer: Independent Reviewer Agent

## Gate Assessment
| Gate | Status | Notes |
|---|---|---|
| R1 File Existence | PASS / FAIL | |
| R2 Compile | PASS / FAIL | |
| R3 Architecture | PASS / FAIL | |
| R4 Test Coverage | PASS / FAIL | |
| R5 Evidence | PASS / FAIL | |
| R6 Static Analysis | PASS / FAIL | |
| R7 WorkStatus | PASS / FAIL | |

## Issues Found
[Specific issues with file references, or "none"]

## Verdict
**APPROVED** | **FAIL**

## Conditions for Re-review (if FAIL)
[Specific changes required before re-submitting Handoff Package]
```

### Dual Sign-Off Process

```
Developer Agent completes Task
  → outputs Handoff Package
  → notifies human
Human opens NEW session
  → activates Reviewer Agent
  → pastes Handoff Package
Reviewer Agent outputs Review Report + verdict
If APPROVED:
  Human tells Developer Agent (new session): mark TASK-NNN Done in WorkStatus
  Human runs CI: ./gradlew test detekt ktlintCheck lint
  CI PASS → Task is Done
If FAIL:
  Developer Agent (new session) fixes issues
  Developer Agent outputs new Handoff Package
  Repeat review
```

---

## §7 — Architecture Fitness Baselines (P15)

Frozen at project start. Verified at every Sprint Review by human + AI.

| Metric | Trinum Threshold |
|---|---|
| Module Fan-In | ≤ 10 |
| Module Fan-Out | ≤ 10 |
| StateFlow chain depth (ViewModel → Composable) | ≤ 3 |
| Composable nesting depth | ≤ 5 |
| DAOs per RepositoryImpl | = 1 |
| Package depth from module root | ≤ 4 |
| Kotlin files per module | ≤ 30 |
| Functions per ViewModel | ≤ 15 |

### Sprint Review Fitness Checklist

At each Sprint Review:
- [ ] `./gradlew :domain:test` — ArchUnit zero-Android-import test passes
- [ ] Module dependency graph unchanged or DEC logged for new edges
- [ ] No module exceeds Fan-In / Fan-Out threshold
- [ ] WorkStatus Technical Debt reviewed: any ARCH-DECAY items addressed?
- [ ] Invariants in docs/invariants.md still hold (spot-check against new code)
- [ ] All ADRs still in Accepted state or supersession documented
