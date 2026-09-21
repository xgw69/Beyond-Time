# 01-glossary.md（术语表）

> schema 固定四字段：中文定义 / 英文术语 / 类比（可选，显式标注"类比："）/ 首次出现文件。
> 新手模式下几乎必读。类比只用在确实贴切的词条上，拿不准宁可不写。
> 规则：API 类名/方法名/映射名一律原样，不翻译；新术语在本文件补齐后才算完成术语 diff。

## 1. 模组 ID（modid）

- 英文术语：`modid`
- 定义：mod 的唯一标识字符串，必须 ASCII 小写，通常与包名对应（如 `examplemod`）。资源位置、注册名都以它作为命名空间。
- 首次出现文件：`SKILL.md`（语言规则）

## 2. 注册表

- 英文术语：`Registry`
- 定义：游戏用来登记方块、物品等一切"内容"的中央清单。内容必须先登记（register）进注册表，游戏才知道它存在并给它分配 ID。
- 类比：注册表 = 游戏的内容清单。
- 首次出现文件：`references/02-common-concepts.md`（注册）

## 3. 副作用

- 英文术语：`Side`
- 定义：代码运行的"端"——客户端（物理客户端，显示画面）或服务端（物理服务器，逻辑权威）。区分物理端与逻辑端：单人游戏也有一个内置逻辑服务端。
- 首次出现文件：`references/02-common-concepts.md`（副作用与分端）

## 4. 物理端 / 逻辑端

- 英文术语：`physical side` / `logical side`
- 定义：物理端指真实的进程（客户端进程 / 服务器进程）；逻辑端指代码逻辑所处的环境（客户端逻辑 / 服务端逻辑）。单人游戏 = 客户端进程内同时运行逻辑客户端与逻辑服务端。
- 首次出现文件：`references/02-common-concepts.md`（副作用与分端）

## 5. 方块实体

- 英文术语：`Block Entity`
- 定义：挂在方块上的"数据盒子"，让方块能存数据、跑逻辑（如箱子存物品、熔炉烧东西）。区别于普通方块（无状态逻辑的静态外观）。
- 首次出现文件：`references/10-modern-lane/20-machines.md`（排期文件，P0 仅引用）

## 6. 数据生成

- 英文术语：`DataGen`
- 定义：用代码生成 JSON 数据文件（配方、模型、语言文件等）的工具链流程，避免手写大量重复 JSON；现代线（NeoForge 1.21.x）支持，1.12.2 不支持。
- 首次出现文件：`references/02-common-concepts.md`（构建流程）

## 7. 资源位置

- 英文术语：`ResourceLocation`
- 定义：Minecraft 里"命名空间 + 路径"的完整资源标识，格式 `namespace:path`，如 `examplemod:example_block`；命名空间默认用 modid。
- 类比：资源位置 = 文件的"完整路径"。
- 首次出现文件：`references/02-common-concepts.md`（资源位置）

## 8. 标签

- 英文术语：`Tag`
- 定义：给方块/物品/生物等分组用的字符串集合（如 `minecraft:logs` 包含所有原木）。配方、战利品、世界生成都能按标签引用，而不是逐个列 ID。
- 首次出现文件：`references/10-modern-lane/10-blocks-items.md`（P1 已交付）

## 9. 战利品表

- 英文术语：`Loot Table`
- 定义：定义"破坏方块 / 打开箱子 / 击杀生物"时掉落什么的 JSON 规则。
- 首次出现文件：`references/10-modern-lane/10-blocks-items.md`（P1 已交付）

## 10. Mixin

- 英文术语：`Mixin`
- 定义：在运行时"织入"并修改原版类的方法/字段的技术，用于不改原版源码实现钩子；需要配合对应加载器的 Mixin 支持（Fabric/NeoForge 常用，Forge 1.12.2 少用）。
- 首次出现文件：`references/10-modern-lane/90-troubleshooting.md`

## 11. 访问宽限器

- 英文术语：`Access Widener`
- 定义：Fabric 生态里放宽原版类成员访问级别（private → public 等）的声明文件（`.accesswidener`）。
- 首次出现文件：`references/10-modern-lane/80-loader-patterns.md`（P1 已交付）

## 12. 访问转换器

