package com.networksensor.app.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.networksensor.app.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepository @Inject constructor(
    private val context: Context
) {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    fun getSensorReading(sensorType: SensorType): Flow<SensorReading> = callbackFlow {
        val sensor = getSensorBySensorType(sensorType)
        
        if (sensor == null) {
            close()
            return@callbackFlow
        }
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val reading = SensorReading(
                        sensorType = sensorType,
                        values = it.values.clone(),
                        accuracy = it.accuracy,
                        timestamp = it.timestamp
                    )
                    trySend(reading)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle accuracy changes if needed
            }
        }
        
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.flowOn(Dispatchers.IO)
    
    fun getDeviceMotion(): Flow<DeviceMotion> = callbackFlow {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        
        var accelerometerData = FloatArray(3)
        var gyroscopeData = FloatArray(3)
        var magnetometerData = FloatArray(3)
        var gravityData = FloatArray(3)
        var linearAccelerationData = FloatArray(3)
        var rotationVectorData = FloatArray(4)
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            accelerometerData = it.values.clone()
                        }
                        Sensor.TYPE_GYROSCOPE -> {
                            gyroscopeData = it.values.clone()
                        }
                        Sensor.TYPE_MAGNETIC_FIELD -> {
                            magnetometerData = it.values.clone()
                        }
                        Sensor.TYPE_GRAVITY -> {
                            gravityData = it.values.clone()
                        }
                        Sensor.TYPE_LINEAR_ACCELERATION -> {
                            linearAccelerationData = it.values.clone()
                        }
                        Sensor.TYPE_ROTATION_VECTOR -> {
                            rotationVectorData = it.values.clone()
                        }
                    }
                    
                    val motion = DeviceMotion(
                        accelerometer = accelerometerData,
                        gyroscope = gyroscopeData,
                        magnetometer = magnetometerData,
                        gravity = gravityData,
                        linearAcceleration = linearAccelerationData,
                        rotationVector = rotationVectorData
                    )
                    trySend(motion)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        listOfNotNull(
            accelerometer,
            gyroscope,
            magnetometer,
            gravity,
            linearAcceleration,
            rotationVector
        ).forEach { sensor ->
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.flowOn(Dispatchers.IO)
    
    fun getEnvironmentalData(): Flow<EnvironmentalData> = callbackFlow {
        val temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        val humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        
        var temperature: Float? = null
        var humidity: Float? = null
        var pressure: Float? = null
        var lightLevel: Float? = null
        var proximity: Float? = null
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                            temperature = it.values[0]
                        }
                        Sensor.TYPE_RELATIVE_HUMIDITY -> {
                            humidity = it.values[0]
                        }
                        Sensor.TYPE_PRESSURE -> {
                            pressure = it.values[0]
                        }
                        Sensor.TYPE_LIGHT -> {
                            lightLevel = it.values[0]
                        }
                        Sensor.TYPE_PROXIMITY -> {
                            proximity = it.values[0]
                        }
                    }
                    
                    val environmentalData = EnvironmentalData(
                        temperature = temperature,
                        humidity = humidity,
                        pressure = pressure,
                        lightLevel = lightLevel,
                        proximity = proximity
                    )
                    trySend(environmentalData)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        listOfNotNull(
            temperatureSensor,
            humiditySensor,
            pressureSensor,
            lightSensor,
            proximitySensor
        ).forEach { sensor ->
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.flowOn(Dispatchers.IO)
    
    @SuppressLint("MissingPermission")
    fun getLocationData(): Flow<LocationData> = callbackFlow {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val locationData = LocationData(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = location.altitude,
                    accuracy = location.accuracy,
                    speed = location.speed,
                    bearing = location.bearing,
                    provider = location.provider ?: "Unknown"
                )
                trySend(locationData)
            }
            
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        
        try {
            // Try GPS first
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L, // 1 second
                    1f, // 1 meter
                    locationListener
                )
            }
            
            // Also try network provider
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    1f,
                    locationListener
                )
            }
        } catch (e: SecurityException) {
            close(e)
        }
        
        awaitClose {
            locationManager.removeUpdates(locationListener)
        }
    }.flowOn(Dispatchers.IO)
    
    fun getAvailableSensors(): List<SensorType> {
        val availableSensors = mutableListOf<SensorType>()
        
        SensorType.values().forEach { sensorType ->
            val sensor = getSensorBySensorType(sensorType)
            if (sensor != null) {
                availableSensors.add(sensorType)
            }
        }
        
        return availableSensors
    }
    
    private fun getSensorBySensorType(sensorType: SensorType): Sensor? {
        return when (sensorType) {
            SensorType.ACCELEROMETER -> sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            SensorType.GYROSCOPE -> sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            SensorType.MAGNETOMETER -> sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            SensorType.GRAVITY -> sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            SensorType.LINEAR_ACCELERATION -> sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            SensorType.ROTATION_VECTOR -> sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            SensorType.ORIENTATION -> sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
            SensorType.PRESSURE -> sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
            SensorType.TEMPERATURE -> sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            SensorType.HUMIDITY -> sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
            SensorType.LIGHT -> sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            SensorType.PROXIMITY -> sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            SensorType.STEP_COUNTER -> sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            SensorType.HEART_RATE -> sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        }
    }
    
    fun getSensorInfo(sensorType: SensorType): String {
        val sensor = getSensorBySensorType(sensorType)
        return if (sensor != null) {
            "Name: ${sensor.name}\n" +
            "Vendor: ${sensor.vendor}\n" +
            "Version: ${sensor.version}\n" +
            "Type: ${sensor.type}\n" +
            "Max Range: ${sensor.maximumRange}\n" +
            "Resolution: ${sensor.resolution}\n" +
            "Power: ${sensor.power} mA\n" +
            "Min Delay: ${sensor.minDelay} μs"
        } else {
            "Sensor not available"
        }
    }
}