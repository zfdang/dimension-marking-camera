package com.zfdang.dimensioncam.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PhotoWithAnnotations {
    @Embedded
    public Photo photo;

    @Relation(
            parentColumn = "id",
            entityColumn = "photoId"
    )
    public List<Annotation> annotations;
}
