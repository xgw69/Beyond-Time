# 现代线 10-blocks-items.md（方块 / 物品 / 配方 / 标签 / 战利品）

<!-- 元信息块 -->
- 适用场景：加新方块、新物品、食物、工具、合成/熔炼配方、标签、方块战利品（内容型核心需求）。
- 前置依赖：`references/10-modern-lane/00-scaffold.md`（工程结构）、`references/03-assets.md`（贴图/模型/blockstate）
- 相关文件：`references/10-modern-lane/70-version-matrix.md`（1.20.1 差异）、`references/10-modern-lane/80-loader-patterns.md`（加载器差异）、`references/10-modern-lane/15-extra-registries.md`（食物进阶/状态效果）、`references/10-modern-lane/90-troubleshooting.md`（排错）、`references/01-glossary.md`（术语）
- 适用加载器：NeoForge
- 适用版本线：1.21.x（NeoForge 21.1.248 / MC 1.21.1）
- 核验日期：2026-08-10

## 目标

本文件教你把"新方块 + 新物品 + 配方 + 标签 + 战利品"完整接入 NeoForge 1.21 工程。代码真相 = `examples/neoforge-1.21/`：注册类的代码块与该模板逐字一致；配方/标签/战利品 JSON 为 1.21.1 真实数据格式（已对照 21.1.248 原版数据核验）。

## 主写法

### 1. 注册方块与物品：DeferredRegister 模式

`src/main/java/com/example/mod/ModBlocks.java` 完整内容（与模板逐字一致）：

```java
package com.example.mod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ExampleMod.MODID);
    public static final Supplier<Block> EXAMPLE_BLOCK =
            BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of());
}
```

`src/main/java/com/example/mod/ModItems.java` 完整内容（与模板逐字一致）：

```java
package com.example.mod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ExampleMod.MODID);
    public static final Supplier<Item> EXAMPLE_ITEM =
            ITEMS.registerSimpleItem("example_item");
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);
}
```

`src/main/java/com/example/mod/ExampleMod.java` 主类（与模板逐字一致，注册入口）：

```java
package com.example.mod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public ExampleMod(IEventBus bus) {
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
    }
}
```

三个 API 的用途：

| API | 作用 |
|---|---|
| `DeferredRegister.createBlocks(MODID)` / `createItems(MODID)` | 建一个"延迟注册表"，登记时统一提交 |
| `BLOCKS.registerSimpleBlock("example_block", properties)` | 注册简单方块（无方块实体、无复杂状态） |
| `ITEMS.registerSimpleItem("example_item")` | 注册普通物品 |
| `ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK)` | 注册方块对应的物品（背包/手持能用） |

名称重载说明：`registerSimpleBlockItem` 在 1.21.1 必须用**带名称重载**（第一个参数写注册名）。P0 实测无名称重载 `registerSimpleBlockItem(ModBlocks.EXAMPLE_BLOCK)` 时，`Supplier<Block>` 会被编译器匹配到 `Holder<Block>` 重载导致问题；字段类型也要写 `DeferredItem<BlockItem>`（`DeferredItem` 实现 `ItemLike`，不是 `Supplier`）。

### 2. 方块属性组合表

`BlockBehaviour.Properties.of()` 是起点，后面按需追加方法链。以下为 API 参考写法（非模板逐字文件）；其中 `strength` 一条已随 P1-8 端到端样例编译验证，其余为 NeoForge 21.1 标准写法，若换版本按 70-version-matrix 核对。

| 想要的效果 | 追加写法 | 说明 |
|---|---|---|
| 石头类（需镐挖，硬度 1.5） | `.strength(1.5F, 6.0F)` | 两个参数 = 硬度、爆炸抗性 |
| 泥土类（徒手可挖） | `.strength(0.5F)` | 单参数 = 硬度 |
| 木头类 | `.strength(2.0F).sound(SoundType.WOOD)` | 音效类型影响挖掘/放置声音 |
| 玻璃类（可透视） | `.noOcclusion().sound(SoundType.GLASS)` | `noOcclusion` 关掉遮挡剔除 |
| 可替换（放方块直接替换它） | `.replaceable()` | 类似草、雪的语义 |
| 发光 | `.lightLevel(state -> 15)` | 亮度 0-15 |
| 需要正确工具才掉物品 | `.requiresCorrectToolForDrops()` | 配合工具标签使用 |
| 秒挖（花/火把手感） | `.instabreak()` | 无挖掘时间 |

### 3. 物品属性组合表

`new Item.Properties()` 是起点；`registerSimpleItem` 可以接收属性参数：

```java
ITEMS.registerSimpleItem("example_item", new Item.Properties());
```

| 想要的效果 | 追加写法 | 说明 |
|---|---|---|
| 默认 64 堆叠 | `new Item.Properties()` | 什么都不加就是 64 |
| 限堆叠 1 | `.stacksTo(1)` | 工具类常用 |
| 食物 | `.food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build())` | 见下节 |

### 4. 食物写法

```java
public static final Supplier<Item> EXAMPLE_FOOD =
        ITEMS.registerSimpleItem("example_food", new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(4)              // 饱食度
                        .saturationModifier(0.3F)  // 饱和度系数
                        .build()));
```

这段代码放在 `ModItems.java` 里，需要额外 `import net.minecraft.world.food.FoodProperties;`（`Item` 等 import 已在 ModItems.java 内）。进阶（吃完附加状态效果、附魔等）可查 `references/10-modern-lane/15-extra-registries.md`。

### 5. 语言文件 en_us.json

`src/main/resources/assets/examplemod/lang/en_us.json` 完整内容（与模板逐字一致）：

