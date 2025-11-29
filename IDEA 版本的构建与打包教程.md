# IDEA 版本的构建与打包教程

本教程指导你如何为不同版本的 IntelliJ IDEA 构建并打包本插件（Chinese Name Annotator）。支持 Windows / macOS / Linux，支持离线构建。

---

## 1. 前置要求

- JDK：17（IDEA 2022.2+ 平台要求 Java 17）
- Gradle：使用本地 Gradle 或项目自带的 Gradle Wrapper
- IntelliJ 平台 SDK：
  - 方案 A（推荐离线）：使用本机已安装的 IntelliJ（通过 localPath 指向安装目录）
  - 方案 B：指定平台版本号（version.set），由 Gradle 自动下载对应 SDK

---

## 2. 两种构建方式

### 方案 A：使用本地 IntelliJ 安装（离线优先）

1) 设置 gradle.properties：

```
# 指向目标构建所使用的 IDEA 安装目录（示例为 Windows 路径，macOS 请见下方）
intellij.localPath=C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2023.1

# 可选：内存配置
org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
```

2) 确保 build.gradle.kts 的 intellij 块支持 localPath：

```kotlin
intellij {
    type.set("IC") // 目标平台：IC 社区版 / IU 旗舰版
    val lp = providers.gradleProperty("intellij.localPath").orNull
    if (!lp.isNullOrBlank()) {
        println("Using local IntelliJ at: $lp")
        localPath.set(lp)
    } else {
        version.set("2023.1") // fallback（无 localPath 时使用）
    }
}
```

3) 不同系统的 localPath 示例：

- Windows（旗舰版）：`C:/Program Files/JetBrains/IntelliJ IDEA 2025.2`
- Windows（社区版）：`C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2023.1`
- macOS（旗舰版）：`/Applications/IntelliJ IDEA.app/Contents`
- macOS（社区版）：`/Applications/IntelliJ IDEA CE.app/Contents`
- Linux：`/opt/idea-IU-2025.2`（按实际安装路径）

优点：不依赖网络下载 SDK；与目标 IDE 版本强一致。

### 方案 B：指定平台版本号（自动下载 SDK）

1) 在 build.gradle.kts 中直接指定：

```kotlin
intellij {
    type.set("IC")
    version.set("2023.1") // 或 2024.1、2025.2 等
}
```

2) 如需离线缓存加速，可设置本地 Gradle 仓库位置（见第 5 节）。

---

## 3. 设定兼容的 build 范围（since-build / until-build）

Gradle IntelliJ Plugin 会在构建时为 plugin.xml 打补丁。你可通过 `patchPluginXml` 明确设置：

```kotlin
patchPluginXml {
    sinceBuild.set("231")   // 最低兼容构建号
    // untilBuild.set("252.*") // 最高兼容（可选）
}
```

常见 IDEA 版本与构建号前缀对照（简表）：

- 2023.1 → 231
- 2023.2 → 232
- 2024.1 → 241
- 2024.2 → 242
- 2025.1 → 251
- 2025.2 → 252

备注：若不设置，插件会根据基准平台自动填充，但为了 Marketplace 发布与明确兼容范围，建议显式设置 sinceBuild（以及必要时的 untilBuild）。

---

## 4. Java 与 Gradle 插件版本建议

- Java 版本：`java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }`
- Gradle IntelliJ Plugin 版本：`1.17.3`（兼容 2025.2，并向后兼容 2020.3+）。
- 如果你要构建非常老的 IDEA 版本，请参考官方兼容矩阵，必要时降低 Gradle IntelliJ Plugin 版本。

---

## 5. 构建命令

以下命令会跳过会尝试启动 IDE 的可搜索项任务（在 CI/无头环境、或高版本 IDE 时可能引发问题）：

- 使用本地 Gradle（Windows 示例）：

```
D:/gradle/gradle-8.2/bin/gradle.bat \
  -g D:/gradle/repository \
  -p F:/aiproject/chinese-name-plugin \
  --no-daemon clean buildPlugin \
  -x test -x buildSearchableOptions -x jarSearchableOptions \
  --warning-mode all
```

- 使用 Gradle Wrapper（跨平台）：

```
./gradlew clean buildPlugin -x test -x buildSearchableOptions -x jarSearchableOptions --warning-mode all
```

可选加速/离线参数：

- 指定 `GRADLE_USER_HOME` 加速缓存：`GRADLE_USER_HOME=.gradle-cache ./gradlew ...`
- 若完全离线，建议配合 **方案 A** 的 localPath 构建，避免下载 SDK。

---

## 6. 产物位置与安装

- 成功后生成 ZIP：`build/distributions/chinese-name-plugin-<version>.zip`
- IDEA 安装：Settings/Preferences → Plugins → 齿轮 → Install Plugin from Disk → 选择 ZIP → 重启 IDE

---

## 7. 为其他版本构建的常见场景

### 7.1 构建 2023.1 兼容包

1) 设置目标平台：

```kotlin
intellij {
    type.set("IC")
    version.set("2023.1")
}
patchPluginXml {
    sinceBuild.set("231")
}
```

2) 执行构建命令（见第 5 节）。

### 7.2 构建 2025.2 兼容包（使用本机安装）

1) `gradle.properties` 中设定：

```
intellij.localPath=D:/JetBrains/IntelliJ IDEA 2025.2
```

2) 保持 `intellij { type.set("IC"); localPath }` 逻辑。
3) 执行构建命令（见第 5 节）。

### 7.3 同时产出多版本（建议分批构建）

- 按目标版本分别修改 `gradle.properties` 或 `build.gradle.kts`，逐一构建产物，分别命名归档。

---

## 8. 常见问题（FAQ）

- Q1：`buildSearchableOptions` 任务失败，启动 IDE 时崩溃？

  - A：在 `tasks {}` 中禁用：
    ```kotlin
    tasks {
        buildSearchableOptions { enabled = false }
        jarSearchableOptions { enabled = false }
    }
    ```
  - 或命令行 `-x buildSearchableOptions -x jarSearchableOptions` 跳过。
- Q2：菜单位置不对（跑到最后）？

  - A：检查 `<add-to-group>` 的 `relative-to-action` 是否为该分组真实存在的 actionId。若 ID 不存在，IDE 会将你的 Action 放在末尾。
- Q3：离线环境如何避免下载？

  - A：使用 **方案 A** 的 `localPath` 指向本地 IDEA 安装。并为 Gradle 指定本地缓存目录（`-g` 或 `GRADLE_USER_HOME`）。
- Q4：如何确定 sinceBuild？

  - A：参考上面的构建号对照，或在目标 IDE 中 Help → About 查看 Build Number 的前三位。

---

## 9. 变更清单对构建的影响（本插件）

- 已使用 Java 17 toolchain，满足 2022.2+ 要求。
- 已将 Gradle IntelliJ Plugin 升级到 `1.17.3`，支持 2025.2。
- 默认禁用了 `buildSearchableOptions/jarSearchableOptions` 任务，避免构建时启动 IDE。
- 支持 `intellij.localPath` 与直接指定 `intellij.version` 两种构建方式。

---

## 10. 最佳实践建议

- 为每个目标版本保留一份构建配置记录（例如在仓库的 docs/ 目录下），便于复用与回溯。
- 发布前使用 Run Plugin Verification（可选）验证兼容性。
- 若要发布到 Marketplace：准备好 README（Markdown）与插件描述（HTML），保持一致。

---

如你告知具体的目标版本（例如 2023.1 / 2024.1 / 2025.2），我可以基于此快速给出一份可直接粘贴的 build.gradle.kts 片段，帮你一步到位构建该版本的安装包。
