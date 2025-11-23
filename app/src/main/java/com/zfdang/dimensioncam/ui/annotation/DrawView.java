package com.zfdang.dimensioncam.ui.annotation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;
import com.zfdang.dimensioncam.data.Annotation;
import com.zfdang.dimensioncam.ui.settings.SettingsManager;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {

    private PhotoView photoView;
    private List<Annotation> annotations = new ArrayList<>();
    private SettingsManager settingsManager;
    private AnnotationDrawer drawer;
    
    // Interaction state
    private Annotation activeAnnotation = null;
    private int activeControlPoint = -1; // 0: start, 1: end, -1: none
    private OnAnnotationChangeListener listener;

    public interface OnAnnotationChangeListener {
        void onAnnotationModified(Annotation annotation);
        void onAnnotationSelected(Annotation annotation);
    }

    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        settingsManager = new SettingsManager(context);
        drawer = new AnnotationDrawer();
    }

    public void setPhotoView(PhotoView photoView) {
        this.photoView = photoView;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
        invalidate();
    }

    public void setListener(OnAnnotationChangeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (photoView == null || annotations == null) return;

        RectF displayRect = photoView.getDisplayRect();
        if (displayRect == null) return;
        
        int arrowStyle = settingsManager.getArrowStyle();
        drawer.draw(canvas, annotations, displayRect, arrowStyle, true, 1.0f);
    }

    // Interaction logic remains the same, need to duplicate mapping logic or expose it from Drawer?
    // Drawer mapping is private. Let's keep interaction logic here as it depends on View coordinates.
    
    private float mapX(float x, RectF rect) {
        return rect.left + x * rect.width();
    }

    private float mapY(float y, RectF rect) {
        return rect.top + y * rect.height();
    }

    private float unmapX(float x, RectF rect) {
        return (x - rect.left) / rect.width();
    }

    private float unmapY(float y, RectF rect) {
        return (y - rect.top) / rect.height();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (photoView == null) return false;
        RectF rect = photoView.getDisplayRect();
        if (rect == null) return false;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activeAnnotation = null;
                activeControlPoint = -1;
                float threshold = 50f; 

                for (int i = annotations.size() - 1; i >= 0; i--) {
                    Annotation a = annotations.get(i);
                    float sx = mapX(a.startX, rect);
                    float sy = mapY(a.startY, rect);
                    float ex = mapX(a.endX, rect);
                    float ey = mapY(a.endY, rect);

                    if (dist(x, y, sx, sy) < threshold) {
                        activeAnnotation = a;
                        activeControlPoint = 0;
                        if (listener != null) listener.onAnnotationSelected(a);
                        return true;
                    } else if (dist(x, y, ex, ey) < threshold) {
                        activeAnnotation = a;
                        activeControlPoint = 1;
                        if (listener != null) listener.onAnnotationSelected(a);
                        return true;
                    }
                }
                return false; 

            case MotionEvent.ACTION_MOVE:
                if (activeAnnotation != null && activeControlPoint != -1) {
                    float newX = unmapX(x, rect);
                    float newY = unmapY(y, rect);
                    
                    newX = Math.max(0, Math.min(1, newX));
                    newY = Math.max(0, Math.min(1, newY));

                    if (activeControlPoint == 0) {
                        activeAnnotation.startX = newX;
                        activeAnnotation.startY = newY;
                    } else {
                        activeAnnotation.endX = newX;
                        activeAnnotation.endY = newY;
                    }
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (activeAnnotation != null) {
                    if (listener != null) {
                        listener.onAnnotationModified(activeAnnotation);
                    }
                    activeAnnotation = null;
                    activeControlPoint = -1;
                    return true;
                }
                break;
        }
        return false;
    }

    private float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.hypot(x2 - x1, y2 - y1);
    }
}