```json
{
  "item.examplemod.example_item": "Example Item",
  "block.examplemod.example_block": "Example Block"
}
```

键名规则：物品 `item.<modid>.<注册名>`，方块 `block.<modid>.<注册名>`；注册名必须是 `registerSimpleItem(...)` / `registerSimpleBlock(...)` 里写的那个字符串。中文显示用相同键建 `zh_cn.json`。

### 6. 合成与熔炼配方

合成配方放 `src/main/resources/data/<modid>/recipe/<任意名>.json`。模板的 `example_item_from_cobblestone.json` 完整内容（1.21 格式，`result` 用 `id`，逐字一致）：

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": ["X"],
  "key": { "X": { "item": "minecraft:cobblestone" } },
  "result": { "id": "examplemod:example_item", "count": 1 }
}
```

无序合成（shapeless）完整示例：

```json
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    { "item": "minecraft:cobblestone" },
    { "item": "minecraft:cobblestone" }
  ],
  "result": { "id": "examplemod:example_item", "count": 2 }
}
```

熔炼配方（`minecraft:smelting`）完整示例（格式已对照 1.21.1 原版 `baked_potato.json` 核验）：

```json
{
  "type": "minecraft:smelting",
  "category": "misc",
  "cookingtime": 200,
  "experience": 0.1,
  "ingredient": { "item": "minecraft:cobblestone" },
  "result": { "id": "examplemod:example_item" }
}
```

1.21 起 `result` 一律写成 `{ "id": "..." }`（可加 `"count"`）；1.20.1 的 `"result": { "item": "..." }` 是旧格式，见 70-version-matrix。

### 7. 标签（Tag）

自定义物品标签 `src/main/resources/data/examplemod/tags/item/example_items.json`：

```json
{
  "values": [
    "examplemod:example_item"
  ]
}
```

自定义方块标签 `src/main/resources/data/examplemod/tags/block/example_blocks.json`：

```json
{
  "values": [
    "examplemod:example_block"
  ]
}
```

1.21 的目录是 `tags/item/` 与 `tags/block/`（单数，已对照原版数据核验）。要往**原版标签**追加内容，把文件放到 `data/minecraft/tags/item/<原版标签名>.json`，默认合并语义（追加 values）：

```json
{
  "replace": false,
  "values": [
    "examplemod:example_item"
  ]
}
```

`"replace": true` 表示用本文件 values **完全顶掉**原版标签内容，一般别用。配方里引用标签用 `{ "tag": "examplemod:example_items" }` 而不是 `"item"`。

### 8. 方块战利品表（Loot Table）

`src/main/resources/data/examplemod/loot_table/blocks/example_block.json`（1.21 目录为 `loot_table` 单数，格式对照原版 `cobblestone.json` 核验）：

```json
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1.0,
      "entries": [
        { "type": "minecraft:item", "name": "examplemod:example_block" }
      ],
      "conditions": [
        { "condition": "minecraft:survives_explosion" }
      ]
    }
  ],
  "random_sequence": "examplemod:blocks/example_block"
}
```

含义：破坏方块时必定掉落 `examplemod:example_block` 本体，且不会被爆炸抹掉。不给方块写战利品表 = 破坏后什么都不掉。

### 9. 数据生成（DataGen）

`数据生成`（DataGen）指用代码生成 JSON 数据文件（配方/模型/语言/战利品），避免手写大量重复 JSON，词条定义见 `references/01-glossary.md`。本仓库模板未含 DataProvider 代码，v0.1 提供上面的**手写 JSON 路线**（配方/标签/战利品均已按 1.21.1 格式核验）；NeoForge DataProvider 最小示例与自动化生成管线列入 **v0.2 排期（参考未验证）**。

## 新手易错点

1. 注册后没加语言文件：物品/方块名显示成键名或英文，`block.examplemod.example_block` 这种键写错也看不到中文名。
2. 纹理路径拼错：模型里写 `examplemod:block/example_block` 而文件实际不在 `textures/block/`，游戏内黑紫（排查细节在 03-assets）。
3. 配方 `result` 写成 `"item"` 而非 `"id"`：1.21 格式错误，配方不生效且日志报错（1.20.1 相反，见 70）。
4. 标签目录写错：1.21 是 `tags/item/`、`tags/block/`（单数）；照老教程写 `tags/items/`、`tags/blocks/` 不生效。
5. 客户端/服务端不分：把渲染/模型等仅客户端逻辑塞进普通注册路径，服务端也会尝试加载而挂掉；本文件内容均双端安全，客户端专属内容见 50-client（P2）。
6. 战利品表路径写错：1.21 是 `data/<modid>/loot_table/blocks/`（单数），写成 `loot_tables/` 不生效，方块破坏后不掉东西。
7. 覆盖原版标签时误开 `"replace": true`：会把原版全部 values 顶掉；追加内容应省略该字段或写 `false`。

## 验证清单

- [ ] 自动：`gradlew build` 通过（`BUILD SUCCESSFUL`）。
- [ ] 自动：`gradlew runClient` 冒烟到主菜单，日志无配方/战利品/注册相关错误。
- [ ] 人工：进世界后用 `/give @s examplemod:example_item` 拿到物品且图标正确。
- [ ] 人工：工作台用配方合成出新物品（shaped 与 shapeless 各试一次）。
- [ ] 人工：放置方块贴图正确；破坏方块掉落本体（战利品表生效）。

## 已有项目改造

流程见 `references/10-modern-lane/00-scaffold.md`「已有项目改造」：在已有 `DeferredRegister` 上追加条目（不新建第二个）→ 补语言文件 → 按 03-assets 补贴图/模型 → 按上文路径放配方/标签/战利品 → 增量验证（build + runClient + 进游戏人工操作）。
