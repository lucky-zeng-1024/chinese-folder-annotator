# Chinese Name Annotator Plugin

一个 IntelliJ IDEA 插件，为项目树中的文件夹添加中文标注。

## 功能特性

- ✨ **为文件夹添加中文标注** - 在项目树中为任何文件夹添加中文名称
- 🎯 **灵活的显示方式** - 可以同时显示原始名称和中文标注，或仅显示中文名称
- 💾 **持久化配置** - 配置自动保存到项目设置中
- ⚙️ **项目级配置** - 在 Settings → Tools → Chinese Name Annotator 中管理所有标注
- 🖱️ **右键菜单快速操作** - 在项目树中右键点击文件夹快速添加标注

## 使用方法

### 方式一：右键菜单

1. 在项目树中右键点击任意文件夹
2. 选择 "Add Chinese Annotation"
3. 在对话框中输入中文名称
4. 可选：勾选 "Hide original folder name" 来隐藏原始文件夹名称
5. 点击 OK

### 方式二：设置面板

1. 打开 Settings/Preferences
2. 导航到 Tools → Chinese Name Annotator
3. 查看所有已配置的标注
4. 可以编辑或删除标注

## 示例

假设你有以下目录结构：

```
test-project
├── src
├── resource
│   ├── script
│   │   ├── gdgzs
│   │   └── gdszs
```

使用此插件后，可以配置为：

```
test-project
├── src
├── resource(资源)
│   ├── script(脚本)
│   │   ├── gdgzs(广东广州市)
│   │   └── gdszs(广东深圳市)
```

或者隐藏原始名称：

```
test-project
├── src
├── 资源
│   ├── 脚本
│   │   ├── 广东广州市
│   │   └── 广东深圳市
```

## 配置文件

配置存储在项目的 `.idea/chineseName.xml` 文件中。

## 系统要求

- IntelliJ IDEA 2023.1 或更高版本
- Java 11 或更高版本

## 开发

### 构建

```bash
./gradlew build
```

### 运行测试

```bash
./gradlew test
```

### 打包插件

```bash
./gradlew buildPlugin
```

生成的插件 JAR 文件位于 `build/distributions/` 目录中。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

