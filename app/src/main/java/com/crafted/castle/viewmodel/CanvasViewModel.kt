package com.crafted.castle.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crafted.castle.data.CanvasState
import com.crafted.castle.data.DrawElement
import com.crafted.castle.data.Repository
import com.crafted.castle.data.Tool
import com.crafted.castle.network.AnalyticsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CanvasViewModel(
    private val repository: Repository,
    private val analytics: AnalyticsService
) : ViewModel() {

    private val _state = MutableStateFlow(CanvasState())
    val state: StateFlow<CanvasState> = _state.asStateFlow()

    // Undo/Redo Stacks
    private val undoStack = mutableListOf<CanvasState>()
    private val redoStack = mutableListOf<CanvasState>()

    // Temporary drawing state
    private var currentDrawingElement: DrawElement? = null

    init {
        viewModelScope.launch {
            analytics.pingServers()
            // Load initial state if needed, currently defaulting to empty for clean start
        }
    }

    fun setTool(tool: Tool) {
        pushUndo()
        _state.value = _state.value.copy(currentTool = tool)
    }

    fun setStrokeColor(color: Color) {
        _state.value = _state.value.copy(strokeColor = color)
    }

    fun updatePan(dx: Float, dy: Float) {
        _state.value = _state.value.copy(
            offsetX = _state.value.offsetX + dx,
            offsetY = _state.value.offsetY + dy
        )
    }

    fun updateZoom(delta: Float) {
        val newScale = (_state.value.scale + delta).coerceIn(0.1f, 5.0f)
        _state.value = _state.value.copy(scale = newScale)
    }

    fun resetZoom() {
        _state.value = _state.value.copy(scale = 1f, offsetX = 0f, offsetY = 0f)
    }

    fun startDrawing(x: Float, y: Float) {
        if (_state.value.currentTool == Tool.HAND) return

        val id = UUID.randomUUID().toString()
        val color = _state.value.strokeColor
        val bg = _state.value.backgroundColor
        val width = _state.value.strokeWidth

        currentDrawingElement = when (_state.value.currentTool) {
            Tool.DRAW -> DrawElement.Freehand(id, color.toLong(), bg.toLong(), width, x, y, listOf(Offset(x, y)))
            Tool.RECTANGLE -> DrawElement.Rectangle(id, color.toLong(), bg.toLong(), width, x, y, 0f, 0f)
            Tool.ELLIPSE -> DrawElement.Ellipse(id, color.toLong(), bg.toLong(), width, x, y, 0f, 0f)
            Tool.LINE -> DrawElement.Line(id, color.toLong(), bg.toLong(), width, x, y, x, y)
            Tool.ARROW -> DrawElement.Arrow(id, color.toLong(), bg.toLong(), width, x, y, x, y)
            Tool.TEXT -> DrawElement.Text(id, color.toLong(), bg.toLong(), width, x, y, "Text", 20f)
            else -> null
        }
    }

    fun moveDrawing(x: Float, y: Float) {
        val current = currentDrawingElement ?: return
        
        currentDrawingElement = when (val el = current) {
            is DrawElement.Freehand -> el.copy(points = el.points + Offset(x, y))
            is DrawElement.Rectangle -> el.copy(width = x - el.startX, height = y - el.startY)
            is DrawElement.Ellipse -> el.copy(width = x - el.startX, height = y - el.startY)
            is DrawElement.Line -> el.copy(endX = x, endY = y)
            is DrawElement.Arrow -> el.copy(endX = x, endY = y)
            else -> el
        }
    }

    fun endDrawing() {
        val element = currentDrawingElement ?: return
        pushUndo()
        _state.value = _state.value.copy(elements = _state.value.elements + element)
        currentDrawingElement = null
        saveState()
    }

    private fun pushUndo() {
        if (undoStack.size > 20) undoStack.removeAt(0)
        undoStack.add(_state.value)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        redoStack.add(_state.value)
        _state.value = undoStack.removeAt(undoStack.lastIndex)
        saveState()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        undoStack.add(_state.value)
        _state.value = redoStack.removeAt(redoStack.lastIndex)
        saveState()
    }

    fun clearCanvas() {
        pushUndo()
        _state.value = _state.value.copy(elements = emptyList())
        saveState()
    }

    private fun saveState() {
        viewModelScope.launch {
            repository.saveState(_state.value)
        }
    }
    
    val currentElement get() = currentDrawingElement
}