- 英文术语：`Access Transformer`
- 定义：Forge/NeoForge 生态里修改类成员可见性或重命名的声明文件（`accesstransformer.cfg` 等）。
- 首次出现文件：`references/10-modern-lane/80-loader-patterns.md`（P1 已交付）

## 13. 映射

- 英文术语：`Mappings`（Yarn / Mojmap / Intermediary）
- 定义：把混淆后的 Minecraft 类/方法/字段名还原成可读名字的对照表。主流有 Mojang 官方映射（Mojmap）、Fabric 的 Yarn 与中间名 Intermediary；mod 编译时按映射名写代码。
- 首次出现文件：`LICENSE`（映射名称免责声明）与 `references/02-common-concepts.md`（构建流程）

## 14. mods.toml

- 英文术语：`mods.toml`
- 定义：Forge/NeoForge 加载器读取的 mod 元数据文件（新版本为 `neoforge.mods.toml`），声明 modid、版本、依赖、许可等。
- 首次出现文件：`references/00-intake.md`（探测规则）

## 15. fabric.mod.json

- 英文术语：`fabric.mod.json`
- 定义：Fabric 加载器读取的 mod 元数据文件，JSON 格式，声明 modid、版本、入口点等。
- 首次出现文件：`references/00-intake.md`（探测规则）

## 16. metadata（元数据）

- 英文术语：`metadata`
- 定义：描述 mod 自身信息的字段集合（modid、名称、版本、作者、依赖），随 mod 文件发布，加载器靠它识别与排序。
- 首次出现文件：`references/00-intake.md`（探测规则）

## 17. 模组加载器

- 英文术语：`Mod Loader`
- 定义：负责启动时加载 mod、管理依赖与入口的框架，如 Forge、NeoForge、Fabric Loader。加载器决定了 mod 的元数据格式与 API 生态。
- 首次出现文件：`references/02-common-concepts.md`（加载器差异概览）

## 18. 整合包

- 英文术语：`Modpack`
- 定义：把多个 mod 与配置打包成一份"直接开玩"的分发包。本技能不做整合包推荐，也不做纯净整合包。
- 首次出现文件：`docs/trigger-tests.md`（反例 N8）

## 19. 客户端 / 服务端

- 英文术语：`Client` / `Server`
- 定义：客户端 = 玩家运行游戏画面的一端；服务端 = 权威计算世界逻辑的一端。mod 代码可能只在其中一端运行，也可能双端都运行（靠 Side 区分）。
- 首次出现文件：`references/02-common-concepts.md`（副作用与分端）

## 20. 崩溃报告

- 英文术语：`crash report`
- 定义：游戏崩溃时自动生成的诊断文件（含堆栈与错误原因），排错的第一步就是读它；mod 开发排查常以 `latest.log` + crash report 为准。
- 首次出现文件：`references/10-modern-lane/90-troubleshooting.md`

## 21. NeoForge

- 英文术语：`NeoForge`
- 定义：Forge 的现代继任加载器（1.21.x 主线推荐），元数据用 `neoforge.mods.toml`，构建用 ModDevGradle。
- 首次出现文件：`SKILL.md`（frontmatter description）

## 22. Fabric

- 英文术语：`Fabric`
- 定义：轻量加载器，元数据用 `fabric.mod.json`，常配合 Fabric API；生态偏轻量模组。
- 首次出现文件：`SKILL.md`（frontmatter description）

## 23. Forge

- 英文术语：`Forge`
- 定义：老牌加载器；1.12.2 独立线使用它（ForgeGradle 2.3 + `GameRegistry`），新版本线已由 NeoForge 继承。
- 首次出现文件：`SKILL.md`（frontmatter description）

## 24. Quilt

- 英文术语：`Quilt`
- 定义：从 Fabric 分叉的社区加载器。本技能只提及、不建独立工程，默认按 Fabric 处理。
- 首次出现文件：`README.md`（技能地图）

## 25. DeferredRegister

- 英文术语：`DeferredRegister`
- 定义：NeoForge 现代线的延迟注册 API：先声明"注册表 + 名称 + 工厂"，加载时统一提交到注册表。
- 首次出现文件：`references/02-common-concepts.md`（注册）

## 26. GameRegistry

- 英文术语：`GameRegistry`
- 定义：Forge 1.12.2 的注册入口；该版本中 `register` 为 private，需经 `findRegistry(...).register(...)` 或注册事件登记。
- 首次出现文件：`references/02-common-concepts.md`（注册）

