# 现代线导航（10-modern-lane）

<!-- 元信息块 -->
- 适用场景：已确认在"现代线"（NeoForge/Fabric 1.21.x、1.20.1）工作的 agent 或新手，打开本文件确定读哪个内容文件。
- 前置依赖：无
- 相关文件：`references/00-intake.md`（路由）、`references/03-assets.md`（顶层，贴图/模型规范）、`references/10-modern-lane/90-troubleshooting.md`（排错）
- 适用加载器：NeoForge（主线）/ Fabric（差异）
- 适用版本线：1.21.x（主线）/ 1.20.1（差异）
- 核验日期：2026-08-10

## 本线定位

现代线以 **NeoForge 1.21.x** 为主线写法（代码真相 = `examples/neoforge-1.21`，NeoForge 21.1.248 / ModDevGradle 2.0.143 / JDK 21），1.20.1 只做差异视图。执行时以「目标加载器 + 目标版本」对应的正文文件为准：版本差异查 `70-version-matrix.md`，加载器差异查 `80-loader-patterns.md`，两者只描述差异，不承担完整写法。

## 文件清单

| 编号 | 文件 | 职责 | 前置依赖 | 状态 |
|---|---|---|---|---|
| - | `README.md` | 本线导航 | 无 | P1 已交付 |
| 00 | `00-scaffold.md` | 新建工程脚手架 + 已有工程改造 | 无（P0 模板与工具链） | P1 已交付 |
| 10 | `10-blocks-items.md` | 方块/物品/配方/标签/战利品/数据生成 | 00-scaffold、03-assets | P1 已交付 |
| 15 | `15-extra-registries.md` | 附魔/药水/状态效果/声音/属性 | 10-blocks-items | P1 已交付（非测试内容标"参考未验证 / v0.2 排期"） |
| 70 | `70-version-matrix.md` | 同加载器版本差异（1.21.x vs 1.20.1） | 10-blocks-items | P1 已交付（1.20.1 行标"参考未验证 / v0.2 排期"） |
| 80 | `80-loader-patterns.md` | 同版本加载器差异（NeoForge→Fabric→Forge） | 10-blocks-items | P1 已交付（Fabric/Forge 行标"参考未验证 / v0.2 排期"） |
| 90 | `90-troubleshooting.md` | 现代线故障速查 | 无 | P0 最小版已交付 |
| 20 | `20-machines.md` | 机器/容器/能量/流体/JEI | 10-blocks-items | P2 排期，未创建 |
| 30 | `30-worldgen.md` | 世界生成 | 10-blocks-items（弱） | P2 排期，未创建 |
| 40 | `40-entities.md` | 实体 | 10-blocks-items（弱） | P2 排期，未创建 |
| 50 | `50-client.md` | 客户端 | 10-blocks-items（弱） | P2 排期，未创建 |
| 60 | `60-networking.md` | 网络 | 实体或机器 | P2 排期，未创建 |
| 85 | `85-testing-ci.md` | 测试/CI/发布 | 至少一个内容区 | P2 末排期，未创建 |

顶层相关文件：`references/03-assets.md`（贴图/模型/blockstate/pack.mcmeta 规范，P1 最小版已交付，P4 深化）。

## 阅读顺序建议

- 新手：`00-scaffold.md`（先建出能跑的工程）→ `10-blocks-items.md`（第一个方块/物品/配方）→ `references/03-assets.md`（贴图与模型，避免黑紫方块）。
- 进阶：`15-extra-registries.md`（附魔/药水/声音/属性）→ `70-version-matrix.md`（要适配 1.20.1 时）→ `80-loader-patterns.md`（要换加载器时）。
- 遇错：先查 `references/10-modern-lane/90-troubleshooting.md`，跨文件通用故障优先，各文件"新手易错点"只管本文件专属问题。

## 体积预算与拆分规则

- 每个 reference 内容文件：目标 ≤20KB，硬上限 30KB（UTF-8 字节）。
- 超限拆分规则：按「核心 / 进阶 / 常见问题」或按加载器拆子文件（如 `10-blocks-items.md` + `10-blocks-items-advanced.md`），拆分后必须同步更新本清单。
- 提交前用实际字节数核验，超限文件必须附拆分记录。

## 核验

- 代码真相：本线正文中的代码必须与 `examples/neoforge-1.21/` 逐字一致；与模板不一致的写法禁止写入。
- 未验证标签：70 的 1.20.1 行、80 的 Fabric/Forge 行、15 的非测试内容均标注"参考未验证 / v0.2 排期"。
- 核验日期：2026-08-10。
