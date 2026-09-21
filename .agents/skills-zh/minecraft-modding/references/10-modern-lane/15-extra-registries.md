# 现代线 15-extra-registries.md（附魔 / 药水 / 状态效果 / 声音 / 属性）

<!-- 元信息块 -->
- 适用场景：加自定义附魔、状态效果（含药水）、音效事件、自定义属性等"非方块/物品"的杂项注册。
- 前置依赖：`references/10-modern-lane/10-blocks-items.md`（DeferredRegister 注册模式）
- 相关文件：`references/10-modern-lane/70-version-matrix.md`（版本差异）、`references/10-modern-lane/80-loader-patterns.md`（加载器差异）、`references/10-modern-lane/90-troubleshooting.md`（排错）、`references/01-glossary.md`（术语）
- 适用加载器：NeoForge
- 适用版本线：1.21.x（NeoForge 21.1.248 / MC 1.21.1）
- 核验日期：2026-08-10

## 目标

本文件是杂项注册速查：每个注册项按「注册方式 → 最小代码 → 常见参数 → 新手易错点」组织。注册模式与 10-blocks-items 相同（`DeferredRegister` + 主类 `register(bus)`），只换 Registries 键与工厂。

## 验证状态说明（先读）

`examples/neoforge-1.21/` 模板只含方块/物品，**不含**本文件四类代码，P1-8 端到端样例范围固定为 00-scaffold + 10-blocks-items，因此本文件全部条目标注 **参考未验证 / v0.2 排期**：API 签名已逐条对照 21.1.248 sources 核验（如构造器可见性、Registries 键名、JSON 字段），但未在本期实机编译/运行。照抄前确认版本，换版本查 70-version-matrix。

## 1. 自定义附魔（Enchantment）

- 注册方式：1.21.1 起附魔是**数据驱动**，不走 DeferredRegister 注册代码，而是放数据文件 `src/main/resources/data/<modid>/enchantment/<name>.json`。
- 最小代码（`data/examplemod/enchantment/example_enchant.json`，格式对照原版 `mending.json` 核验；`effects` 为空 = 占位附魔，不产生实际效果）：

```json
{
  "anvil_cost": 1,
  "description": { "translate": "enchantment.examplemod.example_enchant" },
  "effects": {},
  "max_cost": { "base": 11, "per_level_above_first": 20 },
  "max_level": 1,
  "min_cost": { "base": 1, "per_level_above_first": 10 },
  "slots": ["mainhand"],
  "supported_items": "#minecraft:enchantable/sword",
  "weight": 10
}
```

- 常见参数：`weight`（附魔台权重）、`max_level`、`min_cost`/`max_cost`（附魔台花费）、`slots`（生效槽位，如 `mainhand`/`armor`/`any`）、`supported_items`（可附魔的物品，通常是 `#` 标签）、`exclusive_set`（互斥附魔标签）、`effects`（真实效果，用 EnchantmentEffectComponents，属进阶）。
- 新手易错点：`description.translate` 对应的语言键 `enchantment.examplemod.example_enchant` 要写进 `en_us.json`/`zh_cn.json`，否则附魔名显示为键名；`supported_items` 写普通物品 id 而不是 `#` 标签时，很多工具/武器不适用。
- 验证状态：**参考未验证 / v0.2 排期**（真实附魔效果与实机验证 v0.2）。

## 2. 状态效果（MobEffect）与药水

- 注册方式：`DeferredRegister.create(Registries.MOB_EFFECT, MODID)`。
- 最小代码：

```java
public static final DeferredRegister<MobEffect> MOB_EFFECTS =
        DeferredRegister.create(Registries.MOB_EFFECT, ExampleMod.MODID);
public static final Supplier<MobEffect> EXAMPLE_EFFECT =
        MOB_EFFECTS.register("example_effect",
                () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x88AA44) {});
```

