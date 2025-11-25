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
        textPaint.setTextSize(com.zfdang.dimensioncam.utils.Constants.BASE_TEXT_SIZE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(com.zfdang.dimensioncam.utils.Constants.SHADOW_RADIUS, 0, 0, Color.BLACK);

        controlPointPaint = new Paint();
        controlPointPaint.setAntiAlias(true);
        controlPointPaint.setColor(Color.YELLOW);
        controlPointPaint.setStyle(Paint.Style.FILL);
        controlPointPaint.setAlpha(150);
    }

    public void draw(Canvas canvas, List<Annotation> annotations, RectF rect, int arrowStyle, boolean drawControlPoints,
            float scaleFactor, boolean showId) {
        // Scale text size and stroke width based on image size (for export)
        // scaleFactor = 1.0 for screen view, > 1.0 for high-res export

        float originalTextSize = com.zfdang.dimensioncam.utils.Constants.BASE_TEXT_SIZE;
        textPaint.setTextSize(originalTextSize * scaleFactor);
        textPaint.setShadowLayer(com.zfdang.dimensioncam.utils.Constants.SHADOW_RADIUS * scaleFactor, 0, 0,
                Color.BLACK);

        float controlRadius = com.zfdang.dimensioncam.utils.Constants.CONTROL_POINT_RADIUS * scaleFactor;

        for (Annotation annotation : annotations) {
            float startX = mapX(annotation.startX, rect);
            float startY = mapY(annotation.startY, rect);
            float endX = mapX(annotation.endX, rect);
            float endY = mapY(annotation.endY, rect);

            paint.setColor(annotation.color);
            paint.setStrokeWidth(annotation.width * scaleFactor);

            // Draw line
            canvas.drawLine(startX, startY, endX, endY, paint);

            // Draw endpoints based on style
            if (arrowStyle == SettingsManager.STYLE_T_ARROW_T) {
                // |<----->| T型+箭头+T型：每个端点同时绘制T型和箭头
                drawEndpointShape(canvas, startX, startY, endX, endY, annotation.width * scaleFactor, "T");
                drawEndpointShape(canvas, startX, startY, endX, endY, annotation.width * scaleFactor, "ARROW");
                drawEndpointShape(canvas, endX, endY, startX, startY, annotation.width * scaleFactor, "T");
                drawEndpointShape(canvas, endX, endY, startX, startY, annotation.width * scaleFactor, "ARROW");
            } else if (arrowStyle == SettingsManager.STYLE_T_T) {
                // |-----| T型+T型：两端都是T型
                drawEndpointShape(canvas, startX, startY, endX, endY, annotation.width * scaleFactor, "T");
                drawEndpointShape(canvas, endX, endY, startX, startY, annotation.width * scaleFactor, "T");
            } else if (arrowStyle == SettingsManager.STYLE_ARROW_ARROW) {
                // <-----> 箭头+箭头：两端都是箭头
                drawEndpointShape(canvas, startX, startY, endX, endY, annotation.width * scaleFactor, "ARROW");
                drawEndpointShape(canvas, endX, endY, startX, startY, annotation.width * scaleFactor, "ARROW");
            }

            // Draw text
            // Set text color to match annotation color
            textPaint.setColor(annotation.color);

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
            // Draw text centered and slightly above the line
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

    // 绘制端点形状
    private void drawEndpointShape(Canvas canvas, float tipX, float tipY, float tailX, float tailY, float width,
            String shapeType) {
        float angle = (float) Math.atan2(tailY - tipY, tailX - tipX);
        float size = width * 5;
        if (size < com.zfdang.dimensioncam.utils.Constants.MIN_ARROW_SIZE)
            size = com.zfdang.dimensioncam.utils.Constants.MIN_ARROW_SIZE;

        if (shapeType.equals("T")) {
            // 绘制T型端点
            float tAngle = (float) Math.toRadians(90);
            float x1 = (float) (tipX + Math.cos(angle + tAngle) * (size / 2));
            float y1 = (float) (tipY + Math.sin(angle + tAngle) * (size / 2));
            float x2 = (float) (tipX + Math.cos(angle - tAngle) * (size / 2));
            float y2 = (float) (tipY + Math.sin(angle - tAngle) * (size / 2));
            canvas.drawLine(x1, y1, x2, y2, paint);
        } else if (shapeType.equals("ARROW")) {
            // 绘制箭头端点
            float arrowAngle = (float) Math.toRadians(30);
            float x1 = (float) (tipX + Math.cos(angle + arrowAngle) * size);
            float y1 = (float) (tipY + Math.sin(angle + arrowAngle) * size);
            float x2 = (float) (tipX + Math.cos(angle - arrowAngle) * size);
            float y2 = (float) (tipY + Math.sin(angle - arrowAngle) * size);
            canvas.drawLine(tipX, tipY, x1, y1, paint);
            canvas.drawLine(tipX, tipY, x2, y2, paint);
        }
    }

    private float mapX(float x, RectF rect) {
        return rect.left + x * rect.width();
    }

    private float mapY(float y, RectF rect) {
        return rect.top + y * rect.height();
    }
}
