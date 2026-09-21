# 1.12.2 线 90-troubleshooting.md（故障速查，P1.5 最小版）

<!-- 元信息块 -->
- 适用场景：Forge 1.12.2 开发中崩溃/报错排查（构建期 + 运行期），P1.5 先收四条起步故障。
- 前置依赖：无
- 相关文件：`references/20-legacy-1122-lane/00-scaffold.md`（工具链硬性要求）、`references/20-legacy-1122-lane/10-blocks-items.md`（注册/语言/资源写法）、`docs/verification-notes.md`（P0/P1.5 实测记录）
- 适用加载器：Forge
- 适用版本线：1.12.2（Forge 14.23.5.2847，兼容其他 14.23.5.x patch）
- 核验日期：2026-08-10

> 来源标注规则：每条记录必须注明"真实案例"或"推断"。条目 1 为本仓库 P0 构建实测触发的真实案例；条目 5 为用户在正式客户端（Forge 14.23.5.2864）实测触发的真实案例；条目 2–4 暂无本仓库实测触发记录，按计划标注 **推断（2026-08-10）**，现象文本参照 1.12.2 常见日志（示例）。后续开发中实际触发后回填为"真实案例"并补完整日志摘录。

## 1. ForgeGradle 在 JDK9+ / JRE 下崩溃（`Could not find tools.jar`）

现象（报错原文示例）：

```text
Could not find tools.jar. Please check that C:\Program Files\Java\jre1.8.0_271 contains a valid JDK installation
```

（`<路径>` 处是你实际指向的 JAVA_HOME；构建直接失败，通常在 ForgeGradle 解析 JDK 时抛出。）

- 原因：ForgeGradle 2.3 依赖 JDK 8 的 `tools.jar`（`javac` 内部接口）。JRE 没有 `tools.jar`/`bin\javac.exe`；JDK 9+ 则移除了 `tools.jar`，且 Gradle 4.9 本身也不支持 JDK 9+ 运行。本机 `D:\java` 实为 JRE 8，P0 构建即在此报错。
- 修法：`JAVA_HOME` 指向**便携 JDK 8**（本仓库 `.tools\jdk8\jdk8u502-b07`，含 `bin\javac.exe` 与 `lib\tools.jar`，路径见 `docs/verification-notes.md`）；确认 `java -version` 输出 1.8.x；构建命令前先 `$env:JAVA_HOME = "..."` 再 `.\gradlew.bat build`。
- 来源：**真实案例（2026-08-10，P0 构建实测）**，见 `docs/verification-notes.md`「JDK 8 补充说明」。计划要求统一标"推断"，但本仓库已有实测记录，按设计文档"真实案例或标注推断"规则改标真实案例（偏离点，已记录）。

## 2. mcmod.info 缺失/格式错导致 mod 未加载

现象（1.12.2 常见日志/界面原文示例）：

```text
No mod information found. Ask your mod author to provide a mod mcmod.info file
```

（Mods 界面显示该提示，或 `Forge Mod Loader has successfully loaded N mods` 的 N 里不含你的 mod；mcmod.info 是 JSON 数组格式，一个花括号/逗号写错就整体解析失败。）

- 原因：`mcmod.info` 不在 jar 根目录（`src/main/resources/mcmod.info` 必须打包到 jar 根部）、文件不是合法 JSON 数组、或 `modid` 与 `@Mod(modid = ...)` 不一致；1.12.2 靠它发现并加载 mod。
- 修法：确认 `src/main/resources/mcmod.info` 存在且是 JSON 数组（格式照 `references/20-legacy-1122-lane/00-scaffold.md` 第 4 节）；用 `jar tf build\libs\*.jar` 看产物里 `mcmod.info` 是否在根目录；`modid` 与 `@Mod` 一致；`${version}` 由 `processResources` 展开，不要手动改成字面量。
- 来源：推断（2026-08-10），现象文本为 1.12.2 常见日志/界面文案示例。

## 3. 注册键为空 / 重复注册（Registry 异常）

现象（1.12.2 常见日志原文示例）：

```text
Registry Block: The object Block has been registered twice for the same name
```

或（对同一对象第二次调用 `setRegistryName` 时）：

```text
Attempted to set registry name with existing registry name
```

注册键为空时通常表现为：进游戏后内容不出现、`/give` 报找不到该 id，或启动期 `IllegalArgumentException` 类异常（注册名 null）。

