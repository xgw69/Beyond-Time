# 1.12.2 最小线导航（20-legacy-1122-lane）

<!-- 元信息块 -->
- 适用场景：已确认在"1.12.2 线"（Forge 1.12.2）工作的 agent 或新手，打开本文件确定读哪个内容文件。
- 前置依赖：无
- 相关文件：`references/00-intake.md`（路由）、`references/03-assets.md`（顶层，贴图/模型规范，本线只写差异）、`references/20-legacy-1122-lane/90-troubleshooting.md`（排错）
- 适用加载器：Forge（本线唯一）
- 适用版本线：1.12.2（Forge 14.23.5.2847）
- 核验日期：2026-08-10

## 本线定位

1.12.2 是独立成线的原因：它仍用老版 Forge（ForgeGradle 2.3 + `GameRegistry`），API 与现代 NeoForge 1.21.x 完全不同，无法与现代线共用正文，因此单独维护一个"最小线"。本线只承诺 Forge 1.12.2，代码真相 = `examples/forge-1.12.2/`（Forge 14.23.5.2847 / ForgeGradle 2.3-SNAPSHOT / JDK 8 / Gradle 4.9）。1.12.2 不同 patch（如 2847 与其他）差异小，本线一律以 **14.23.5.2847** 为准并注明，不设版本矩阵。

## 文件清单

| 编号 | 文件 | 职责 | 前置依赖 | 状态 |
|---|---|---|---|---|
| - | `README.md` | 本线导航 | 无 | P1.5 已交付 |
| 00 | `00-scaffold.md` | 新建 1.12.2 工程 + 已有工程改造 | 无（P0 模板与工具链） | P1.5 已交付 |
| 10 | `10-blocks-items.md` | 方块/物品/配方/语言文件/模型贴图 | 00-scaffold、03-assets | P1.5 已交付 |
| 90 | `90-troubleshooting.md` | 1.12.2 专属故障速查 | 无 | P1.5 已交付（最小版） |
| 15 | `15-extra-registries.md` | 附魔/药水/状态效果/声音/属性 | 10-blocks-items | P3 排期，未创建 |
| 20 | `20-machines.md` | 机器/容器/能量/流体/JEI | 10-blocks-items | P3 排期，未创建 |
| 30 | `30-worldgen.md` | 世界生成 | 10-blocks-items（弱） | P3 排期，未创建 |
| 40 | `40-entities.md` | 实体 | 10-blocks-items（弱） | P3 排期，未创建 |
| 50 | `50-client.md` | 客户端 | 10-blocks-items（弱） | P3 排期，未创建 |
| 60 | `60-networking.md` | 网络 | 实体或机器 | P3 排期，未创建 |
| 85 | `85-testing-ci.md` | 测试/CI/发布 | 至少一个内容区 | P3 排期，未创建 |

本线**无 70/80**：单加载器单版本，不设版本矩阵与加载器差异视图；版本线差异按上文"以 2847 为准"处理。

## 阅读顺序建议

- 新手：`00-scaffold.md`（先建出能跑的工程）→ `10-blocks-items.md`（第一个方块/物品/配方）→ `references/03-assets.md`（贴图与模型，按 `10-blocks-items.md` 的"模型/贴图（1.12.2 差异）"节对照复数目录）。
- 遇错：先查 `references/20-legacy-1122-lane/90-troubleshooting.md`，跨文件通用故障优先，各文件"新手易错点"只管本文件专属问题。

## 工具链警示

- **必须 JDK 8**：ForgeGradle 2.3 在 JDK 9+ 无法工作；本仓库验证用便携 JDK 8（`.tools/jdk8/jdk8u502-b07`）。注意本机 `D:\java` 实为 JRE（无 `tools.jar` / `javac`），不能用于构建；`JAVA_HOME` 必须指向便携 JDK8。
- **必须 Gradle 4.9**：一律走 wrapper（`gradle/wrapper/gradle-wrapper.properties` 指向腾讯云镜像 `gradle-4.9-bin.zip`），不要用系统 gradle。
- 工具链路径与实测记录见 `docs/verification-notes.md`；首次构建会下载 Gradle 4.9 与 Minecraft 1.12.2 环境，耗时较长，需要网络。

## 体积预算与拆分规则

- 每个 reference 内容文件：目标 ≤20KB，硬上限 30KB（UTF-8 字节）。
- 超限拆分规则：按「核心 / 进阶 / 常见问题」拆子文件（如 `10-blocks-items.md` + `10-blocks-items-advanced.md`），拆分后必须同步更新本清单。
- 提交前用实际字节数核验。

## 核验

- 代码真相：本线正文中的代码必须与 `examples/forge-1.12.2/` 逐字一致；与模板不一致的写法禁止写入。
- 未验证标签：`10-blocks-items.md` 的食物/工具扩展写法标注"参考未验证 / v0.2 排期"；`90-troubleshooting.md` 的故障记录标注"推断（2026-08-10）"。
- 核验日期：2026-08-10。
