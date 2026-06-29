AI Independent Reviewer Agent Contract v1.2 — Android AI-Assisted Development Design Principles v6.1+

| **AI Independent Reviewer Agent Contract** Evidence Budgeting · Dual-Agent Architecture · Quality Assurance Protocol **Version v1.2** Date 2026-06 Compatible with Android AI Dev Design Principles v6.1 + AI Session Guide v1.0+ *This document is an independent governance supplement for teams with dual-agent capability. Use alongside the main framework (Design Principles). Does not supersede any principle in the main framework.* |
| --- |

| **v1.2 Changes** |
| --- |
| §3.5 ★ — Added Review Report File Persistence: Reviewer Agent must write `docs/reviews/REVIEW-TASK-NNN.md` after every review round. File format is concise (gate table + verdict + issues only; no prose padding). Re-reviews append a new dated section rather than overwriting. Chapter 7 Reviewer checklist updated with step 9. |

| **v1.1 Changes** |
| --- |
| R6 architecture decay detection extended to cover all 7 P15 architecture budget thresholds (previously only Fan-In/Fan-Out ≤10). Compatible references updated to Design Principles v6.0 + AI Execution Handbook v2.0. |

| **Documents Loaded by Reviewer Agent** |
| --- |
| **Must Read** | Android AI Dev Design Principles v6.1 (rule authority) |
| **Must Read** | AI Session Guide v1.0+ (principle text and self-checks) |
| **Must Read** | Task Spec file for the Task under review (READS / CREATES / MODIFIES / DELETES / DoD) |
| **Must Read** | All ADRs listed in Task Evidence field (Accepted status) |
| **Must Read** | WorkStatus.md (current project state, TD table, Failure Mode Classes) |
| **Must Read** | docs/invariants.md (if exists) |
| **Must Read** | docs/ProjectGlossary.md (if exists) |
| **Must NOT Read** | Developer Agent session history or context (maintain independence) |
| **Must NOT Read** | KnowledgeStatus.md / WorkStatus_Archive.md (out of review scope) |
| **Must NOT Read** | Bootstrap Guide (project init only; not a code review basis) |

**Evidence Authority Order (P13.1)**
Design Principles → ADR (Accepted) / invariants.md → Product Contract → DEC → WorkStatus → Code
If Reviewer finds two sources in conflict and the order cannot resolve it: output CONFLICT and wait for developer adjudication. Do not infer a resolution.

---

| **Chapter 1: Positioning & Scope** |
| --- |

| **► Positioning** The main framework (Design Principles v6.1) is the "constitution" of every AI development session — it defines what may and must not be done. This document is the "judicial procedure" — it defines how to independently verify whether the constitution has been followed. They are kept separate because the same agent cannot objectively review code it generated (self-referential verification risk). This document is mandatory only for teams with dual-agent capability; single-agent teams may use it as a self-check protocol. |
| --- |

| **Problem Statement** | The main framework has established a complete evidence governance system (P13 + P13.1 + P14) and quality gates (P11 + P11.1–P11.3). However, the enforcer and the enforced are the same agent. This creates a structural problem: when an AI self-verifies code it generated, it tends to confirm "code matches my understanding" rather than "code matches the project specification." Writing tests that confirm its own logic, selectively ignoring architectural violations, treating its inferences as ground truth — these are known failure modes of single-agent self-verification. |
| --- | --- |
| **Prerequisites** | **This protocol requires the following infrastructure:** Two fully independent AI sessions (no shared context, no shared memory). Changes produced by Developer Agent can be passed to Reviewer Agent as diffs or file snapshots. Reviewer Agent's review result can be written back to WorkStatus to trigger Task status changes. A version control system (Git) as the only shared state carrier between the two agents. If any of the above cannot be fully satisfied, refer to Chapter 5 Degraded Operation Mode. |

---

| **Chapter 2: Dual-Agent Architecture Contract** |
| --- |

| **2.1 Role Definition & Ownership Boundary** |
| --- |

| | **Attribute** | **Developer Agent** | **Reviewer Agent** |
| --- | --- | --- | --- |
| **Context** | Session context source | Main framework + WorkStatus + current Task Spec + READS files | Fresh blank session. Independently reads current disk state. Inherits no context from Developer Agent. |
| **Ownership** | File operation permissions | CREATES / MODIFIES / DELETES (full write access) | Read-only. Single write target: the review log section of WorkStatus + `docs/reviews/REVIEW-TASK-NNN.md`. |
| **Responsibility** | Core task | Generate specification-compliant code per main framework principles; pass local compile gate; update WorkStatus. | Independently verify code truly satisfies the evidence chain (ADR / Product Contract / Task Spec) — not merely "runs correctly." |
| **Prohibited** | Strictly forbidden actions | Must not self-review generated code and then directly mark Task as Done. | Must not modify any code file. Must not inherit Developer Agent session context. Must not output PASS without completing all review gates. |

