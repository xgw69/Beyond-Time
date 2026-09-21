# 1.12.2 线 00-scaffold.md（新建工程 + 已有工程改造）

<!-- 元信息块 -->
- 适用场景：全新 1.12.2 工程（从零搭 Forge 1.12.2 工程骨架）或已有工程改造（在已有 1.12.2 mod 工程里加内容前的结构核对）。
- 前置依赖：P0 模板与工具链（`examples/forge-1.12.2/`、便携 JDK 8 与 Gradle 4.9 路径见 `docs/verification-notes.md`）
- 相关文件：`references/20-legacy-1122-lane/10-blocks-items.md`（注册/配方/模型各节）、`references/03-assets.md`（顶层贴图模型规范，本线差异见 10 的"模型/贴图（1.12.2 差异）"节）、`references/20-legacy-1122-lane/90-troubleshooting.md`（排错）
- 适用加载器：Forge
- 适用版本线：1.12.2（Forge 14.23.5.2847 / ForgeGradle 2.3-SNAPSHOT / JDK 8 / Gradle 4.9）
- 核验日期：2026-08-10

## 目标

本文件解决两个问题：从零新建一个能跑的 Forge 1.12.2 工程；以及把内容加进一个已有 1.12.2 工程时，先核对结构、找到正确的注册入口。代码真相 = `examples/forge-1.12.2/`，下列代码块与该模板逐字一致。

## 主写法：新建工程

### 1. 工具链硬性要求

1.12.2 老工具链对 JDK/Gradle 版本是**硬约束**，版本不对构建直接崩（排错见 `references/20-legacy-1122-lane/90-troubleshooting.md` 条目 1）：

| 工具 | 要求 | 说明 |
|---|---|---|
| JDK | 必须是 **JDK 8** | ForgeGradle 2.3 在 JDK 9+ 无法工作（`Could not find tools.jar` 类错误）；本机 `D:\java` 实为 **JRE 8**（无 `tools.jar` / `javac`），不能用于构建；本仓库验证用便携 JDK 8 `.tools\jdk8\jdk8u502-b07` |
| Gradle | 必须是 **4.9**，走 wrapper | `gradle/wrapper/gradle-wrapper.properties` 的 `distributionUrl` 指向腾讯云镜像 `gradle-4.9-bin.zip`（官方源在本机网络受限，见 `docs/verification-notes.md`）；不要用系统 gradle |
| Forge | 1.12.2-**14.23.5.2847** | 该 patch 的 `-userdev.jar` 在 Forge maven 仍保留（2855/2856/2858/2859/2860 已移除，见 `docs/verification-notes.md`）；本线一律以 2847 为准 |

### 2. 目录结构

```text
example-mod/                  # 你的工程根目录（可任意命名）
├── build.gradle
├── gradle.properties
├── gradlew / gradlew.bat / gradle/wrapper/    # Gradle wrapper 四件套（直接复用模板文件）
└── src/main/
    ├── java/com/example/mod/    # 包名按你的 group 调整
    │   └── ExampleMod.java      # @Mod 主类（单文件 mod：注册/配方/客户端模型全在这一个文件）
    └── resources/
        ├── mcmod.info           # mod 元数据（version 由 processResources 展开，见第 4 节）
        └── assets/examplemod/   # blockstates / models / lang / textures（见 10-blocks-items 与 03-assets）
```

`src/main/java` 放 Java 源码，`src/main/resources` 放非代码资源。注意两点与 1.21 现代线的差异：本模板**没有 `settings.gradle`**（ForgeGradle 2.3 工程默认以目录名为根工程名，模板即如此）；**没有 `data/` 目录**（1.12.2 的配方 JSON 也放在 `assets/<modid>/recipes/`，见 10-blocks-items）。本模板也**没有 `pack.mcmeta`**（1.12.2 mod 的资源由 Forge 直接加载，不需要资源包元数据）。

### 3. build.gradle / gradle.properties

`build.gradle` 完整内容（与模板逐字一致）：

```groovy
buildscript {
    repositories {
        maven { url = 'https://maven.minecraftforge.net/' }
        mavenCentral()
    }
    dependencies {
        classpath 'net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT'
    }
}
apply plugin: 'net.minecraftforge.gradle.forge'

version = '1.0.0'
group = 'com.example.mod'
archivesBaseName = 'examplemod'
sourceCompatibility = targetCompatibility = '1.8'

minecraft {
    version = '1.12.2-14.23.5.2847'
    runDir = 'run'
    mappings = 'snapshot_20171003'
}

processResources {
    inputs.property 'version', project.version
    from(sourceSets.main.resources.srcDirs) {
        include 'mcmod.info'
        expand 'version': project.version
    }
}

jar {
    finalizedBy reobfJar
}
```

`gradle.properties` 完整内容（与模板逐字一致）：

```properties
org.gradle.jvmargs=-Xmx2G
org.gradle.daemon=false
```

