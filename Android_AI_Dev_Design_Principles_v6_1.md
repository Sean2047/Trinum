**Android AI 辅助开发**

**设计原则 · 治理框架 · 模式指南**

*Android AI-Assisted Development*

*Design Principles · Governance Framework · Pattern Guide*

版本 v6.1　　日期 2026-06　　适用 AI 多会话 Android 项目

| **统一元原则 Unified Meta-Principle** |
| --- |
| 将每一个决策、依赖关系、用户流程、所有权边界、接口与任务上下文设计为 |
| 显式、可追溯、权威且机器可验证的形式。 |
|  |
| Design every decision, dependency, user flow, ownership boundary, interface and |
| task context to be explicit, traceable, authoritative and machine-verifiable. |
| AI consumes structured knowledge — never reconstructs reality through inference. |
|  |
| ► 本文档是 AI 每次会话的强制上下文。在开始生成任何代码之前， |
| AI 必须确认已读取本文档、WorkStatus 文件和当前 Task 规格。 |

| **v6.1 变更说明 Changes from v6.0** |
| --- |
| P0.5 ★ — 新增 ADR 定期审查规则：每 3 Sprint 开发者执行一次 ADR 有效性审查（失效检查 / 合并建议 / 废弃归档）；AI 在发现疑似失效 ADR 时，应在 Knowledge Alert 中标注 |
| P0.5 added: ADR Periodic Review — every 3 Sprints, developer reviews all Accepted ADRs for obsolescence and mergeability; AI flags suspected stale ADRs in Knowledge Alert |
| P15.1 ★ — 新增概念复杂度预算原则：引入新架构概念名称（Manager/Coordinator/Provider 等）前，必须证明现有概念无法表达；否则禁止引入，须 DEC 记录并等待开发者确认 |
| Added P15.1 Architecture Concept Complexity Budget: every new concept name must justify why existing names cannot express it; self-approval prohibited; DEC + developer confirmation required |
| Quick Reference ★ — 新增 Tier 列（L0 每次必查 / L1 触发执行 / L2 大型项目或周期审查）；层级定义见 Quick Reference 说明行 |
| Quick Reference: added Tier column (L0 Mandatory every session / L1 Standard trigger-based / L2 Large project or periodic) |
| P17 / AI Handbook ★ — Knowledge Alert 格式新增 Decision Confidence 字段：High / Medium / Low / Unknown，映射至 P13.1 证据层级；Unknown 状态禁止继续生成 |
| P17 / AI Handbook: Knowledge Alert format adds Decision Confidence section; maps to P13.1 evidence tiers; Unknown blocks generation |

| **v6.0 变更说明 Changes from v5.2.4** |
| --- |
| P15 ★ — 扩展：新增架构预算数值边界（StateFlow 链深度 ≤3、Composable 嵌套 ≤5、每 Repository DAO 数 =1、包深度 ≤4、Feature 模块文件数 ≤30），与 Fan-In/Fan-Out ≤10 共同构成 7 条硬性门禁 |
| P15 extended: Architecture Budget numerical thresholds (StateFlow chain ≤3, Composable nesting ≤5, DAOs per Repo =1, package depth ≤4, module files ≤30); 7 hard limits total |
| P18 ★ — 新增 AI 上下文预算原则（Session 证据按 7 类分配；超配触发归档/拆分规则；Session 开始前强制预算估算）|
| Added P18 AI Context Budget (7-category session evidence allocation; overflow triggers archival or split rule; mandatory budget check at session start) |
| 设计阶段检查清单新增 P18 上下文预算估算项 |
| Design Phase Checklist: added P18 context budget check item |
| AI 会话结束检查清单新增 P18 overflow 规则检查项 |
| Session End Checklist: added P18 overflow rule check item |
| P9   — 新增 WorkStatus > 150 行时 AI 必须停止请求归档的行为规则 |
| Added AI behavior rule: stop and request archival when WorkStatus > 150 lines |

| **第负二层：验证层  Layer -2: Validation Layer** *Correctness Before Consistency* |
| --- |

| **P-2  验证门禁（正确性先于一致性）** *Validation Gate — Correctness Before Consistency* |
| --- |
| **问题** **Problem** | 文档强调一致性。但 AI 多会话开发最大的风险不是架构不一致，而是所有会话一致地朝错误方向前进。正确地做错误的事，依然是失败。 |
| **规则** **Rule** | 每个 Sprint 结束时执行一次产品假设验证，分三个步骤： 第一步：验证问题   该功能是否被目标用户实际使用？产品假设是否仍然成立？   User Flow 中是否有节点用户从未到达（Flow 死胡同信号）？   是否有任何功能应当删除而非扩展？ 第二步：允许的响应   假设成立 → 继续下一个 Sprint   假设失败 → 允许删除功能，乃至推翻 ADR，按 P0.5 正式升级 ADR 状态   假设未知 → 将验证列入下一 Sprint 为最高优先级 Task 第三步：记录验证结果   将验证结果写入 WorkStatus 当前状态段   如需调整 ADR，必须先记录 DEC，再按 P0.5 正式升级 ADR 状态 AI 不得因「已有代码」而拒绝删除验证失败的功能。 |
| **关联模式** **Patterns** | [迭代/Iterative]  [规格/Specification] |
| **AI 自检** **AI Self-Check** | ✓ 本 Sprint 已执行产品假设验证，结果已写入 WorkStatus ✓ Validation executed this Sprint; result recorded in WorkStatus ✓ 如验证失败，已将调整作为下一 Sprint 的首位 Task 安排 ✓ If validation failed, adjustment scheduled as top-priority Task next Sprint |

| **第负一层：产品与需求层  Layer -1: Product ****&**** Requirements Layer** *Before Architecture* |
| --- |

| **P-1  产品契约冻结** *Product Contract Freeze — Before Architecture Decisions* |
| --- |
| **问题** **Problem** | 缺少产品契约时，AI 会话在生成代码的同时悄然扩展需求（功能膨胀）。若流程存在死胡同，系统在技术上完整但体验上断裂。 |
| **规则** **Rule** | 以下两个子步骤必须按序完成： 【子步骤 1：定义 User Flow（先于功能列表）】 用节点序列描述用户完成核心目标的完整路径。 Flow 定义规则：每个节点必须有明确的后继节点（无死胡同）；每个节点必须可从前序节点到达（无孤岛入口）；MVP Flow 节点数量建议 5～9 个。 【子步骤 2：从 Flow 推导产品契约字段】 产品目标（一句话）、目标用户画像、MVP 功能范围（节点→功能显式映射）、非目标、成功指标。 AI 在任何会话中遇到超出 Flow 的需求时，停止并报告，不实现。 |
| **关联模式** **Patterns** | [规格/Specification]  [责任链/Chain of Responsibility] |
| **AI 自检** **AI Self-Check** | ✓ 子步骤 1：User Flow 节点序列已定义，每个节点有后继，无死胡同，无孤岛 ✓ Step 1 complete: User Flow defined with no dead ends and no isolated entry points ✓ 子步骤 2：MVP 功能列表从 Flow 节点推导，每个功能有对应节点 ✓ Out-of-scope requests stopped and reported, not implemented |

| **第零层：项目设计阶段  Layer 0: Project Design Phase** *Before First Line of Code* |
| --- |

