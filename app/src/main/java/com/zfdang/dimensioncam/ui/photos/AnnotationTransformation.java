package com.zfdang.dimensioncam.ui.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.zfdang.dimensioncam.data.Annotation;
import com.zfdang.dimensioncam.ui.annotation.AnnotationDrawer;
import com.zfdang.dimensioncam.ui.settings.SettingsManager;

import java.security.MessageDigest;
import java.util.List;

public class AnnotationTransformation extends BitmapTransformation {

    private static final String ID = "com.zfdang.dimensioncam.ui.photos.AnnotationTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final List<Annotation> annotations;
    private final int arrowStyle;
    private final AnnotationDrawer drawer;

    public AnnotationTransformation(Context context, List<Annotation> annotations) {
        this.annotations = annotations;
        this.arrowStyle = new SettingsManager(context).getArrowStyle();
        this.drawer = new AnnotationDrawer();
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (annotations == null || annotations.isEmpty()) {
            return toTransform;
        }

        // We need a mutable bitmap to draw on. 
        // Glide's toTransform might be immutable or recycled if we return a different one.
        // Best practice: create a new bitmap or copy.
        // Since we want to draw ON TOP of the image, we can try to copy.
        
        Bitmap result = pool.get(toTransform.getWidth(), toTransform.getHeight(), Bitmap.Config.ARGB_8888);
        result.setHasAlpha(true);
        
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(toTransform, 0, 0, null);
        
        RectF rect = new RectF(0, 0, result.getWidth(), result.getHeight());
        
        // Scale factor: Adjust for thumbnail size vs full screen
        // Assuming thumbnail is small, we might want thicker lines relative to the image size?
        // Or just use 1.0f and let it be small. 
        // Let's try 1.0f first.
        
        drawer.draw(canvas, annotations, rect, arrowStyle, false, 1.0f);
        
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AnnotationTransformation) {
            AnnotationTransformation other = (AnnotationTransformation) o;
            return annotations.equals(other.annotations) && arrowStyle == other.arrowStyle;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + annotations.hashCode() + arrowStyle;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        // We should also include annotations hash in cache key so it updates when annotations change
        messageDigest.update(String.valueOf(annotations.hashCode()).getBytes(CHARSET));
    }
}
