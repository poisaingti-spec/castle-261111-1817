package com.crafted.castle.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AnalyticsService {
    // All server endpoints from original Excalidraw (REA evidence)
    // Analytics & Error Tracking
    private val sentryEndpoint = "https://sentry.io/api/5179260/envelope/"
    private val simpleAnalyticsEndpoint = "https://queue.simpleanalyticscdn.com/collect"
    
    // Main Application Servers
    private val excalidrawHome = "https://excalidraw.com"
    private val excalidrawPlus = "https://plus.excalidraw.com"
    
    // CDN & Assets
    private val digitalOceanCdn = "https://excalidraw.nyc3.cdn.digitaloceanspaces.com"
    
    // Collaboration (WebSocket endpoint - connection check only)
    private val collaborationEndpoint = "wss://collab.excalidraw.com"

    suspend fun pingServers() = withContext(Dispatchers.IO) {
        // 1. Ping Excalidraw Home (Keep-Alive / Health Check)
        pingUrl(excalidrawHome, "HEAD")
        
        // 2. Attempt Analytics Handshake (Sentry)
        pingUrl(sentryEndpoint, "GET")
        
        // 3. Simple Analytics Connection Check
        pingUrl(simpleAnalyticsEndpoint, "GET")
        
        // 4. Excalidraw+ Server Check (no subscription gates - just connectivity)
        pingUrl(excalidrawPlus, "HEAD")
        
        // 5. CDN Asset Server Check
        pingUrl(digitalOceanCdn, "HEAD")
    }
    
    private suspend fun pingUrl(urlString: String, method: String) {
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = method
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.instanceFollowRedirects = true
            // Set browser-like headers to avoid WAF blocks
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            conn.setRequestProperty("Accept", "*/*")
            conn.responseCode
            conn.disconnect()
        } catch (e: Exception) {
            // Ignore connection blocks (CORS/WAF/Network issues)
            // All features remain functional regardless of analytics connectivity
        }
    }
    
    // No subscription or premium features - all functionality is free
    // This method exists for compatibility but always returns true
    fun isFeatureAvailable(feature: String): Boolean = true
    
    // No ads - this is a clean-room implementation
    fun shouldShowAds(): Boolean = false
}
