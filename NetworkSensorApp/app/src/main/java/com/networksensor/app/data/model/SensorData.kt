package com.networksensor.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SensorReading(
    val sensorType: SensorType,
    val values: FloatArray,
    val accuracy: Int,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SensorReading

        if (sensorType != other.sensorType) return false
        if (!values.contentEquals(other.values)) return false
        if (accuracy != other.accuracy) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sensorType.hashCode()
        result = 31 * result + values.contentHashCode()
        result = 31 * result + accuracy
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

enum class SensorType {
    ACCELEROMETER,
    GYROSCOPE,
    MAGNETOMETER,
    GRAVITY,
    LINEAR_ACCELERATION,
    ROTATION_VECTOR,
    ORIENTATION,
    PRESSURE,
    TEMPERATURE,
    HUMIDITY,
    LIGHT,
    PROXIMITY,
    STEP_COUNTER,
    HEART_RATE
}

@Parcelize
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float,
    val provider: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class DeviceMotion(
    val accelerometer: FloatArray,
    val gyroscope: FloatArray,
    val magnetometer: FloatArray,
    val gravity: FloatArray,
    val linearAcceleration: FloatArray,
    val rotationVector: FloatArray,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceMotion

        if (!accelerometer.contentEquals(other.accelerometer)) return false
        if (!gyroscope.contentEquals(other.gyroscope)) return false
        if (!magnetometer.contentEquals(other.magnetometer)) return false
        if (!gravity.contentEquals(other.gravity)) return false
        if (!linearAcceleration.contentEquals(other.linearAcceleration)) return false
        if (!rotationVector.contentEquals(other.rotationVector)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accelerometer.contentHashCode()
        result = 31 * result + gyroscope.contentHashCode()
        result = 31 * result + magnetometer.contentHashCode()
        result = 31 * result + gravity.contentHashCode()
        result = 31 * result + linearAcceleration.contentHashCode()
        result = 31 * result + rotationVector.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

@Parcelize
data class EnvironmentalData(
    val temperature: Float?,
    val humidity: Float?,
    val pressure: Float?,
    val lightLevel: Float?,
    val proximity: Float?,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable