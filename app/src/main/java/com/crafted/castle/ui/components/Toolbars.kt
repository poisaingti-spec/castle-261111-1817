package com.crafted.castle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.crafted.castle.data.Tool
import com.crafted.castle.ui.theme.MaitisDarkSurfaceVariant
import com.crafted.castle.ui.theme.MaitisPurplePrimary
import com.crafted.castle.viewmodel.CanvasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(viewModel: CanvasViewModel) {
    TopAppBar(
        title = {
            Text(
                "castle",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaitisPurplePrimary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            IconButton(onClick = { viewModel.undo() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Undo", tint = Color.White)
            }
            IconButton(onClick = { viewModel.redo() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Redo", tint = Color.White)
            }
            IconButton(onClick = { viewModel.clearCanvas() }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.White)
            }
            IconButton(onClick = { /* Export Logic - all features free */ }) {
                Icon(Icons.Default.Share, contentDescription = "Export", tint = Color.White)
            }
        }
    )
}

@Composable
fun BottomToolBar(viewModel: CanvasViewModel) {
    val state by viewModel.state.collectAsState()
    
    val tools = listOf(
        ToolButtonData(Icons.Default.PanTool, Tool.HAND, "Hand"),
        ToolButtonData(Icons.Default.TouchApp, Tool.SELECTION, "Select"),
        ToolButtonData(Icons.Default.Rectangle, Tool.RECTANGLE, "Rectangle"),
        ToolButtonData(Icons.Default.Hexagon, Tool.DIAMOND, "Diamond"),
        ToolButtonData(Icons.Default.Circle, Tool.ELLIPSE, "Ellipse"),
        ToolButtonData(Icons.Default.NorthEast, Tool.ARROW, "Arrow"),
        ToolButtonData(Icons.Default.DragHandle, Tool.LINE, "Line"),
        ToolButtonData(Icons.Default.Edit, Tool.DRAW, "Draw"),
        ToolButtonData(Icons.Default.Title, Tool.TEXT, "Text"),
        ToolButtonData(Icons.Default.SquareFoot, Tool.STICKY, "Sticky"),
        ToolButtonData(Icons.Default.Backspace, Tool.ERASER, "Eraser")
    )
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaitisDarkSurfaceVariant,
        shadowElevation = 8.dp
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(tools) { toolData ->
                ToolButton(
                    icon = toolData.icon,
                    tool = toolData.tool,
                    current = state.currentTool,
                    label = toolData.label,
                    onClick = { viewModel.setTool(toolData.tool) }
                )
            }
        }
    }
}

private data class ToolButtonData(
    val icon: ImageVector,
    val tool: Tool,
    val label: String
)

@Composable
private fun ToolButton(
    icon: ImageVector,
    tool: Tool,
    current: Tool,
    label: String,
    onClick: () -> Unit
) {
    val isSelected = tool == current
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaitisPurplePrimary else Color.Transparent,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun FloatingActions(viewModel: CanvasViewModel) {
    val state by viewModel.state.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            color = MaitisDarkSurfaceVariant.copy(alpha = 0.9f),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.zoomOut() }) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White)
                }
                IconButton(onClick = { viewModel.resetZoom() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Zoom", tint = Color.White)
                }
                IconButton(onClick = { viewModel.zoomIn() }) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { viewModel.undo() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Undo", tint = Color.White)
                }
                IconButton(onClick = { viewModel.redo() }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Redo", tint = Color.White)
                }
            }
        }
    }
}
