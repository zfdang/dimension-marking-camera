package com.dimensioncam.utils

import android.graphics.*
import com.dimensioncam.data.model.ArrowStyle
import com.dimensioncam.data.model.Marking
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Utility class for rendering markings on canvas or bitmap
 */
object MarkingRenderer {
    
    /**
     * Draw a single marking on a canvas
     * 
     * @param canvas Canvas to draw on
     * @param marking Marking data
     * @param imageWidth Actual image width in pixels
     * @param imageHeight Actual image height in pixels
     * @param arrowStyle Global arrow style setting
     * @param density Screen density for dp/sp to px conversion
     */
    fun drawMarking(
        canvas: Canvas,
        marking: Marking,
        imageWidth: Int,
        imageHeight: Int,
        arrowStyle: ArrowStyle,
        density: Float
    ) {
        // Convert normalized coordinates to actual pixels
        val startX = marking.startX * imageWidth
        val startY = marking.startY * imageHeight
        val endX = marking.endX * imageWidth
        val endY = marking.endY * imageHeight
        
        // Convert dp to pixels
        val lineWidth = marking.lineWidthDp * density
        val textSize = marking.textSizeSp * density
        
        // Create paint for the line
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = marking.lineColor
            strokeWidth = lineWidth
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        
        // Draw the main line
        canvas.drawLine(startX, startY, endX, endY, linePaint)
        
        // Draw endpoints based on arrow style
        when (arrowStyle) {
            ArrowStyle.ARROW -> drawArrowEnds(canvas, startX, startY, endX, endY, linePaint)
            ArrowStyle.T_CAP -> drawTCapEnds(canvas, startX, startY, endX, endY, linePaint)
            ArrowStyle.CIRCLE -> drawCircleEnds(canvas, startX, startY, endX, endY, linePaint)
        }
        
        // Draw distance text in the center
        drawDistanceText(
            canvas,
            marking,
            startX, startY, endX, endY,
            textSize
        )
    }
    
    /**
     * Draw arrow endpoints (→  ——)
     */
    private fun drawArrowEnds(
        canvas: Canvas,
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        paint: Paint
    ) {
        val arrowLength = paint.strokeWidth * 8
        val arrowAngle = Math.toRadians(30.0)
        
        // Calculate angle of the line
        val angle = atan2((endY - startY).toDouble(), (endX - startX).toDouble())
        
        // Draw arrow at end point
        val arrowAngle1 = angle + Math.PI - arrowAngle
        val arrowAngle2 = angle + Math.PI + arrowAngle
        
        val arrowX1 = endX + arrowLength * cos(arrowAngle1).toFloat()
        val arrowY1 = endY + arrowLength * sin(arrowAngle1).toFloat()
        val arrowX2 = endX + arrowLength * cos(arrowAngle2).toFloat()
        val arrowY2 = endY + arrowLength * sin(arrowAngle2).toFloat()
        
        canvas.drawLine(endX, endY, arrowX1, arrowY1, paint)
        canvas.drawLine(endX, endY, arrowX2, arrowY2, paint)
    }
    
    /**
     * Draw T-cap endpoints (⊣ —— ⊢)
     */
    private fun drawTCapEnds(
        canvas: Canvas,
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        paint: Paint
    ) {
        val capLength = paint.strokeWidth * 5
        
        // Calculate angle perpendicular to the line
        val angle = atan2((endY - startY).toDouble(), (endX - startX).toDouble())
        val perpAngle = angle + Math.PI / 2
        
        // Draw T-cap at start
        val startCap1X = startX + capLength * cos(perpAngle).toFloat()
        val startCap1Y = startY + capLength * sin(perpAngle).toFloat()
        val startCap2X = startX - capLength * cos(perpAngle).toFloat()
        val startCap2Y = startY - capLength * sin(perpAngle).toFloat()
        canvas.drawLine(startCap1X, startCap1Y, startCap2X, startCap2Y, paint)
        
        // Draw T-cap at end
        val endCap1X = endX + capLength * cos(perpAngle).toFloat()
        val endCap1Y = endY + capLength * sin(perpAngle).toFloat()
        val endCap2X = endX - capLength * cos(perpAngle).toFloat()
        val endCap2Y = endY - capLength * sin(perpAngle).toFloat()
        canvas.drawLine(endCap1X, endCap1Y, endCap2X, endCap2Y, paint)
    }
    
