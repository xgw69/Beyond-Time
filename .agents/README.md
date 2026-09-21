# 项目内技能包（vendored skills）

这里放了两份第三方 Minecraft 开发技能包，供 Codex / Claude Code / Cursor 等 agent 在开发本模组时查阅。
两份都是 MIT 许可，仓库里保留原始文件与出处。

| 目录 | 来源 | 版本 | 说明 |
|---|---|---|---|
| `skills/` | [Jahrome907/minecraft-agent-skills](https://github.com/Jahrome907/minecraft-agent-skills) | `dd57c5a`（2026-09-12） | 13 个技能，覆盖 NeoForge/Fabric 模组、世界生成、资源包、测试、CI 发布、服务端运维等，面向 MC 26.x |
| `skills-zh/` | [Zhangmu-XL/minecraft-mod-skills](https://github.com/Zhangmu-XL/minecraft-mod-skills) | `be94791`（2026-08-10） | 中文新手版，单个 `minecraft-modding` 技能 + 分层 references |

## 为什么中文版没有装进 `skills/`

两份技能包都有一个叫 `minecraft-modding` 的技能，同名技能放在同一个技能根目录下会互相冲突或被重复加载。
因此当前生效的是 `skills/`（覆盖面更广，且已针对 26.x 更新），中文版放在 `skills-zh/` 作为中文入门参考。

想反过来（以中文版为唯一生效技能），把两份目录互换即可。

## 更新方式

```bash
git clone --depth 1 https://github.com/Jahrome907/minecraft-agent-skills.git
cp -r minecraft-agent-skills/.agents/skills/* <项目>/.agents/skills/
```

注意：本机 hosts 把 `github.com` 指向了本地加速器，git 需要 `git config --global http.schannelCheckRevoke false`
才能正常克隆（证书吊销检查会失败）。
