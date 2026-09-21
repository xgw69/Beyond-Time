---
name: minecraft-modding
description: >
  面向中文新手的 Minecraft Java 版 mod 开发技能，处理"我想做个模组、加个新方块或物品、
  做台会发电的机器、加新维度或生物群系、自定义生物、做 GUI、加合成配方"这类需求。
  Use when developing or scaffolding Minecraft Java Edition mods (模组/mod) for
  NeoForge, Fabric, or Forge (1.21.x / 1.20.1 / 1.12.2) — e.g. adding blocks,
  items, machines, worldgen, entities, GUIs, recipes, or porting between versions.
  Not for datapacks, resource packs, server plugins, or Bedrock addons.
---

# Minecraft Mod 开发技能（中文新手版）

## 触发

- 触发（双语）：模组、mod、NeoForge、Fabric、Forge、1.12.2、方块、物品、机器、合成、维度、生物、GUI、附魔、崩溃排查（mod 开发语境）、已有 mod 工程改造。description 按"职责 + 边界 + 例子"做语义匹配，不是关键词清单。
- 反例（不触发）：数据包、资源包、服务端插件（Bukkit/Paper/Spigot）、Bedrock/网易基岩版附加包、游戏本体打不开、服务器配置、光影、WorldEdit、整合包推荐、MCP 工具安装。这些场景明确不属于本技能，不要进入问诊。
- 范围外清单以 [SUPPORT.md](../../SUPPORT.md) 为准。

## 阶段 0：问诊（Intake）

读 `references/00-intake.md` 执行：

1. 探测项目目录：`neoforge.mods.toml` → NeoForge；`fabric.mod.json` → Fabric；`mods.toml` → Forge；版本从 `gradle.properties` / `gradle/libs.versions.toml` 推断。探测不到的才问，探测到的不要重复问。
2. 语境确认：用户输入未含 Minecraft/模组/加载器/版本等语境时（如只说"做个会发电的机器"），
   先问一句"你是要做 Minecraft Java 版模组里的……吗？"；用户否认 → 明确本技能范围外，
   不进入问诊，按通用问题处理。
3. "想做什么内容"是无条件必问，任何探测都替代不了。
4. 新手模式：先解释加载器与版本差异，给出默认推荐（NeoForge 1.21.x），允许回答"我不懂"。
5. 输出路由结论：加载器 + 版本线 + 内容类型 + 目标文件完整路径。
6. 能力清单：对照 `SUPPORT.md`；SUPPORT.md 未承诺的能力，走失败处理。

## 阶段 1：路由

- 1.21.x / 1.20.1 → `references/10-modern-lane/`；1.12.2 → `references/20-legacy-1122-lane/`。
- 版本差异查 `references/10-modern-lane/70-version-matrix.md`（P1 交付，P0 尚未创建，命中时按 00-intake 降级规则处理）；加载器差异查 `references/10-modern-lane/80-loader-patterns.md`（P1 交付，P0 尚未创建，同上）；跨版本/跨格差异查 `references/05-porting.md`（v0.1 不存在，走失败处理降级：说明暂不支持 + 替代路径）。
- 执行以"目标版本 + 目标加载器"的 lane 正文为准；矩阵/速查只是差异视图，不承载完整写法。
- 路由映射不到的需求：明确告知不支持，不猜测。

## 阶段 2：执行

1. 按路由结论打开对应内容文件，每个文件按统一结构执行：元信息块 → 目标 → 主写法 → 新手易错点 → 验证清单 → 已有项目改造。
2. 代码逐字来自文件正文，不自行发挥；遇到文件内没有的写法，回到 00-intake 重新路由或走失败处理。
3. 收尾验证按 lane 分级：
   - 现代线（NeoForge 1.21.x）：`gradlew build` 通过；数据生成跑通（仅现代线）；`runClient` 启动到主菜单且日志无致命错误；人工功能操作标注"人工"。
   - 1.12.2 lane：无数据生成、无 GameTests；验证 = build + 主菜单 + 人工功能，JDK 8 环境。
   - 做不到的验证显式标注"待环境（原因）"，绝不假装通过。

## 失败处理

- 模板拉取失败 → 用本仓库 `examples/` 下的自写最小模板代码块。
- 需求超出能力清单 → 说明暂不支持 + 给出可用替代路径。
- 版本/加载器未知 → 标记"未验证"并提示用户确认，不猜测。
- 矩阵外版本 → 明确不支持，不猜测。

## 语言规则

- 中文为主；术语首现双语（中文定义 + 英文原词）。
- 代码 / API / JSON / 命令 / 报错原文逐字复制，不转写、不翻译。
- modid、包名、路径必须 ASCII 小写。