| **P0  架构决策记录（ADR）冻结** *Architecture Decision Records — Locked Before Any Code* |
| --- |
| **问题** **Problem** | 无 ADR 时，不同 AI 会话基于不同假设生成代码（混用 MVVM/MVI、Hilt/Koin），导致架构跨会话悄然漂移。 |
| **规则** **Rule** | 所有 ADR 在 Foundation Task 启动前完成冻结，ADR 状态管理遵循 P0.5：   架构模式：MVVM / MVI（选一，全项目统一）   UI 框架：Jetpack Compose vs XML（不允许混用）   依赖注入：Hilt / Koin（锁定版本）   导航：Navigation Compose / Fragments   数据库：Room 版本 + DAO 策略   网络：Retrofit + OkHttp 配置 AI 读取任何文件前先确认 ADR 存在，且所引用 ADR 均为 Accepted 状态。 |
| **关联模式** **Patterns** | [抽象工厂/Abstract Factory] |
| **AI 自检** **AI Self-Check** | ✓ 已读取 ADR 文档并确认架构模式、UI 框架、DI 框架已锁定 ✓ ADR document read; all referenced ADRs in Accepted status |

| **P0.5  ADR 生命周期与状态机** *ADR Lifecycle — Immutable Law with Explicit State Machine* |
| --- |
| **问题** **Problem** | AI 在会话中可能直接编辑已冻结的 Accepted ADR 文件而不自知，造成架构历史不可追溯。 |
| **规则** **Rule** | ADR 状态机（四状态，单向流转）：   Draft      → 草案，可修改。Foundation Task 启动前必须全部转为 Accepted。   Accepted   → 现行生效，不可变。若需变更，必须建立新 ADR（Supersedes: ADR-XXX），变更前记录 DEC。   Superseded → 已被新 ADR 替代，保留供历史查阅。P13.1 冲突检测时不得作为有效证据。   Archived   → 超过两个 Sprint 的 Superseded ADR 可移入 ADR_Archive/。   ★ ADR 定期审查（开发者职责，每 3 Sprint 执行一次，AI 不主动触发）：   ① 检查所有 Accepted ADR：是否有条目因项目演进而实际失效但尚未正式 Superseded？   ② 是否有两条 Accepted ADR 职责已高度重叠，可合并为一条？   ③ 对确认失效的条目，建立 Superseding ADR 或直接移入 Archived 状态。   AI 职责：若会话中发现引用 ADR 与当前代码库实际状态存在明显偏差，在 Knowledge Alert 中标注为「疑似失效 ADR」，供开发者在下次 ADR Review 时处理。 |
| **关联模式** **Patterns** | [状态/State]  [备忘录/Memento] |
| **AI 自检** **AI Self-Check** | ✓ 我引用的所有 ADR 均为 Accepted 状态；未引用任何 Superseded 或 Archived ADR ✓ 本次会话没有直接编辑任何 Accepted 状态的 ADR 文件 ★ ✓ 若发现引用 ADR 与代码库实际状态存在明显偏差，已在 Knowledge Alert 中标注「疑似失效 ADR」 ★ ✓ If an Accepted ADR appears misaligned with current code reality, flagged in Knowledge Alert as suspected stale ADR |

| **P0.1  命名约定与共享资产预识别** *Naming Conventions **&** Shared Asset Pre-Identification* |
| --- |
| **规则** **Rule** | 命名规则：   ViewModel：{Feature}ViewModel（如 HomeViewModel）   Use Case：{Verb}{Noun}UseCase（如 GetActivePlanUseCase）   Repository 接口：{Domain}Repository   DAO：{Entity}Dao  │  UiState：{Feature}UiState   资源：{type}_{feature}_{descriptor} 命名约定是 Foundation Task 的强制输出物。AI 发现偏差时停止并报告。 共享资产：被 2+ 模块引用的数据类 → 共享 domain model；跨屏幕读取的数据 → Repository 接口；需持久化的实体 → DAO（所有签名提前锁定）；所有 Retrofit 接口方法签名。 |
| **关联模式** **Patterns** | [模板方法/Template Method] |
| **AI 自检** **AI Self-Check** | ✓ 我生成的每个文件名符合命名约定 ✓ Every filename I generate conforms to the naming convention |

| **P0.2 ★  非功能需求冻结（NFR）v5.2.3** *Non-Functional Requirements Freeze — Foundation Deliverable* |
| --- |
| **问题** **Problem** | AI 生成的代码可能功能全部正确，但 NFR 完全失败——冷启动超3秒、查询无索引、令牌明文存储。 |
| **规则** **Rule** | Foundation Task 前冻结以下指标：   Performance:  Cold Start < 2s  │  Frame Drop < 1%   Database:     Query < 100ms   │  Pagination required > 100 rows   Network:      Timeout = 15s   │  Retry = 3   Security:     No plaintext token storage   Offline:      Core features work without network   Accessibility: TalkBack + min touch target 48dp + content description naming convention Feature Task 生成代码前检查 NFR 约束。违反 NFR 的实现在同一会话内修正。 ★ NFR 违反后果（v5.2.3 新增）： 未达标的 NFR 项视为 P11 编译门禁阻断（Blocker），与 P11 挂钩。 AI 必须在同一会话内修正，不得将 NFR 违反遗留至下一会话或降级为 TD。 NFR violation consequence: Unmet NFR items are treated as P11 compile gate blockers. AI must fix within the same session; must not defer or downgrade to TD. |
| **关联模式** **Patterns** | [策略/Strategy] |
| **AI 自检** **AI Self-Check** | ✓ 我在生成数据库查询前已检查 NFR 中的查询性能约束 ✓ I checked NFR constraints before generating database queries ★ ✓ 本次会话中发现的 NFR 违反已在同一会话内修正，未遗留 ★ ✓ Any NFR violations found this session have been fixed within this session, not deferred |

| **P0.3  依赖注册表（Dependency Registry）** *Dependency Registry — Versions Locked in Foundation Task* |
| --- |
| **问题** **Problem** | 依赖版本若未集中锁定，不同 AI 会话会引入不同版本，导致 API 不兼容、编译失败或运行时崩溃。 |
| **规则** **Rule** | Foundation Task 创建并拥有 libs.versions.toml，锁定： kotlin = "2.1.0" │ agp = "8.5.0" │ compose-bom = "2024.09.00" room = "2.7.0" │ hilt = "2.52" │ retrofit = "2.11.0" Feature Task 不得修改任何版本号 新依赖或版本升级 → DEC → 专属 Upgrade Task 升级必须覆盖全部受影响模块，不得局部升级 |
| **关联模式** **Patterns** | [单例/Singleton]  [外观/Facade] |
| **AI 自检** **AI Self-Check** | ✓ 我在此 Feature Task 中没有引入新依赖或升级版本 ✓ I have not introduced new dependencies or upgrades in this Feature Task |

| **P0.4 ★  可观测性计划（Observability Plan）v5.2.3** *Observability Plan — Foundation Task Deliverable* |
| --- |
| **问题** **Problem** | 未提前定义日志、崩溃和埋点规范时，AI 会话各自使用 Log.d()、println 或不埋任何点，导致生产问题无法诊断。 |
| **规则** **Rule** | Foundation Task 冻结以下规范：   日志框架：Timber（禁止直接使用 Log.*）   崩溃收集：Crashlytics   分析：Firebase Analytics   事件命名：{noun}_{verb}，如 workout_created / plan_deleted   禁止在事件属性中传入 PII // Correct Timber.d("Plan loaded: id=%d", plan.id) // Wrong — PII leak Log.d("TAG", "user: ${user.email}") ★ Crashlytics 告警阈值指针（v5.2.3 新增）： Crashlytics 崩溃率告警阈值（P0/P1/P2 响应 SLA）定义在《Release & Ops Guide》§5.1。 AI 会话不直接管理发布监控，但生成的代码必须符合本规则中 Timber / Crashlytics 集成规范。 ★ Feature Flag 清理指针（v5.2.3 新增）： P5.1 定义 Flag 创建规则。Flag 在 Fully Enabled 状态持续超过 2 个 Sprint 后， 由《Release & Ops Guide》§8.2 触发清理。AI 会话若发现此情况应记录 TD 提示人类清理。 |
| **关联模式** **Patterns** | [装饰/Decorator] |
| **AI 自检** **AI Self-Check** | ✓ 我在此 Task 中使用 Timber 而非 Log.* 记录日志 ✓ I used Timber (not Log.*) for all logging in this Task |

