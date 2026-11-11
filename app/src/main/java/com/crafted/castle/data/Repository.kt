package com.crafted.castle.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<String> by dataStore("castle_state.json")

class Repository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    val stateFlow: Flow<CanvasState> = context.dataStore.data.map { jsonString ->
        try {
            if (jsonString.isEmpty()) CanvasState()
            else json.decodeFromString<CanvasState>(jsonString)
        } catch (e: Exception) {
            CanvasState()
        }
    }

    suspend fun saveState(state: CanvasState) {
        val elementsOnly = state.copy(scale = 1f, offsetX = 0f, offsetY = 0f)
        context.dataStore.updateData { json.encodeToString(elementsOnly) }
    }
    
    suspend fun clear() {
        context.dataStore.updateData { "" }
    }
}
