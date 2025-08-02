package com.networksensor.app.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.telephony.SignalStrength
import com.networksensor.app.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.InetAddress
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

@Singleton
class NetworkRepository @Inject constructor(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    
    fun getCurrentNetworkInfo(): Flow<NetworkInfo> = flow {
        val networkInfo = getCurrentNetworkInfoSync()
        emit(networkInfo)
    }.flowOn(Dispatchers.IO)
    
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentNetworkInfoSync(): NetworkInfo = withContext(Dispatchers.IO) {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        
        val connectionType = when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> ConnectionType.WIFI
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                when (telephonyManager.dataNetworkType) {
                    TelephonyManager.NETWORK_TYPE_NR -> ConnectionType.MOBILE_5G
                    TelephonyManager.NETWORK_TYPE_LTE -> ConnectionType.MOBILE_LTE
                    TelephonyManager.NETWORK_TYPE_HSPAP,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_UMTS -> ConnectionType.MOBILE_3G
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_GPRS -> ConnectionType.MOBILE_2G
                    else -> ConnectionType.MOBILE_4G
                }
            }
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> ConnectionType.ETHERNET
            else -> ConnectionType.UNKNOWN
        }
        
        val networkName = when (connectionType) {
            ConnectionType.WIFI -> getWifiNetworkName()
            else -> telephonyManager.networkOperatorName
        }
        
        val signalStrength = getSignalStrength(connectionType)
        val speedTest = performQuickSpeedTest()
        
        NetworkInfo(
            connectionType = connectionType,
            networkName = networkName,
            signalStrength = signalStrength,
            downloadSpeed = speedTest.downloadSpeed,
            uploadSpeed = speedTest.uploadSpeed,
            latency = speedTest.latency
        )
    }
    
    @SuppressLint("MissingPermission")
    private fun getWifiNetworkName(): String? {
        return try {
            val wifiInfo = wifiManager.connectionInfo
            wifiInfo.ssid?.replace("\"", "")
        } catch (e: Exception) {
            null
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun getSignalStrength(connectionType: ConnectionType): Int {
        return when (connectionType) {
            ConnectionType.WIFI -> {
                try {
                    val wifiInfo = wifiManager.connectionInfo
                    WifiManager.calculateSignalLevel(wifiInfo.rssi, 5)
                } catch (e: Exception) {
                    0
                }
            }
            else -> {
                try {
                    // This would require more complex implementation for cellular signal strength
                    3 // Default value
                } catch (e: Exception) {
                    0
                }
            }
        }
    }
    
    suspend fun performSpeedTest(): Flow<SpeedTestResult> = flow {
        val testUrls = listOf(
            "https://speed.cloudflare.com/__down?bytes=25000000", // 25MB
            "https://httpbin.org/bytes/10000000", // 10MB
            "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"
        )
        
        var totalDownloadSpeed = 0.0
        var totalLatency = 0L
        var successfulTests = 0
        
        for (url in testUrls) {
            try {
                val result = performSingleSpeedTest(url)
                totalDownloadSpeed += result.downloadSpeed
                totalLatency += result.latency
                successfulTests++
                
                emit(SpeedTestResult(
                    downloadSpeed = totalDownloadSpeed / successfulTests,
                    uploadSpeed = result.uploadSpeed,
                    latency = totalLatency / successfulTests,
                    jitter = result.jitter,
                    packetLoss = result.packetLoss,
                    testDuration = result.testDuration
                ))
            } catch (e: Exception) {
                // Continue with next test
            }
        }
    }.flowOn(Dispatchers.IO)
    
    private suspend fun performQuickSpeedTest(): SpeedTestResult = withContext(Dispatchers.IO) {
        try {
            performSingleSpeedTest("https://httpbin.org/bytes/1000000") // 1MB for quick test
        } catch (e: Exception) {
            SpeedTestResult(0.0, 0.0, 0L, 0L, 0.0, 0L)
        }
    }
    
    private suspend fun performSingleSpeedTest(url: String): SpeedTestResult = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        
        val latency = measureLatency("8.8.8.8")
        
        val downloadTime = measureTimeMillis {
            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.bytes()
                    }
                }
            } catch (e: IOException) {
                throw e
            }
        }
        
        // Estimate file size (this would be more accurate with actual file sizes)
        val fileSizeBytes = 1000000 // 1MB
        val downloadSpeedMbps = (fileSizeBytes * 8.0) / (downloadTime * 1000.0) // Convert to Mbps
        
        SpeedTestResult(
            downloadSpeed = downloadSpeedMbps,
            uploadSpeed = 0.0, // Upload test would require a different endpoint
            latency = latency,
            jitter = 0L,
            packetLoss = 0.0,
            testDuration = downloadTime
        )
    }
    
    private suspend fun measureLatency(host: String): Long = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            InetAddress.getByName(host).isReachable(5000)
            System.currentTimeMillis() - startTime
        } catch (e: Exception) {
            0L
        }
    }
    
    @SuppressLint("MissingPermission")
    fun getWifiDetails(): Flow<WifiInfo> = flow {
        try {
            val wifiInfo = wifiManager.connectionInfo
            val dhcpInfo = wifiManager.dhcpInfo
            
            emit(WifiInfo(
                ssid = wifiInfo.ssid?.replace("\"", ""),
                bssid = wifiInfo.bssid,
                frequency = wifiInfo.frequency,
                linkSpeed = wifiInfo.linkSpeed,
                rssi = wifiInfo.rssi,
                networkId = wifiInfo.networkId,
                ipAddress = intToIp(dhcpInfo.ipAddress)
            ))
        } catch (e: Exception) {
            emit(WifiInfo(null, null, 0, 0, 0, 0, null))
        }
    }.flowOn(Dispatchers.IO)
    
    @SuppressLint("MissingPermission")
    fun getCellularDetails(): Flow<CellularInfo> = flow {
        try {
            emit(CellularInfo(
                networkType = getNetworkTypeString(),
                operatorName = telephonyManager.networkOperatorName,
                mcc = telephonyManager.networkOperator?.substring(0, 3),
                mnc = telephonyManager.networkOperator?.substring(3),
                signalStrength = 3, // Would need more complex implementation
                cellId = null, // Would require location permissions and more complex implementation
                lac = null
            ))
        } catch (e: Exception) {
            emit(CellularInfo("Unknown", null, null, null, 0, null, null))
        }
    }.flowOn(Dispatchers.IO)
    
    private fun getNetworkTypeString(): String {
        return when (telephonyManager.dataNetworkType) {
            TelephonyManager.NETWORK_TYPE_NR -> "5G NR"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
            else -> "Unknown"
        }
    }
    
    private fun intToIp(ip: Int): String {
        return "${ip and 0xFF}.${ip shr 8 and 0xFF}.${ip shr 16 and 0xFF}.${ip shr 24 and 0xFF}"
    }
}