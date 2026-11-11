package com.crafted.castle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.crafted.castle.ui.components.BottomToolBar
import com.crafted.castle.ui.components.CastleCanvas
import com.crafted.castle.ui.components.FloatingActions
import com.crafted.castle.ui.components.TopBar
import com.crafted.castle.viewmodel.CanvasViewModel

@Composable
fun CastleApp(viewModel: CanvasViewModel) {
    Scaffold(
        topBar = { TopBar(viewModel) },
        bottomBar = { BottomToolBar(viewModel) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CastleCanvas(viewModel = viewModel)
            FloatingActions(viewModel = viewModel)
        }
    }
}