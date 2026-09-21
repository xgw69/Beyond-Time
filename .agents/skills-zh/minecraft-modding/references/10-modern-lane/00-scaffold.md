# 现代线 00-scaffold.md（新建工程 + 已有工程改造）

<!-- 元信息块 -->
- 适用场景：全新工程（从零搭 NeoForge 1.21 工程骨架）或已有工程改造（在已有 mod 工程里加内容前的结构核对）。
- 前置依赖：无（P0 模板与工具链：`examples/neoforge-1.21/`、JDK 21 与 Gradle 8.14 路径见 `docs/verification-notes.md`）
- 相关文件：`references/10-modern-lane/10-blocks-items.md`「注册」节（注册类定义）、`references/03-assets.md`（资源目录）、`references/10-modern-lane/70-version-matrix.md` 与 `references/10-modern-lane/80-loader-patterns.md`（差异）、`references/10-modern-lane/90-troubleshooting.md`（排错）
- 适用加载器：NeoForge
- 适用版本线：1.21.x（NeoForge 21.1.248 / ModDevGradle 2.0.143 / JDK 21）
- 核验日期：2026-08-10

## 目标

本文件解决两个问题：从零新建一个能跑的 NeoForge 1.21 工程；以及把内容加进一个已有工程时，先核对结构、找到正确的注册入口。代码真相 = `examples/neoforge-1.21/`，下列代码块与该模板逐字一致。

## 主写法：新建工程

### 1. 目录结构

```text
example-mod/                  # 你的工程根目录（可任意命名）
├── settings.gradle
├── build.gradle
├── gradle.properties
├── gradlew / gradlew.bat / gradle/wrapper/    # Gradle wrapper 四件套
└── src/main/
    ├── java/com/example/mod/    # 包名按你的 group 调整
    │   ├── ExampleMod.java      # @Mod 主类
    │   ├── ModBlocks.java       # 方块注册类（见 10-blocks-items「注册」节）
    │   └── ModItems.java        # 物品注册类（见 10-blocks-items「注册」节）
    └── resources/
        ├── META-INF/neoforge.mods.toml
        ├── pack.mcmeta
        ├── assets/examplemod/   # 贴图/模型/语言文件（见 03-assets.md）
        └── data/examplemod/     # 配方/标签/战利品等数据（见 10-blocks-items.md）
```

`src/main/java` 放 Java 源码，`src/main/resources` 放非代码资源；`assets/` 下的东西只影响外观与文本，`data/` 下的东西定义游戏玩法（配方、标签、战利品）。两者目录都按 `modid` 命名，别写错。

### 2. settings.gradle / build.gradle / gradle.properties

`settings.gradle` 完整内容：

```groovy
pluginManagement {
    repositories {
        maven { url = 'https://maven.neoforged.net/releases' }
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = 'example-neoforge-1.21'
```

`build.gradle` 完整内容（版本 21.1.248 / ModDevGradle 2.0.143，已由 P0 实测）：

```groovy
plugins {
    id 'java'
    id 'net.neoforged.moddev' version '2.0.143'
}

group = 'com.example.mod'
version = '1.0.0'

neoForge {
    version = '21.1.248'
    runs {
        client { client() }
        server { server() }
    }
    mods {
        examplemod {
            sourceSet sourceSets.main
        }
    }
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}
```

`gradle.properties` 完整内容：

```properties
org.gradle.jvmargs=-Xmx2G
org.gradle.daemon=false
```

要点：`neoForge { version = '21.1.248' }` 决定 NeoForge 版本，`net.neoforged.moddev` 插件的 `2.0.143` 决定工具链；`runs { client {} }` 会生成 `runClient` 任务。Gradle wrapper 四件套（`gradlew` / `gradlew.bat` / `gradle/wrapper/gradle-wrapper.jar` / `gradle/wrapper/gradle-wrapper.properties`）直接复用模板文件即可，wrapper 指向腾讯云 Gradle 8.14 镜像（官方源在本机网络受限，见 `docs/verification-notes.md`）。

### 3. neoforge.mods.toml 与 pack.mcmeta

`src/main/resources/META-INF/neoforge.mods.toml` 完整内容：

```toml
modLoader="javafml"
loaderVersion="[4,)"
license="MIT"

[[mods]]
modId="examplemod"
version="1.0.0"
displayName="Example Mod (NeoForge 1.21)"
authors="Codex"
description="Minimal self-written example mod."
```

`src/main/resources/pack.mcmeta` 完整内容：

```json
{ "pack": { "description": "examplemod resources", "pack_format": 34 } }
```

要点：`modId` 是 mod 的唯一标识，`neoforge.mods.toml` 里的 `modId` 必须与 Java 主类 `@Mod(...)` 里写的值一致；`pack_format` 34 对应 1.21.1（1.20.1 是 15，见 70-version-matrix）。

