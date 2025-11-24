# 数据库迁移策略

## 当前状态
- **数据库版本**: 2
- **迁移策略**: `fallbackToDestructiveMigration()`
- **位置**: [`AppDatabase.java`](file:///Users/zfdang/workspaces/dimension-marking-camera/app/src/main/java/com/zfdang/dimensioncam/data/AppDatabase.java)

---

## ⚠️ 重要说明

**升级应用将删除所有数据**

当数据库schema更新时，应用使用**破坏性迁移策略**，这意味着：

- ✗ 所有照片引用将被删除
- ✗ 所有标注数据将丢失  
- ✓ 用户需要重新添加照片
- ✓ 原始图片文件不会被删除（仅删除数据库引用）

---

## 📋 实施原因

选择破坏性迁移的主要考虑：

### 1. **简化开发**
- 无需编写复杂的迁移脚本
- 降低开发和测试成本
- 减少迁移过程中的潜在bug

### 2. **应用特性**
DimensionCam是一个**本地工具应用**：
- 没有云端备份需求
- 用户可以随时重新添加照片
- 标注数据可通过导出功能保存
- 主要用于临时测量和标注

### 3. **数据可恢复性**
用户可以通过以下方式保护数据：
- 使用「导出」功能保存带标注的图片
- 导出的图片包含所有标注，永久保存
- 原始照片文件保留在设备上

---

## 🔄 当前数据库结构

### Photos 表
```sql
CREATE TABLE photos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    originalPath TEXT NOT NULL,
    createdAt INTEGER NOT NULL
);
```

### Annotations 表
```sql
CREATE TABLE annotations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    photoId INTEGER NOT NULL,
    startX REAL,
    startY REAL,
    endX REAL,
    endY REAL,
    measuredValue REAL,
    color INTEGER,
    width REAL,
    order INTEGER,
    unit INTEGER,
    FOREIGN KEY(photoId) REFERENCES photos(id) ON DELETE CASCADE
);
```

### 版本历史
- **Version 1**: 初始版本
- **Version 2**: 当前版本（添加了unit字段等）

---

## 💡 未来改进方案

如果需要保留用户数据，可以考虑以下策略：

### 方案1: 实现Room Migration
```java
@Database(entities = {Photo.class, Annotation.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 添加新字段示例
            database.execSQL("ALTER TABLE annotations ADD COLUMN newField TEXT");
        }
    };
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "dimension_cam_database")
                            .addMigrations(MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

### 方案2: 导出/导入功能增强
- 实现数据库导出为JSON功能
- 添加数据导入功能
- 用户可手动备份和恢复所有数据

### 方案3: 云端同步
- 集成云存储服务（Firebase, AWS等）
- 自动备份照片引用和标注数据
- 多设备同步

---

## 📝 用户指南

### 升级前建议
1. **导出重要照片**: 使用应用内的「导出」功能保存所有带标注的图片
2. **记录照片**: 截图或记录重要的标注数据
3. **备份原图**: 确保原始照片已保存在设备相册中

### 升级后操作
1. 重新添加需要标注的照片
2. 参考之前导出的图片重新创建标注（如需要）

---

## 🔗 相关资源

- [Room Persistence Library - Migrations](https://developer.android.com/training/data-storage/room/migrating-db-versions)
- [Android Database Best Practices](https://developer.android.com/topic/libraries/architecture/room)

---

## 📧 反馈

如果您认为应该实现数据保留功能，请在GitHub提issue：
https://github.com/zfdang/dimension-marking-camera/issues

---

*文档版本: 1.0*  
*最后更新: 2025-11-25*
