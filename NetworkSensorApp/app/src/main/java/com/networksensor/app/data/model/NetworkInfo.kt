package com.networksensor.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NetworkInfo(
    val connectionType: ConnectionType,
    val networkName: String?,
    val signalStrength: Int,
    val downloadSpeed: Double, // Mbps
    val uploadSpeed: Double, // Mbps
    val latency: Long, // ms
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

enum class ConnectionType {
    WIFI,
    MOBILE_LTE,
    MOBILE_4G,
    MOBILE_3G,
    MOBILE_2G,
    MOBILE_5G,
    ETHERNET,
    UNKNOWN
}

@Parcelize
data class SpeedTestResult(
    val downloadSpeed: Double,
    val uploadSpeed: Double,
    val latency: Long,
    val jitter: Long,
    val packetLoss: Double,
    val testDuration: Long,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class WifiInfo(
    val ssid: String?,
    val bssid: String?,
    val frequency: Int,
    val linkSpeed: Int,
    val rssi: Int,
    val networkId: Int,
    val ipAddress: String?
) : Parcelable

@Parcelize
data class CellularInfo(
    val networkType: String,
    val operatorName: String?,
    val mcc: String?,
    val mnc: String?,
    val signalStrength: Int,
    val cellId: Int?,
    val lac: Int?
) : Parcelable