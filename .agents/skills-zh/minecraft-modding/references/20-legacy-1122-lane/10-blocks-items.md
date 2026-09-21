# 1.12.2 线 10-blocks-items.md（方块 / 物品 / 配方 / 语言文件 / 模型贴图）

<!-- 元信息块 -->
- 适用场景：在 Forge 1.12.2 工程里加新方块、新物品、合成配方、语言文件，或给它们配模型/贴图（本线内容型核心）。
- 前置依赖：`references/20-legacy-1122-lane/00-scaffold.md`（工程结构，第 5 节主类骨架）
- 相关文件：`references/03-assets.md`（顶层贴图/模型规范，本文件只写 1.12.2 差异）、`references/20-legacy-1122-lane/90-troubleshooting.md`（排错）、`references/01-glossary.md`（术语）
- 适用加载器：Forge
- 适用版本线：1.12.2（Forge 14.23.5.2847）
- 核验日期：2026-08-10

## 目标

本文件教你在 1.12.2 工程里加"新方块 + 新物品 + 配方 + 语言文件 + 模型贴图"。代码真相 = `examples/forge-1.12.2/`：注册/配方代码与模板 `ExampleMod.java` 逐字一致，资源 JSON 与模板 `assets/` 文件逐字一致。1.12.2 的注册方式与现代 NeoForge（`DeferredRegister`）完全不同：注册入口是 `GameRegistry`，全部写在主类的生命周期方法里。

## 主写法

### 1. 注册方块与物品（GameRegistry）

下面的字段定义与 `preInit` 注册方法来自模板 `ExampleMod.java`，逐字一致：

```java
    public static final Block EXAMPLE_BLOCK = new Block(Material.ROCK)
            .setRegistryName(MODID, "example_block")
            .setUnlocalizedName(MODID + ".example_block");
    public static final Item EXAMPLE_BLOCK_ITEM = new ItemBlock(EXAMPLE_BLOCK)
            .setRegistryName(EXAMPLE_BLOCK.getRegistryName())
            .setUnlocalizedName(MODID + ".example_block");
    public static final Item EXAMPLE_ITEM = new Item()
            .setRegistryName(MODID, "example_item")
            .setUnlocalizedName(MODID + ".example_item");
```

```java
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.findRegistry(Block.class).register(EXAMPLE_BLOCK);
        GameRegistry.findRegistry(Item.class).register(EXAMPLE_BLOCK_ITEM);
        GameRegistry.findRegistry(Item.class).register(EXAMPLE_ITEM);
    }
```

三个对象各司其职：

| 对象 | 写法要点 |
|---|---|
| 方块 `EXAMPLE_BLOCK` | `new Block(Material.ROCK)` 建方块 → `setRegistryName(MODID, "example_block")` 给注册名 → `setUnlocalizedName(MODID + ".example_block")` 给翻译键前缀 |
| 方块物品 `EXAMPLE_BLOCK_ITEM` | `new ItemBlock(EXAMPLE_BLOCK)` 包住方块 → `setRegistryName(EXAMPLE_BLOCK.getRegistryName())` 与方块**同名**（1.12.2 约定）→ `setUnlocalizedName` 与方块相同 |
| 普通物品 `EXAMPLE_ITEM` | `new Item()` → `setRegistryName(MODID, "example_item")` → `setUnlocalizedName(MODID + ".example_item")` |

注册要点：

- 1.12.2（2847）的 `GameRegistry.register(...)` 是 private，必须用 `GameRegistry.findRegistry(Block.class).register(...)` / `GameRegistry.findRegistry(Item.class).register(...)`（见 `docs/verification-notes.md` 实测记录）。
- **`setRegistryName` 不能省**：注册名（registry name）是加载器给内容分配的全局唯一键，格式 `modid:name`；漏掉会注册键为 null，内容不加载（见 90-troubleshooting 条目 3）。
- 1.12.2 是"扁平化（flattening）"之前的版本：方块/物品的 **metadata（数据值/damage）** 与 **注册名** 是分离的两套概念。注册名是字符串唯一键（`examplemod:example_block`），metadata 是 0-15 的整数，用来区分同一个注册名下的子类型（如羊毛颜色、不同木材）。本文件注册的都是无子类型内容（metadata 恒为 0），不需要碰 metadata；带子类型的写法属 P3 扩展（legacy 15 排期）。

### 2. 语言文件 en_us.lang

`src/main/resources/assets/examplemod/lang/en_us.lang` 完整内容（与模板逐字一致）：

```properties
item.examplemod.example_item.name=Example Item
tile.examplemod.example_block.name=Example Block
```

1.12.2 的键名规则（注意与 1.21 的 `en_us.json` 不同）：

- 物品：`item.<modid>.<注册名>.name`
- 方块：`tile.<modid>.<注册名>.name`（方块用 `tile.` 前缀，是 1.12.2 的硬编码约定，不是笔误）
- 键以 `.name` 结尾；文件是 `.lang` 格式（`键=值`），不是 JSON。

