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
    public static final int UNIT_MM = 0;
    public static final int UNIT_CM = 1;
    public static final int UNIT_DM = 2;
    public static final int UNIT_M = 3;

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
    public int unit; // Unit of measurement

    // Constructor
    public Annotation(long photoId, float startX, float startY, float endX, float endY, float measuredValue, int color, float width, int order, int unit) {
        this.photoId = photoId;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.measuredValue = measuredValue;
        this.color = color;
        this.width = width;
        this.order = order;
        this.unit = unit;
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
        this.unit = other.unit;
    }

    public static String getUnitString(int unit) {
        switch (unit) {
            case UNIT_MM: return "mm";
            case UNIT_CM: return "cm";
            case UNIT_DM: return "dm";
            case UNIT_M: return "m";
            default: return "cm";
        }
    }

    public static int getUnitStringResource(int unit) {
        switch (unit) {
            case UNIT_MM: return com.zfdang.dimensioncam.R.string.unit_mm;
            case UNIT_CM: return com.zfdang.dimensioncam.R.string.unit_cm;
            case UNIT_DM: return com.zfdang.dimensioncam.R.string.unit_dm;
            case UNIT_M: return com.zfdang.dimensioncam.R.string.unit_m;
            default: return com.zfdang.dimensioncam.R.string.unit_cm;
        }
    }
}