## 27. ModDevGradle

- 英文术语：`ModDevGradle`
- 定义：NeoForge 官方的 Gradle 插件（`net.neoforged.moddev`），负责配置 NeoForge、run 任务与数据生成。
- 首次出现文件：`references/02-common-concepts.md`（构建流程）

## 28. ForgeGradle

- 英文术语：`ForgeGradle`
- 定义：老 Forge 的 Gradle 插件；1.12.2 用 2.3-SNAPSHOT，仅支持 JDK 8。
- 首次出现文件：`references/02-common-concepts.md`（加载器差异概览）

## 29. Gradle wrapper（gradlew）

- 英文术语：`Gradle wrapper`（`gradlew` / `gradlew.bat`）
- 定义：随仓库提交的 Gradle 启动脚本，自动下载并运行工程指定版本的 Gradle，本机无需全局安装。
- 首次出现文件：`references/02-common-concepts.md`（构建流程）

## 30. GUI

- 英文术语：`GUI`
- 定义：图形用户界面；mod 语境常指机器/容器/自定义界面（对应内容类型 50-client）。
- 首次出现文件：`references/00-intake.md`（内容→文件映射表）

## 31. JEI

- 英文术语：`JEI`（Just Enough Items）
- 定义：物品/配方查看类 mod（"足够多的物品"），机器类 mod 常做 JEI 配方显示集成。
- 首次出现文件：`references/00-intake.md`（内容→文件映射表）

## 32. 数据包

- 英文术语：`datapack`
- 定义：以 JSON 定制游戏玩法（配方、战利品、结构等）的资源包形式，**不是 mod**；本技能不触发数据包需求。
- 首次出现文件：`SKILL.md`（触发反例）

## 33. 资源包

- 英文术语：`resource pack`
- 定义：替换贴图/模型/语言等外观与文本的包，**不是 mod**；本技能不触发资源包需求（mod 内资源除外）。
- 首次出现文件：`SKILL.md`（触发反例）

## 34. 服务端插件

- 英文术语：`server plugin`（Bukkit / Paper / Spigot）
- 定义：运行在服务端软件上的插件生态（如 Bukkit API），与 Java 版 mod（客户端+服务端均可）是两回事；本技能不支持。
- 首次出现文件：`SKILL.md`（触发反例）

## 35. 基岩版附加包

- 英文术语：`Bedrock addon`
- 定义：基岩版（含网易基岩版）的附加包格式，与 Java 版 mod 不兼容；本技能不支持。
- 首次出现文件：`SKILL.md`（触发反例）

## 36. MCP

- 英文术语：`MCP`（Mod Coder Pack）
- 定义：老式反混淆/反编译工具链（1.12.2 时代常见）；本技能不提供 MCP 安装咨询（trigger-tests 反例 N10）。
- 首次出现文件：`docs/trigger-tests.md`（反例 N10）

## 37. ItemStack

- 英文术语：`ItemStack`
- 定义："物品堆"：物品类型 + 数量 + 附加数据的实例，配方、容器、掉落都以它为单位。
- 首次出现文件：`examples/forge-1.12.2/src/main/java/com/example/mod/ExampleMod.java`（配方代码）

## 38. IEventBus

- 英文术语：`IEventBus`
- 定义：NeoForge/Forge 的事件总线接口；注册表挂在总线上、事件监听器通过它分发。
- 首次出现文件：`examples/neoforge-1.21/src/main/java/com/example/mod/ExampleMod.java`

## 39. blockstate（方块状态）

- 英文术语：`blockstate`
- 定义：方块的"状态定义"，也指 `assets/<ns>/blockstates/<name>.json` 文件：把状态映射到模型；
  缺失时游戏内出现黑紫方块/`missing model`。
- 首次出现文件：`references/10-modern-lane/90-troubleshooting.md`（条目 2）

## 40. 配方

- 英文术语：`Recipe`
- 定义：定义"怎么加工出什么"的 JSON 规则，按类型分合成配方（`crafting_shaped` / `crafting_shapeless`）与熔炼配方（`smelting`）等；1.21 起 `result` 用 `id` 字段。
- 首次出现文件：`references/10-modern-lane/10-blocks-items.md`（合成与熔炼配方）

## 41. 附魔