    /**
     * Draw circle endpoints (● —— ●)
     */
    private fun drawCircleEnds(
        canvas: Canvas,
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        paint: Paint
    ) {
        val radius = paint.strokeWidth * 3
        val circlePaint = Paint(paint).apply {
            style = Paint.Style.FILL
        }
        
        // Draw circle at start
        canvas.drawCircle(startX, startY, radius, circlePaint)
        
        // Draw circle at end
        canvas.drawCircle(endX, endY, radius, circlePaint)
    }
    
    /**
     * Draw distance text in the center of the line with background
     */
    private fun drawDistanceText(
        canvas: Canvas,
        marking: Marking,
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        textSize: Float
    ) {
        // Format distance text
        val unitText = if (marking.distanceUnit.name == "MM") "mm" else "cm"
        val distanceText = String.format("%.1f %s", marking.distanceValue, unitText)
        
        // Create text paint
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = marking.textColor
            this.textSize = textSize
            textAlign = Paint.Align.CENTER
        }
        
        // Calculate text position (center of line)
        val centerX = (startX + endX) / 2
        val centerY = (startY + endY) / 2
        
        // Measure text for background
        val textBounds = Rect()
        textPaint.getTextBounds(distanceText, 0, distanceText.length, textBounds)
        
        // Draw background rectangle
        val padding = textSize * 0.3f
        val backgroundRect = RectF(
            centerX - textBounds.width() / 2 - padding,
            centerY + textBounds.top - padding,
            centerX + textBounds.width() / 2 + padding,
            centerY + textBounds.bottom + padding
        )
        
        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 0, 0, 0) // Semi-transparent black
            style = Paint.Style.FILL
        }
        
        canvas.drawRoundRect(backgroundRect, padding, padding, backgroundPaint)
        
        // Draw text
        canvas.drawText(
            distanceText,
            centerX,
            centerY - (textBounds.top + textBounds.bottom) / 2,
            textPaint
        )
    }
    
    /**
     * Check if a point is near a marking (for selection)
     * 
     * @param marking The marking to check
     * @param touchX Touch X coordinate (normalized 0-1)
     * @param touchY Touch Y coordinate (normalized 0-1)
     * @param threshold Distance threshold (normalized)
     * @return true if the touch is near the marking
     */
    fun isNearMarking(
        marking: Marking,
        touchX: Float,
        touchY: Float,
        threshold: Float = 0.03f
    ): Boolean {
        // Calculate distance from touch point to line segment
        val lineLength = distanceBetween(marking.startX, marking.startY, marking.endX, marking.endY)
        
        if (lineLength == 0f) {
            return distanceBetween(touchX, touchY, marking.startX, marking.startY) < threshold
        }
        
        // Project touch point onto the line
        val t = ((touchX - marking.startX) * (marking.endX - marking.startX) +
                (touchY - marking.startY) * (marking.endY - marking.startY)) / (lineLength * lineLength)
        
        val clampedT = t.coerceIn(0f, 1f)
        
        // Find closest point on line
        val closestX = marking.startX + clampedT * (marking.endX - marking.startX)
        val closestY = marking.startY + clampedT * (marking.endY - marking.startY)
        
        // Check distance
        return distanceBetween(touchX, touchY, closestX, closestY) < threshold
    }
    
    /**
     * Check if a point is near the start endpoint
     */
    fun isNearStartPoint(
        marking: Marking,
        touchX: Float,
        touchY: Float,
        threshold: Float = 0.03f
    ): Boolean {
        return distanceBetween(touchX, touchY, marking.startX, marking.startY) < threshold
    }
    
    /**
     * Check if a point is near the end endpoint
     */
    fun isNearEndPoint(
        marking: Marking,
        touchX: Float,
        touchY: Float,
        threshold: Float = 0.03f
    ): Boolean {
        return distanceBetween(touchX, touchY, marking.endX, marking.endY) < threshold
    }
    
    /**
     * Calculate distance between two points
     */
    private fun distanceBetween(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}