要点：`minecraft { version = '1.12.2-14.23.5.2847' }` 决定 Forge 版本，`mappings = 'snapshot_20171003'` 决定 1.12.2 的映射名（代码里的类/方法名都按它写）；`runDir = 'run'` 让游戏运行数据（配置/日志/存档）落在工程根的 `run/` 目录。Gradle wrapper 四件套直接复用模板文件即可，wrapper 已指向腾讯云 Gradle 4.9 镜像。

**`jar { finalizedBy reobfJar }` 不能删**（P1.5 实测修正，见 `docs/verification-notes.md`）：ForgeGradle 2.3 的 `jar` 任务默认**不会**被 `reobfJar` 收尾，直接 `gradlew build` 产出的 jar 可能仍含开发映射名（MCP 名，如 `Material.ROCK`），正式客户端加载时抛 `NoSuchFieldError`（见 90-troubleshooting 条目 5）。这一行保证每次 `gradlew build` 后自动执行 reobf，把 jar 转成正式客户端能读的 SRG 名（如 `Material.field_151576_e`）。

### 4. mcmod.info（${version} 展开机制）

`src/main/resources/mcmod.info` 完整内容（与模板逐字一致）：

```json
[
  {
    "modid": "examplemod",
    "name": "Example Mod",
    "version": "${version}",
    "description": "Minimal self-written example mod (Forge 1.12.2).",
    "authors": ["Codex"]
  }
]
```

要点：`modid` 必须与 Java 主类 `@Mod(modid = ...)` 里写的值一致；`"version": "${version}"` 不是给游戏读的最终值——构建时 `processResources` 的 `expand 'version': project.version` 会把它替换成 `build.gradle` 里的 `version = '1.0.0'`，产物 jar 内 `mcmod.info` 的 version 字段是展开后的 `1.0.0`。若在别的构建系统里手动打包，`${version}` 不会被展开，加载器读到的版本就是字面量 `${version}`。

### 5. 主类骨架

`src/main/java/com/example/mod/ExampleMod.java` 完整内容（与模板逐字一致）。1.12.2 的注册/配方/客户端模型都写在这个单文件主类里，用事件处理器（`@Mod.EventHandler`）挂到 Forge 生命周期：

```java
package com.example.mod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ExampleMod.MODID, name = "Example Mod", version = "1.0.0")
public class ExampleMod {
    public static final String MODID = "examplemod";

    public static final Block EXAMPLE_BLOCK = new Block(Material.ROCK)
            .setRegistryName(MODID, "example_block")
            .setUnlocalizedName(MODID + ".example_block");
    public static final Item EXAMPLE_BLOCK_ITEM = new ItemBlock(EXAMPLE_BLOCK)
            .setRegistryName(EXAMPLE_BLOCK.getRegistryName())
            .setUnlocalizedName(MODID + ".example_block");
    public static final Item EXAMPLE_ITEM = new Item()
            .setRegistryName(MODID, "example_item")
            .setUnlocalizedName(MODID + ".example_item");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.findRegistry(Block.class).register(EXAMPLE_BLOCK);
        GameRegistry.findRegistry(Item.class).register(EXAMPLE_BLOCK_ITEM);
        GameRegistry.findRegistry(Item.class).register(EXAMPLE_ITEM);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.addShapedRecipe(
                new ResourceLocation(MODID, "example_item"), null,
                new ItemStack(EXAMPLE_ITEM),
                "X", 'X', Blocks.COBBLESTONE);
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void clientPreInit(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(EXAMPLE_BLOCK_ITEM, 0,
                new ModelResourceLocation(EXAMPLE_BLOCK.getRegistryName(), "normal"));
        ModelLoader.setCustomModelResourceLocation(EXAMPLE_ITEM, 0,
                new ModelResourceLocation(EXAMPLE_ITEM.getRegistryName(), "inventory"));
    }
}
```

骨架结构：`preInit`（注册方块/物品）→ `init`（代码配方）→ `clientPreInit`（仅客户端，挂模型）。每一段的逐行讲解在 `references/20-legacy-1122-lane/10-blocks-items.md`，本文件不重复。

### 6. 构建与运行

```powershell
$env:JAVA_HOME = "<JDK8 home>"   # 本仓库验证环境 = .tools\jdk8\jdk8u502-b07，见 docs/verification-notes.md
cd 你的工程目录
.\gradlew.bat build
.\gradlew.bat runClient
```

首次运行说明：
- 第一次执行会在 wrapper 引导下下载 Gradle 4.9、ForgeGradle 依赖与 Minecraft 1.12.2 反编译环境，耗时较长（本机实测数分钟到十几分钟），需要网络；下载源受限时按 `docs/verification-notes.md` 的镜像说明处理。
- `gradlew build` 产出 jar 在 `build/libs/`（模板产物名 `examplemod-1.0.0.jar`）；`gradlew runClient` 会弹出游戏窗口，冒烟口径 = 日志出现 `Forge Mod Loader has successfully loaded` 且 FATAL 计数为 0（P0 实测，见 `docs/verification-notes.md`）。
- 映射名分两套：开发期（编译/runClient）用 MCP 名（`Material.ROCK`），**发布给正式客户端的 jar 必须是 SRG 名**（reobf 后 `Material.field_151576_e`）。`build.gradle` 里的 `jar { finalizedBy reobfJar }` 已保证 build 即 reobf；若用 IDE/其他方式打包，发布前必须跑一次 `.\gradlew.bat reobfJar` 并用 `javap -c -p` 抽查产物里是 `field_151576_e` 而非 `ROCK`（判断方法见 90-troubleshooting 条目 5）。
- `build/`、`run/`、`.gradle/` 都是本地生成物，不入库（仓库 `.gitignore` 已覆盖）。
- 常见坑：开发客户端联机/登录报证书错误（`Could not validate server authentication` 类）时，可在 runClient 的 JVM 参数加 `-Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignoreServerSecurityCertificate=true`；仅冒烟主菜单不需要。

