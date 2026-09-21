# 03-assets.md（贴图 / 模型 / blockstate / pack.mcmeta 规范，P1 最小版）

<!-- 元信息块 -->
- 适用场景：给自己 mod 的新方块/新物品配贴图、模型、blockstate，或修"黑紫方块 / missing model"前先核对资源路径。
- 前置依赖：无（对照 `examples/neoforge-1.21/` 的 `assets/` 文件）
- 相关文件：`references/10-modern-lane/00-scaffold.md`（资源目录与 pack.mcmeta）、`references/10-modern-lane/10-blocks-items.md`（注册与语言文件）、`references/10-modern-lane/70-version-matrix.md`（pack_format 差异）、`references/10-modern-lane/90-troubleshooting.md`（黑紫方块排错）
- 适用加载器：NeoForge（资源格式对 Fabric 同样适用，加载器差异见 `references/10-modern-lane/80-loader-patterns.md`）
- 适用版本线：1.21.x（pack_format 34）
- 核验日期：2026-08-10

## 目标

本文件是 P1 最小版：只覆盖"新方块 + 新物品"必须的几条资源规则，让它们在游戏内显示正确贴图而不是黑紫方块。深化内容（动画贴图 mcmeta、复杂模型、字体、音效、imagegen 管线）在 P4，本文件不覆盖。代码真相 = `examples/neoforge-1.21/src/main/resources/assets/examplemod/`，下列 JSON 与该模板逐字一致。

## 主写法

### 1. 贴图：16×16 PNG 与路径约定

方块贴图放 `src/main/resources/assets/<modid>/textures/block/<name>.png`，物品贴图放 `src/main/resources/assets/<modid>/textures/item/<name>.png`。必须是 **16×16 像素的 PNG**（模板的 `example_block.png`、`example_item.png` 均为 16×16，已核验）。物品图标通常用透明背景（PNG 的 alpha 通道），方块贴图一般铺满。

资源引用用 `namespace:path`，路径**不带 `textures/` 前缀、不带 `.png` 后缀**：

```text
examplemod:block/example_block   ->  assets/examplemod/textures/block/example_block.png
examplemod:item/example_item     ->  assets/examplemod/textures/item/example_item.png
```

### 2. blockstate：简单方块

`src/main/resources/assets/<modid>/blockstates/<name>.json`，简单方块 = 只有一种状态一种模型。完整内容（与模板 `blockstates/example_block.json` 逐字一致）：

```json
{"variants": {"": {"model": "examplemod:block/example_block"}}}
```

`""` 表示"没有状态属性"；`model` 指向 `models/block/` 下的模型。带朝向/含水等多状态的方块属于进阶内容，P4。

### 3. 模型：cube_all / item/generated / 方块物品

方块模型 `src/main/resources/assets/<modid>/models/block/<name>.json`，`cube_all` = 六面同一张贴图。完整内容（与模板 `models/block/example_block.json` 逐字一致）：

```json
{"parent": "minecraft:block/cube_all", "textures": {"all": "examplemod:block/example_block"}}
```

物品模型 `src/main/resources/assets/<modid>/models/item/<name>.json`，`item/generated` = 用贴图直接显示。完整内容（与模板 `models/item/example_item.json` 逐字一致）：

```json
{"parent": "minecraft:item/generated", "textures": {"layer0": "examplemod:item/example_item"}}
```

方块物品（背包与手持时显示成方块）的模型 = 直接指向方块模型。完整内容（与模板 `models/item/example_block.json` 逐字一致）：

```json
{"parent": "examplemod:block/example_block"}
```

对应关系：`blockstates/<name>.json` → `models/block/<name>.json` → 贴图；`models/item/<name>.json` 决定物品栏/手持外观。

### 4. pack.mcmeta

`src/main/resources/pack.mcmeta` 完整内容（已在 00-scaffold 出现，此处确认资源包格式号）：

```json
{ "pack": { "description": "examplemod resources", "pack_format": 34 } }
```

`pack_format` 34 对应 1.21.1；1.20.1 是 15（差异见 70-version-matrix）。

### 5. 占位贴图生成方法（PowerShell System.Drawing）

P0 模板的 16×16 占位贴图即用 System.Drawing 生成（模板 PNG 已入库并核验为 16×16）。生成纯色占位方块贴图：

```powershell
Add-Type -AssemblyName System.Drawing
$bmp = New-Object System.Drawing.Bitmap 16, 16
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.Clear([System.Drawing.Color]::FromArgb(255, 90, 160, 90))   # 不透明绿
$g.Dispose()
$bmp.Save("textures\block\example_block.png", [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()
```

生成透明底物品图标：把 `Clear` 的颜色换成 `FromArgb(0, 90, 160, 90)`（alpha=0），再画图案。注意先建好 `textures\block\` / `textures\item\` 目录；保存后可用图片查看器确认尺寸与透明度。正式美术走 P4 的 imagegen 管线，本文件只保证"能显示、不黑紫"。

## 新手易错点

1. 贴图路径写错：模型里写 `examplemod:block/example_block`，实际文件必须是 `assets/examplemod/textures/block/example_block.png`，多写/漏写 `textures/` 或 `.png` 都会 missing texture。
2. 命名空间不是 modid：写成 `minecraft:block/...` 会去原版资源里找，找不到就黑紫。
3. 只有模型没有 blockstate：`blockstates/<name>.json` 缺失时游戏内黑紫方块（日志报 `Missing model ... for blockstate`，见 90-troubleshooting）。
4. 方块物品模型缺失：背包/手持时黑紫，但放地上正常；补 `models/item/<name>.json`。
5. PNG 不合规：非 16×16、或把 JPG/BMP 直接改后缀成 `.png`，游戏不认。
6. 改了 JSON/贴图后不重开：资源有缓存，改完清理 `run/` 或重启客户端再验证。

## 验证清单

- [ ] 自动（对照模板）：blockstate / cube_all 模型 / item/generated 模型 / 方块物品模型 / pack.mcmeta 与 `examples/neoforge-1.21` 逐字一致。
- [ ] 自动：`gradlew build` 通过，产物 jar 内含 `assets/<modid>/`（可用 `jar tf build\libs\*.jar` 查看）。
- [ ] 自动：`gradlew runClient` 冒烟，日志无 `Missing model` / blockstate 缺失警告。
- [ ] 人工：进世界放置方块贴图正确、手持/物品栏图标正确（归 P1-8 端到端人工验证）。

## 已有项目改造

给已有工程的方块/物品补资源 = 照上述路径**新增**文件，不改动既有资源；先确认 `<modid>` 与注册名一致（注册名来自 10-blocks-items 的 `registerSimpleBlock("example_block", ...)` 里的字符串）。改完按验证清单重跑 build + 进游戏核对。
