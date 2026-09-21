# 运行与调试渠道

这份文档是给你（作者）测试用的。测试由你进行（R9），我负责让"跑起来看效果"尽量省事。

## 1. 最常用：一键启动带模组的游戏

双击 `tools\dev-client.bat`，或命令行：

```bash
./gradlew runClient
```

它会直接启动带本模组的 Minecraft 26.2（不需要你自己装 NeoForge、不需要往 mods 文件夹丢 jar）。
多出来的参数会原样传给 Gradle，比如 `tools\dev-client.bat --dry-run` 可以只做一次检查而不真的开游戏。

> `tools\*.bat` 故意只用英文注释、并且用 **CRLF** 换行。`cmd.exe` 对 LF-only 的批处理和非
> UTF-8 代码页下的中文都会解析出错（表现为把单词切一半报"不是内部或外部命令"），改动这两个
> 文件时请保持这个约定。

| 位置 | 说明 |
|---|---|
| `run/` | 开发用游戏目录：存档、配置、截图都在这里 |
| `run/logs/latest.log` | 运行日志，报错先看这里 |
| `run/saves/` | 存档，可以随时备份/删除做干净测试 |

第一次启动会稍慢（要准备运行环境），之后就快了。

## 2. 改动生效方式

| 你改了什么 | 怎么生效 |
|---|---|
| 贴图 / 模型 / 语言文件 | 游戏里按 **F3 + T** 重载资源包，不用重启 |
| Java 代码 | 重启 `runClient`（我会尽量提供调试命令减少重启需求） |
| 配方 / 战利品 / 数据 JSON | 双击 `tools\dev-data.bat`（等价于 `./gradlew runData`）后 F3+T 或重启 |

资源与数据生成产物会写进 `src/generated/resources/`，由我提交进仓库。

## 3. 方便测试的调试命令

**还没做** —— 等你确认要不要（见 `docs/DESIGN.md` 第 7 节）。现在的替代办法是创造模式：
Beyond-Time 页签里已经有两样东西，配方也都在配方书里。如果要做，计划长这样：

```mcfunction
/beyondtime give <物品>         # 直接拿物品
/beyondtime unlock all          # 解锁全部观察对象/微生物（测试用）
/beyondtime unlock <编号>       # 解锁单个条目，如 M-01
/beyondtime reset               # 清空进度，回到初始状态
/beyondtime kit                 # 拿一套测试用工具包
```

需要什么别的调试指令直接说，我加上去。

## 4. 其他命令

```bash
./gradlew build              # 构建 jar（build/libs/beyondtime-<version>.jar）
./gradlew runServer          # 专用服务端（测试多人/服务端侧逻辑）
./gradlew runGameTestServer  # 自动化测试，跑完自动退出
./gradlew runData            # 生成资源与数据
```

## 5. 想在正式客户端里玩（可选）

如果哪天想装进自己平时的游戏：

1. 装一个 NeoForge 26.2 的客户端（用官方安装器，装到 `.minecraft`）；
2. 把 `build/libs/beyondtime-<version>.jar` 丢进 `.minecraft/mods/`；
3. 用 NeoForge 26.2 的启动配置启动。

开发阶段建议还是用 `runClient`，日志和报错更清楚。

## 6. 出问题怎么办

| 现象 | 先做什么 |
|---|---|
| 游戏起不来 | 看 `run/logs/latest.log` 最后的报错，直接发给我 |
| 贴图显示成紫黑格 | 检查路径与文件名（`assets/beyondtime/textures/item/<名字>.png`），然后 F3+T |
| 改的 JSON 不生效 | 跑一次 `tools\dev-data.bat`，再 F3+T |
| 构建报 Java 25 找不到 | 见 `docs/SETUP.md` 第 1 节（JDK 25 路径） |