| **第一层：架构原则  Layer 1: Architecture Principles** |
| --- |

| **P1  文件所有权唯一** *Exclusive File Ownership — One File, One Task* |
| --- |
| **问题** **Problem** | 脚手架 Task 创建存根文件，后续 Task 再完成它们。存根是隐藏负债——在文件列表中无法追踪，在会话间容易遗忘。 |
| **规则** **Rule** | 脚手架 Task 仅创建：构建配置、空目录、真正的入口点。禁止创建任何功能存根。需要的文件在需要时创建。执行：Task 文档含 CREATES 列表。Sprint 结束时交叉核查。 |
| **关联模式** **Patterns** | [命令/Command] |
| **AI 自检** **AI Self-Check** | ✓ 我本次会话创建的每个文件已在当前 Task 的 CREATES 列表中 ✓ Every file I create this session is listed in the current Task CREATES |

| **P2  共享资产提前冻结** *Shared Assets Frozen Before Feature Work Begins* |
| --- |
| **规则** **Rule** | 共享资产包括：Repository 接口（所有方法签名）、Domain model 数据类、DAO 方法签名（body 可为 TODO()，签名不可改）、Hilt 模块绑定 / NavGraph 路由字符串、Retrofit 接口签名。 Foundation Task 定义所有 DAO 方法签名。Feature Task 只填充 body，不添加新签名。 发现新共享资产时：① 停止 ② 记录 DEC ③ 在微任务中定义 ④ 再继续 |
| **关联模式** **Patterns** | [单例/Singleton]  [桥接/Bridge]  [代理/Proxy] |
| **AI 自检** **AI Self-Check** | ✓ 我没有在 Feature Task 中新增 DAO/Repository 方法签名 ✓ I have not added new DAO/Repository signatures in Feature Tasks |

| **P2.1  数据库 Schema 演化治理** *Database Schema Evolution Governance* |
| --- |
| **问题** **Problem** | AI 后期修改 @Entity 字段而未提供 Migration，导致用户设备上的数据库升级崩溃。 |
| **规则** **Rule** | 每次 @Entity 变更必须：编写 Migration(from, to) 并注册到 Room.databaseBuilder；exportSchema = true 并将 schema JSON 提交到 Git；在 MODIFIES 中列出受影响文件；变更触发 DEC 记录。 val MIGRATION_3_4 = object : Migration(3, 4) {     override fun migrate(db: SupportSQLiteDatabase) {         db.execSQL("ALTER TABLE plan ADD COLUMN notes TEXT")     } } |
| **关联模式** **Patterns** | [备忘录/Memento] |
| **AI 自检** **AI Self-Check** | ✓ 我修改 @Entity 时已同步编写 Migration 并提交 schema JSON ✓ I wrote Migration and committed schema JSON for every @Entity change |

| **P3  层边界绝对化** *Absolute Layer Boundaries — Compile-Error Level Enforcement* |
| --- |
| **规则** **Rule** | 层边界规则：   domain/ 层  ✓ 纯 Kotlin，零 Android framework 导入               ✕ 不引用 data/ 或 ui/   data/   层  ✓ 可引用 domain/ 接口和 model               ✕ 不引用 ui/ 或 domain/usecase/   ui/     层  ✓ 可引用 domain/ model 和 usecase               ✕ 不直接引用 data/ 执行：Sprint 1 中添加 ArchUnit 测试。编译时检查 > 约定。 |
| **关联模式** **Patterns** | [外观/Facade]  [适配器/Adapter]  [桥接/Bridge] |
| **AI 自检** **AI Self-Check** | ✓ 我在 domain/ 中没有引入任何 Android 导入 ✓ No Android imports introduced into domain/ |

| **P3.1  协程与流作用域契约** *Coroutines **&** Flow Scope Contract — Frozen in ADR* |
| --- |
| **问题** **Problem** | P3 层边界规定了文件引用关系，但没有规定协程作用域归属。AI 在不同会话中会混用 viewModelScope/lifecycleScope，在错误层调用 collect。 |
| **规则** **Rule** | Foundation Task ADR 中必须锁定协程边界约定：   作用域归属：viewModelScope 归属 ViewModel，repositoryScope 不存在   StateFlow 转化：Repository 只暴露 suspend fun / Flow<T>，ViewModel 负责转化为 StateFlow   collect 位置：禁止在 Repository 内部 collect；禁止在 Composable 中直接 collect（应用 collectAsStateWithLifecycle） // Correct: Repository exposes Flow<T>; ViewModel converts via .stateIn(viewModelScope) // Wrong: Repository collects internally -> memory leak // Wrong: Composable uses launch{collect} -> multiple subscriptions on recompose |
| **关联模式** **Patterns** | [责任链/Chain of Responsibility]  [外观/Facade] |
| **AI 自检** **AI Self-Check** | ✓ Repository 层只暴露 suspend fun / Flow<T>，未内部 collect ✓ Composable 中用 collectAsStateWithLifecycle 而非直接 collect |

| **P3.2  UiState 单一可信源** *UiState Single Source of Truth* |
| --- |
| **问题** **Problem** | 多个 Composable 持有独立的 mutable state，与 ViewModel.UiState 产生竞争，导致 UI 与业务逻辑状态不同步。 |
| **规则** **Rule** | 业务状态治理规则：   业务状态在 ViewModel 中以 UiState data class 聚合并暴露为 StateFlow   Composable 只读取 UiState，不持有独立的 mutableStateOf 引用（布局动画除外）   UiState 的每个字段变更通过 ViewModel 事件触发，不在 UI 层直接修改 data class HomeUiState(     val plans: List<Plan> = emptyList(),     val isLoading: Boolean = false,     val error: String? = null ) |
| **关联模式** **Patterns** | [SSOT · 单一可信源]  [单向数据流/UDF] |
| **AI 自检** **AI Self-Check** | ✓ 此 Task 中 Composable 只读取 ViewModel.UiState，无独立 mutable 业务状态 ✓ Composables in this Task only read ViewModel.UiState; no independent mutable business state |

| **P3.3  UI 事件四象限契约** *UI Event Taxonomy Contract* |
| --- |
| **问题** **Problem** | P3.2 覆盖了持久状态，但 Toast、导航等一次性异步事件在多会话 AI 开发中是高频错误点。 |
| **规则** **Rule** | Foundation Task ADR 必须锁定 UI 事件四象限定义：   UiState  — 持续存在，驱动 UI 渲染：StateFlow<UiState> — collectAsStateWithLifecycle()   UiEffect — 一次消费，触发后消失：Channel<UiEffect> → receiveAsFlow()，LaunchedEffect 消费   UiAction — 用户输入事件（UI → VM）：Sealed class，ViewModel.onAction(action: UiAction)   UiIntent — 业务意图（VM → 用例层）：UseCase 调用参数，不暴露至 UI 层 强制规则：UiEffect 必须使用 Channel；UiEffect 在 Composable 中必须通过 LaunchedEffect(key) 消费；Foundation Task 必须输出四个类型定义文件。 |
| **关联模式** **Patterns** | [SSOT]  [命令/Command] |
| **AI 自检** **AI Self-Check** | ✓ 本 Task 中一次性事件（导航、Toast）使用 UiEffect Channel，未塞入 UiState ✓ UiEffect 通过 LaunchedEffect 消费，非 launch{collect} ✓ Foundation Task 已输出 UiState / UiEffect / UiAction / UiIntent 类型定义文件 |

