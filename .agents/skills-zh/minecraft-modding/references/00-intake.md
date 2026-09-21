# 00-intake.md（唯一路由表）

> 本文件是阶段 0 问诊的执行手册，也是唯一路由表。能力清单以 `SUPPORT.md` 为准；
> 本文档与 SKILL.md 的差异以本文档为准（SKILL.md 只做触发与总纲）。
> 体积预算：≤15KB，只留决策键与核心映射；详表下沉到各 reference 文件头部的"适用场景"。

## 探测规则（只探测，不猜测）

- `neoforge.mods.toml` → NeoForge；`fabric.mod.json` → Fabric；`mods.toml` → Forge。
- 版本从 `gradle.properties` / `gradle/libs.versions.toml` 推断；推断不出则问。
- 部分识别（如只有加载器、没有版本）→ 只问缺失项，不问已识别项。
- 全新项目（无任何文件）→ 进入新手模式。
- "想做什么内容"是无条件必问，任何探测都替代不了。

## 三档模式

### 新手模式（全新项目 / 用户说"我不懂"）

先做语境确认：输入未含 Minecraft/模组/加载器/版本等语境时（如只说"做个会发电的机器"），
先问"你是要做 Minecraft Java 版模组里的……吗？"；用户否认 → 明确本技能范围外，不进入问诊。

确认是 Minecraft 场景后，再解释加载器与版本差异（一句话版：NeoForge 1.21.x 是现代主流，1.12.2 是老版本独立线；
Fabric 是轻量加载器），给出默认推荐 **NeoForge 1.21.x**，然后问内容意图，允许回答"我不懂"。

示例对话：

- 用户："我想做个模组，但我什么都不懂。"
  - 问诊：解释加载器/版本 → 推荐 NeoForge 1.21.x → 必问"想做什么内容"。
- 用户："想加个能发光的方块。"
  - 路由结论：NeoForge / 现代线(1.21.x) / 方块与物品 / `references/10-modern-lane/10-blocks-items.md`。
- 用户："不懂这些，随便做个简单的。"
  - 问诊：按推荐组合走，内容以"新方块 + 物品 + 配方"起步，路由同上。

### 标准模式（有明确目标、无现成工程）

直接问内容意图 + 加载器/版本偏好，然后给路由结论。

示例对话：

- 用户："用 Forge 给 1.12.2 加个物品。"
  - 路由结论：Forge / legacy 线(1.12.2) / 方块与物品 / `references/20-legacy-1122-lane/10-blocks-items.md`。
- 用户："NeoForge 1.21 的机器怎么做？"
  - 路由结论：NeoForge / 现代线(1.21.x) / 机器 / `references/10-modern-lane/20-machines.md`。

### 已有项目模式（探测到工程文件）

先跑探测规则；只问缺失项；内容意图必问；已有项目改造一律路由到对应 lane 的 00-scaffold 第二节。

示例对话：

- 用户："这是我现成的 Fabric 工程，版本 1.21.1，想加个生物。"
  - 问诊：探测到 `fabric.mod.json` + 1.21.1 → 只问内容（已识别的不问）→
    路由结论：Fabric / 现代线(1.21.x) / 实体 / `references/10-modern-lane/40-entities.md` +
    改造接入口 `references/10-modern-lane/00-scaffold.md`「已有项目改造」。
- 用户："工程里只有一个 mods.toml，版本不确定。"
  - 问诊：探测到 Forge → 只问版本 + 内容。

## 内容→文件映射表

| 用户需求 | 目标文件（现代线 / legacy 线） |
|---|---|
| 新方块/物品/食物/工具/武器 | 10-modern-lane/10-blocks-items.md / 20-legacy-1122-lane/10-blocks-items.md（配方/标签/战利品/数据生成同文件） |
| 附魔/药水/状态效果/声音/属性 | 10-modern-lane/15-extra-registries.md |
| 机器/容器/能量/流体/JEI | 10-modern-lane/20-machines.md |
| 维度/生物群系/结构 | 10-modern-lane/30-worldgen.md |
| 生物/AI/生成规则 | 10-modern-lane/40-entities.md |
| GUI/渲染/粒子/音效 | 10-modern-lane/50-client.md |
| 多人同步/自定义网络包 | 10-modern-lane/60-networking.md |
| 崩溃/报错排查 | 10-modern-lane/90-troubleshooting.md / 20-legacy-1122-lane/90-troubleshooting.md |
| 移植（跨版本/跨加载器） | 05-porting.md（v0.1 降级，见下） |
| 已有项目改造 | 对应 lane 的 00-scaffold.md 第二节 |
| 贴图/模型/blockstate/mcmeta | 03-assets.md |

映射不到的需求 → 失败处理：明确告知不支持，不猜测。

## 能力清单与降级规则

- 能力清单 = `SUPPORT.md` 的支持矩阵，本文档不复制其内容，问诊时直接引用。
- 降级规则：目标文件不存在（如 v0.1 的 `05-porting.md`）→ 走 SKILL.md 失败处理
  （说明暂不支持 + 替代路径），**不得指向不存在的路径**。
- 当前能力状态（2026-08-10 P1.5 后刷新）：现代线正文（00-scaffold / 10-blocks-items /
  15-extra-registries / 70-version-matrix / 80-loader-patterns）、03-assets 与 legacy 正文
  （00-scaffold / 10-blocks-items / 90-troubleshooting）均已交付，两条承诺路径端到端验证通过；
  05-porting.md 与 P2/P3 内容文件尚未交付，用户需求命中时按上述降级规则处理并在回复中说明排期。

## 路由结论格式

```text
路由结论 = 加载器 + 版本线 + 内容类型 + 目标文件完整路径
示例：NeoForge / 现代线(1.21.x) / 方块与物品 / references/10-modern-lane/10-blocks-items.md
```

路由结论必须输出完整路径（相对 `skills/minecraft-modding/`），并附带一句话说明为什么路由到这里。
