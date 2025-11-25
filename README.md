# DimCam 尺标相机

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)

🌐 **[Project Website](https://dimcam.zfdang.com)** | 📥 **[Download APK](https://dimcam.zfdang.com/download.html)** | 🐙 **[GitHub](https://github.com/zfdang/dimension-marking-camera)**

---

## English

### Description
**DimCam** (Dimension Marking Camera | 中文名：尺标相机) is an open-source Android application that lets users capture or select photos, add precise measurement annotations between points, and export beautifully annotated images. Built with a clean MVVM architecture using Java, Room, Glide, and PhotoView.

### ✨ Features
- **📷 Photo Management** – Capture photos or select from gallery, view thumbnails in grid layout, non-destructive deletion
- **📏 Precise Annotation** – Place start/end points, input distance values with multiple units (mm, cm, dm, m)
- **🎨 Customizable Styles** – Choose from 3 endpoint styles:
  - `|<----->|` (T-Arrow-T)
  - `|-----|` (T-T)
  - `<----->` (Arrow-Arrow)
- **🌈 Rich Colors** – 6 color options: Red, Green, Blue, White, Black, Purple
- **✏️ Flexible Editing** – Drag control points, adjust line width (1-10 pixels), undo/redo operations
- **🌍 Multi-language** – Full support for English and Chinese (中文) with auto-detection
- **💾 Export** – Save high-quality annotated images to device storage
- **🏗️ Clean Architecture** – MVVM pattern with LiveData, Repository, and Room database

### 🛠️ Technical Highlights
- **Platform**: Android 7.0+ (API 24)
- **Architecture**: MVVM + LiveData + Repository pattern
- **Database**: Room persistence library
- **Image Loading**: Glide
- **Photo Viewing**: PhotoView with zoom/pan support
- **Language**: Java + XML layouts

### 📦 Installation
```bash
# Clone the repository
git clone https://github.com/zfdang/dimension-marking-camera.git
cd dimension-marking-camera
```
Open the project in Android Studio (minimum API 24) and let Gradle sync.

### 🔨 Build & Release
```bash
# Build a signed release APK
./gradlew assembleRelease
```
The generated APK can be found at `app/build/outputs/apk/release/`.

### 📱 Usage
1. Launch the app and navigate to the **Photos** tab
2. Add a photo using camera or gallery
3. Switch to the **Annotation** tab
4. Tap to place start and end points on the image
5. Enter the distance measurement and customize the style
6. Use the **Export** button in the toolbar to save your annotated image

### 🤝 Contributing
Contributions are welcome! Please fork the repository, create a feature branch, and submit a pull request. Run `./gradlew lint` before submitting to ensure code quality.

### 📄 License
This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.

---

## 中文

### 项目简介
**DimCam 尺标相机**（Dimension Marking Camera）是一款开源 Android 应用，支持拍摄或选择照片后进行精确的尺寸标注（两点之间的距离测量），并可导出带标注的高质量图片。项目采用 MVVM 架构，使用 Java、Room、Glide、PhotoView 等现代 Android 库构建。

### ✨ 功能特性
- **📷 照片管理** – 支持相机拍照或相册选择，网格预览缩略图，非破坏性删除
- **📏 精确标注** – 可放置起点/终点，输入距离值，支持多种单位（毫米、厘米、分米、米）
- **🎨 自定义样式** – 3种端点样式可选：
  - `|<----->|` (T型箭头T型)
  - `|-----|` (T型T型)
  - `<----->` (箭头箭头)
- **🌈 丰富颜色** – 6种颜色可选：红色、绿色、蓝色、白色、黑色、紫色
- **✏️ 灵活编辑** – 可拖拽控制点，调整线条粗细（1-10像素），支持撤销/重做操作
- **🌍 多语言支持** – 完整支持中文和英文界面，自动语言检测
- **💾 图片导出** – 将高质量的标注图片保存到设备存储
- **🏗️ 清晰架构** – 采用 MVVM 模式、LiveData、Repository 与 Room 数据库

### 🛠️ 技术亮点
- **平台**: Android 7.0+ (API 24)
- **架构**: MVVM + LiveData + Repository 模式
- **数据库**: Room 持久化库
- **图片加载**: Glide
- **图片查看**: PhotoView 支持缩放/平移
- **开发语言**: Java + XML 布局

### 📦 安装步骤
```bash
# 克隆仓库
git clone https://github.com/zfdang/dimension-marking-camera.git
cd dimension-marking-camera
```
使用 Android Studio 打开项目（最低 API 24），Gradle 会自动同步。

### 🔨 构建与发布
```bash
# 生成签名的 Release APK
./gradlew assembleRelease
```
生成的 APK 位于 `app/build/outputs/apk/release/`。

### 📱 使用指南
1. 启动应用，进入 **照片** 页面
2. 使用相机拍照或从相册选择照片
3. 切换到 **标注** 页面
4. 点击图片放置起点和终点
5. 输入距离测量值并自定义样式
6. 使用工具栏的 **导出** 按钮保存标注后的图片

### 🤝 贡献代码
欢迎 Fork 本仓库并提交 Pull Request。请在提交前使用 `./gradlew lint` 检查代码风格。

### 📄 许可证
本项目采用 **MIT 许可证**，详情请参阅 [LICENSE](LICENSE) 文件。

## 🔗 Links

- 🌐 **Project Website**: [https://dimcam.zfdang.com](https://dimcam.zfdang.com)
- 📥 **Download**: [https://dimcam.zfdang.com/download.html](https://dimcam.zfdang.com/download.html)
- 🐙 **GitHub**: [https://github.com/zfdang/dimension-marking-camera](https://github.com/zfdang/dimension-marking-camera)
- 👤 **Author**: [zfdang](https://github.com/zfdang)