| **P4  模块依赖图预定义** *Module Dependency DAG — Locked at Design Stage* |
| --- |
| **规则** **Rule** | Foundation Task 前输出模块依赖有向无环图（DAG）。每个模块的 build.gradle 依赖在设计阶段锁定。 示例：:app → :feature:home → :domain → :data:local 禁止模块循环依赖 |
| **关联模式** **Patterns** | [中介者/Mediator]  [外观/Facade] |
| **AI 自检** **AI Self-Check** | ✓ 我新增的依赖关系符合 DAG，没有引入循环 ✓ New dependencies I introduce conform to the DAG with no cycles |

| **P5  API 契约优先** *API Contract First — All Retrofit Signatures in Foundation Task* |
| --- |
| **规则** **Rule** | 所有 Retrofit 接口方法签名在 Foundation Task 中完整定义，不在 Feature Task 中增量添加：完整的注解与路径；完整的参数类型（@Query, @Body, @Path）；完整的返回类型（Response<T> 或 Flow<T>）；所有对应的 DTO 数据类。 |
| **关联模式** **Patterns** | [适配器/Adapter]  [代理/Proxy] |
| **AI 自检** **AI Self-Check** | ✓ 我没有在 Feature Task 中向 Retrofit 接口添加新方法 ✓ I have not added new methods to Retrofit interfaces in Feature Tasks |

| **P5.1  Feature Flag 优先** *Feature Flag First — High-Risk Features Behind Flags* |
| --- |
| **问题** **Problem** | AI 直接将高风险功能集成到主流程，无法灰度、无法回滚。 |
| **规则** **Rule** | 以下类型功能必须通过 Feature Flag 包裹：   AI/ML 推理模块；支付/订阅流程；数据同步/冲突解决；实验性 UI 变更 if (featureFlags.isEnabled(Flag.AI_COACH)) {     AiCoachScreen() } else {     ManualPlanScreen() } Flag 定义在 Foundation Task 的共享资产中。Feature Task 只读取 Flag，不定义新 Flag（需 DEC）。 Flag 清理规则：见《Release & Ops Guide》§8.2（Fully Enabled > 2 Sprints → 触发清理）。 |
| **关联模式** **Patterns** | [策略/Strategy]  [代理/Proxy] |
| **AI 自检** **AI Self-Check** | ✓ 高风险功能已通过 Feature Flag 包裹，而非直接集成 ✓ High-risk features are behind Feature Flags, not directly integrated |

| **P5.2  序列化与混淆契约** *Serialization **&** Obfuscation Native Contract* |
| --- |
| **问题** **Problem** | AI 生成 DTO / @Entity 时漏加序列化注解或 @Keep，R8 混淆后运行时崩溃。 |
| **规则** **Rule** | CREATES DTO 时：必须添加 @Serializable（kotlinx.serialization）或 @SerializedName（Gson） CREATES @Entity 时：必须添加 @Keep 或在 proguard-rules.pro 中显式保留 @Serializable data class PlanDto(@SerialName("plan_id") val planId: String, val name: String) @Keep @Entity(tableName = "plans") data class PlanEntity(...) |
| **关联模式** **Patterns** | [装饰/Decorator] |
| **AI 自检** **AI Self-Check** | ✓ 本 Task CREATES 的每个 DTO 已加 @Serializable / @SerializedName ✓ 本 Task CREATES 的每个 @Entity 已加 @Keep |

| **P15  架构适应度函数与预算** *Architecture Fitness Functions & Budget* |
| --- |
| **问题** **Problem** | Detekt/Ktlint 等静态分析工具只能发现代码层面的问题，无法感知架构层面的结构退化。转发层（Helper/Manager/Coordinator）会悄然堆积。这是 AI 多会话开发的特有风险：每次会话独立合规，但跨会话累积腐化。 |
| **规则** **Rule** | 两类可执行的架构适应度指标（Foundation Task 冻结基线，Sprint Review 时验证）： 【指标一】调用链转发层禁令   domain → usecase → repository 之间，禁止插入无独立业务逻辑的转发层。   高风险命名模式：class *Helper / class *Manager / class *Coordinator   class *Wrapper / class *Adapter / class *Facade   判断：该类的所有公开方法是否只是透传调用？若是，则为非法转发层。 【指标二】架构预算数值边界（7条硬性门禁，任一违反 → ARCH-DECAY DEC）   Fan-In（被依赖数）≤ 10 / Fan-Out（依赖数）≤ 10   StateFlow 链深度 ≤ 3 层（ViewModel → UI 转换层数）   Composable 嵌套深度 ≤ 5 层   每个 Repository 实现对应 DAO 数 = 1   包深度（从根模块起）≤ 4 层   Feature 模块 Kotlin 文件数 ≤ 30 超标处理流程：   ① 停止当前功能 Task   ② 在 WorkStatus 记录 DEC-TYPE: ARCH-DECAY   ③ 建立最高优先级架构修复 Task   ④ 架构修复 Task 通过 ArchUnit 验证后，方可恢复功能开发 |
| **关联模式** **Patterns** | [ArchUnit · 架构测试]  [适配器/Adapter 反模式识别] |
| **AI 自检** **AI Self-Check** | ✓ 本次会话调用链中，没有插入无独立业务逻辑的转发类 ✓ 若发现高风险命名的中间层，已确认其具有独立业务逻辑 ✓ 本 Sprint 已执行模块扇入/扇出统计；超标后已建立 ARCH-DECAY DEC ✓ 本 Sprint 已检查全部 7 条架构预算边界；任一超标后已建立 ARCH-DECAY DEC |

| **P15.1 ★  概念复杂度预算** *Architecture Concept Complexity Budget* |
| --- |
| **问题** **Problem** | P15 约束了结构性指标（Fan-Out、嵌套层数等），但不限制语义层面的概念爆炸。AI 在多会话中很容易引入 RepositoryManager → RepositoryCoordinator → RepositoryFacade——每个独立满足 Fan-Out 约束，但合并后认知复杂度已经爆炸。Concept Explosion 比 Code Explosion 更隐蔽、更难回滚。 The problem: every new class satisfies P15 structural metrics, yet collectively they create unmanageable cognitive load. |
| **规则** **Rule** | 每引入一个新架构概念名称（类名后缀或新角色词，如 Manager / Coordinator / Provider / Registry / Facade / Gateway），必须在同一会话内完成以下两步——禁止自我批准：   第一步：证明现有概念无法表达   列出项目中现有的同类角色词（从 WorkStatus 文件注册表和 docs/invariants.md 提取）   明确说明新名称与已有名称的本质区别（职责差异，而非规模差异或命名偏好）   若仅是职责细分、粒度调整或风格偏好，则沿用现有名称，禁止引入新词   第二步：DEC 记录 + 等待确认   在 WorkStatus 记录 DEC：新概念名称 + 引入原因 + 与现有概念的本质区别   DEC 须经开发者在下一会话确认后，新概念名称方可进入代码库   在当前会话中，用临时占位符命名（Temp{Function}），不使用未经确认的新角色词 |
| **关联模式** **Patterns** | [规格/Specification]  [备忘录/Memento] |
| **AI 自检** **AI Self-Check** | ✓ 本次会话未引入未经验证的新架构角色词；或已通过「现有概念无法表达」验证并记录 DEC ✓ No new architectural concept names introduced without existing-concept justification and DEC |