- 原因：忘了 `setRegistryName(...)`（注册键为 null）；或同一注册名被注册两次（常见于两个注册入口都注册同一批对象，如主类 `preInit` 与注册事件各注册一次）；或对同一对象重复调用 `setRegistryName`。
- 修法：每个方块/物品只 `setRegistryName` 一次，注册名全局唯一（`modid:name`）；注册只走**一个**入口（本线写法 = 主类 `preInit` 里的 `GameRegistry.findRegistry(Block/Item.class).register(...)`，追加内容在**同处**追加，不新建第二个入口）；不要把 `setRegistryName` 写进父类又写进子类。写法对照 `references/20-legacy-1122-lane/10-blocks-items.md` 第 1 节。
- 来源：推断（2026-08-10），现象文本为 1.12.2 社区常见日志示例。

## 4. 纹理 missing（黑紫方块/物品）

现象（1.12.2 常见日志原文示例）：

```text
[WARN] Missing model ... for blockstate examplemod:example_block
```

游戏内表现为黑紫相间的方块/物品（missing texture/model）。

- 原因：blockstate / 模型 / 贴图三者的路径与文件不一致。1.12.2 特有坑：贴图目录是**复数** `textures/blocks/`、`textures/items/`（照 1.21 单数教程写会黑紫）；blockstate 简单方块的状态键是 `"normal"`、模型值写 `examplemod:example_block`（不带 `block/` 前缀）；模型里引用贴图也带复数段（`examplemod:blocks/example_block`）。
- 修法：按资源位置（`namespace:path`）逐层核对：blockstate → `models/block/<name>.json` → `textures/blocks/<name>.png`；JSON 用 `{}` 校验；方块物品模型 `models/item/<name>.json` parent 指向方块模型（背包/手持不黑紫）；清理 `run/` 缓存后重启。规范与逐字示例见 `references/20-legacy-1122-lane/10-blocks-items.md` 第 5 节。
- 来源：推断（2026-08-10），现象文本为 1.12.2 常见日志示例。

## 5. 正式客户端加载时报 `NoSuchFieldError: ROCK`（jar 未 reobf）

现象（报错原文，2026-08-10 用户崩溃报告摘录）：

```text
java.lang.NoSuchFieldError: ROCK
	at com.example.mod.ExampleMod.<clinit>(ExampleMod.java:23)
	at net.minecraftforge.fml.common.FMLModContainer.constructMod(FMLModContainer.java:539)
```

（`<clinit>` 是类静态初始化；`ExampleMod.java:23` 在你的工程里是字段初始化行，如 `new Block(Material.ROCK)`。启动到 "Initializing game" 阶段即崩，mod 未加载。）

- 原因：**映射名（mappings）两套体系混用**。开发期（`gradlew runClient`/IDE）用的是 MCP 名（`Material.ROCK`）；正式客户端的 Minecraft 是混淆后运行环境，字段名是 SRG 名（`Material.field_151576_e`）。ForgeGradle 2.3 的 `jar` 任务默认**不保证**执行 reobf（`reobfJar`），直接 `gradlew build` 产出的 jar 可能仍是 MCP 名，正式客户端加载时找不到 `ROCK` 字段就抛 `NoSuchFieldError`。该报错与 mod 逻辑无关，纯打包问题。
- 修法：`build.gradle` 必须有 `jar { finalizedBy reobfJar }`（完整 build.gradle 见 `references/20-legacy-1122-lane/00-scaffold.md` 第 3 节），保证 `gradlew build` 后自动 reobf；改完**重新 build**（必要时 `clean`）再取 `build/libs/` 的 jar。发布前抽查：`javap -c -p -classpath build\libs\examplemod-1.0.0.jar com.example.mod.ExampleMod`，输出应是 `Field net/minecraft/block/material/Material.field_151576_e`，若看到 `Material.ROCK` 则 jar 未 reobf（重跑 `.\gradlew.bat reobfJar` 或检查该行是否被误删）。
- 来源：**真实案例（2026-08-10，用户 PCL 启动器崩溃报告，Forge 14.23.5.2864）**；本仓库 P1.5 端到端期间复现并修正（见 `docs/verification-notes.md`「P1.5 端到端验证」的产物说明与「发现与偏离」）。

## 排查顺序

构建失败先查条目 1（工具链）；开发环境正常但**正式客户端启动即崩**先查条目 5（未 reobf）；游戏能开但 mod 不出现先查条目 2（mcmod.info）；mod 出现但内容缺失/报注册错先查条目 3；内容出现但外观黑紫先查条目 4。跨文件通用问题（现代线 Mixin 等）不适用于本线，1.12.2 没有 Mixin 默认支持。
