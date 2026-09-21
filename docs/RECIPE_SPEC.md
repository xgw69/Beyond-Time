# 配方写法标准（作者 → 开发方）

你按这份约定写，我就能**直接转成游戏里的配方**（我用数据生成写，不手写 JSON，避免出错）。
写错了/写漏了我会先问你，不会自己替你编配方。

## 0. 一句话版本

**九宫格就画三行**，每行三个格子，空格或 `|` 分隔；不想要的位置写 `空`。

```
[R-01] 有序合成 3x3   输出: 显微镜(X-01) x1
铁锭  空    铁锭
空    玻璃  空
空    木板  空
```

## 1. 通用规则

| 写法 | 含义 |
|---|---|
| `空` 或 `.` | 这个格子不放东西 |
| `铁锭` | 原版物品，用中文名（见第 4 节对照表），我会查出准确 id |
| `minecraft:iron_ingot` | 也可以直接写 id |
| `X-01` / `I-03` / `M-02` | 本模组的物品，用 `docs/LORE_INTAKE.md` 里的编号；也可以写你给它起的名字 |
| `#minecraft:planks` | 标签（任何木板都行）；`#` 开头即表示标签 |
| `木板|原木` | 任一材料都行（我做成"替代材料"） |
| `铁锭x3` | 数量 3（**只用于无序合成和输出**，见下面说明） |
| `分类: building` | 可选，配方书归类，取值 `building`/`redstone`/`equipment`/`misc`，不写默认 `misc` |
| `配方书: 否` | 可选，不写默认进配方书 |

**关于数量**：有序合成（九宫格）里**每个格子只能放 1 个物品**，"三个铁锭"就画三个格子里的铁锭，
写成 `铁锭x3` 放在一个格子里是无效的，我会提醒你。只有**无序合成**才用 `x数量` 表示要几个。

## 2. 各类配方的标准写法

### 2.1 有序合成（工作台 3×3 / 背包 2×2）

画格子。位置有意义，所以**留空很重要**。

```
[R-01] 有序合成 3x3   输出: 显微镜(X-01) x1
铁锭  空    铁锭
空    玻璃  空
空    木板  空
```

背包合成（2×2）就画两行两列：

```
[R-02] 有序合成 2x2   输出: 采样拭子(I-01) x1
木棍  线
空    木棍
```

### 2.2 无序合成

不关心位置，只列材料：

```
[R-03] 无序合成   输出: 某种混合粉末 x2
材料: 骨粉x2, 荧石粉x1, 沙子x1
```

### 2.3 熔炼类（熔炉 / 高炉 / 烟熏 / 营火）

```
[R-04] 熔炼   输入: 某种矿石 x1 → 输出: 某种锭 x1   经验: 0.7   时间: 200t
[R-05] 高炉   输入: 某种矿石 x1 → 输出: 某种锭 x1   经验: 0.7   时间: 100t
[R-06] 烟熏   输入: 生肉 x1     → 输出: 熟肉 x1
[R-07] 营火   输入: 生肉 x1     → 输出: 熟肉 x1     时间: 600t
```

经验和时间可以不写（我按原版同类配方给默认值，并在提交信息里注明用的默认值）。
想省事可以写"同熔炼"，我就做一份一样的。

### 2.4 切石机

```
[R-08] 切石   输入: 石头 x1 → 输出: 石板 x4
```

### 2.5 锻造台

两种要分清，写的时候标明：

```
[R-09] 锻造-升级   模板: 下界合金升级模板 / 底材: 钻石镐 / 添加物: 下界合金锭 → 输出: 下界合金镐
[R-10] 锻造-纹饰   模板: 某种纹饰模板 / 底材: 任意盔甲 / 添加物: 对应材料 → 输出: 带纹饰的盔甲
```

### 2.6 本模组机器的配方（显微镜、克隆舱之类）

这类不是原版配方类型，格式等你确定机器机制后再定；先按下面的形式写思路也行：

```
[R-11] 显微镜观察   输入: 某样本 x1 → 解锁: M-01   条件: 需要显微镜(X-01)已在附近
```

### 2.7 药水酿造

原版酿造**不是 JSON 配方**，需要单独实现。写清楚：

```
[R-12] 酿造   基础药水: 粗制的药水 / 材料: 某物 → 输出: 某种药水
```

