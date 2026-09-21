# 现代线 80-loader-patterns.md（NeoForge / Fabric / Forge 等价写法）

<!-- 元信息块 -->
- 适用场景：同一内容要在另一个加载器里实现（如 NeoForge → Fabric、NeoForge → Forge），先查本表逐功能点对照。
- 前置依赖：`references/10-modern-lane/10-blocks-items.md`（NeoForge 主写法）
- 相关文件：`references/10-modern-lane/70-version-matrix.md`（版本差异）、`references/02-common-concepts.md`（运行机制通识）、`references/05-porting.md`（移植，v0.1 不存在则降级）
- 适用加载器：NeoForge（主）/ Fabric / Forge
- 适用版本线：1.21.x（NeoForge/Fabric）/ 1.20.1（Forge）
- 核验日期：2026-08-10

## 目标

本表把 NeoForge 1.21.x 的主写法（以 10-blocks-items 为准）逐个功能点翻译成 Fabric 1.21.x 与 Forge 1.20.1 的等价写法，方便换加载器时知道"对应概念在哪里"。运行机制（注册/分端/构建流程）见 02-common-concepts，本表**只做等价对照，不承担完整写法**；正文只写主写法（NeoForge 1.21.x），不要在正文里重复三份代码。

## 等价写法对照表

| 功能点 | NeoForge 1.21.x（P1 已编译验证） | Fabric 1.21.x | Forge 1.20.1 | 验证状态 |
|---|---|---|---|---|
| 注册入口 | `DeferredRegister.createBlocks/createItems(MODID)`，主类构造器 `register(bus)` | `Registry.register(Registries.BLOCK, Identifier.of(MODID, "name"), block)`；入口 = `public static void onInitialize()`（`fabric.mod.json` 的 `entrypoint`） | `DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)` + `register`；或 `RegisterEvent` | NeoForge：P1 已编译验证；Fabric/Forge 行：参考未验证 / v0.2 排期 |
| 方块物品注册 | `ITEMS.registerSimpleBlockItem("name", ModBlocks.EXAMPLE_BLOCK)`（`DeferredItem<BlockItem>`） | 方块物品 = 普通物品：`Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()))` | `ITEMS.register("name", () -> new BlockItem(block, new Item.Properties()))` | 同上 |
| 事件订阅 | 主类构造器拿 `IEventBus`：`bus.addListener(MyEvents::onX)`；或 `@EventBusSubscriber` + 静态方法 | Fabric API 的接口回调/事件类（如 `PlayerBlockBreakEvents`、`ServerPlayConnectionEvents`） | `@Mod.EventBusSubscriber` + `@SubscribeEvent`，或 `MinecraftForge.EVENT_BUS.register(...)` | 同上 |
| 客户端初始化入口 | `@EventBusSubscriber(value = Dist.CLIENT)` + `FMLClientSetupEvent`（ModDevGradle `runs { client {} }`） | `fabric.mod.json` 的 `"client"` entrypoint → `ClientModInitializer.onInitializeClient()` | `@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)` + `FMLClientSetupEvent` | 同上 |
| 元数据文件 | `META-INF/neoforge.mods.toml` | `fabric.mod.json` | `META-INF/mods.toml` | 同上 |
| 工具链 | ModDevGradle 2.0.143 + JDK 21 + Gradle 8.14 | Fabric Loom + JDK 17/21 | ForgeGradle + JDK 17（1.20.1 需要 JDK 17） | 同上 |

## 使用规则

1. 执行以「目标加载器 + 目标版本」为准：正文永远只写主写法（NeoForge 1.21.x），换加载器时按本表逐功能点改写，不要整文件照抄三份。
2. v0.1 默认单加载器工程（每加载器一个独立工程）；多加载器单工程（Architectury / Stonecutter）属 v0.2 进阶内容。
3. 跨版本 + 跨加载器（如 1.20.1 Forge → 1.21.4 NeoForge）属于移植，走 05-porting（v0.1 不存在则降级）。
4. Fabric/Forge 行全部为"参考未验证 / v0.2 排期"：本期只在 NeoForge 端到端验证过，换加载器前必须用真实工程核对。

## 新手易错点

1. 把 NeoForge 的 `DeferredRegister` 直接搬进 Fabric：Fabric 没有 DeferredRegister，注册用 `Registry.register`。
2. 事件模型混用：NeoForge 的事件总线（`IEventBus`）与 Fabric 的接口回调不互通，不能照抄监听器写法。
3. 客户端初始化入口放错：Fabric 的客户端逻辑必须走 `fabric.mod.json` 的 `"client"` entrypoint，NeoForge 走 `Dist.CLIENT` 事件，两者不通用。
4. 元数据文件混用：`neoforge.mods.toml`、`fabric.mod.json`、`mods.toml` 各认各的，放错文件加载器直接忽略或报错。
5. 期望一份源码同时跑三加载器：v0.1 默认单加载器工程；多加载器工程是 v0.2 进阶，不要自己硬塞。

## 验证清单

- [ ] 自动：NeoForge 列随 P1 模板与端到端样例验证（build + runClient）。
- [ ] 待环境：Fabric/Forge 列需各自真实工程核对，v0.2 排期；本期不承诺（标注"参考未验证"）。
