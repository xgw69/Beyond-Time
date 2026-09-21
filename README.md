# Deep Time（深时）

一个关于**这个世界的历史**的 Minecraft 模组：从显微镜下的微生物开始，一步步做古DNA测序、
克隆出生物过去的形态，找回失落的魔法与科技，最后造出能回到各个维度过去的机器，
弄清末影人为什么怕水、猪猡兽到了主世界为什么会僵尸化、地狱和末地为什么曾经满是水。

An archaeology mod for Minecraft: microbes under a microscope, ancient DNA, cloning the
ancestral forms of mobs, lost magic and technology, and time machines that travel to the past
of every dimension.

## 目标平台

| 项目 | 版本 |
|---|---|
| Minecraft | 26.2 |
| NeoForge | 26.2.0.88 |
| Java | 25 |
| 构建 | Gradle 9.2.1 + ModDevGradle 2.0.147 |

## 当前状态

M0 已完成：项目骨架可构建，入口类与注册中枢就位。
内容开发计划见 [docs/DESIGN.md](docs/DESIGN.md)，环境细节见 [docs/SETUP.md](docs/SETUP.md)。

## 构建与运行

```bash
./gradlew build                # 构建 jar -> build/libs/deeptime-<version>.jar
./gradlew runClient            # 启动带本模组的客户端
./gradlew runServer            # 启动专用服务端
./gradlew runData              # 运行数据生成，产物写入 src/generated/resources
./gradlew runGameTestServer    # 跑 GameTest（自动化验证）
```

Windows 上用 `gradlew.bat`。需要 JDK 25 参与工具链，配置方法见 [docs/SETUP.md](docs/SETUP.md)。

## 目录结构

```
src/main/java/com/deeptime/    模组代码（入口、注册中枢、后续按功能分包）
src/main/resources/            手写资源（语言文件等）
src/main/templates/            neoforge.mods.toml 模板，构建时展开占位符
src/generated/resources/       数据生成产物（提交进仓库）
docs/                          设计蓝图与环境说明
```

## 素材

物品/方块贴图、实体模型与贴图由作者制作，规格见 [docs/DESIGN.md](docs/DESIGN.md) 第 11 节。

## 许可证

暂定 All Rights Reserved，如需开源请改 `gradle.properties` 的 `mod_license`。

## 致谢

项目结构与部分规范参考了两个 Minecraft agent 技能包（均为 MIT）：

- [Jahrome907/minecraft-agent-skills](https://github.com/Jahrome907/minecraft-agent-skills)
- [Zhangmu-XL/minecraft-mod-skills](https://github.com/Zhangmu-XL/minecraft-mod-skills)

构建骨架基于官方 [MDK-26.2-ModDevGradle](https://github.com/NeoForgeMDKs/MDK-26.2-ModDevGradle)。