## 3. 懒得写格式时

直接自然语言说就行，例如："三个铁锭竖着排，中间隔一格放玻璃，下面放木板"。
我会转成上面的标准格式**发回给你确认**，确认后再实现。规则类的设定（数量、解锁条件）
仍然以你写的为准。

## 4. 常见原版材料对照表

写中文名即可；拿不准的写中文，我去核对准确 id。常用的：

| 中文 | id | 中文 | id |
|---|---|---|---|
| 木棍 | `minecraft:stick` | 铁锭 | `minecraft:iron_ingot` |
| 铁粒 | `minecraft:iron_nugget` | 金锭 | `minecraft:gold_ingot` |
| 铜锭 | `minecraft:copper_ingot` | 钻石 | `minecraft:diamond` |
| 下界合金锭 | `minecraft:netherite_ingot` | 煤炭 | `minecraft:coal` |
| 木炭 | `minecraft:charcoal` | 红石 | `minecraft:redstone` |
| 荧石粉 | `minecraft:glowstone_dust` | 荧石 | `minecraft:glowstone` |
| 圆石 | `minecraft:cobblestone` | 石头 | `minecraft:stone` |
| 深板岩圆石 | `minecraft:cobbled_deepslate` | 石砖 | `minecraft:stone_bricks` |
| 玻璃 | `minecraft:glass` | 玻璃板 | `minecraft:glass_pane` |
| 沙子 | `minecraft:sand` | 粘土球 | `minecraft:clay_ball` |
| 砖块 | `minecraft:bricks` | 下界砖 | `minecraft:nether_brick` |
| 纸 | `minecraft:paper` | 书 | `minecraft:book` |
| 皮革 | `minecraft:leather` | 线 | `minecraft:string` |
| 羽毛 | `minecraft:feather` | 骨头 | `minecraft:bone` |
| 骨粉 | `minecraft:bone_meal` | 燧石 | `minecraft:flint` |
| 火药 | `minecraft:gunpowder` | 粘液球 | `minecraft:slime_ball` |
| 蜘蛛眼 | `minecraft:spider_eye` | 发酵蛛眼 | `minecraft:fermented_spider_eye` |
| 烈焰粉 | `minecraft:blaze_powder` | 岩浆膏 | `minecraft:magma_cream` |
| 末影珍珠 | `minecraft:ender_pearl` | 恶魂之泪 | `minecraft:ghast_tear` |
| 紫水晶碎片 | `minecraft:amethyst_shard` | 下界石英 | `minecraft:quartz` |
| 黑曜石 | `minecraft:obsidian` | 哭泣的黑曜石 | `minecraft:crying_obsidian` |
| 末地石 | `minecraft:end_stone` | 紫颂果 | `minecraft:chorus_fruit` |
| 海晶碎片 | `minecraft:prismarine_shard` | 海晶砂粒 | `minecraft:prismarine_crystals` |
| 鹦鹉螺壳 | `minecraft:nautilus_shell` | 潮涌核心 | `minecraft:conduit` |
| 龙息 | `minecraft:dragon_breath` | 幻翼膜 | `minecraft:phantom_membrane` |
| 潜影壳 | `minecraft:shulker_shell` | 回响碎片 | `minecraft:echo_shard` |
| 附魔之瓶 | `minecraft:experience_bottle` | 蜂蜜瓶 | `minecraft:honey_bottle` |
| 铁块 | `minecraft:iron_block` | 铜块 | `minecraft:copper_block` |
| 金块 | `minecraft:gold_block` | 钻石块 | `minecraft:diamond_block` |

常用标签：

| 标签 | 含义 |
|---|---|
| `#minecraft:planks` | 任意木板 |
| `#minecraft:logs` | 任意原木 |
| `#minecraft:stone_bricks` | 任意石砖 |
| `#minecraft:coals` | 煤炭或木炭 |
| `#c:ingots/iron` | NeoForge 通用标签：任意铁锭（跨模组） |
| `#c:gems/diamond` | 任意钻石 |

## 5. 写到哪

配方统一写进 `docs/RECIPES.md`（也可以直接发给我，我录入）。每条给一个编号 `R-##`，
我会在提交信息里引用编号，方便对照"哪条配方对应哪次提交"。
