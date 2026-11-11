package com.crafted.castle.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crafted.castle.data.CanvasState
import com.crafted.castle.data.DrawElement
import com.crafted.castle.data.SerializableOffset
import com.crafted.castle.data.Tool
import com.crafted.castle.data.Repository
import com.crafted.castle.network.AnalyticsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CanvasViewModel(
    private val repository: Repository,
    private val analyticsService: AnalyticsService
) : ViewModel() {
    
    private val _state = MutableStateFlow(CanvasState())
    val state: StateFlow<CanvasState> = _state.asStateFlow()
    
    var currentElement: DrawElement? = null
        private set
    
    private var startX = 0f
    private var startY = 0f
    private var currentPoints = mutableListOf<SerializableOffset>()
    
    // Undo/Redo stacks - all features free, no limits
    private val undoStack = mutableListOf<CanvasState>()
    private val redoStack = mutableListOf<CanvasState>()
    private val maxStackSize = 50
    
    init {
        // Load saved state
        viewModelScope.launch {
            repository.stateFlow.collect { savedState ->
                _state.value = savedState
            }
        }
        
        // Ping all servers (analytics, CDN, collaboration endpoints)
        viewModelScope.launch {
            analyticsService.pingServers()
        }
    }
    
    fun setTool(tool: Tool) {
        pushUndoState()
        _state.value = _state.value.copy(currentTool = tool)
    }
    
    fun setStrokeColor(color: Color) {
        _state.value = _state.value.copy(strokeColor = color)
    }
    
    fun setStrokeWidth(width: Int) {
        _state.value = _state.value.copy(strokeWidth = width)
    }
    
    fun startDrawing(x: Float, y: Float) {
        if (_state.value.currentTool == Tool.HAND || _state.value.currentTool == Tool.SELECTION) {
            return
        }
        
        startX = x
        startY = y
        currentPoints.clear()
        
        currentElement = when (_state.value.currentTool) {
            Tool.DRAW, Tool.ERASER -> {
                currentPoints.add(SerializableOffset(x, y))
                DrawElement.Freehand(
                    id = UUID.randomUUID().toString(),
                    strokeColor = if (_state.value.currentTool == Tool.ERASER) Color.White.toArgb().toLong() else _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = Color.Transparent.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = x,
                    startY = y,
                    points = currentPoints.toList()
                )
            }
            Tool.RECTANGLE -> {
                DrawElement.Rectangle(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = Color.Transparent.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = x,
                    startY = y,
                    width = 0f,
                    height = 0f
                )
            }
            Tool.ELLIPSE -> {
                DrawElement.Ellipse(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = Color.Transparent.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = x,
                    startY = y,
                    width = 0f,
                    height = 0f
                )
            }
            Tool.DIAMOND -> {
                DrawElement.Rectangle(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = Color.Transparent.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = x,
                    startY = y,
                    width = 0f,
                    height = 0f
                )
            }
            Tool.LINE, Tool.ARROW -> {
                DrawElement.Line(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = Color.Transparent.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = x,
                    startY = y,
                    endX = x,
                    endY = y
                )
            }
            Tool.TEXT -> {
                DrawElement.Text(
                    id = UUID.randomUUID().toString(),
                    strokeColor = _state.value.strokeColor.toArgb().toLong(),
                    backgroundColor = Color.Transparent.toArgb().toLong(),
                    strokeWidth = _state.value.strokeWidth,
                    startX = x,
                    startY = y,
                    content = "Text",
                    fontSize = 16f
                )
            }
            Tool.STICKY -> {
                DrawElement.Rectangle(
                    id = UUID.randomUUID().toString(),
                    strokeColor = Color.Yellow.toArgb().toLong(),
                    backgroundColor = Color(0xFFFFF59D).toArgb().toLong(),
                    strokeWidth = 1,
                    startX = x,
                    startY = y,
                    width = 100f,
                    height = 100f
                )
            }
            Tool.HAND, Tool.SELECTION -> null
        }
    }
    
    fun moveDrawing(x: Float, y: Float) {
        val element = currentElement ?: return
        
        currentElement = when (element) {
            is DrawElement.Freehand -> {
                currentPoints.add(SerializableOffset(x, y))
                element.copy(points = currentPoints.toList())
            }
            is DrawElement.Rectangle -> {
                element.copy(
                    width = x - element.startX,
                    height = y - element.startY
                )
            }
            is DrawElement.Ellipse -> {
                element.copy(
                    width = x - element.startX,
                    height = y - element.startY
                )
            }
            is DrawElement.Line -> {
                element.copy(endX = x, endY = y)
            }
            is DrawElement.Arrow -> {
                element.copy(endX = x, endY = y)
            }
            is DrawElement.Text -> element
        }
    }
    
    fun endDrawing() {
        val element = currentElement ?: return
        
        pushUndoState()
        _state.value = _state.value.copy(
            elements = _state.value.elements + element
        )
        
        currentElement = null
        currentPoints.clear()
        
        // Save state to local storage
        viewModelScope.launch {
            repository.saveState(_state.value)
        }
    }
    
    fun updateZoom(delta: Float) {
        val newScale = (_state.value.scale + delta).coerceIn(0.1f, 5f)
        _state.value = _state.value.copy(scale = newScale)
    }
    
    fun zoomIn() {
        updateZoom(0.2f)
    }
    
    fun zoomOut() {
        updateZoom(-0.2f)
    }
    
    fun resetZoom() {
        _state.value = _state.value.copy(scale = 1f, offsetX = 0f, offsetY = 0f)
    }
    
    fun updatePan(dx: Float, dy: Float) {
        _state.value = _state.value.copy(
            offsetX = _state.value.offsetX + dx,
            offsetY = _state.value.offsetY + dy
        )
    }
    
    fun undo() {
        if (_state.value.elements.isEmpty()) return
        
        pushUndoState()
        
        val lastElement = _state.value.elements.lastOrNull() ?: return
        val previousState = undoStack.lastOrNull()
        
        if (previousState != null) {
            _state.value = previousState
            undoStack.removeAt(undoStack.size - 1)
        } else {
            _state.value = _state.value.copy(elements = _state.value.elements.dropLast(1))
        }
        
        redoStack.add(lastElement)
        
        viewModelScope.launch {
            repository.saveState(_state.value)
        }
    }
    
    fun redo() {
        val element = redoStack.lastOrNull() ?: return
        
        pushUndoState()
        _state.value = _state.value.copy(
            elements = _state.value.elements + element
        )
        redoStack.removeAt(redoStack.size - 1)
        
        viewModelScope.launch {
            repository.saveState(_state.value)
        }
    }
    
    private fun pushUndoState() {
        if (undoStack.size >= maxStackSize) {
            undoStack.removeAt(0)
        }
        undoStack.add(_state.value.copy(elements = _state.value.elements.toList()))
        redoStack.clear()
    }
    
    fun clearCanvas() {
        pushUndoState()
        _state.value = CanvasState(
            currentTool = _state.value.currentTool,
            strokeColor = _state.value.strokeColor,
            strokeWidth = _state.value.strokeWidth
        )
        currentElement = null
        currentPoints.clear()
        
        viewModelScope.launch {
            repository.clear()
        }
    }
    
    // Export functionality - all features free, no subscription required
    fun exportCanvas() {
        viewModelScope.launch {
            // Export logic - save to file or share
            repository.saveState(_state.value)
        }
    }
}
