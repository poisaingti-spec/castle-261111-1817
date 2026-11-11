package com.crafted.castle.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
sealed class DrawElement {
    abstract val id: String
    abstract val strokeColor: Long // Color.toArgb()
    abstract val backgroundColor: Long
    abstract val strokeWidth: Int
    abstract val startX: Float
    abstract val startY: Float

    @Serializable
    data class Freehand(
        override val id: String,
        override val strokeColor: Long,
        override val backgroundColor: Long,
        override val strokeWidth: Int,
        override val startX: Float,
        override val startY: Float,
        val points: List<Offset>
    ) : DrawElement()

    @Serializable
    data class Rectangle(
        override val id: String,
        override val strokeColor: Long,
        override val backgroundColor: Long,
        override val strokeWidth: Int,
        override val startX: Float,
        override val startY: Float,
        val width: Float,
        val height: Float
    ) : DrawElement()

    @Serializable
    data class Ellipse(
        override val id: String,
        override val strokeColor: Long,
        override val backgroundColor: Long,
        override val strokeWidth: Int,
        override val startX: Float,
        override val startY: Float,
        val width: Float,
        val height: Float
    ) : DrawElement()

    @Serializable
    data class Line(
        override val id: String,
        override val strokeColor: Long,
        override val backgroundColor: Long,
        override val strokeWidth: Int,
        override val startX: Float,
        override val startY: Float,
        val endX: Float,
        val endY: Float
    ) : DrawElement()

    @Serializable
    data class Arrow(
        override val id: String,
        override val strokeColor: Long,
        override val backgroundColor: Long,
        override val strokeWidth: Int,
        override val startX: Float,
        override val startY: Float,
        val endX: Float,
        val endY: Float
    ) : DrawElement()

    @Serializable
    data class Text(
        override val id: String,
        override val strokeColor: Long,
        override val backgroundColor: Long,
        override val strokeWidth: Int,
        override val startX: Float,
        override val startY: Float,
        val content: String,
        val fontSize: Float
    ) : DrawElement()
}

enum class Tool {
    HAND, SELECTION, RECTANGLE, DIAMOND, ELLIPSE, ARROW, LINE, DRAW, TEXT, STICKY, ERASER
}

data class CanvasState(
    val elements: List<DrawElement> = emptyList(),
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val currentTool: Tool = Tool.DRAW,
    val strokeColor: Color = Color.Black,
    val backgroundColor: Color = Color.Transparent,
    val strokeWidth: Int = 2
)