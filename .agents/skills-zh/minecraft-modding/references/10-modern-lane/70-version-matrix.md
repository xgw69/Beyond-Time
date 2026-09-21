# 现代线 70-version-matrix.md（1.21.x vs 1.20.1 差异视图）

<!-- 元信息块 -->
- 适用场景：要把现有写法适配到另一个版本（1.21.x ↔ 1.20.1），先查本表确认哪些字段/API 会变。
- 前置依赖：`references/10-modern-lane/10-blocks-items.md`（正文写法）
- 相关文件：`references/10-modern-lane/80-loader-patterns.md`（加载器差异）、`references/10-modern-lane/00-scaffold.md`（工具链）、`references/05-porting.md`（移植，v0.1 不存在则降级，见 00-intake）
- 适用加载器：NeoForge（同加载器版本差异）
- 适用版本线：1.21.x（主线）/ 1.20.1（差异）
- 核验日期：2026-08-10

## 目标

本表只描述「NeoForge 1.21.x 与 NeoForge 1.20.1」之间的写法差异，**不承担完整写法**；正文永远以「目标版本 + 目标加载器」的 lane 正文为准（如 1.21.x 写 10-blocks-items 本体，目标 1.20.1 时按本表调整）。跨版本 + 跨加载器（如 1.20.1 Forge → 1.21.4 NeoForge）属于**移植**，走 `references/05-porting.md`；v0.1 该文件不存在，按 00-intake 降级规则处理（明确告知暂不支持 + 替代路径）。

## 差异矩阵

| 内容类型 | 1.21.x 写法（P1 已编译验证） | 1.20.1 写法 | 差异等级 | 验证状态 |
|---|---|---|---|---|
| 注册方式（DeferredRegister） | `DeferredRegister.createBlocks/createItems(MODID)`；`registerSimpleBlock` / `registerSimpleItem` / `registerSimpleBlockItem("name", block)`（带名称重载，P0 实测 1.21.1 必须） | NeoForge 20.1 同套 API；`registerSimpleBlockItem` 无名称重载时的重载解析可能不同 | 低 | 1.20.1 行：参考未验证 / v0.2 排期（需 1.20.1 真实工程核对） |
| 主类与事件总线 | `@Mod(MODID)` + 构造器 `IEventBus bus`，`DeferredRegister.register(bus)` | 同左 | 低 | 1.20.1 行：参考未验证 / v0.2 排期 |
| 数据组件（Data Components） | 1.20.5+ 引入：`Item.Properties().component(...)`、`itemStack.set(DataComponents.X, ...)`；自定义组件用 `Registries.DATA_COMPONENT_TYPE` 注册 | 无 Data Components，用旧 NBT/Capability（如 `ItemStack.setTag`） | 高 | 1.20.1 行：参考未验证 / v0.2 排期 |
| 配方 `result` 字段 | `"result": { "id": "examplemod:example_item", "count": 1 }` | `"result": { "item": "minecraft:cobblestone", "count": 1 }`（`item` 字段） | 中 | 1.20.1 行：参考未验证 / v0.2 排期 |
| 资源包 `pack_format` | 34（1.21.1） | 15（1.20.1） | 低 | 1.20.1 行：参考未验证 / v0.2 排期 |
| `ResourceLocation` API | `ResourceLocation.fromNamespaceAndPath(ns, path)` / `ResourceLocation.parse("ns:path")`；`new ResourceLocation(ns, path)` 不可用 | `new ResourceLocation(ns, path)` 常用，`fromNamespaceAndPath` 尚未引入 | 中 | 1.20.1 行：参考未验证 / v0.2 排期 |
| 自定义附魔 | 数据驱动：`data/<modid>/enchantment/<name>.json`（见 15-extra-registries） | Java 类注册：DeferredRegister + `Enchantment` 子类/构造 | 高 | 1.20.1 行：参考未验证 / v0.2 排期 |
| 标签目录 | `data/<modid>/tags/item/`、`tags/block/`（单数） | `data/<modid>/tags/items/`、`tags/blocks/`（复数） | 中 | 1.20.1 行：参考未验证 / v0.2 排期 |
| 战利品表目录 | `data/<modid>/loot_table/blocks/`（单数） | `data/<modid>/loot_tables/blocks/`（复数） | 中 | 1.20.1 行：参考未验证 / v0.2 排期 |
| 工具链 | NeoForge 21.1.248 + ModDevGradle 2.0.143 + JDK 21 + Gradle 8.14 | NeoForge 20.1.x + ModDevGradle 2.0.x（具体版本需真实工程核对） | 中 | 1.20.1 行：参考未验证 / v0.2 排期 |

## 使用规则

1. 本表只描述差异，不承担完整写法；要完整写法回对应正文文件（10-blocks-items / 03-assets / 15-extra-registries）。
2. 同一加载器换版本：按本表逐行核对受影响点，其余写法原样保留。
3. 跨版本 + 跨加载器：属于移植，走 05-porting（v0.1 不存在则降级）。
4. 版本不在矩阵内（如 1.16、1.19）：明确不支持，不猜测（见 SKILL.md 失败处理）。

## 新手易错点

1. 照 1.20.1 教程写 `"result": { "item": ... }` 到 1.21 工程：配方不生效。
2. 照 1.20.1 写 `tags/items/`、`loot_tables/`：1.21 不认复数目录。
3. 照 1.20.1 写 `new ResourceLocation(...)`：1.21 编译不过，改用 `fromNamespaceAndPath`。
4. 把 1.21 的数据驱动附魔 JSON 当成 1.20.1 的 Java 注册，或反过来：两代写法完全不同，按本表切换。
5. 以为换版本只是改 `neoForge { version = ... }`：数据结构（配方/标签/战利品目录、附魔）会一起变，必须按本表逐行核对。

## 验证清单

- [ ] 自动：1.21.x 列已随 P1 模板与端到端样例验证（build + runClient）。
- [ ] 待环境：1.20.1 列需 1.20.1 真实工程逐行核对，v0.2 排期；本期不承诺。
