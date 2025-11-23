# DimensionCam

## English

### Description
DimensionCam (中文名：尺寸标注相机) is an open‑source Android application that lets users capture or select photos, add measurement annotations (distance between points), and export the annotated images. It follows a clean MVVM architecture using Java, Room, Glide, and PhotoView.

### Features
- **Photo Management** – Add photos from camera or gallery, view a grid of thumbnails, and delete non‑destructively.
- **Annotation Tools** – Place start/end points, input distance values, choose arrow styles, drag control points, undo/redo, and reorder annotations.
- **Export** – Save annotated images to device storage.
- **Settings** – Switch UI language between English and Chinese, configure arrow style, units, and view version info.
- **Architecture** – MVVM with LiveData, Repository pattern, and Room database.

### Installation
```bash
# Clone the repository
git clone https://github.com/zfdang/dimension-marking-camera.git
cd dimension-marking-camera
```
Open the project in Android Studio (minimum API 21) and let Gradle sync.

### Build & Release
```bash
# Build a signed release APK (keystore is included for demo purposes)
./gradlew assembleRelease
```
The generated APK can be found at `app/build/outputs/apk/release/app-release.apk`.

### Usage
1. Launch the app.
2. Use the **Photos** tab to add a picture (camera or gallery).
3. Switch to the **Annotation** tab, tap to place start/end points, enter the distance, and adjust styling.
4. Export the result via the **Export** button in the toolbar.

### Contributing
Contributions are welcome! Please fork the repository, create a feature branch, and submit a pull request. Follow the existing code style and run `./gradlew lint` before submitting.

### License
This project is licensed under the **MIT License** – see the `LICENSE` file for details.

---

## 中文

### 项目简介
DimensionCam（中文名：尺寸标注相机）是一款开源 Android 应用，支持拍摄或选择照片后进行尺寸标注（两点之间的距离），并可导出带标注的图片。项目采用 Java + XML，遵循 MVVM 架构，使用 Room、Glide、PhotoView 等库。

### 功能特性
- **照片管理**：支持相机拍照或相册选择，网格预览缩略图，非破坏性删除。
- **标注工具**：可放置起点/终点，输入距离值，选择箭头样式，拖拽控制点，支持撤销/重做和标注排序。
- **导出**：将标注后的图片保存到本地存储。
- **设置**：支持中英文切换、箭头样式、计量单位以及查看版本信息。
- **架构**：采用 MVVM、LiveData、Repository 与 Room 数据库，实现代码解耦。

### 安装步骤
```bash
# 克隆仓库
git clone https://github.com/zfdang/dimension-marking-camera.git
cd dimension-marking-camera
```
使用 Android Studio 打开项目（最低 API 21），Gradle 会自动同步。

### 构建与发布
```bash
# 生成签名的 Release APK（示例 keystore 已提交）
./gradlew assembleRelease
```
生成的 APK 位于 `app/build/outputs/apk/release/app-release.apk`。

### 使用指南
1. 启动应用。
2. 在 **照片** 页面添加图片（相机或相册）。
3. 切换到 **标注** 页面，点击放置起点/终点，输入距离并可调节样式。
4. 通过工具栏的 **导出** 按钮保存标注后的图片。

### 贡献代码
欢迎 Fork 本仓库并提交 Pull Request。请在提交前使用 `./gradlew lint` 检查代码风格，保持与现有代码一致。

### 许可证
本项目采用 **MIT 许可证**，详情请参阅根目录下的 `LICENSE` 文件。
