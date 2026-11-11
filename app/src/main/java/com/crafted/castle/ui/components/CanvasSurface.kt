package com.crafted.castle.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.crafted.castle.data.DrawElement
import com.crafted.castle.data.SerializableOffset
import com.crafted.castle.data.Tool
import com.crafted.castle.viewmodel.CanvasViewModel
import org.koin.compose.koinInject

@Composable
fun CastleCanvas(viewModel: CanvasViewModel = koinInject()) {
    val state by viewModel.state.collectAsState()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(state) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    if (state.currentTool == Tool.HAND) {
                        viewModel.updatePan(pan.x, pan.y)
                    } else {
                        viewModel.updateZoom((zoom - 1) * 0.5f)
                    }
                }
            }
            .pointerInput(state) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val x = (offset.x - state.offsetX) / state.scale
                        val y = (offset.y - state.offsetY) / state.scale
                        viewModel.startDrawing(x, y)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (state.currentTool == Tool.HAND) {
                            viewModel.updatePan(dragAmount.x, dragAmount.y)
                        } else {
                            val x = ((change.position.x - state.offsetX) / state.scale)
                            val y = ((change.position.y - state.offsetY) / state.scale)
                            viewModel.moveDrawing(x, y)
                        }
                    },
                    onDragEnd = {
                        if (state.currentTool != Tool.HAND) {
                            viewModel.endDrawing()
                        }
                    }
                )
            }
    ) {
        drawRect(Color.White)

        with(drawContext.canvas.nativeCanvas) {
            save()
            translate(state.offsetX, state.offsetY)
            scale(state.scale, state.scale)

            state.elements.forEach { drawElement(it) }
            viewModel.currentElement?.let { drawElement(it) }

            restore()
        }
    }
}

private fun DrawScope.drawElement(element: DrawElement) {
    val stroke = Stroke(width = element.strokeWidth.toFloat(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
    val paintColor = Color(element.strokeColor)

    when (element) {
        is DrawElement.Freehand -> {
            if (element.points.size > 1) {
                val path = Path().apply {
                    moveTo(element.points[0].x, element.points[0].y)
                    element.points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(path, paintColor, style = stroke)
            }
        }
        is DrawElement.Rectangle -> {
            drawRect(
                color = paintColor,
                topLeft = Offset(element.startX, element.startY),
                size = androidx.compose.ui.geometry.Size(element.width, element.height),
                style = stroke
            )
        }
        is DrawElement.Ellipse -> {
            drawOval(
                color = paintColor,
                topLeft = Offset(element.startX, element.startY),
                size = androidx.compose.ui.geometry.Size(element.width, element.height),
                style = stroke
            )
        }
        is DrawElement.Line -> {
            drawLine(
                color = paintColor,
                start = Offset(element.startX, element.startY),
                end = Offset(element.endX, element.endY),
                strokeWidth = element.strokeWidth.toFloat()
            )
        }
        is DrawElement.Arrow -> {
            drawLine(
                color = paintColor,
                start = Offset(element.startX, element.startY),
                end = Offset(element.endX, element.endY),
                strokeWidth = element.strokeWidth.toFloat()
            )
            val angle = Math.atan2((element.endY - element.startY).toDouble(), (element.endX - element.startX).toDouble())
            val headLen = 20
            val x1 = element.endX - headLen * Math.cos(angle - Math.PI / 6)
            val y1 = element.endY - headLen * Math.sin(angle - Math.PI / 6)
            val x2 = element.endX - headLen * Math.cos(angle + Math.PI / 6)
            val y2 = element.endY - headLen * Math.sin(angle + Math.PI / 6)
            
            drawLine(
                color = paintColor,
                start = Offset(element.endX, element.endY),
                end = Offset(x1.toFloat(), y1.toFloat()),
                strokeWidth = element.strokeWidth.toFloat()
            )
            drawLine(
                color = paintColor,
                start = Offset(element.endX, element.endY),
                end = Offset(x2.toFloat(), y2.toFloat()),
                strokeWidth = element.strokeWidth.toFloat()
            )
        }
        is DrawElement.Text -> {
            val textPaint = android.graphics.Paint().apply {
                color = paintColor.toArgb()
                textSize = element.fontSize
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(element.content, element.startX, element.startY, textPaint)
        }
    }
}