| **P18  AI 上下文预算** *AI Context Budget — Session Evidence Allocation* |
| --- |
| **问题** **Problem** | Session 上下文窗口有限。ADR、WorkStatus、Task 规格、Pattern 卡片无控制地增长会降低 AI 连贯性、增加幻觉风险。当前框架只有 WorkStatus 150 行上限，但对整体 Context 分配无预算约束。 |
| **规则** **Rule** | 每个 Session 的证据按 7 类分配上限（占可用 Context 的比例）：  WorkStatus（当前）20% / 引用 ADR 20% / 当前 Task 规格 15% / Pattern 卡片 15% / ProjectGlossary.md 10% / 本手册 10% / 不变量登记表 5%  超配处理规则：WorkStatus > 150 行 → 停止并请求归档 / 引用 ADR > 5 份 → 非直接引用 ADR 摘要化 / Task READS > 8 个文件 → 停止并请求拆分 Task / Pattern 卡片 > 3 张 → 只加载最相关的 2 张 / Glossary > 200 条 → 只加载当前 Task 领域相关条目  Session 开始强制步骤：生成任何代码前，估算本次 Session 合并证据量是否在预算内；若超配，先执行对应 overflow 规则再继续。 |
| **关联模式** **Patterns** | [证据优先级 P13.1]  [归档规则 P9]  [Task 规模 P7] |
| **AI 自检** **AI Self-Check** | ✓ Session 开始时估算了合并证据量；确认在预算范围内 ✓ 任何类别超配时，已执行对应 overflow 规则后再继续 ✓ WorkStatus < 150 行；Task READS ≤ 8 个 |

| **第二层：Task 设计原则  Layer 2: Task Design Principles** |
| --- |

| **P6  每个 Task 显式声明文件契约** *Every Task Declares Its File Contract Explicitly* |
| --- |
| **规则** **Rule** | 四个必须字段：   READS：AI 生成代码前必须读取的现有文件完整路径   CREATES：此 Task 创建的每个文件（一个文件 = 一个所有者）   MODIFIES：此 Task 修改的现有文件及修改说明   DELETES：此 Task 删除的文件 冲突检测：启动 Task 前检查：是否有其他进行中的 Task 在 CREATES 或 MODIFIES 中列出了同一文件？若有，串行化。 |
| **关联模式** **Patterns** | [命令/Command] |
| **AI 自检** **AI Self-Check** | ✓ 我已读取所有 READS 文件后才开始生成代码 ✓ I read all READS files before generating any code |

| **P6.1  上下文重对齐门禁** *Context Reconciliation Gate — Multi-Session / Multi-Agent Alignment* |
| --- |
| **问题** **Problem** | P6 的冲突检测假设串行开发。但多窗口 AI 会话或多 Agent 并行工作时，即使每个决策单独合法，合并后也可能产生互相冲突的共享资产状态。 |
| **规则** **Rule** | 以下任一事件发生后，必须执行重对齐流程，再继续编码：   Git Pull / Merge / Rebase   ADR 状态变更（任何 ADR 从 Draft → Accepted 或 Accepted → Superseded）   WorkStatus 被其他 Task/Agent 更新 重对齐流程（顺序执行，不得跳步）：   ① 重新读取 WorkStatus 最新版本   ② 重新读取当前 Task 所有 READS 文件（以磁盘版本为准，不使用记忆）   ③ 重新读取 ADR 与 DEC Log（确认所有 ADR 均为 Accepted 状态）   ④ 重新执行 P13 证据链验证   ⑤ 确认无冲突后，方可继续编码 |
| **关联模式** **Patterns** | [备忘录/Memento]  [观察者/Observer] |
| **AI 自检** **AI Self-Check** | ✓ 本次会话前若有 Git 分支操作，已执行完整重对齐流程 ✓ P13 evidence chain re-verified after reconciliation before code generation |

| **P7  Task 规模适配单次上下文窗口** *Tasks Sized for One AI Context Window* |
| --- |
| **规则** **Rule** | 每个 Task 产出 3～5 个新文件，或 1 个复杂文件加其测试。超出则拆分。   CREATES 不超过 5 个文件——超出说明 Task 边界划分过宽   READS 不超过 8 个文件——超出说明 Task 需要拆分 拆分启发：若 Task 同时涉及新数据模型 + 新 UI 屏幕 → 拆为两个 Task。 |
| **关联模式** **Patterns** | [组合/Composite] |
| **AI 自检** **AI Self-Check** | ✓ 若 CREATES > 5 个文件，我已停止并请求拆分 ✓ If CREATES > 5 or READS > 8, I stopped and requested a Task split |

| **P8  共享模型变更须记录决策** *Shared Model Changes Require a Decision Record* |
| --- |
| **规则** **Rule** | 记录内容：DEC-ID │ 日期 │ 变更内容 │ 变更原因 │ 受影响文件列表 │ 其他 Task 中需更新的内容。 流程：会话发现共享模型需变更时：   ① 停止编写代码  ② 记录决策（DEC）  ③ 列举所有受影响文件  ④ 将变更作为独立微任务完成后再继续 |
| **关联模式** **Patterns** | [备忘录/Memento] |
| **AI 自检** **AI Self-Check** | ✓ 我没有在未记录 DEC 的情况下修改共享资产 ✓ I have not modified shared assets without logging a DEC |

| **第三层：流程原则  Layer 3: Process Principles** |
| --- |

| **P9 ★  WorkStatus — 项目的唯一持久记忆 v5.2.3** *WorkStatus — The Project's Only Persistent Memory* |
| --- |
| **规则** **Rule** | WorkStatus 必须包含以下章节：## Current Status / ## Task Progress / ## File Registry / ## DAO Addition Log / ## Decision Log (DEC) / ## Technical Debt / ## Review Queue（双 Agent 时）/ ## Session Output。 更新纪律：会话结束前更新，不是结束后。 ★ 归档触发规则（v5.2.3 新增）： 每个 Sprint 开始时，由人类开发者将超过一个 Sprint 的 Done Task 移至 WorkStatus_Archive.md。 AI 职责：若会话开始时发现 WorkStatus 超过 150 行，立即停止并请求人类执行归档， 不得在未归档的情况下继续生成代码。 |
| **关联模式** **Patterns** | [观察者/Observer]  [备忘录/Memento] |
| **AI 自检** **AI Self-Check** | ✓ 我在会话开始时已读取 WorkStatus 并确认当前 Task ID ✓ I read WorkStatus and confirmed current Task at session start ★ ✓ 若会话开始时 WorkStatus 超过 150 行，我已停止并请求归档后再继续 ★ ✓ If WorkStatus exceeded 150 lines at session start, I stopped and requested archival |

| **P9.1  技术债务注册表** *Technical Debt Registry* |
| --- |
| **规则** **Rule** | 每个 TODO 必须注册——不得留为裸代码注释： // Wrong: // TODO: optimize this query later // Correct: // TD-003: query optimization — Impact: Medium, Sprint 5 规则：每个 TD 条目必须指定：影响评估、目标 Sprint、负责 Task ID。当前 Sprint 内待处理 TD 不超过 3 个；超出则降低功能开发优先级。TD 偿还时在 WorkStatus 中将状态改为 Done，禁止静默删除记录。 |
| **关联模式** **Patterns** | [备忘录/Memento]  [观察者/Observer] |
| **AI 自检** **AI Self-Check** | ✓ 没有裸 TODO()；所有债务已关联 TD-ID 并记录在 WorkStatus ✓ No bare TODO(); all debt linked to TD-ID in WorkStatus |

