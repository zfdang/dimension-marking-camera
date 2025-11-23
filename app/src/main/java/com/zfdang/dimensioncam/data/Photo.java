package com.zfdang.dimensioncam.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photos")
public class Photo {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String originalPath; // URI string or file path
    public long createdAt;

    public Photo(String originalPath, long createdAt) {
        this.originalPath = originalPath;
        this.createdAt = createdAt;
    }
}
