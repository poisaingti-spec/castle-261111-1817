package com.crafted.castle.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crafted.castle.data.CanvasState
import com.crafted.castle.data.DrawElement
import com.crafted.castle.data.Repository
import com.crafted.castle.data.SerializableOffset
import com.crafted.castle.data.Tool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CanvasViewModel(private val repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow(CanvasState())
    val state: StateFlow<CanvasState> = _state.asStateFlow()

    private var currentPoints = mutableListOf<SerializableOffset>()
    private var currentStartOffset: Offset? = null

    val currentElement: DrawElement?
        get() = when (_state.value.currentTool) {
            Tool.DRAW -> {
                if (currentPoints.isNotEmpty() && currentStartOffset != null) {
                    DrawElement.Freehand(
                        id = UUID.randomUUID().toString(),
                        strokeColor = _state.value.strokeColor.toArgb().toLong(),
                        backgroundColor = _state.value.backgroundColor.toArgb().toLong(),
                        strokeWidth = _state.value.strokeWidth,
                        startX = currentStartOffset!!.x,
                        startY = currentStartOffset!!.y,
                        points = currentPoints.toList()
                    )
                } else null
            }
            else -> null
        }

    init {
        viewModelScope.launch {
            repository.stateFlow.collect { savedState ->
                _state.value = savedState
            }
        }
    }

    fun setTool(tool: Tool) {
        _state.value = _state.value.copy(currentTool = tool)
    }

    fun setStrokeColor(color: androidx.compose.ui.graphics.Color) {
        _state.value = _state.value.copy(strokeColor = color)
    }

    fun setStrokeWidth(width: Int) {
        _state.value = _state.value.copy(strokeWidth = width)
    }

    fun startDrawing(x: Float, y: Float) {
        if (_state.value.currentTool == Tool.HAND) return
        currentPoints.clear()
        currentStartOffset = Offset(x, y)
        currentPoints.add(SerializableOffset.fromOffset(Offset(x, y)))
    }

    fun moveDrawing(x: Float, y: Float) {
        if (_state.value.currentTool == Tool.HAND) return
        currentPoints.add(SerializableOffset.fromOffset(Offset(x, y)))
    }

    fun endDrawing() {
        if (currentPoints.isEmpty() || currentStartOffset == null) return
        
        val newElement = when (_state.value.currentTool) {
            Tool.DRAW -> {
                DrawElement.Freehand(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = _state.value.backgroundColor.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = currentStartOffset!!.x,
                    startY = currentStartOffset!!.y,
                    points = currentPoints.toList()
                )
            }
            Tool.RECTANGLE -> {
                val lastPoint = currentPoints.last()
                DrawElement.Rectangle(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = _state.value.backgroundColor.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = currentStartOffset!!.x,
                    startY = currentStartOffset!!.y,
                    width = lastPoint.x - currentStartOffset!!.x,
                    height = lastPoint.y - currentStartOffset!!.y
                )
            }
            Tool.ELLIPSE -> {
                val lastPoint = currentPoints.last()
                DrawElement.Ellipse(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = _state.value.backgroundColor.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = currentStartOffset!!.x,
                    startY = currentStartOffset!!.y,
                    width = lastPoint.x - currentStartOffset!!.x,
                    height = lastPoint.y - currentStartOffset!!.y
                )
            }
            Tool.LINE -> {
                val lastPoint = currentPoints.last()
                DrawElement.Line(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = _state.value.backgroundColor.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = currentStartOffset!!.x,
                    startY = currentStartOffset!!.y,
                    endX = lastPoint.x,
                    endY = lastPoint.y
                )
            }
            Tool.ARROW -> {
                val lastPoint = currentPoints.last()
                DrawElement.Arrow(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = _state.value.backgroundColor.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = currentStartOffset!!.x,
                    startY = currentStartOffset!!.y,
                    endX = lastPoint.x,
                    endY = lastPoint.y
                )
            }
            else -> null
        }

        if (newElement != null) {
            val newElements = _state.value.elements + newElement
            _state.value = _state.value.copy(elements = newElements)
            saveState()
        }
        
        currentPoints.clear()
        currentStartOffset = null
    }

    fun updatePan(dx: Float, dy: Float) {
        _state.value = _state.value.copy(
            offsetX = _state.value.offsetX + dx,
            offsetY = _state.value.offsetY + dy
        )
    }

    fun updateZoom(delta: Float) {
        val newScale = (_state.value.scale + delta).coerceIn(0.1f, 5f)
        _state.value = _state.value.copy(scale = newScale)
    }

    fun clearCanvas() {
        _state.value = _state.value.copy(elements = emptyList())
        saveState()
    }

    fun undo() {
        val elements = _state.value.elements
        if (elements.isNotEmpty()) {
            _state.value = _state.value.copy(elements = elements.dropLast(1))
            saveState()
        }
    }

    fun redo() {
        // Would need an undo stack for proper implementation
    }

    private fun saveState() {
        viewModelScope.launch {
            repository.saveState(_state.value)
        }
    }
}