| **P9.2  复用阈值治理** *Reuse Threshold — Code Clone Governance* |
| --- |
| **规则** **Rule** | 当即将创建第三个结构相似的组件时，必须触发强制复用评估：   ① 停止创建新文件   ② 列出现有相似组件（名称 + 路径 + 相似点）   ③ 记录 DEC，评估三种重构选项：提取共享 Composable；引入 Base Screen 抽象；参数化差异并合并。   ④ 等待人类确认重构方向后，再继续 「第三个结构相似组件」是固定触发阈值——不依赖主观相似度判断。 |
| **关联模式** **Patterns** | [模板方法/Template Method]  [组合/Composite] |
| **AI 自检** **AI Self-Check** | ✓ 若此 Task 创建了第三个结构相似组件，已触发 DEC 复用评估 ✓ If this Task creates the third structurally similar component, DEC reuse evaluation triggered |

| **P10  决策同步传播** *Decision Propagation Is Synchronous — No Cross-Session Deferral* |
| --- |
| **规则** **Rule** | 决策影响多个现有文件时，必须在同一会话内更新所有依赖文件：   ① 列出所有引用了变更组件的文件   ② 在同一会话内更新每个引用   ③ 会话结束前运行编译检查   ④ 在 WorkStatus 中更新每个变更的文件 |
| **关联模式** **Patterns** | [职责链/Chain of Responsibility] |
| **AI 自检** **AI Self-Check** | ✓ 我在同一会话内更新了所有受影响文件 ✓ I updated all affected files in the same session after making a decision |

| **第四层：质量原则  Layer 4: Quality Principles** |
| --- |

| **P11  编译门禁** *Every Session Ends with a Compile Gate* |
| --- |
| **规则** **Rule** | 最低门禁（强制）：./gradlew :app:kaptDebugKotlin && ./gradlew :app:compileDebugKotlin 推荐门禁（含单元测试）：./gradlew test 门禁失败在同一会话内修复。不以红色构建结束会话。 |
| **关联模式** **Patterns** | [策略/Strategy] |
| **AI 自检** **AI Self-Check** | ✓ 运行了编译门禁并通过，才结束本次会话 ✓ Ran the compile gate and it passed before ending this session |

| **P11.1  测试金字塔架构** *Test Pyramid Architecture* |
| --- |
| **规则** **Rule** | Foundation Task 冻结测试策略：   单元测试 70%  → ViewModel, UseCase, Repository (fake)   集成测试 20%  → Room DAO, Retrofit (MockWebServer)   UI 测试  10%  → Compose semantics / critical flows 每个 Feature Task DoD 必须包含：ViewModel → StateFlow 单元测试；UseCase → 成功路径 + 错误路径；Repository → 使用 FakeDataSource 的集成测试；Screen → 关键路径的 Compose UI 测试。 |
| **关联模式** **Patterns** | [模板方法/Template Method]  [代理/Proxy] |
| **AI 自检** **AI Self-Check** | ✓ 此 Task 中的 ViewModel 有对应的单元测试 ✓ The ViewModel in this Task has corresponding unit tests |

| **P11.2 ★  CI 为权威门禁 v5.2.3** *CI Is the Authority — Local Compile Is Necessary But Not Sufficient* |
| --- |
| **规则** **Rule** | 本地编译通过 ≠ 完成。CI 通过 = 完成。 CI 管道必须运行：compile（强制）/ unit test（强制）/ lint（强制，Error 导致失败）/ detekt（强制）/ ktlint（强制）/ dependency scan（推荐）。 ★ 发布管道关联（v5.2.3 新增）： CI PASS 同时是进入发布管道的强制入场条件。未通过 CI 的 Task 不得进入任何发布轨道。完整发布流程由《Release & Ops Guide》规范（独立文档，不读入 AI 会话上下文）。 CI PASS is also the mandatory entry condition for the release pipeline. Full release process governed by Release & Ops Guide (not loaded into AI session context). |
| **关联模式** **Patterns** | [职责链/Chain of Responsibility] |
| **AI 自检** **AI Self-Check** | ✓ 此 Task 的代码已通过所有 CI 检查 ✓ Code from this Task has passed all CI checks |

| **P11.3  静态分析门禁** *Static Analysis Gate* |
| --- |
| **规则** **Rule** | detekt.yml 配置：   complexity:     LongMethod: threshold: 30     CyclomaticComplexMethod: threshold: 10 Lint Error = Build Failure（不可降级为 Warning） |
| **关联模式** **Patterns** | [策略/Strategy]  [装饰/Decorator] |
| **AI 自检** **AI Self-Check** | ✓ 我生成的方法 ≤30 行，圈复杂度 ≤10 ✓ Methods I generate are ≤30 lines with cyclomatic complexity ≤10 |

| **P12  ProGuard/R8 早期规划** *ProGuard/R8 — Foundation Task Deliverable* |
| --- |
| **规则** **Rule** | proguard-rules.pro 由 Foundation Task 创建并独占。识别所有需要混淆规则的依赖：Retrofit / OkHttp；Gson / Moshi / Kotlinx Serialization；Room（如使用反射特性）；自定义 @Keep 注解注册表。Feature Task 不得修改 proguard-rules.pro。新依赖需要规则 → DEC → 专属微任务。 |
| **关联模式** **Patterns** | [装饰/Decorator] |
| **AI 自检** **AI Self-Check** | ✓ 我没有在此 Feature Task 中修改 proguard-rules.pro ✓ I have not modified proguard-rules.pro in this Feature Task |

| **第五层：证据原则  Layer 5: Evidence Principles** |
| --- |

| **P13  生成前引用** *Evidence Before Generation — No Inference-Based Code* |
| --- |
| **规则** **Rule** | 生成代码前必须显式引用权威来源：   Evidence:     ADR-003 (MVVM + Hilt)     domain/repository/ExercisePlanRepository.kt     Task A-019 Spec (CREATES: HomeViewModel)   Action: Generate HomeViewModel based on above evidence 若无法引用具体证据：停止并请求澄清。不基于推断生成。 |
| **关联模式** **Patterns** | [模板方法/Template Method]  [规格/Specification] |
| **AI 自检** **AI Self-Check** | ✓ 我在生成每个核心类前列出了具体证据文件（READS + ADR） ✓ Listed specific evidence files before generating each core class ✓ 没有在未读取 READS 文件的情况下推断接口签名或数据结构 |

| **P13.1  证据优先级层级** *Evidence Priority Hierarchy — Conflict Resolution Protocol* |
| --- |
| **规则** **Rule** | 优先级层级：   P1（最高）— ADR（Accepted 状态）：架构决策的最高权威。ADR 互相冲突：停止并上报。   P2       — 产品契约（含 User Flow）：功能范围和用户流程的权威。范围外需求：停止并上报。   P3       — DEC（决策变更记录）：覆盖较低优先级来源，但自身必须符合 ADR。   P4       — WorkStatus（当前状态）：Task 状态和文件注册表的权威。不能覆盖 ADR 或产品契约。   P5（最低）— 代码（当前实现）：当代码与较高来源冲突时，修正代码。 冲突规则：「代码已存在」从不是覆盖 ADR 的理由。 |
| **关联模式** **Patterns** | [规格/Specification]  [责任链/Chain of Responsibility] |
| **AI 自检** **AI Self-Check** | ✓ 生成代码时已确认证据来源之间无冲突；或停止并上报了冲突 ✓ 没有用低优先级来源（代码/WorkStatus）覆盖高优先级来源（ADR） |

