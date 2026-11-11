package com.crafted.castle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.crafted.castle.data.Tool
import com.crafted.castle.ui.theme.MaitisPurplePrimary
import com.crafted.castle.ui.theme.MaitisDarkSurfaceVariant
import com.crafted.castle.viewmodel.CanvasViewModel

@Composable
fun TopBar(viewModel: CanvasViewModel) {
    TopAppBar(
        title = {
            Text(
                "castle",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaitisPurplePrimary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            IconButton(onClick = { viewModel.clearCanvas() }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear")
            }
            IconButton(onClick = { /* Export Logic */ }) {
                Icon(Icons.Default.Share, contentDescription = "Export")
            }
        }
    )
}

@Composable
fun BottomToolBar(viewModel: CanvasViewModel) {
    val state by viewModel.state.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaitisDarkSurfaceVariant,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(androidx.compose.foundation.gestures.ScrollableState { 0f }) // Simplified scroll
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolButton(Icons.Default.PanTool, Tool.HAND, state.currentTool) { viewModel.setTool(Tool.HAND) }
            ToolButton(Icons.Default.TouchApp, Tool.SELECTION, state.currentTool) { viewModel.setTool(Tool.SELECTION) }
            ToolButton(Icons.Default.Rectangle, Tool.RECTANGLE, state.currentTool) { viewModel.setTool(Tool.RECTANGLE) }
            ToolButton(Icons.Default.Hexagon, Tool.DIAMOND, state.currentTool) { viewModel.setTool(Tool.DIAMOND) } // Approx
            ToolButton(Icons.Default.Circle, Tool.ELLIPSE, state.currentTool) { viewModel.setTool(Tool.ELLIPSE) }
            ToolButton(Icons.Default.NorthEast, Tool.ARROW, state.currentTool) { viewModel.setTool(Tool.ARROW) }
            ToolButton(Icons.Default.DragHandle, Tool.LINE, state.currentTool) { viewModel.setTool(Tool.LINE) } // Approx
            ToolButton(Icons.Default.Edit, Tool.DRAW, state.currentTool) { viewModel.setTool(Tool.DRAW) }
            ToolButton(Icons.Default.Title, Tool.TEXT, state.currentTool) { viewModel.setTool(Tool.TEXT) }
            ToolButton(Icons.Default.SquareFoot, Tool.STICKY, state.currentTool) { viewModel.setTool(Tool.STICKY) }
            ToolButton(Icons.Default.Backspace, Tool.ERASER, state.currentTool) { viewModel.setTool(Tool.ERASER) }
        }
    }
}

@Composable
private fun ToolButton(icon: ImageVector, tool: Tool, current: Tool, onClick: () -> Unit) {
    val isSelected = tool == current
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaitisPurplePrimary else Color.Transparent,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = tool.name)
        }
    }
}

@Composable
fun FloatingActions(viewModel: CanvasViewModel) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.TopEnd),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(onClick = { viewModel.updateZoom(0.2f) }, containerColor = MaitisPurplePrimary) {
            Icon(Icons.Default.Add, "Zoom In")
        }
        FloatingActionButton(onClick = { viewModel.updateZoom(-0.2f) }, containerColor = MaitisPurplePrimary) {
            Icon(Icons.Default.Remove, "Zoom Out")
        }
        FloatingActionButton(onClick = { viewModel.resetZoom() }, containerColor = MaitisDarkSurfaceVariant) {
            Icon(Icons.Default.Refresh, "Reset")
        }
    }
    
    Row(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomStart),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(onClick = { viewModel.undo() }, containerColor = MaitisDarkSurfaceVariant) {
            Icon(Icons.Default.Undo, "Undo")
        }
        FloatingActionButton(onClick = { viewModel.redo() }, containerColor = MaitisDarkSurfaceVariant) {
            Icon(Icons.Default.Redo, "Redo")
        }
    }
}