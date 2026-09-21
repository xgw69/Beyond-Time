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

M0 完成：项目骨架可构建，入口类与注册中枢就位，开发与测试渠道已就绪。

- 内容由作者定义，开发方不自行设计设定。
- 规则与协作方式见 [docs/DESIGN.md](docs/DESIGN.md)
- 设定登记表见 [docs/LORE_INTAKE.md](docs/LORE_INTAKE.md)
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

统一 **32×32**，路径与细则见 [docs/DESIGN.md](docs/DESIGN.md) 第 5 节。

## 许可证

MIT，见 [LICENSE](LICENSE)。

## 致谢

项目骨架基于官方 [MDK-26.2-ModDevGradle](https://github.com/NeoForgeMDKs/MDK-26.2-ModDevGradle)；
开发过程参考了两份 MIT 技能包（见 `.agents/README.md`）。