| **P14  证据新鲜度门禁** *Context Freshness Gate* |
| --- |
| **规则** **Rule** | 以下触发事件发生后，将相关证据文件标记为过期。重读后才能继续生成。   Git Pull / Merge / Rebase    → 全部 READS + WorkStatus + ADR   ADR 状态变更                → 全部 ADR + 受影响 READS   新 Task 开始                → WorkStatus + 新 Task READS 「我刚读过」不代表证据有效。有效性由触发事件决定，不由时间决定。 |
| **关联模式** **Patterns** | [备忘录/Memento]  [观察者/Observer] |
| **AI 自检** **AI Self-Check** | ✓ 本次会话前已确认无触发事件发生；或已重读所有相关文件 ✓ Did not generate code based on session-cached content that may have been externally updated |

**快速参考总览  Quick Reference Summary**

**Tier 说明：L0 = 每次会话必须主动执行（与 Task 类型无关） / L1 = 触发条件满足时执行（大多数会话适用） / L2 = 多模块大型项目或周期性评审时执行**

| **层/P** | **Tier** | **原则** | **一句话规则** | **核心模式** |
| --- | --- | --- | --- | --- |
| **P-2** | L1 | 验证门禁 | 正确性先于一致性；每 Sprint 验证产品假设及 Flow 节点完成情况 | 迭代·规格 |
| **P-1** | L1 | 产品契约冻结 | 先定义 User Flow（5-9节点），再从 Flow 推导 MVP 功能列表 | 规格·责任链 |
| **P0** | L0 | ADR 冻结 | ADR 在首行代码前完成并锁定；状态管理遵循 P0.5 | 抽象工厂 |
| **P0.1** | L1 | 命名约定+共享资产 | 命名约定是 Foundation Task 强制输出物 | 模板方法 |
| **P0.2 ★** | L1 | NFR 冻结 | 性能/安全/离线目标量化；违反 = P11 Blocker（v5.2.3） | 策略 |
| **P0.3** | L1 | 依赖注册表 | 所有库版本锁定在 libs.versions.toml | 单例·外观 |
| **P0.4 ★** | L1 | 可观测性计划 | Timber+Crashlytics+Analytics；阈值见 Release Ops §5.1（v5.2.3） | 装饰 |
| **P0.5** | L0 | ADR 生命周期 | Draft→Accepted→Superseded→Archived；Accepted 不可就地修改 | 状态·备忘录 |
| **P1** | L1 | 文件所有权唯一 | 一个文件只属于一个 Task，无存根 | 命令 |
| **P2** | L1 | 共享资产提前冻结 | 所有接口/DAO 签名在功能 Task 前定型 | 单例·桥接·代理 |
| **P2.1** | L1 | Schema 演化治理 | @Entity 变更必须有 Migration + schema JSON | 备忘录 |
| **P3** | L1 | 层边界绝对化 | domain 零 Android 导入；ui 不直接调 data | 外观·适配器·桥接 |
| **P3.1** | L1 | 协程作用域契约 | Repository 只暴露 Flow<T>；ViewModel 转化为 StateFlow | 责任链·外观 |
| **P3.2** | L1 | UiState 单一可信源 | 业务状态在 ViewModel.UiState；Composable 不留 mutable 引用 | SSOT·UDF |
| **P3.3** | L1 | UI 事件四象限契约 | UiState/UiEffect/UiAction/UiIntent；UiEffect 必须 Channel | SSOT·命令 |
| **P4** | L2 | 模块依赖图预定义 | 多模块 DAG 在设计阶段锁定 | 中介者·外观 |
| **P5** | L1 | API 契约优先 | Retrofit 签名在 Foundation Task 完整定义 | 适配器·代理 |
| **P5.1** | L1 | Feature Flag 优先 | 高风险功能必须通过 Feature Flag 包裹；清理规则见 Ops §8.2 | 策略·代理 |
| **P5.2** | L1 | 序列化与混淆契约 | DTO/Entity CREATES 时必加 @Serializable / @Keep | 装饰 |
| **P6** | L0 | 文件契约显式 | 每个 Task 声明 READS/CREATES/MODIFIES/DELETES | 命令 |
| **P6.1** | L0 | 上下文重对齐门禁 | Git 操作/ADR 变更后必须重读全部证据，再继续编码 | 备忘录·观察者 |
| **P7** | L1 | 单会话规模 | CREATES 3~5个；READS ≤8个；超出则拆分 | 组合 |
| **P8** | L0 | 共享变更决策记录 | 共享资产变更必须记录 DEC | 备忘录 |
| **P9 ★** | L0 | WorkStatus 为项目记忆 | 会话结束前更新；>150行时停止请求归档（v5.2.3） | 观察者·备忘录 |
| **P9.1** | L1 | 技术债务注册表 | TODO() 必须关联 TD-ID；债务在 WorkStatus 可见 | 备忘录·观察者 |
| **P9.2** | L1 | 复用阈值治理 | 创建第三个结构相似组件时强制触发 DEC 复用评估 | 模板方法·组合 |
| **P9.3 ★** | L1 | 失败模式登记表 | Sprint 回顾：本次 Bug 是否属于根因类？若是新类则登记 | 观察者·备忘录 |
| **P10** | L0 | 决策同步传播 | 所有受影响文件在同一会话内更新 | 职责链 |
| **P11** | L0 | 编译门禁 | 门禁失败在同一会话内修复，不以红色构建结束 | 策略 |
| **P11.1** | L1 | 测试金字塔架构 | 70% 单测/20% 集成/10% UI；Feature Task 必须含测试 | 模板方法·代理 |
| **P11.2 ★** | L0 | CI 为权威门禁 | CI PASS = Done；CI PASS 也是发布管道入场条件（v5.2.3） | 职责链 |
| **P11.3** | L2 | 静态分析门禁 | Detekt + Ktlint + Lint Error = Build Failure | 策略·装饰 |
| **P12** | L2 | ProGuard 早期规划 | 混淆规则在 Foundation Task 识别并配置 | 装饰 |
| **P13** | L0 | 生成前引用 | 无依据文件则停止请求澄清，不推断生成 | 模板方法·规格 |
| **P13.1** | L0 | 证据优先级层级 | ADR > 产品契约 > DEC > WorkStatus > 代码；冲突停止上报 | 规格·责任链 |
| **P14** | L0 | 证据新鲜度门禁 | Git/ADR/Task 三类事件触发强制重读相关证据文件 | 备忘录·观察者 |
| **P15 ★** | L2 | 架构适应度函数与预算 | 无转发层；7条数值边界（Fan-In/Out≤10、StateFlow≤3、嵌套≤5、DAO=1、包深度≤4、文件≤30）；超标 ARCH-DECAY DEC | ArchUnit·状态 |
| **P15.1 ★** | L1 | 概念复杂度预算 | 引入新架构角色词前必须证明现有概念无法表达；自我批准禁止；须 DEC + 开发者确认 | 规格·备忘录 |
| **P16 ★** | L1 | 不变量登记表 | 领域真相注册；AI 触碰相关领域前必须验证无违反 | 规格·备忘录 |
| **P17** | L1 | 知识警报 | 每 Task 结束输出 Knowledge Alert（★优先级术语、决策解释、具名 Task 知识缺口、L0–L4 模块成熟度） | 观察者·备忘录 |
| **P17.1** | L1 | KNOWLEDGE TD 生命周期 | AI 开启；开发者推进；仅开发者关闭；代码通过 ≠ 理解建立 | 备忘录·状态 |
| **P17.2** | L1 | Glossary 提案协议 | Session 开始读取；AI 提案不直接写入；含完整条目格式 | 规格·备忘录 |
| **P17.3** | L1 | 依赖掌控分级 | 新库标注 Understand / Use / Black Box；驱动知识债务决策 | 策略·装饰 |
| **P18 ★** | L0 | AI 上下文预算 | 7类证据分配上限；超配触发归档/拆分规则；Session 开始前强制估算 | 策略·备忘录 |

