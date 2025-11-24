package com.zfdang.dimensioncam.ui.annotation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.zfdang.dimensioncam.data.Annotation;
import com.zfdang.dimensioncam.ui.settings.SettingsManager;

import java.util.List;

public class AnnotationDrawer {

    private Context context;
    private Paint paint;
    private Paint textPaint;
    private Paint controlPointPaint;

    public AnnotationDrawer(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setShadowLayer(2, 0, 0, Color.BLACK);

        controlPointPaint = new Paint();
        controlPointPaint.setAntiAlias(true);
        controlPointPaint.setColor(Color.YELLOW);
        controlPointPaint.setStyle(Paint.Style.FILL);
        controlPointPaint.setAlpha(150);
    }

    public void draw(Canvas canvas, List<Annotation> annotations, RectF rect, int arrowStyle, boolean drawControlPoints, float scaleFactor, boolean showId) {
        // Scale text size and stroke width based on image size (for export)
        // scaleFactor = 1.0 for screen view, > 1.0 for high-res export
        
        float originalTextSize = 40;
        textPaint.setTextSize(originalTextSize * scaleFactor);
        textPaint.setShadowLayer(2 * scaleFactor, 0, 0, Color.BLACK);
        
        float controlRadius = 20f * scaleFactor;

        for (Annotation annotation : annotations) {
            float startX = mapX(annotation.startX, rect);
            float startY = mapY(annotation.startY, rect);
            float endX = mapX(annotation.endX, rect);
            float endY = mapY(annotation.endY, rect);

            paint.setColor(annotation.color);
            paint.setStrokeWidth(annotation.width * scaleFactor);

            // Draw line
            canvas.drawLine(startX, startY, endX, endY, paint);

            // Draw endpoints
            drawEndpoint(canvas, startX, startY, endX, endY, annotation.width * scaleFactor, arrowStyle);
            drawEndpoint(canvas, endX, endY, startX, startY, annotation.width * scaleFactor, arrowStyle);

            // Draw text
            // Show value and ID (1-based index) if requested
            String unitString = context.getString(Annotation.getUnitStringResource(annotation.unit));
            String text;
            if (showId) {
                int index = annotations.indexOf(annotation);
                text = String.format("%.0f %s (#%d)", annotation.measuredValue, unitString, index + 1);
            } else {
                text = String.format("%.0f %s", annotation.measuredValue, unitString);
            }
            float midX = (startX + endX) / 2;
            float midY = (startY + endY) / 2;
            
            // Calculate angle
            float deltaX = endX - startX;
            float deltaY = endY - startY;
            float angleDegrees = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
            
            // Keep text readable (not upside down)
            if (angleDegrees > 90) {
                angleDegrees -= 180;
            } else if (angleDegrees < -90) {
                angleDegrees += 180;
            }
            
            canvas.save();
            canvas.rotate(angleDegrees, midX, midY);
            // Draw text centered at midX, midY, slightly above the line
            // Align center is default for drawText? No, default is LEFT.
            // Need to set align to CENTER in constructor or here.
            // Let's check constructor. It doesn't set align.
            // So we should set it here or in constructor.
            // Let's assume we modify constructor or set it here.
            // Ideally set it once. But let's set it here to be safe/clear or check constructor again.
            // Constructor: textPaint.setStyle(Paint.Style.FILL); textPaint.setTextSize(40); ...
            // It does NOT set Align. Let's add it to constructor or set it here.
            // Setting here is safer for this specific change.
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, midX, midY - (10 * scaleFactor), textPaint);
            canvas.restore();

            // Draw control points
            if (drawControlPoints) {
                canvas.drawCircle(startX, startY, controlRadius, controlPointPaint);
                canvas.drawCircle(endX, endY, controlRadius, controlPointPaint);
            }
        }
        
        // Reset text size
        textPaint.setTextSize(originalTextSize);
    }

    private void drawEndpoint(Canvas canvas, float tipX, float tipY, float tailX, float tailY, float width, int style) {
        float angle = (float) Math.atan2(tailY - tipY, tailX - tipX);
        float size = width * 5;
        if (size < 30) size = 30; // Minimum size

        if (style == SettingsManager.STYLE_ARROW) {
            float arrowAngle = (float) Math.toRadians(30);
            float x1 = (float) (tipX + Math.cos(angle + arrowAngle) * size);
            float y1 = (float) (tipY + Math.sin(angle + arrowAngle) * size);
            float x2 = (float) (tipX + Math.cos(angle - arrowAngle) * size);
            float y2 = (float) (tipY + Math.sin(angle - arrowAngle) * size);
            canvas.drawLine(tipX, tipY, x1, y1, paint);
            canvas.drawLine(tipX, tipY, x2, y2, paint);
        } else if (style == SettingsManager.STYLE_T_SHAPE) {
            float tAngle = (float) Math.toRadians(90);
            float x1 = (float) (tipX + Math.cos(angle + tAngle) * (size/2));
            float y1 = (float) (tipY + Math.sin(angle + tAngle) * (size/2));
            float x2 = (float) (tipX + Math.cos(angle - tAngle) * (size/2));
            float y2 = (float) (tipY + Math.sin(angle - tAngle) * (size/2));
            canvas.drawLine(x1, y1, x2, y2, paint);
        } else if (style == SettingsManager.STYLE_DOT) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(tipX, tipY, width * 2, paint);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    private float mapX(float x, RectF rect) {
        return rect.left + x * rect.width();
    }

    private float mapY(float y, RectF rect) {
        return rect.top + y * rect.height();
    }
}
