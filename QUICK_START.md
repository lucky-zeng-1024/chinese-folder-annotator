# 快速开始指南

## 安装和配置

### 前置要求

- IntelliJ IDEA 2023.1 或更高版本
- Java 11 或更高版本
- Gradle 8.1（可选，项目包含 gradle wrapper）

### 开发环境设置

1. **克隆或下载项目**

   ```bash
   git clone https://github.com/yourusername/chinese-name-plugin.git
   cd chinese-name-plugin
   ```
2. **在 IntelliJ IDEA 中打开项目**

   - File → Open → 选择项目目录
   - 等待 Gradle 同步完成
3. **运行插件**

   - 在 IDE 中按 `Ctrl+F5`（或 Run → Run）
   - 这会启动一个新的 IntelliJ IDEA 实例，加载你的插件
4. **测试插件**

   - 在新启动的 IDE 中创建或打开一个项目
   - 在项目树中右键点击任意文件夹
   - 选择 "Add Chinese Annotation"
   - 输入中文名称并保存

## 使用场景示例

### 场景 1：标注资源文件夹

1. 右键点击 `resource` 文件夹
2. 选择 "Add Chinese Annotation"
3. 输入中文名称：`资源`
4. 不勾选 "Hide original folder name"
5. 点击 OK

**结果**：`resource(资源)`

### 场景 2：隐藏原始名称

1. 右键点击 `script` 文件夹
2. 选择 "Add Chinese Annotation"
3. 输入中文名称：`脚本`
4. 勾选 "Hide original folder name"
5. 点击 OK

**结果**：`脚本`

### 场景 3：管理所有标注

1. 打开 Settings/Preferences
2. 导航到 Tools → Chinese Name Annotator
3. 查看所有已配置的标注
4. 可以：
   - 修改 "Hide Original" 列的值
   - 选择行后点击 "Delete" 删除单个标注
   - 点击 "Clear All" 删除所有标注

## 常见问题

### Q: 标注没有显示？

**A**:

- 确保你已经保存了标注（点击 OK）
- 尝试刷新项目树（F5）
- 检查 Settings → Tools → Chinese Name Annotator 中是否有配置

### Q: 如何删除标注？

**A**:

- 方式 1：右键点击文件夹 → Add Chinese Annotation → 清空中文名称 → OK
- 方式 2：Settings → Tools → Chinese Name Annotator → 选择行 → Delete

### Q: 配置文件在哪里？

**A**: `.idea/chineseName.xml`

### Q: 可以为同一个文件夹设置多个标注吗？

**A**: 不可以，每个文件夹只能有一个标注。如果重新设置，会覆盖之前的标注。

### Q: 标注会被版本控制跟踪吗？

**A**: 配置文件在 `.idea` 目录中，通常不会被提交到版本控制。如果需要共享配置，可以手动导出和导入。

## 开发技巧

### 调试插件

1. 在代码中设置断点
2. 按 `Ctrl+F5` 启动调试
3. 在新启动的 IDE 中触发相应操作
4. 调试器会在断点处停止

### 查看日志

- 在运行的 IDE 中，打开 Help → Show Log in Explorer/Finder
- 查看 `idea.log` 文件

### 修改后重新加载

- 修改代码后，重新按 `Ctrl+F5` 启动新的 IDE 实例
- 不需要手动构建，Gradle 会自动处理

## 构建和打包

### 本地构建

```bash
./gradlew build
```

### 创建可安装的插件

```bash
./gradlew buildPlugin
```

生成的文件位于 `build/distributions/`，可以通过以下方式安装：

- Settings → Plugins → ⚙️ → Install Plugin from Disk

## 提交到 JetBrains Marketplace

1. 构建插件：`./gradlew buildPlugin`
2. 访问 https://plugins.jetbrains.com/
3. 注册账户并创建新插件
4. 上传 `build/distributions/chinese-name-plugin-1.0.0.zip`
5. 填写插件信息
6. 等待审核（通常 1-2 天）

## 下一步

- 查看 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) 了解项目结构
- 查看 [README.md](README.md) 了解功能详情
- 在 GitHub 上创建 Issue 报告 bug 或建议功能

## 获取帮助

- 查看 IntelliJ Platform SDK 文档：https://plugins.jetbrains.com/docs/intellij/
- 查看示例插件：https://github.com/JetBrains/intellij-sdk-code-samples
- 在 JetBrains 论坛提问：https://intellij-support.jetbrains.com/hc/en-us/community/topics