**★ 表示 v6.0 / v6.1 新增或修改的原则**

**附录 C：会话检查清单  Appendix C: Session Checklists**

**设计阶段检查清单（Foundation Task 前）Design Phase Checklist**

| ☐ | P-2: 产品假设验证机制已确认：每 Sprint 结束执行一次验证 |
| --- | --- |
| ☐ | P-1 子步骤 1: User Flow 节点序列（5-9节点，无死胡同，无孤岛） |
| ☐ | P-1 子步骤 2: 目标、MVP、Out of Scope、成功指标（从 Flow 推导） |
| ☐ | P0: 所有 ADR 已完成并冻结，状态均为 Accepted（P0.5） |
| ☐ | P0.5: 已确认无任何 ADR 处于 Draft 状态 |
| ☐ | P0.1: 命名约定文档已输出 |
| ☐ | P0.2: NFR 已量化并冻结：性能 / 安全 / 离线 |
| ☐ | P0.3: 依赖注册表 libs.versions.toml 已创建 |
| ☐ | P0.4: 可观测性规范已冻结：Timber + Crashlytics + Analytics |
| ☐ | P3.3: UI 事件四象限类型定义已输出：UiState / UiEffect / UiAction / UiIntent |
| ☐ | P2: 共享资产清单已从 User Flow 节点推导 |
| ☐ | P4: 模块依赖 DAG 已定义 |
| ☐ | P5: 所有 Retrofit 签名已完整定义 |
| ☐ | P5.1: Feature Flag 清单已识别高风险功能 |
| ☐ | P11.1: 测试策略已冻结 |
| ☐ | P12: ProGuard 依赖已识别 |
| ☐ | Foundation Task CREATES 已覆盖所有共享资产 |
| ☐ | P15: 架构适应度基线测量脚本已纳入 Foundation Task CREATES |
| ☐ | ★ P16: 不变量登记表已播种（docs/invariants.md）——最少 3 条：至少一条架构类（数据流向/真实源所有权）、一条业务类（来自 User Flow 的产品规则）、一条数据类（字段写入权限） |
| ☐ | ★ 技术设计检查点：对于含非简单状态转换或跨模块交互的功能，已在 Foundation Task 前输出类协作关系说明与状态机 |
| ☐ | ★ P18：估算本 Session 合并证据量（ADR + WorkStatus + Task 规格 + Pattern 卡片）确认在预算范围内；WorkStatus < 150 行；引用 ADR ≤ 5 份 |

**AI 会话开始检查清单  AI Session Start Checklist**

| ☐ | P14: 检查证据新鲜度——上次读取后是否发生触发事件（Git 操作 / ADR 变更 / 新 Task）？若有，先重读相关文件 |
| --- | --- |
| ☐ | P6.1: 若发生 Git Pull/Merge/Rebase，执行完整重对齐流程 |
| ☐ | 读取 WorkStatus 并确认当前 Task ID |
| ☐ | ★ 若 WorkStatus 超过 150 行，停止并请求人类执行归档（P9 — v5.2.3） |
| ☐ | 读取当前 Task 规格的所有 READS 文件 |
| ☐ | 列出将 CREATES 和 MODIFIES 的文件，确认与 Task 规格一致 |
| ☐ | 检查 MODIFIES 列表：是否与其他进行中 Task 冲突？若有则串行化 |
| ☐ | 确认：此 Task 是否涉及未记录 DEC 的共享资产变更？若有则先记录 |
| ☐ | 运行 P13.1 证据冲突检测：ADR / Product Contract / DEC / WorkStatus 之间是否有矛盾？若有则停止并上报 |
| ☐ | 检查所有引用 ADR 的状态：是否有 Superseded 或 Archived 状态的 ADR 被错误引用？（P0.5） |
| ☐ | 确认 ADR + NFR + 产品契约（含 User Flow）约束在本次会话中有效 |
| ☐ | 列出本次生成的 Evidence（READS + ADR）——P13 |
| ☐ | 明确本次会话结束条件：CREATES 全部存在 + 编译门禁通过 + WorkStatus 已更新 |

**AI 会话结束检查清单  AI Session End Checklist**

| ☐ | 运行编译门禁（最低：kapt + compile；推荐：./gradlew test） |
| --- | --- |
| ☐ | 运行单元测试（此 Task CREATES 中的每个 ViewModel/UseCase） |
| ☐ | 本地运行 Detekt + Ktlint；零 Error |
| ☐ | 更新 WorkStatus 文件注册表（CREATES + MODIFIES 中的每个文件） |
| ☐ | 更新 WorkStatus Task 进度表（状态 → Done 或 In Progress） |
| ☐ | 记录本次会话产物到会话日志 |
| ☐ | 若有 DAO 新增，更新 DAO 新增日志 |
| ☐ | 若本次有决策变更，更新决策日志 |
| ☐ | 若本次引入技术债务，更新 WorkStatus TD 表（P9.1） |
| ☐ | 若本次创建了第三个结构相似组件，确认已触发 P9.2 DEC 复用评估 |
| ☐ | 若本次修改了 ADR，确认执行了 Superseded 流程而非就地修改（P0.5） |
| ☐ | ★ P9.3 回顾：本次会话有 Bug 吗？它是否属于某个根因类？若属于新类，已添加至 WorkStatus ## Failure Mode Classes |
| ☐ | ★ P16 回顾：本次会话是否揭示了隐性不变量？若有，已添加至 docs/invariants.md 并在 WorkStatus 文件注册表中记录 |
| ☐ | ★ P18：本次 Session 中有 Context 类别超配吗？若有，确认已执行对应 overflow 规则（归档 / 拆分 Task / 摘要化） |
| ☐ | ★ 若本次发现并修正了 NFR 违反，记录修正内容（P0.2 — v5.2.3） |
| ☐ | 写入下一个 Task ID 到 WorkStatus 当前状态 |
| ☐ | 检查过期证据（P14）：本次会话是否触发了任何新鲜度事件？若有，记录到 WorkStatus |

**附录 D：WorkStatus 归档与上下文精简化  Appendix D: WorkStatus Archival ****&**** Context Efficiency**

| **周期归档规则（每 Sprint 开始时）Sprint-Start Archival Rules** |
| --- |
| 规则 1：将 Task 进度表中所有状态为 Done 且超过一个 Sprint 的记录移入 WorkStatus_Archive.md |
| 规则 2：将决策日志中超过两个 Sprint 的已关闭 DEC 移入归档文件 |
| 规则 3：保留：所有进行中和阻塞状态的 Task + 最近一个 Sprint 的 DEC + 所有 TD + 全量 ADR |
| 规则 4：归档后 WorkStatus 有效行数应不超过 150 行（超出则继续归档更早的记录） |
|  |
| ★ AI 触发规则（v5.2.3 新增）： |
| AI 在会话开始时发现 WorkStatus > 150 行时，必须停止并提示用户执行周期归档， |
| 不得默默加载全量文件或跳过归档继续生成代码。 |

Android AI 辅助开发设计原则 v6.1　　40 项原则完整版