- 英文术语：`Enchantment`
- 定义：给武器/工具/盔甲附加特殊效果（如锋利、保护）的机制；1.21.1 起为数据驱动，用 `data/<ns>/enchantment/<name>.json` 定义。
- 首次出现文件：`references/10-modern-lane/15-extra-registries.md`（自定义附魔）

## 42. 状态效果

- 英文术语：`MobEffect`（旧称 Effect）
- 定义：作用于生物的持续效果（如中毒、再生）；1.21.1 注册用 `DeferredRegister.create(Registries.MOB_EFFECT, ...)`。
- 首次出现文件：`references/10-modern-lane/15-extra-registries.md`（状态效果与药水）

## 43. 药水

- 英文术语：`Potion`
- 定义：把状态效果做成可喝/可投掷物品的包装；要让自定义效果进药水，还需注册 `Potion` 与酿造配方。
- 首次出现文件：`references/10-modern-lane/15-extra-registries.md`（状态效果与药水）

## 44. 音效事件

- 英文术语：`SoundEvent`
- 定义：代码里引用某个声音的"事件"；要真正播放还需 `sounds.json` 声明 + `.ogg` 音频文件。
- 首次出现文件：`references/10-modern-lane/15-extra-registries.md`（音效事件）

## 45. 属性

- 英文术语：`Attribute`
- 定义：生物数值属性（如攻击力、移动速度）；自定义属性注册用 `Registries.ATTRIBUTE` + `RangedAttribute`。
- 首次出现文件：`references/10-modern-lane/15-extra-registries.md`（自定义属性）

## 46. 数据组件

- 英文术语：`Data Components`
- 定义：1.20.5+ 取代旧 NBT 的物品附加数据体系（`itemStack.set(DataComponents.X, ...)`）；1.20.1 及以前用 NBT。
- 首次出现文件：`references/10-modern-lane/70-version-matrix.md`（差异矩阵）

## 47. 数据提供器

- 英文术语：`DataProvider`
- 定义：NeoForge 数据生成（DataGen）里负责产出某一类 JSON 的代码类；本仓库 v0.1 用手写 JSON 路线，DataProvider 示例 v0.2 排期。
- 首次出现文件：`references/10-modern-lane/10-blocks-items.md`（数据生成）

## 48. 数据驱动

- 英文术语：`data-driven`
- 定义：把内容定义放进数据文件（JSON）而非 Java 代码的机制；1.21.1 附魔等已数据驱动。
- 首次出现文件：`references/10-modern-lane/15-extra-registries.md`（自定义附魔）

## 49. 标识符

- 英文术语：`Identifier`
- 定义：Fabric 版的"资源位置"（对应 NeoForge 的 ResourceLocation），格式 `namespace:path`，如 `minecraft:stone`。
- 首次出现文件：`references/10-modern-lane/80-loader-patterns.md`（等价写法对照表）

## 50. ItemFood

- 英文术语：`ItemFood`
- 定义：Forge 1.12.2 的食物物品基类；构造参数为（恢复饱食度、饱和度系数、是否可喂狼），如 `new ItemFood(4, 0.3F, false)`。1.21 现代线用 `food(...)` 属性写法，两者不同。
- 首次出现文件：`references/20-legacy-1122-lane/10-blocks-items.md`（1.12.2 特色差异：食物与工具）

## 51. 未本地化名称

- 英文术语：`unlocalizedName`
- 定义：1.12.2 时代物品/方块的名字键前缀（配合 `en_us.lang` 的 `item.<modid>.<name>.name` / `tile.<modid>.<name>.name` 使用），通过 `setUnlocalizedName(...)` 设置。1.13+ 起被语言文件键（language key）机制取代，不再需要该方法。
- 首次出现文件：`references/20-legacy-1122-lane/10-blocks-items.md`（注册方块与物品）

## 52. SRG 名

- 英文术语：`SRG name`（Searge name）
- 定义：Minecraft 混淆后运行环境的类/方法/字段名体系（如 `Material.field_151576_e`），正式客户端加载 jar 时使用；与开发期 MCP 名（如 `Material.ROCK`）相对。发布 jar 必须 reobf 成 SRG 名，否则正式客户端抛 `NoSuchFieldError`。
- 首次出现文件：`references/20-legacy-1122-lane/00-scaffold.md`（build.gradle 与映射名说明）