| **2.2 Handoff Protocol — Developer → Reviewer Transition** |
| --- |

| **After completing a Task, Developer Agent must output a standardized Handoff Package before handoff:** ## Handoff Package — Task [ID] ### Change Summary - CREATES: [file list] - MODIFIES: [file list + change description] - DELETES: [file list] ### Evidence Declaration (P13) - Evidence ADR: [referenced ADR numbers + status] - Evidence READS: [referenced file path list] - Evidence DEC: [referenced DEC numbers if any] ### Local Gate Status (P11) - Compile gate: PASS / FAIL - Unit tests: PASS / FAIL (pass rate: X/Y) - Detekt: PASS / FAIL - Ktlint: PASS / FAIL ### Developer Agent Self-Disclosure - Known limitations or uncertainties: [N/A if none] - Suggested Reviewer focus areas: [N/A if none] **Handoff Package is written to the Review Queue section of WorkStatus. Must not be verbally communicated. Reviewer Agent reads the Handoff Package from WorkStatus as the sole entry point to begin review.** |
| --- |

| **2.3 Dual Sign-Off Protocol — Task Completion Requires Both Agent Approvals** |
| --- |

| **Phase** | **Executor** | **Completion Condition** | **WorkStatus Status** |
| --- | --- | --- | --- |
| **①** | Developer Agent completes code generation + local gates | All CREATES files exist + compile/test/Detekt/Ktlint pass + Handoff Package written to WorkStatus | **In Review** |
| **②** | Reviewer Agent completes independent review | All review gates pass + Review Report written to WorkStatus + `docs/reviews/REVIEW-TASK-NNN.md` created | **Done** |
| **②'** | Reviewer Agent finds issues | Review Report written to WorkStatus + `docs/reviews/REVIEW-TASK-NNN.md` created + issues categorized (see Chapter 3) | **Blocked** |

**Task status may only change from In Review to Done after Reviewer Agent outputs PASS and writes the Review Report. Developer Agent must not perform this status change independently.**

---

| **Chapter 3: Reviewer Agent Review Protocol** |
| --- |

| **Core Principle: Black-Box Independence** Reviewer Agent must assume it knows nothing about how this Task was executed. The only information sources are: current disk state (code files) + project specification files (ADRs / Product Contract / Task Spec) + Handoff Package in WorkStatus. Must not skip any review step on the grounds that "Developer Agent already checked this." Must not read Developer Agent session history or reasoning chain — conclusions come from the review steps, not from Developer Agent's explanations. |
| --- |

| **3.1 Reviewer Agent Session Start Checklist** |
| --- |

| **Confirm before starting (execute in order; no skipping):** **① Evidence Freshness Check (P14)** Read WorkStatus; confirm Task current status is In Review. Check whether any Git operations or ADR status changes occurred since last ADR read — if so, execute P6.1 re-alignment first. **② Independent Specification Reading (P13)** Independently read from disk: main framework principles document + current Task Spec (including READS / CREATES / MODIFIES). Independently read all referenced ADRs; confirm each ADR status is Accepted (P0.5). Read Product Contract (User Flow); confirm Task scope does not exceed Flow nodes. **③ Evidence Conflict Detection (P13.1)** Independently execute evidence priority check: are there contradictions between ADR vs DEC vs WorkStatus vs current code? If conflict found: flag CONFLICT in Review Report; wait for human decision; do not adjudicate independently. **④ Read Handoff Package** Read the Handoff Package submitted by Developer Agent from the WorkStatus Review Queue. Record the declared CREATES / MODIFIES / DELETES list as the boundary of the review scope. |
| --- |

| **3.2 Review Checklist — The Seven Review Gates** |
| --- |