## 新手易错点

1. `JAVA_HOME` 指到 JRE 或 JDK 9+：ForgeGradle 2.3 直接崩（`Could not find tools.jar` 或类版本错误），见 90-troubleshooting 条目 1。
2. 用系统 `gradle` 而不是 `gradlew`：本工程必须 Gradle 4.9，一律走 wrapper。
3. 只复制 `build.gradle` 却漏掉 `gradle/wrapper/gradle-wrapper.properties`：wrapper 无法解析，构建失败；四件套（`gradlew` / `gradlew.bat` / `gradle/wrapper/gradle-wrapper.jar` / `gradle/wrapper/gradle-wrapper.properties`）一起复制。
4. `modid` 不一致：`mcmod.info` 的 `modid` 与 `@Mod(modid = ...)` 的值不同，mod 加载失败（见 90-troubleshooting 条目 2）。
5. 手动改 `mcmod.info` 里的 `${version}`：构建时会被 `processResources` 覆盖；要改版本改 `build.gradle` 的 `version`。
6. 加 `pack.mcmeta` 或 `data/` 目录：1.12.2 mod 不需要，本线不写（贴图/模型差异见 10-blocks-items）。
7. 删掉或没写 `jar { finalizedBy reobfJar }`：开发环境正常、正式客户端启动即抛
   `NoSuchFieldError: ROCK`（jar 未 reobf，见 90-troubleshooting 条目 5）。

## 验证清单

- [ ] 自动（对照模板）：目录结构与上述代码块与 `examples/forge-1.12.2/` 逐字一致。
- [ ] 自动：与 10-blocks-items 的最小内容（注册 + 语言文件 + 贴图）一起完成后，`gradlew build` 输出 `BUILD SUCCESSFUL`。
- [ ] 自动（发布前必做）：`javap -c -p -classpath build\libs\<产物名>.jar <主类>` 抽查产物为 SRG 名
  （`Material.field_151576_e` 而非 `Material.ROCK`），否则正式客户端启动即崩（见 90-troubleshooting 条目 5）。
- [ ] 自动：`gradlew runClient` 冒烟到主菜单，日志出现 `Forge Mod Loader has successfully loaded` 且无 FATAL（P0 口径，见 `docs/verification-notes.md`）。
- [ ] 人工：进世界放置/使用新内容（归 P1.5-5 端到端人工验证）。

## 已有项目改造

流程：识别（`mcmod.info` + `build.gradle`）→ 定位注册入口 → 追加注册（不新建第二个入口）→ 增量验证。

识别要点：有 `src/main/resources/mcmod.info` 且 `build.gradle` 用 `apply plugin: 'net.minecraftforge.gradle.forge'` + `version = '1.12.2-...'` = Forge 1.12.2 工程（探测规则另见 `references/00-intake.md`）。注册入口的特征是 `GameRegistry.findRegistry(Block/Item.class).register(...)`（2847 起 `GameRegistry.register` 为 private，只能用 `findRegistry(...).register(...)` 或注册事件）。

三种最常见的 1.12.2 已有工程形态：

| 形态 | 特征 | 检查清单 |
|---|---|---|
| 1. 单文件 mod | 全部注册写在 `ExampleMod.java` 的 `preInit` 里 | ① 找到 `preInit` 里的注册块；② 在它上面追加 `GameRegistry.findRegistry(Block/Item.class).register(...)` 条目；③ 不要新建第二个注册入口方法；④ build → runClient |
| 2. 已有注册辅助类 | `ModBlocks`/`ModItems` 或注册事件类里已有注册逻辑 | ① 定位已有的 `findRegistry(...).register(...)` 所在；② 追加条目到同处（同一注册入口）；③ 确认主类 `preInit` 只触发一次注册；④ build → runClient |
| 3. 多 mod / 多 modid 工程 | `mcmod.info` 是数组，多个 modid | ① 确认目标内容的 modid；② 只在该 modid 对应的注册处追加，别混用；③ `mcmod.info` 数组元素与 modid 一一对应；④ build → runClient |

增量验证要点：只加内容不改结构时，build 通过且 runClient 主菜单无错误即可；语言文件或贴图缺失不会报编译错，而是在游戏内显示键名/黑紫方块（分别见 10-blocks-items 与 03-assets 的易错点）。
