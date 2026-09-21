# 90-troubleshooting.md（现代线故障速查，P0 最小版）

<!-- 元信息块 -->
- 适用场景：NeoForge 1.21.x（及现代线）开发中崩溃/报错排查，P0 先收三条起步故障。
- 前置依赖：无
- 相关文件：无（P1 起随 10-blocks-items 等内容文件补充专有问题）
- 适用加载器：NeoForge（现代线）
- 适用版本线：1.21.x / 1.20.1
- 核验日期：2026-08-10

> 来源标注规则：每条记录必须注明"真实案例"或"推断"。P0 三条均无实测触发记录，
> 全部标注 **推断（2026-08-10）**；P1–P3 开发中实际触发后回填为"真实案例"并补日志摘录。

## 1. Mixin 注入失败（`Invalid injection point`）

现象（报错原文示例）：

```text
[ERROR] Mixin apply failed: mixins.example.json:example.mixin.ExampleMixin from mod examplemod ->
java.lang.Exception: Invalid injection point @Inject at ... with spec arguments [method ...]
```

- 原因：`@Inject` 注解里写的 `method` 目标在当前 Minecraft 版本/映射下不存在或签名写错；
  混淆名与映射名不匹配是常见来源。
- 修法：核对目标方法名与签名（按当前版本的映射名写）；必要时改用 `@Redirect`/`@WrapOperation`
  等更稳的注入点；在 `mixin config` 里确认 mixin 类路径正确。
- 来源：推断（2026-08-10）。

## 2. blockstate 缺失（游戏内黑紫方块 / `missing model`）

现象（日志原文示例）：

```text
[WARN] Missing model ... for blockstate examplemod:example_block
```

游戏内表现为黑紫相间的方块（missing texture/model）。

- 原因：blockstate JSON / model JSON / 贴图路径三者之一缺失或路径写错，与资源位置不一致；
  常见错误是 `blockstates/example_block.json` 里模型名与 `models/block/` 下文件名对不上，
  或贴图目录写成了单数 `texture` 而非 `textures`。
- 修法：按资源位置（`namespace:path`）逐层核对：blockstate → model parent/textures → 贴图文件；
  JSON 用 `{}` 校验语法；清理 `run/` 缓存后重启验证。
- 来源：推断（2026-08-10）。

## 3. `NoClassDefFoundError` / `ClassNotFoundException`（依赖缺失）

现象（日志原文示例）：

```text
java.lang.NoClassDefFoundError: net/example/lib/SomeClass
```

- 原因：代码用到了某个库/API 类，但该依赖没有在 build 配置里声明、没有被打进产物，
  或加载器混用导致类路径不一致（如把 NeoForge 的依赖当 Fabric 依赖加载）。
- 修法：在 `build.gradle` 的 `dependencies { }` 里显式声明依赖并确认 `implementation`
  会进入运行时类路径；检查 mod 依赖的加载器版本与元数据（`neoforge.mods.toml` 的 `[[dependencies]]`）；
  不同加载器混用时拆成独立工程或按 80-loader-patterns 的差异视图重写。
- 来源：推断（2026-08-10）。
