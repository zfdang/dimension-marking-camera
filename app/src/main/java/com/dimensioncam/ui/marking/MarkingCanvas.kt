package com.dimensioncam.ui.marking

import android.graphics.Paint
import android.graphics.PointF
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import coil.compose.AsyncImage
import com.dimensioncam.data.model.ArrowStyle
import com.dimensioncam.data.model.Marking
import com.dimensioncam.utils.MarkingRenderer

@Composable
fun MarkingCanvas(
    photoUri: Uri,
    markings: List<Marking>,
    arrowStyle: ArrowStyle,
    selectedMarkingId: Long?,
    isCreatingMarking: Boolean,
    newMarkingStart: Pair<Float, Float>?,
    newMarkingEnd: Pair<Float, Float>?,
    onMarkingSelected: (Long) -> Unit,
    onNewMarkingStartSet: (Float, Float) -> Unit,
    onNewMarkingEndUpdate: (Float, Float) -> Unit,
    onNewMarkingFinished: () -> Unit,
    onMarkingPointDragged: (Long, Boolean, Float, Float) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var imageSize by remember { mutableStateOf(Offset.Zero) }
    
    // Dragging state for control points
    var draggingMarkingId by remember { mutableLongStateOf(-1L) }
    var draggingIsStart by remember { mutableStateOf(false) }
    
    val density = LocalDensity.current.density
    
    // Transform state for pinch-to-zoom
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f)
        
        // Limit panning based on scale
        val maxX = (imageSize.x * (scale - 1)) / 2
        val maxY = (imageSize.y * (scale - 1)) / 2
        
        offset = Offset(
            x = (offset.x + panChange.x).coerceIn(-maxX, maxX),
            y = (offset.y + panChange.y).coerceIn(-maxY, maxY)
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = transformState)
    ) {
        // Photo
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isCreatingMarking, draggingMarkingId) {
                    if (isCreatingMarking) {
                        // Creating new marking
                        detectDragGestures(
                            onDragStart = { offset ->
                                val normalized = normalizeCoordinates(offset, size.width.toFloat(), size.height.toFloat())
                                onNewMarkingStartSet(normalized.x, normalized.y)
                            },
                            onDrag = { change, _ ->
                                val normalized = normalizeCoordinates(change.position, size.width.toFloat(), size.height.toFloat())
                                onNewMarkingEndUpdate(normalized.x, normalized.y)
                            },
                            onDragEnd = {
                                onNewMarkingFinished()
                            }
                        )
                    } else if (draggingMarkingId >= 0) {
                        // Dragging control point
                        detectDragGestures(
                            onDrag = { change, _ ->
                                val normalized = normalizeCoordinates(change.position, size.width.toFloat(), size.height.toFloat())
                                onMarkingPointDragged(draggingMarkingId, draggingIsStart, normalized.x, normalized.y)
                            },
                            onDragEnd = {
                                draggingMarkingId = -1L
                            }
                        )
                    } else {
                        // Selecting marking or control points
                        detectTapGestures { offset ->
                            val normalized = normalizeCoordinates(offset, size.width.toFloat(), size.height.toFloat())
                            
                            // Check if tapped on control points first
                            var foundControlPoint = false
                            for (marking in markings.reversed()) {
                                if (marking.id == selectedMarkingId) {
                                    if (MarkingRenderer.isNearStartPoint(marking, normalized.x, normalized.y)) {
                                        draggingMarkingId = marking.id
                                        draggingIsStart = true
                                        foundControlPoint = true
                                        break
                                    } else if (MarkingRenderer.isNearEndPoint(marking, normalized.x, normalized.y)) {
                                        draggingMarkingId = marking.id
                                        draggingIsStart = false
                                        foundControlPoint = true
                                        break
                                    }
                                }
                            }
                            
                            // If no control point, check if tapped on marking
                            if (!foundControlPoint) {
                                var selectedId: Long? = null
                                for (marking in markings.reversed()) {
                                    if (MarkingRenderer.isNearMarking(marking, normalized.x, normalized.y)) {
                                        selectedId = marking.id
                                        break
                                    }
                                }
                                if (selectedId != null) {
                                    onMarkingSelected(selectedId)
                                }
                            }
                        }
                    }
                },
            contentScale = ContentScale.Fit,
            onSuccess = { result ->
                imageSize = Offset(
                    result.painter.intrinsicSize.width,
                    result.painter.intrinsicSize.height
                )
            }
        )
        
        // Markings overlay
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            // Draw existing markings
            markings.sortedBy { it.displayOrder }.forEach { marking ->
                MarkingRenderer.drawMarking(
                    canvas = drawContext.canvas.nativeCanvas,
                    marking = marking,
                    imageWidth = canvasWidth.toInt(),
                    imageHeight = canvasHeight.toInt(),
                    arrowStyle = arrowStyle,
                    density = density
                )
                
                // Draw control points for selected marking
                if (marking.id == selectedMarkingId) {
                    val controlPointRadius = 15f * density
                    val controlPointColor = androidx.compose.ui.graphics.Color.Red
                    
                    drawCircle(
                        color = controlPointColor,
                        radius = controlPointRadius,
                        center = Offset(
                            marking.startX * canvasWidth,
                            marking.startY * canvasHeight
                        )
                    )
                    
                    drawCircle(
                        color = controlPointColor,
                        radius = controlPointRadius,
                        center = Offset(
                            marking.endX * canvasWidth,
                            marking.endY * canvasHeight
                        )
                    )
                }
            }
            
            // Draw new marking being created
            if (isCreatingMarking && newMarkingStart != null && newMarkingEnd != null) {
                val tempMarking = Marking(
                    id = -1,
                    photoId = -1,
                    startX = newMarkingStart.first,
                    startY = newMarkingStart.second,
                    endX = newMarkingEnd.first,
                    endY = newMarkingEnd.second,
                    distanceValue = 0f,
                    distanceUnit = com.dimensioncam.data.model.DistanceUnit.CM,
                    lineColor = androidx.compose.ui.graphics.Color.Red.toArgb(),
                    lineWidthDp = 3f,
                    textColor = androidx.compose.ui.graphics.Color.White.toArgb(),
                    textSizeSp = 14f,
                    displayOrder = markings.size
                )
                
                // Draw line only (no text for preview)
                val paint = Paint().apply {
                    color = tempMarking.lineColor
                    strokeWidth = tempMarking.lineWidthDp * density
                    style = Paint.Style.STROKE
                    strokeCap = Paint.Cap.ROUND
                }
                
                drawContext.canvas.nativeCanvas.drawLine(
                    tempMarking.startX * canvasWidth,
                    tempMarking.startY * canvasHeight,
                    tempMarking.endX * canvasWidth,
                    tempMarking.endY * canvasHeight,
                    paint
                )
            }
        }
    }
}

/**
 * Normalize screen coordinates to 0-1 range
 */
private fun normalizeCoordinates(offset: Offset, width: Float, height: Float): PointF {
    return PointF(
        (offset.x / width).coerceIn(0f, 1f),
        (offset.y / height).coerceIn(0f, 1f)
    )
}
