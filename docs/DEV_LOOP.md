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

### 2.1 反复调显微镜外形时

显微镜的**模型是手写的**（`src/main/resources/assets/beyondtime/models/block/microscope.json`），
datagen 只生成指向它的 blockstate。所以你可以随便改那个 JSON，回游戏按 **F3 + T** 立刻看到新形状，
不用跑 `runData`、不用重启。

要注意的是：**碰撞箱写在 Java 里**（`MicroscopeBlock#NORTH_SHAPE`），改了模型元素就得同步改它，
否则描边和实际外形会对不上。两边的坐标表和备选方案都在 `docs/MICROSCOPE.md`。

想直接换成备选外形（比如 B 方案）：

```powershell
Copy-Item tools\microscope-variants\b_tower.json src\main\resources\assets\beyondtime\models\block\microscope.json
```

再按 `docs/MICROSCOPE.md` 里 B 方案那段替换 `NORTH_SHAPE`，重启一次即可。

## 3. 方便测试的调试命令

**已确认不做** `/beyondtime` 调试命令。测试走创造模式：Beyond-Time 页签里有显微镜和培养皿，
配方也都在配方书里；需要的材料直接搜索物品栏拿。

如果测试过程中发现"每次都要手动摆一遍太麻烦"，告诉我具体卡在哪一步，我给你想办法
（比如做成一个结构文件、或者临时加一条指令）。

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
| 界面槽位/背景错位 | 界面背景 PNG 必须 **256×256**（面板画在左上角 176×166 里），见 `docs/MICROSCOPE.md` 4.4 |
| 方块外观和描边不一致 | 模型 JSON 和 `MicroscopeBlock#NORTH_SHAPE` 的坐标没对上 |
| 构建报 Java 25 找不到 | 见 `docs/SETUP.md` 第 1 节（JDK 25 路径） |
