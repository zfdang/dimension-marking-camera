package com.zfdang.dimensioncam.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "annotations",
        foreignKeys = @ForeignKey(entity = Photo.class,
                parentColumns = "id",
                childColumns = "photoId",
                onDelete = CASCADE),
        indices = {@Index("photoId")})
public class Annotation {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long photoId;
    public float startX;
    public float startY;
    public float endX;
    public float endY;
    public float measuredValue; // The distance value entered by user
    public int color; // ARGB color
    public float width; // Stroke width
    public int order; // For z-ordering

    // Constructor
    public Annotation(long photoId, float startX, float startY, float endX, float endY, float measuredValue, int color, float width, int order) {
        this.photoId = photoId;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.measuredValue = measuredValue;
        this.color = color;
        this.width = width;
        this.order = order;
    }
    
    // Copy constructor for Undo functionality
    public Annotation(Annotation other) {
        this.id = other.id;
        this.photoId = other.photoId;
        this.startX = other.startX;
        this.startY = other.startY;
        this.endX = other.endX;
        this.endY = other.endY;
        this.measuredValue = other.measuredValue;
        this.color = other.color;
        this.width = other.width;
        this.order = other.order;
    }
}
