# Beyond-Time

一个关于 **Minecraft 世界历史** 的模组：从显微镜下的微生物开始，逐步发现古老的故事，
克隆出部分生物过去的形态，找回失落的魔法与科技，最后造出能回到各个维度过去的机器，
弄清这个世界到底发生过什么。

An archaeology mod for Minecraft: microscopes and microbial samples, ancient DNA, the ancestral
forms of living creatures, and machines that travel to the past of each dimension.

## 目标平台

| 项目 | 版本 |
|---|---|
| Minecraft | 26.2 |
| NeoForge | 26.2.0.88 |
| Java | 25 |
| 构建 | Gradle 9.2.1 + ModDevGradle 2.0.147 |
| mod id | `beyondtime`（暂定） |

## 当前状态

M0、M1 完成，M2 进行中。

- M1 已有：显微镜（非完整方块、四个朝向、可放入/取出培养皿）、培养皿（采集/清洗/采集后不可堆叠）、
  配方与战利品数据生成。外形已选定**方案 A（台面式）**。
- M2 已有：11 种微生物的数据、**采集名单**（精确方块 + 原版标签 + 主世界/下界/末地三个维度的空气）、
  样本数据组件（来源 + 各菌种读数 + 总菌数）、**显微镜报告界面**（每格：32×32 图标 + 名称 + 占比，
  另有来源与菌数）。
  **等作者测试，并审阅 `docs/MICROBE_PROFILES.md` 的名单与比例。**

- 内容由作者定义，开发方不自行设计设定。
- 规则与协作方式见 [docs/DESIGN.md](docs/DESIGN.md)
- 显微镜外形方案、贴图规格、界面坐标见 [docs/MICROSCOPE.md](docs/MICROSCOPE.md)
- 11 种微生物的**设定原文**见 [docs/LORE_MICROBES.md](docs/LORE_MICROBES.md)
- **采集名单与各维度分布**见 [docs/MICROBE_PROFILES.md](docs/MICROBE_PROFILES.md)
- 设定登记表见 [docs/LORE_INTAKE.md](docs/LORE_INTAKE.md)
- 配方写法标准见 [docs/RECIPE_SPEC.md](docs/RECIPE_SPEC.md)（配方登记在 [docs/RECIPES.md](docs/RECIPES.md)）
- 运行/测试方式见 [docs/DEV_LOOP.md](docs/DEV_LOOP.md)
- 环境与镜像配置见 [docs/SETUP.md](docs/SETUP.md)

## 快速开始

```bash
./gradlew build      # 构建
./gradlew runClient  # 直接启动带本模组的 Minecraft 26.2（最常用的调试方式）
./gradlew runData    # 重新生成资源与数据 JSON
```

Windows 可直接双击 `tools\dev-client.bat` / `tools\dev-data.bat`。

## 目录结构

```
src/main/java/com/beyondtime/  模组代码
src/main/resources/            手写资源（语言文件等）
src/main/templates/            neoforge.mods.toml 模板，构建时展开占位符
src/generated/resources/       数据生成产物（提交进仓库）
docs/                          设计、设定登记表、环境与调试说明
tools/                         调试用快捷脚本
.agents/                       第三方 Minecraft 开发技能包（MIT，见目录内 README）
```

## 素材规格

物品/方块贴图统一 **32×32**，容器界面背景统一 **256×256**（可见面板画在左上角 176×166 里）。
路径、UV 规则与固定坐标见 [docs/DESIGN.md](docs/DESIGN.md) 第 5 节与
[docs/MICROSCOPE.md](docs/MICROSCOPE.md) 第 4 节。

## 许可证

MIT，见 [LICENSE](LICENSE)。

## 致谢

项目骨架基于官方 [MDK-26.2-ModDevGradle](https://github.com/NeoForgeMDKs/MDK-26.2-ModDevGradle)；
开发过程参考了两份 MIT 技能包（见 `.agents/README.md`）。
