# 02-common-concepts.md（运行机制通识）

> 前置依赖：无（术语定义见 `references/01-glossary.md`，本文不重复定义）。
> 适用加载器：通用（代码片段分别取自 NeoForge 1.21.x 与 Forge 1.12.2 模板，见 `examples/`）。
> 核验日期：2026-08-10。

## 1. 注册（Registry）

**一句话结论：** 方块、物品等一切"新内容"必须先登记进注册表，游戏才知道它存在。

新手解释：注册表（Registry）是游戏维护的"内容清单"。你写一个 `new Block(...)` 只是造了个 Java 对象；
不注册的话，游戏根本不知道它，存档里也不会出现这个方块。注册动作做了两件事：给它一个稳定的 ID
（以 modid 为命名空间），并把它挂进对应类别的清单（方块清单、物品清单……）。现代线用
`DeferredRegister`（延迟注册，加载时统一提交），1.12.2 用 `GameRegistry.register`（预初始化阶段直接登记）。

在代码里长什么样（NeoForge 1.21.x，`examples/neoforge-1.21`）：

```java
public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(ExampleMod.MODID);
public static final Supplier<Block> EXAMPLE_BLOCK =
        BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of());

// 主类构造器里把注册表挂到事件总线
public ExampleMod(IEventBus bus) {
    ModBlocks.BLOCKS.register(bus);
}
```

在代码里长什么样（Forge 1.12.2，`examples/forge-1.12.2`）：

```java
@Mod.EventHandler
public void preInit(FMLPreInitializationEvent event) {
    GameRegistry.register(EXAMPLE_BLOCK);
    GameRegistry.register(new ItemBlock(EXAMPLE_BLOCK)
            .setRegistryName(EXAMPLE_BLOCK.getRegistryName()));
}
```

## 2. 副作用与分端（Side）

**一句话结论：** 代码运行在"客户端"或"服务端"，不是所有代码两端都该跑；客户端代码碰服务端状态就会出问题。

新手解释：物理端指真实进程——客户端进程和服务器进程；逻辑端指逻辑环境。单人游戏里你的客户端进程内
同时跑了一个逻辑服务端，所以看起来"只有一台电脑"也有双端。渲染、输入、GUI 只在物理客户端存在；
存档、生物 AI、掉落等权威计算在逻辑服务端。1.12.2 用 `@SideOnly(Side.CLIENT)` 把仅客户端代码隔离，
现代线（NeoForge）按事件/入口区分（如 `client` run 配置），跨端逻辑用网络包同步，而不是直接访问。

在代码里长什么样（Forge 1.12.2）：

```java
@SideOnly(Side.CLIENT)
@Mod.EventHandler
public void clientPreInit(FMLPreInitializationEvent event) {
    ModelLoader.setCustomModelResourceLocation(EXAMPLE_BLOCK, 0,
            new ModelResourceLocation(EXAMPLE_BLOCK.getRegistryName(), "normal"));
}
```

## 3. 资源位置（ResourceLocation）

**一句话结论：** 一切资源用 `命名空间:路径` 定位，命名空间默认就是 modid。

新手解释：Minecraft 用 `ResourceLocation` 统一标识方块、物品、贴图、配方等资源，格式
`namespace:path`，如 `examplemod:example_block`。mod 自己创造的资源一律用自己的 modid 做命名空间，
避免和原版或其他 mod 撞名。JSON 里引用模型/贴图时，路径要对应 `assets/<namespace>/` 下的目录结构。

在代码里长什么样：

```java
// 1.12.2：注册名就是 ResourceLocation
.setRegistryName(MODID, "example_block")

// JSON：模型引用同命名空间资源
// { "parent": "minecraft:block/cube_all", "textures": { "all": "examplemod:block/example_block" } }
```

## 4. 构建流程

**一句话结论：** 用 Gradle wrapper 拉依赖、编译、打包；`gradlew build` 的产物 jar 在 `build/libs/`。

新手解释：仓库里的 `gradlew`/`gradlew.bat` 是 Gradle wrapper，它会自动下载对应版本的 Gradle，
所以本机不用全局安装 Gradle。Java 版本由工程决定：NeoForge 1.21.x 需要 JDK 21，Forge 1.12.2
需要 JDK 8（ForgeGradle 2.3 在 JDK 9+ 无法工作）。`gradlew build` 会执行编译 + 资源处理 + 打包；
`gradlew runClient` 启动游戏客户端。构建产物在 `build/libs/<工程名>-<版本>.jar`；`build/`、`run/`、
`.gradle/` 都是本地生成物，不入库。

在代码里长什么样（NeoForge 1.21.x 的 `build.gradle`）：

```groovy
plugins {
    id 'java'
    id 'net.neoforged.moddev' version '2.0.143'
}
neoForge {
    version = '21.1.248'
    runs {
        client { client() }
        server { server() }
    }
}
```

> 具体版本号以 `examples/neoforge-1.21/build.gradle` 与 `examples/forge-1.12.2/build.gradle` 为准。

## 5. 加载器差异概览（NeoForge / Fabric / Forge）

**一句话结论：** 三个加载器的"注册 + 事件"模型不同，但概念一一对应；细节差异查
`references/10-modern-lane/80-loader-patterns.md`。

新手解释：Forge 是历史最久的老牌加载器（1.12.2 用 ForgeGradle 2.3 + `GameRegistry` + `@Mod.EventHandler`）；
NeoForge 是 Forge 的现代继任者（1.21.x 用 ModDevGradle + `DeferredRegister` + 事件总线）；
Fabric 是轻量加载器（`fabric.mod.json` + 入口点 + `Registry.register` + Fabric API）。
三者都遵循同一套游戏底层概念：注册、分端、资源位置——这就是本文件存在的意义：
先懂通识，再看差异，而不是换加载器就全部重学。三个加载器的逐项对比在
`references/10-modern-lane/80-loader-patterns.md`（P1 交付，P0 尚未创建；P0 阶段以本文件概览为准）。

一句话对比：

| 维度 | NeoForge 1.21.x | Fabric 1.21.x | Forge 1.12.2 |
|---|---|---|---|
| 元数据 | `neoforge.mods.toml` | `fabric.mod.json` | `mcmod.info`（1.12.2） |
| 注册方式 | `DeferredRegister` | `Registry.register`（Fabric API） | `GameRegistry.register` |
| 事件模型 | 事件总线（IEventBus） | 回调/事件（Fabric API） | `@Mod.EventHandler` 阶段方法 |
| 工具链 | ModDevGradle + JDK 21 | Fabric Loom + JDK 17/21 | ForgeGradle 2.3 + JDK 8 |