| **Gate** | **Check Item** | **Review Content & Pass Criteria** |
| --- | --- | --- |
| **R1** | **File Contract Compliance** | Verify all files in the CREATES list exist on disk; files in MODIFIES list have been modified; files in DELETES list no longer exist. Criteria: file existence 100% consistent with Handoff Package declaration. Any discrepancy = FAIL. |
| **R2** | **Evidence Chain Verification** | Independently execute P13: trace back from each core class in CREATES to confirm its source ADR and READS files exist and are in Accepted status. Key question: do the interface signatures in the code exactly match definitions in the READS files? Any "inferred generation" sign (signature mismatch, field name inconsistency) = FAIL. |
| **R3** | **Layer Boundary Compliance** | Inspect import statements in CREATES files: does domain/ layer have Android imports? Does ui/ layer directly reference data/ layer? Check whether Repository implementations in domain/ layer have internal collect (P3.1 violation). Criteria: any P3 / P3.1 / P3.2 / P3.3 violation = FAIL. |
| **R4** | **Shared Asset Integrity** | Check whether MODIFIES includes DAO / Repository interface / Retrofit interface: if so, there must be a corresponding DEC entry (P2 / P8). Check whether DTOs and @Entity have @Serializable / @Keep (P5.2). Criteria: shared asset change without DEC, or DTO/@Entity missing annotation = FAIL. |
| **R5** | **Test Adequacy** | Check whether each ViewModel in CREATES has a corresponding unit test file (P11.1). Key question: do the tests exercise real business logic, or merely call functions and assert "not null"? Fake tests that confirm the agent's own logic are the primary failure mode to identify. Criteria: ViewModel without tests = FAIL. Test coverage insufficient (missing success path + error path) = WARNING. |
| **R6** | **Architecture Decay Detection** | Execute P15 check: do any classes in CREATES match high-risk naming patterns (Helper/Manager/Wrapper/Coordinator)? If so, determine whether they have independent business logic. Check whether this change violates any of the 7 P15 architecture budget thresholds: ① Fan-In > 10 ② Fan-Out > 10 ③ StateFlow chain depth > 3 ④ Composable nesting depth > 5 ⑤ DAOs per Repository implementation > 1 ⑥ Package depth from module root > 4 ⑦ Kotlin files per feature module > 30. Criteria: forwarding layer without independent business logic = FAIL. Any architecture budget threshold exceeded = FAIL (must log ARCH-DECAY DEC). |
| **R7** | **Code Reuse Assessment** | Check Composable components in CREATES: is this the third or more structurally similar component in the project (P9.2)? If so, verify whether Developer Agent triggered a DEC reuse evaluation. Creating a third similar component without triggering a DEC = FAIL. |

| **3.3 Review Outcome Classification & Handling** |
| --- |

| **Outcome** | **Trigger Condition** | **Handling Procedure** |
| --- | --- | --- |
| **PASS ✓** | R1–R7 all pass | Write Review Report to WorkStatus + write `docs/reviews/REVIEW-TASK-NNN.md` → update Task status to Done |
| **PASS WITH WARNING ⚠** | WARNING-level issues only (no FAIL) | Write Review Report; WARNING items automatically registered as WorkStatus TD with assigned TD-ID. Update Task status to Done; TD items remain Pending and must be resolved within the target Sprint. Write `docs/reviews/REVIEW-TASK-NNN.md`. |
| **BLOCKED ✕** | Any R1–R7 FAIL | Write Review Report listing all FAIL items + specific evidence (filename, line number, violated principle number). Update Task status to Blocked. Write `docs/reviews/REVIEW-TASK-NNN.md`. Developer Agent prioritizes fixing FAIL items in the next session; must not continue other feature development. |
| **CONFLICT ⊗** | Evidence source conflict found (P13.1) | Stop review. Write Conflict Report explicitly identifying the conflicting sources (e.g., ADR-003 and DEC-012 contradict each other). Update Task status to Blocked. Write `docs/reviews/REVIEW-TASK-NNN.md`. Human decision required before proceeding; Reviewer Agent must not adjudicate independently. |

| **3.4 Review Report Format — WorkStatus Entry** |
| --- |

| ## Review Report — Task [ID]  [date] ### Verdict PASS / PASS WITH WARNING / BLOCKED / CONFLICT ### Review Context - Reviewer context source: fresh session (no Developer Agent context inherited) - Referenced ADRs: [ADR-XXX (Accepted), ADR-YYY (Accepted)] - Evidence freshness: confirmed (no Git operations / ADR changes triggered re-read) ### Gate Results R1 File Contract Compliance:    PASS / FAIL R2 Evidence Chain Verification:    PASS / FAIL R3 Layer Boundary Compliance:    PASS / FAIL R4 Shared Asset Integrity:         PASS / FAIL R5 Test Adequacy:                  PASS / WARNING / FAIL R6 Architecture Decay Detection:  PASS / FAIL R7 Code Reuse Assessment:         PASS / FAIL ### WARNING Items (auto-registered as TD) TD-XXX │ [description] │ Impact: Low │ Target Sprint: [N] ### FAIL Items (if any) FAIL-001 │ R2 │ HomeViewModel.kt:42 │ interface signature does not match ExercisePlanRepository.kt │ P13 ### Reviewer Agent Sign-Off Reviewed-by: Reviewer Agent (independent session) Timestamp: [ISO 8601] |
| --- |

