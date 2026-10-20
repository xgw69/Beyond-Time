# Changelog

本项目遵循语义化版本，格式参考 Keep a Changelog。

## [0.1.0] - 未发布

### Added

- NeoForge 26.2 项目骨架：入口类 `DeepTime`、客户端入口 `DeepTimeClient`、注册中枢 `DTRegistries`
- 数据生成与运行配置（client / server / gameTestServer / data）
- 构建镜像与重试配置，适配中国大陆网络环境
- 设计蓝图 `docs/DESIGN.md` 与环境说明 `docs/SETUP.md`
- 显微镜 X-01（非完整方块、四朝向、培养皿槽位）与培养皿 P-02（采集/清洗/不可堆叠）
- 11 种微生物数据、方块/标签/三维度空气采集表、显微镜报告界面
- 知识系统：每玩家 `beyondtime:knowledge` attachment、`observe`/`grant`、死亡复制与登录同步
- 采集器 T-01、密封组织样本 T-02、基础生物分析仪 M-01（部分报告、关闭即消失）
- 可采集实体标签 `#beyondtime:samplable`
- 组织档案 `TissueProfiles`（核心生物手写，其余按 MobCategory 兜底）与 `TissueReport`
- 配方 R-04（采集器）、R-05（基础分析仪）
- 新贴图（采集器、密封样本、分析仪六面、256×256 界面背景）与 `tools/make_machine_textures.py`
- 文档 `docs/TISSUE.md`、`docs/MICROSCOPE.md`、`docs/MICROBE_PROFILES.md`、`docs/MACHINES.md`

### Changed

- 项目更名为 **Beyond-Time**：mod id `deeptime` → `beyondtime`，包名 `com.deeptime` → `com.beyondtime`，
  入口类 `DeepTime`/`DeepTimeClient` → `BeyondTime`/`BeyondTimeClient`，注册中枢 `DTRegistries` → `BTRegistries`
- 许可证改为 MIT
- `docs/DESIGN.md` 重写：设定类内容一律由作者提供，开发方只保留规则、协作流程与技术框架