- 常见参数：`MobEffectCategory`（`NEUTRAL`/`BENEFICIAL`/`HARMFUL`，决定药水粒子颜色框）、颜色值 `0xRRGGBB`、`applyEffectTick` 每 tick 逻辑（继承后覆写，属进阶）。注册后可用 `/effect give @s examplemod:example_effect` 测试。
- 新手易错点：1.21.1 的 `MobEffect` 构造器是 **protected**，直接 `new MobEffect(...)` 编译不过；必须写成匿名子类 `new MobEffect(...) {}` 或自己继承。
- 药水（Potion）：让效果进可喝药水还需要 `Registries.POTION` 注册 `Potion` + 酿造配方（BrewingRecipe），是独立链条；本期**参考未验证 / v0.2 排期**，先只用 `/effect` 命令验证效果本身。
- 验证状态：**参考未验证 / v0.2 排期**（API 签名已核验，未实机编译）。

## 3. 音效事件（SoundEvent）

- 注册方式：`DeferredRegister.create(Registries.SOUND_EVENT, MODID)`。
- 最小代码：

```java
public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
        DeferredRegister.create(Registries.SOUND_EVENT, ExampleMod.MODID);
public static final Supplier<SoundEvent> EXAMPLE_SOUND =
        SOUND_EVENTS.register("example_sound",
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "example_sound")));
```

- 常见参数：`createVariableRangeEvent(location)`（音量随距离衰减，常用）；`createFixedRangeEvent(location, range)`（固定可听范围）。事件注册后，还要在 `assets/<modid>/sounds.json` 声明并放 `.ogg` 音频文件（`assets/<modid>/sounds/<name>.ogg`），才能用 `/playsound` 或代码播放。
- 新手易错点：只注册 `SoundEvent` 不配 `sounds.json` + `.ogg`，播放时无声且日志报找不到 sound；`.ogg` 必须是 Ogg Vorbis 格式，mp3/wav 不认。
- 验证状态：**参考未验证 / v0.2 排期**。

## 4. 自定义属性（Attribute）

- 注册方式：`DeferredRegister.create(Registries.ATTRIBUTE, MODID)`。
- 最小代码：

```java
public static final DeferredRegister<Attribute> ATTRIBUTES =
        DeferredRegister.create(Registries.ATTRIBUTE, ExampleMod.MODID);
public static final Supplier<Attribute> EXAMPLE_ATTRIBUTE =
        ATTRIBUTES.register("example_attribute",
                () -> new RangedAttribute("attribute.name.examplemod.example_attribute", 0.0D, -1024.0D, 1024.0D));
```

- 常见参数：`RangedAttribute(描述键, 默认值, 最小值, 最大值)`；构造器的 `attribute.name...` 是属性显示名的语言键。
- 新手易错点：1.21.1 的 `Attribute` 构造器是 **protected**，别写 `new Attribute(0.0D)`；要用 `RangedAttribute`（public）。仅注册属性还不会让任何生物拥有它，要挂到实体类型或物品属性修饰器上（属进阶，v0.2）。
- 验证状态：**参考未验证 / v0.2 排期**。

## 新手易错点（汇总）

1. 只改 10-blocks-items 的注册模式却换了 Registries 键：`create(Registries.MOB_EFFECT, ...)` 的泛型类型必须与键一致，写错类型编译不过。
2. 忘了把新 DeferredRegister 挂总线：每个新注册表都要在主类构造器 `register(bus)`，漏了不报错但内容不在注册表。
3. protected 构造器直接 `new`：`MobEffect`、`Attribute` 都必须用子类/RangedAttribute 形式。
4. 附魔 JSON 少了语言键：`description.translate` 的键必须在语言文件里，否则游戏内显示键名。
5. 音效只注册事件不配音频资源：`sounds.json` + `.ogg` 缺一不可。
6. 1.21.1 附魔不走 DeferredRegister：照老教程写 `Registries.ENCHANTMENT.register(...)` 属旧写法，本期以数据驱动 JSON 为准（1.20.1 差异见 70-version-matrix）。

## 验证清单

- [ ] 自动：`gradlew build` 通过（本文件代码不参与 P1-8 样例，此清单为 v0.2 实机验证占位）。
- [ ] 人工（v0.2）：`/effect give @s examplemod:example_effect` 生效；`/playsound examplemod:example_sound` 可播放；自定义附魔能附到目标物品；自定义属性数值生效。

## 已有项目改造

在已有工程加杂项注册 = 新建对应 `DeferredRegister` 静态字段 → 主类构造器补 `register(bus)` → 按上文补 JSON/资源 → 增量验证。注意不要往 10-blocks-items 的 `BLOCKS`/`ITEMS` 注册表里塞其他类型（每个注册表只装一类内容）。