| **3.5 ★ Review Report File Persistence** |
| --- |

| **Rule** | After writing the Review Report entry to WorkStatus, Reviewer Agent must create or update: `docs/reviews/REVIEW-TASK-[NNN].md` |
| --- | --- |
| **File format** | Concise — gate table (7 rows) + verdict + issues list only. No prose padding. See standard format below. |
| **Re-review rule** | Do not overwrite. Append a new `## Round N — [date]` section for each review round. The final verdict of the file reflects the most recent round. |
| **Ownership** | Owned by the review process; not owned by any feature Task. Register in WorkStatus File Registry under the reviewed Task ID (e.g., Owning Task = TASK-002, Status = Reviewed). |
| **Directory creation** | If `docs/reviews/` does not exist, Reviewer Agent creates it as part of the first review. |
| **File format** | `# REVIEW-TASK-[NNN]` `**Final Verdict:** [APPROVED \| BLOCKED \| APPROVED WITH WARNING]` `` `## Round N — [date]` `\| Gate \| Result \| Note \|` `\|---\|---\|---\|` `\| R1–R7 rows \|` `` `**Verdict:** [APPROVED \| BLOCKED]` `` `**Issues:** (omit section if none)` `- [Gate]: [one-line description]` `` `**Process notes:** (omit section if none)` `- [non-blocking observations]` |
| **Scope constraint** | Reviewer Agent writes only this file and the WorkStatus review log. No other files may be created or modified. |

---

| **Chapter 4: Evidence Budgeting — Context Quality Control** |
| --- |

| **Why Evidence Budget belongs in this document rather than the main framework** The main framework's P7 uses file count (READS ≤ 8) as a coarse but stable proxy for context control. Token budgeting is a more precise control mechanism, but depends on a specific model's tokenizer and changes across model iterations — making it unsuitable for a main framework targeting all projects. This document targets teams with a specific toolchain — teams that have typically locked a model version, making token budgets configurable parameters rather than hard constants. Additionally, Evidence Budgeting is naturally aligned with the Reviewer Agent's "black-box independence" principle: the Reviewer's context must be controlled, minimal, and precisely referenced. |
| --- |

| **4.1 Evidence Budget Principle** |
| --- |

| **Core concept** | The governance target is not file count but "effective evidence quality." A 2000-line legacy implementation file may provide far less effective evidence than a 100-line interface definition. **The Evidence Budget goal is: ensure every token in the AI's context window when generating code is effective evidence, not noise.** |
| --- | --- |
| **Budget tiers** | **Task Type — Recommended Token Limit — Notes** Standard Feature Task — **≤ 8,000 tokens** — Configurable; teams adjust based on model used. This is the reference baseline. Foundation Task — **≤ 15,000 tokens** — Foundation Tasks naturally have larger evidence volume; separate higher limit. Reviewer Agent — **≤ 12,000 tokens** — Reviewer must read specification files + review code; slightly higher than standard Feature Task. |

| **4.2 Over-Budget Handling: Evidence Trimming** |
| --- |