中文显示用同样的键建 `zh_cn.lang`；注册名必须是 `setRegistryName` 里写的那个字符串。

### 3. 1.12.2 特色差异：食物与工具（参考未验证 / v0.2 排期）

以下为 1.12.2 标准 API 的最小示例，**模板未包含、未随本线端到端验证**，按 best-effort 标注"参考未验证 / v0.2 排期"，需要时再按 90-troubleshooting 排错。

食物用 `ItemFood`（构造参数 = 恢复饱食度、饱和度系数、是否可喂狼）：

```java
    public static final Item EXAMPLE_FOOD = new ItemFood(4, 0.3F, false)
            .setRegistryName(MODID, "example_food")
            .setUnlocalizedName(MODID + ".example_food");
```

工具的材料用 `Item.ToolMaterial`，通过 Forge 的 `EnumHelper.addToolMaterial` 定义后传入工具构造器：

```java
    public static final Item.ToolMaterial EXAMPLE_MATERIAL =
            EnumHelper.addToolMaterial("EXAMPLE", 2, 500, 6.0F, 2.0F, 14);
    public static final Item EXAMPLE_PICKAXE = new ItemPickaxe(EXAMPLE_MATERIAL)
            .setRegistryName(MODID, "example_pickaxe")
            .setUnlocalizedName(MODID + ".example_pickaxe");
```

### 4. 配方

#### 代码配方（与模板逐字一致）

模板用代码注册一个 1 圆石 → 1 `example_item` 的有序合成，写在 `init` 里：

```java
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.addShapedRecipe(
                new ResourceLocation(MODID, "example_item"), null,
                new ItemStack(EXAMPLE_ITEM),
                "X", 'X', Blocks.COBBLESTONE);
    }
```

签名：`addShapedRecipe(ResourceLocation 注册键, ResourceLocation 分组, ItemStack 产物, Object... 形状)`。第一个参数用 `new ResourceLocation(MODID, "example_item")` 给配方一个唯一注册键；`null` 表示不分组；`"X", 'X', Blocks.COBBLESTONE` 是形状语法（字符串行 + 占位符字符 + 对应物品，空格表示空格子）。

#### JSON 合成配方（1.12.2 格式）

1.12.2 的 JSON 合成配方放 **`src/main/resources/assets/<modid>/recipes/`**（不是 `data/`，那是 1.13+ 的路径）。`result` 用 **`item`** 键（不是 1.21 的 `id`）。以下为 1.12.2 真实数据格式（对照 Forge 1.12 官方文档核验）。

`assets/examplemod/recipes/example_item.json`（与代码配方等价的有序合成，完整示例）：

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": ["X"],
  "key": { "X": { "item": "minecraft:cobblestone" } },
  "result": { "item": "examplemod:example_item", "count": 1 }
}
```

无序合成完整示例：

```json
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    { "item": "minecraft:cobblestone" },
    { "item": "minecraft:cobblestone" }
  ],
  "result": { "item": "examplemod:example_item", "count": 2 }
}
```

要点：`pattern` + `key` 定义有序形状（空格 = 空格子）；`ingredients` 定义无序材料；`result` 里 `item` 是产物注册名，`count` 是数量（默认 1）；`data` 字段可指定 metadata（无子类型的物品不用写）。

#### 熔炼（1.12.2 不走 JSON）

**计划偏离说明**：原计划要求"熔炼配方 JSON 完整示例（`minecraft:smelting`）"，但 1.12.2 的熔炼**不是 JSON 配方**——Forge 1.12 官方文档明确"烧炼要用 `GameRegistry.addSmelting(input, output, exp)`，因为烧炼现在不是基于 JSON 的"（1.13 起熔炼才进 JSON）。按设计文档"代码真相 / 真实可运行"原则，本文件不写会在 1.12.2 中静默失效的熔炼 JSON，改给正确的代码写法（参考写法，非模板代码，v0.2 排期验证）：

```java
GameRegistry.addSmelting(Blocks.COBBLESTONE, new ItemStack(EXAMPLE_ITEM), 0.1F);
```

### 5. 模型/贴图（1.12.2 差异）

以 `references/03-assets.md` 为基础，1.12.2 与 1.21 的差异如下，所有 JSON 与模板 `assets/` 文件逐字一致：

**目录是复数**：方块贴图 `assets/<modid>/textures/blocks/<name>.png`，物品贴图 `assets/<modid>/textures/items/<name>.png`（1.21 是单数 `block/`、`item/`）。模型/blockstate 引用贴图时也带复数段（见下）。

**blockstate** `assets/examplemod/blockstates/example_block.json`（简单方块，完整内容）：

```json
{"variants": {"normal": {"model": "examplemod:example_block"}}}
```

差异点：1.12.2 简单方块的状态键是 `"normal"`（1.21 是 `""`），且 `model` 值写 `examplemod:example_block`（**不带 `block/` 前缀**，由加载器按方块模型解析到 `models/block/example_block.json`）。

**方块模型** `assets/examplemod/models/block/example_block.json`（`cube_all` = 六面同一张贴图，完整内容）：

```json
{"parent": "minecraft:block/cube_all", "textures": {"all": "examplemod:blocks/example_block"}}
```

**物品模型** `assets/examplemod/models/item/example_item.json`（`item/generated` = 用贴图直接显示，完整内容）：

```json
{"parent": "minecraft:item/generated", "textures": {"layer0": "examplemod:items/example_item"}}
```

**方块物品模型** `assets/examplemod/models/item/example_block.json`（背包/手持时显示成方块 = parent 直接指向方块模型，完整内容）：

```json
{"parent": "examplemod:block/example_block"}
```

**客户端模型注册**：1.12.2 用 `ModelLoader.setCustomModelResourceLocation`，`ModelResourceLocation` 的包名是 `net.minecraft.client.renderer.block.model`（1.21 是 `net.minecraft.client.resources.model`，注意区别）。模板代码（逐字一致）：

```java
    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void clientPreInit(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(EXAMPLE_BLOCK_ITEM, 0,
                new ModelResourceLocation(EXAMPLE_BLOCK.getRegistryName(), "normal"));
        ModelLoader.setCustomModelResourceLocation(EXAMPLE_ITEM, 0,
                new ModelResourceLocation(EXAMPLE_ITEM.getRegistryName(), "inventory"));
    }
