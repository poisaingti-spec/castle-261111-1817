package com.crafted.castle.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AnalyticsService {
    // Endpoints discovered via REA evidence
    private val sentryEndpoint = "https://sentry.io/api/5179260/envelope/"
    private val simpleAnalyticsEndpoint = "https://queue.simpleanalyticscdn.com/collect"
    private val excalidrawHome = "https://excalidraw.com"

    suspend fun pingServers() = withContext(Dispatchers.IO) {
        try {
            // 1. Ping Excalidraw Home (Keep-Alive)
            val url = URL(excalidrawHome)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "HEAD"
            conn.connectTimeout = 2000
            conn.readTimeout = 2000
            conn.responseCode
            conn.disconnect()
        } catch (e: Exception) {
            // Ignore connection blocks (CORS/WAF)
        }

        try {
            // 2. Attempt Analytics Handshake (No data sent, just connection check)
            val saUrl = URL(simpleAnalyticsEndpoint)
            val saConn = saUrl.openConnection() as HttpURLConnection
            saConn.requestMethod = "GET"
            saConn.connectTimeout = 2000
            saConn.responseCode
            saConn.disconnect()
        } catch (e: Exception) {
            // Ignore
        }
    }
}