| **When estimated token volume of READS files exceeds budget, trim in priority order — no skipping levels:** **Trim Level 1 (highest priority): Remove non-essential READS files** Re-examine the READS list; remove implementation files where "the information can be derived from ADRs or interface signatures." Principle: if the file contains no information the AI cannot obtain from other smaller evidence sources, it does not need to be read. Note: after removal, the Task READS list must be updated and the reason recorded. **Trim Level 2: Interface Stubbing** Use only when Level 1 is insufficient to meet the budget. Extract interface skeletons from oversized files, with these rules: Retain: class declarations, interface signatures (method name + parameter types + return types), core data class fields. Remove: function body implementations (replace with // Stubbed), comments, log statements. Prohibited: any modification or simplification of interface signatures themselves — signatures are evidence, not noise. // Stubbing example interface ExercisePlanRepository {     suspend fun getPlan(id: String): Result<Plan>  // full signature retained     fun observePlans(): Flow<List<Plan>>           // full signature retained     // Stubbed: suspend fun savePlan(plan: Plan)  // not needed for this Task } **Critical constraint: stubbing is only for "reducing noise," never for "simplifying complex interfaces." If an interface signature itself is complex, that reflects real system complexity and must be read in full.** **Trim Level 3 (last resort): Task Split** If Level 1 and 2 still exceed budget, the Task boundaries are too wide. Split the Task per P7 split rules; each sub-Task independently controls its evidence budget. Generating code while over budget is prohibited — this is a trimming failure, not a tolerable state. |
| --- |

| **4.3 Evidence Budget Enforcement for Reviewer Agent** |
| --- |

| Reviewer Agent is also subject to evidence budget constraints, but its budget composition differs from Developer Agent: | **Reviewer must read (incompressible)** | **Reviewer may stub** | Main framework principles document (full read) | Current Task Spec (READS / CREATES list) | All referenced ADRs (full read, verify status) | Product Contract + User Flow | Handoff Package (full read) | Implementation code in CREATES files (review interface signatures and imports only) | Test files (read test structure; full implementation not required) | Large READS files (parts already stubbed by Developer Agent) |
| --- |

---

| **Chapter 5: Degraded Operation Mode** |
| --- |

| **When to use degraded operation** When a team temporarily cannot run two fully independent AI sessions, degraded mode may be used. Degraded mode does not mean abandoning review — it is the minimum acceptable standard under constrained conditions. Task completion standards under degraded mode are lower than dual-agent mode; risks must be explicitly flagged in WorkStatus. |
| --- |

| **Mode** | **Permitted Simplifications** | **Constraints That Must Be Preserved** |
| --- | --- | --- |
| **Single-Agent Self-Check Mode** | Reviewer Agent role played by the same agent in a new session. Full dual-agent infrastructure not required. | New session must start from zero; must not carry context from the previous session. All seven R1–R7 review gates must be fully executed. Review Report must be written to WorkStatus and `docs/reviews/`. WorkStatus must note: Single-Agent Degraded Mode. |
| **No-Review Emergency Mode** | Skip Reviewer Agent process. Rely only on Developer Agent's local gates. | Task status may only be marked Done (Unreviewed), never Done. A supplementary review Task must be scheduled in the next Sprint. WorkStatus must explicitly flag: Unreviewed Risk. |

---

| **Chapter 6: WorkStatus Extension Sections** |
| --- |

Teams enabling this protocol must add the following sections to WorkStatus.md (append after existing sections):

| ## Review Queue │ Task ID │ Handoff Time │ Developer Agent Declaration │ Status │ │ TASK-019 │ 2026-06-15 │ CREATES: 3 files, local PASS │ In Review │ ## Review Log │ Task ID │ Review Time │ Verdict │ FAIL/WARNING Count │ Reviewer Sign-Off │ │ TASK-018 │ 2026-06-14 │ PASS │ 0 │ Reviewer Agent (independent session) │ │ TASK-017 │ 2026-06-13 │ BLOCKED │ 2 FAIL │ Reviewer Agent (independent session) │ ## Evidence Budget Log │ Task ID │ Type │ Est. Tokens │ Over Budget │ Action │ │ TASK-019 │ Feature │ ~6,200 │ No │ N/A │ │ TASK-016 │ Feature │ ~9,800 │ Yes │ Level 2: removed 2 implementation file stubs │ |
| --- |

---

| **Chapter 7: Quick Reference** |
| --- |

| **Developer Agent Pre-Handoff Checklist** | **Reviewer Agent Session Start Checklist** |
| --- | --- |
| 1. All CREATES files exist on disk | 1. Open a fully independent session (zero context) |
| 2. Local compile gate passes (KSP + compile) | 2. Execute P14 evidence freshness check |
| 3. Unit tests pass (ViewModel / UseCase) | 3. Independently read ADRs (confirm all Accepted) |
| 4. Detekt + Ktlint zero errors | 4. Read Product Contract + User Flow |
| 5. WorkStatus File Registry updated | 5. Read Handoff Package |
| 6. Evidence budget estimated; within limit or trimming complete | 6. Execute all seven R1–R7 review gates |
| 7. Handoff Package written to WorkStatus Review Queue | 7. Output Review Report; write to WorkStatus |
| 8. Task status updated to In Review | 8. Update Task status (Done / Blocked / Conflict) |
| | **9. ★ Write `docs/reviews/REVIEW-TASK-NNN.md` (§3.5)** |

| **Evidence Budget Quick Decision Tree** |
| --- |
| Is the estimated token volume of READS files over the budget threshold? ├─ No → Use as-is; no action needed └─ Yes → Trim Level 1: can non-essential READS files be removed?              ├─ Yes → Remove, re-estimate, return to start              └─ No → Trim Level 2: interface stubbing for oversized files                          ├─ Within limit → continue                          └─ Still over → Trim Level 3: Task split (per P7) |

v1.2 — AI Independent Reviewer Agent Contract + Evidence Budgeting