```

签名是 `setCustomModelResourceLocation(Item, int metadata, ModelResourceLocation)`：第二个参数是 metadata（无子类型 = 0），第三个参数由注册名 + 变体字符串构成（方块物品用 `"normal"` 与 blockstate 对应，普通物品用 `"inventory"`）。**该方法第一个参数要求 `Item`**，所以方块物品传 `EXAMPLE_BLOCK_ITEM` 而不是 `EXAMPLE_BLOCK`（P0 实测修正，见 `docs/verification-notes.md`）。

**占位贴图**：生成方法同 03-assets 的 System.Drawing 脚本，唯一差异是保存目录用复数（`textures\blocks\example_block.png` / `textures\items\example_item.png`），方块贴图铺满（alpha=255），物品图标透明底（alpha=0）再画图案。

## 新手易错点

1. 忘记 `setRegistryName`：注册键为空，内容不加载或启动报错（见 90-troubleshooting 条目 3）。
2. metadata 概念混淆：1.12.2 里 metadata（damage/数据值）与注册名是两套东西；本文件内容 metadata 恒为 0，不需要写；带子类型（`setHasSubtypes(true)`）的物品在配方 JSON 里**必须**写 `data` 字段，否则任意 metadata 都能参与合成。
3. 语言文件键名写错：1.12.2 是 `en_us.lang`（非 JSON），物品 `item.<modid>.<name>.name`、方块 `tile.<modid>.<name>.name`；键名写错游戏里显示键名原文。
4. 纹理目录用单数：1.12.2 是 `textures/blocks/`、`textures/items/`（复数），照 1.21 教程写 `texture/block/` 会黑紫（见 90-troubleshooting 条目 4）。
5. JDK 版本不是 8：ForgeGradle 2.3 崩（`Could not find tools.jar` 类），见 90-troubleshooting 条目 1。
6. 配方 JSON 放错位置或文件名以下划线开头：1.12.2 配方在 `assets/<modid>/recipes/`（不是 `data/`），文件名不能以 `_` 开头（Forge 保留给 `_factories.json` 等）。
7. blockstate 的 `model` 值照 1.21 写 `examplemod:block/example_block`：1.12.2 模板写 `examplemod:example_block`（不带 `block/`），照模板写。

## 验证清单

- [ ] 自动（对照模板）：注册代码、`en_us.lang`、blockstate/模型 JSON 与 `examples/forge-1.12.2/` 逐字一致。
- [ ] 自动：`gradlew build` 输出 `BUILD SUCCESSFUL`（JAVA_HOME=便携 JDK8）。
- [ ] 自动：`gradlew runClient` 冒烟到主菜单，日志出现 `Forge Mod Loader has successfully loaded` 且 FATAL 计数 0。
- [ ] 人工：进游戏后 `/give @p examplemod:example_item` 拿到物品且图标正确。
- [ ] 人工：工作台 1 圆石合成出 `example_item`（代码配方生效）。
- [ ] 人工：放置 `example_block`，贴图正确（非黑紫）。

## 已有项目改造

流程见 `references/20-legacy-1122-lane/00-scaffold.md`「已有项目改造」：定位已有 `GameRegistry.findRegistry(...).register(...)` 注册入口 → 在**同处**追加本文件的字段定义与注册（不新建第二个入口）→ 补 `en_us.lang` → 按第 5 节补 blockstate/模型/复数目录贴图 → 配方放 `assets/<modid>/recipes/` → 增量验证（build + runClient + 进游戏人工操作）。
