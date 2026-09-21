# 开发环境说明

本文记录本机（以及中国大陆网络环境下）跑通构建所需的配置。全部配置都已在仓库里，
只有 JDK 25 的路径是机器相关的。

## 1. JDK 25

MC 26.2 需要 Java 25 工具链。本机没有独立安装 JDK 25，
但 Minecraft 启动器已经为 26.x 下载了一份（自带 `javac`）：

```
C:/Users/xgw/AppData/Roaming/.minecraft/runtime/java-runtime-epsilon
```

构建脚本不想把机器路径写进仓库，因此这份路径放在**用户级**配置里：

```
%USERPROFILE%\.gradle\gradle.properties
    org.gradle.java.installations.paths=C:/Users/xgw/AppData/Roaming/.minecraft/runtime/java-runtime-epsilon
```

注意：启动器升级或清理 runtimes 时这份运行时可能被替换，届时构建会报找不到 Java 25，
重新指一次路径即可。长期建议装一个独立 JDK 25（Temurin / Microsoft OpenJDK 均可），
然后把上面那行改到新路径。

## 2. 依赖下载与镜像

这台机器的网络比较特殊，仓库里做了三处适配：

| 问题 | 处理 |
|---|---|
| `maven.neoforged.net` 经常被重置 | `settings.gradle` 在 settings 层应用 `net.neoforged.moddev.repositories`，把 CERNET 镜像 `https://mirrors.cernet.edu.cn/bmclapi/` 放在最前，官方源留作兜底 |
| 连接偶发重置导致 Gradle 直接失败 | `gradle.properties` 里开 `org.gradle.internal.repository.max.retries=8`、`initial.backoff=1000` |
| `services.gradle.org` 会 302 到 `github.com`，而本机 hosts 把 github 指到本地加速器，Java 因证书链失败 | Gradle wrapper 的 `distributionUrl` 改用 `https://mirrors.cloud.tencent.com/gradle/gradle-9.2.1-bin.zip` |

只有 `maven.neoforged.net/mojang-meta/` 没有镜像，仍然走官方源，靠上面的重试兜住。

要在网络正常的机器上改回官方源，恢复两点即可：

1. `gradle/wrapper/gradle-wrapper.properties` 的 `distributionUrl` 换回
   `https://services.gradle.org/distributions/gradle-9.2.1-bin.zip`；
2. `settings.gradle`、`build.gradle` 里删掉 CERNET 镜像仓库（`build.gradle` 未直接声明仓库）。

## 3. 首次构建会下载多少内容

首次构建实测下载量（2026-09-21 实测）：

| 内容 | 大小 |
|---|---|
| Gradle 9.2.1 发行包 | ≈ 143 MB |
| Maven 依赖（NeoForge、Minecraft 库、ModDevGradle 等） | ≈ 217 MB |
| NeoForm 运行时与中间产物（`~/.gradle/caches/neoformruntime`） | ≈ 289 MB |
| 合计 | ≈ 650 MB |

之后改代码只需要增量编译，不再重复下载。如果某个仓库特别慢，可以给它单独配代理，
或临时设置 `HTTP_PROXY` / `HTTPS_PROXY` 环境变量后重跑 Gradle。

## 4. 常用命令

```bash
./gradlew build --console=plain        # 构建
./gradlew runClient                    # 客户端
./gradlew runData                      # 数据生成
./gradlew runGameTestServer            # GameTest 自动化验证
./gradlew --refresh-dependencies build # 依赖元数据异常时强制刷新
```

## 5. 排错速查

| 现象 | 原因 / 处理 |
|---|---|
| `PKIX path building failed` | 目标地址被局域网代理/加速器拦截且证书不被 Java 信任；换直连镜像，或把该 CA 导入 JDK 的 cacerts |
| `No matching toolchains found for Java 25` | 第 1 节的 JDK 25 路径失效，重新指定 |
| 依赖 `Connection reset` | 网络抖动；仓库已配 8 次重试，仍失败可重跑，或 `--refresh-dependencies` |
| 客户端起不来 / 卡在资源加载 | 先用 `runData` 确认资源与数据 JSON 能生成 |