### 4. 主类骨架

`src/main/java/com/example/mod/ExampleMod.java` 完整内容：

```java
package com.example.mod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public ExampleMod(IEventBus bus) {
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
    }
}
```

主类用 `@Mod(MODID)` 声明，构造器接收事件总线（`IEventBus`）。`ModBlocks.BLOCKS.register(bus)` / `ModItems.ITEMS.register(bus)` 是"注册入口"：把两个 DeferredRegister 挂到总线上，内容才会进注册表。这两个类的完整定义（`DeferredRegister.createBlocks` / `createItems` / `registerSimpleBlock` / `registerSimpleItem` 等）在 `references/10-modern-lane/10-blocks-items.md`「注册」节，本文件不重复。

### 5. 构建与运行

```powershell
$env:JAVA_HOME = "<JDK21 home>"   # 本仓库验证环境见 docs/verification-notes.md
cd 你的工程目录
.\gradlew.bat build
.\gradlew.bat runClient
```

首次启动说明：
- 第一次执行会在 wrapper 引导下下载 Gradle 8.14 与 Minecraft 依赖/资产，耗时较长，需要网络；下载源受限时按 `docs/verification-notes.md` 的镜像说明处理。
- `gradlew build` 产出 jar 在 `build/libs/`；`gradlew runClient` 会弹出游戏窗口，冒烟口径 = 进到主菜单且日志无致命错误。
- `build/`、`run/`、`.gradle/` 都是本地生成物，不入库（模板的 `.gitignore` 已覆盖）。

## 新手易错点

1. 只复制 `build.gradle` 却漏掉 `settings.gradle` 的 `pluginManagement`：插件仓库解析失败，报 `Plugin ... could not be found`。
2. JDK 版本不对：NeoForge 1.21.x 需要 JDK 21；用 JDK 8/17 会报 toolchain 或 class 版本错误。
3. `modId` 不一致：`neoforge.mods.toml` 的 `modId` 与 `@Mod(...)` 的值不同，mod 加载失败。
4. 定义 `DeferredRegister` 后忘了在构造器里 `register(bus)`：编译能过、游戏能开，但内容不会进注册表。
5. 用系统 `gradle` 而不是 `gradlew`：版本不匹配（本项目需 Gradle 8.14），一律走 wrapper。
6. 资源目录名写错：`assets/` 与 `data/` 下的第一层必须是 `modid`，写错会在游戏内表现为缺贴图/配方不生效（排错见 90-troubleshooting）。

## 验证清单

- [ ] 自动（对照模板）：目录结构与上述代码块与 `examples/neoforge-1.21/` 逐字一致。
- [ ] 自动：与 10-blocks-items 的最小内容（注册类 + 语言文件 + 贴图）一起完成后，`gradlew build` 输出 `BUILD SUCCESSFUL`。
- [ ] 自动：`gradlew runClient` 冒烟到主菜单，日志无致命错误（P0 口径，见 `docs/verification-notes.md`）。
- [ ] 人工：进世界放置/使用新内容（归 P1-8 端到端人工验证）。

## 已有项目改造

流程：识别加载器与版本（探测规则见 `references/00-intake.md`：`neoforge.mods.toml` → NeoForge，版本从 `gradle.properties` / `gradle/libs.versions.toml` 推断）→ 定位已有注册入口 → 在现有 `DeferredRegister` 上追加（不新建第二个）→ 增量验证（build + runClient）。

三种最常见的已有工程形态：

| 形态 | 特征 | 检查清单 |
|---|---|---|
| 1. 已有 DeferredRegister + 内容类 | `ModBlocks`/`ModItems` 里已有 `public static final DeferredRegister.Blocks BLOCKS = ...` | ① 找到该声明；② 直接在它上面追加 `registerSimpleBlock` / `registerSimpleItem`，不要新建第二个 `DeferredRegister`；③ 主类构造器已有 `register(bus)` 则不动；④ build → runClient |
| 2. 只有 @Mod 主类，无 DeferredRegister | 主类构造器里没有 `register(bus)`，内容用旧式直接注册 | ① 在内容类里建 `DeferredRegister` 并把旧条目迁进来；② 主类构造器补 `register(bus)`；③ build → runClient |
| 3. 多 mod / 多源集工程 | 一个工程里多个 `modId` | ① 确认目标内容的 `modId`；② 只在该 modid 对应的 `DeferredRegister` 上追加，别混用；③ `neoforge.mods.toml` 的 `[[mods]]` 数量与 modid 对应；④ build → runClient |

增量验证要点：只加内容类不改结构时，build 通过且 runClient 主菜单无错误即可；语言文件或贴图缺失不会报编译错，而是在游戏内显示英文名/黑紫方块（分别见 10-blocks-items 与 03-assets 的易错点）。
