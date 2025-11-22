# DimensionCam (尺标相机)

A powerful Android application for annotating real-world distances on photos.

## Features

- 📷 **Photo Management**: Import photos from gallery or capture with camera
- 📐 **Dimension Marking**: Add precision distance measurements to photos
- 🎨 **Customizable Styles**: Individual colors and widths for each marking
- 🔄 **Layer Control**: Drag-to-reorder markings for proper layering
- 🌐 **Bilingual**: Supports English and Chinese (auto-detect + manual)
- 💾 **Export**: Save annotated photos as JPEG to gallery
- ↩️ **Undo**: Undo the last marking operation

## Technical Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Repository pattern
- **Database**: Room for persistence
- **Settings**: DataStore Preferences
- **Image Loading**: Coil
- **Camera**: CameraX

## Requirements

- Android 7.0 (API 24) or higher
- Camera and storage permissions

## Building

```bash
./gradlew assembleDebug
```

## Key Components

### Data Layer
- **Room Database**: Photos and markings with cascade delete
- **DataStore**: App settings (arrow style, units, language)
- **Repositories**: Clean data access layer

### UI Layer
- **Photos Tab**: Grid view with camera/gallery integration
- **Marking Tab**: Interactive canvas with pinch-to-zoom
- **Settings Tab**: Global preferences management

### Core Utils
- **MarkingRenderer**: Draws markings with three arrow styles
- **ImageExporter**: Exports photos with rendered markings
- **LocaleManager**: Handles bilingual support

## Features in Detail

### Arrow Styles
1. **Single Arrow** (→ ——): Directional arrow at endpoint
2. **T-Cap** (⊣ —— ⊢): T-shaped caps at both ends
3. **Circle** (● —— ●): Circular dots at both ends

### Distance Units
- Millimeters (mm)
- Centimeters (cm)
- Precision: 1 decimal place

### Marking Workflow
1. Tap "Add Marking" button
2. Drag from start to end point
3. Enter measured distance
4. Marking saved with current style settings

### Editing Markings
- Tap marking to select
- Drag red control points to adjust position
- Open edit panel to change distance, colors, sizes
- Delete marking if needed

## License

Apache 2.0 (see LICENSE file)

## Author

DimensionCam Team
