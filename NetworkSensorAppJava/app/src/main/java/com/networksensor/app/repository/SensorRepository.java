package com.networksensor.app.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.networksensor.app.model.LocationData;
import com.networksensor.app.model.SensorData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class SensorRepository {
    private static final String TAG = "SensorRepository";
    
    private final Context context;
    private final SensorManager sensorManager;
    private final LocationManager locationManager;
    private final ExecutorService executorService;
    
    private final Map<SensorData.SensorType, BehaviorSubject<SensorData>> sensorSubjects;
    private final Map<SensorData.SensorType, SensorEventListener> sensorListeners;
    private final BehaviorSubject<LocationData> locationSubject;
    private LocationListener locationListener;

    public SensorRepository(Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.executorService = Executors.newFixedThreadPool(4);
        
        this.sensorSubjects = new HashMap<>();
        this.sensorListeners = new HashMap<>();
        this.locationSubject = BehaviorSubject.create();
        
        initializeSensors();
    }

    private void initializeSensors() {
        // Initialize subjects for all sensor types
        for (SensorData.SensorType sensorType : SensorData.SensorType.values()) {
            sensorSubjects.put(sensorType, BehaviorSubject.create());
        }
    }

    public Observable<SensorData> getSensorData(SensorData.SensorType sensorType) {
        BehaviorSubject<SensorData> subject = sensorSubjects.get(sensorType);
        if (subject == null) {
            return Observable.empty();
        }

        // Start listening to the sensor if not already listening
        if (!sensorListeners.containsKey(sensorType)) {
            startSensorListening(sensorType);
        }

        return subject.subscribeOn(Schedulers.io());
    }

    private void startSensorListening(SensorData.SensorType sensorType) {
        Sensor sensor = getSensorByType(sensorType);
        if (sensor == null) {
            Log.w(TAG, "Sensor not available: " + sensorType);
            return;
        }

        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                SensorData sensorData = new SensorData(sensorType, event.values.clone(), event.accuracy);
                BehaviorSubject<SensorData> subject = sensorSubjects.get(sensorType);
                if (subject != null) {
                    subject.onNext(sensorData);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle accuracy changes if needed
                Log.d(TAG, "Sensor accuracy changed: " + sensor.getName() + " accuracy: " + accuracy);
            }
        };

        sensorListeners.put(sensorType, listener);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Started listening to sensor: " + sensorType);
    }

    private Sensor getSensorByType(SensorData.SensorType sensorType) {
        switch (sensorType) {
            case ACCELEROMETER:
                return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            case GYROSCOPE:
                return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            case MAGNETOMETER:
                return sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            case GRAVITY:
                return sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            case LINEAR_ACCELERATION:
                return sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            case ROTATION_VECTOR:
                return sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            case ORIENTATION:
                return sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            case PRESSURE:
                return sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            case TEMPERATURE:
                return sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            case HUMIDITY:
                return sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            case LIGHT:
                return sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            case PROXIMITY:
                return sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            case STEP_COUNTER:
                return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            case HEART_RATE:
                return sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            default:
                return null;
        }
    }

    @SuppressLint("MissingPermission")
    public Observable<LocationData> getLocationData() {
        if (locationListener == null) {
            startLocationListening();
        }
        return locationSubject.subscribeOn(Schedulers.io());
    }

    @SuppressLint("MissingPermission")
    private void startLocationListening() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationData locationData = new LocationData(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAltitude(),
                    location.getAccuracy(),
                    location.getSpeed(),
                    location.getBearing(),
                    location.getProvider() != null ? location.getProvider() : "Unknown"
                );
                locationSubject.onNext(locationData);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "Location provider status changed: " + provider + " status: " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Location provider enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Location provider disabled: " + provider);
            }
        };

        try {
            // Try GPS first
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L, // 1 second
                    1f, // 1 meter
                    locationListener
                );
                Log.d(TAG, "Started GPS location updates");
            }

            // Also try network provider
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    1f,
                    locationListener
                );
                Log.d(TAG, "Started network location updates");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted", e);
        }
    }

    public List<SensorData.SensorType> getAvailableSensors() {
        List<SensorData.SensorType> availableSensors = new ArrayList<>();
        
        for (SensorData.SensorType sensorType : SensorData.SensorType.values()) {
            Sensor sensor = getSensorByType(sensorType);
            if (sensor != null) {
                availableSensors.add(sensorType);
            }
        }
        
        return availableSensors;
    }

    public String getSensorInfo(SensorData.SensorType sensorType) {
        Sensor sensor = getSensorByType(sensorType);
        if (sensor != null) {
            return "Name: " + sensor.getName() + "\n" +
                   "Vendor: " + sensor.getVendor() + "\n" +
                   "Version: " + sensor.getVersion() + "\n" +
                   "Type: " + sensor.getType() + "\n" +
                   "Max Range: " + sensor.getMaximumRange() + "\n" +
                   "Resolution: " + sensor.getResolution() + "\n" +
                   "Power: " + sensor.getPower() + " mA\n" +
                   "Min Delay: " + sensor.getMinDelay() + " μs";
        } else {
            return "Sensor not available";
        }
    }

    public Observable<EnvironmentalData> getEnvironmentalData() {
        return Observable.combineLatest(
            getSensorData(SensorData.SensorType.TEMPERATURE).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.HUMIDITY).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.PRESSURE).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.LIGHT).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.PROXIMITY).startWith(new SensorData()),
            (temp, humidity, pressure, light, proximity) -> {
                EnvironmentalData envData = new EnvironmentalData();
                
                if (temp.getValues().length > 0) {
                    envData.temperature = temp.getValues()[0];
                }
                if (humidity.getValues().length > 0) {
                    envData.humidity = humidity.getValues()[0];
                }
                if (pressure.getValues().length > 0) {
                    envData.pressure = pressure.getValues()[0];
                }
                if (light.getValues().length > 0) {
                    envData.lightLevel = light.getValues()[0];
                }
                if (proximity.getValues().length > 0) {
                    envData.proximity = proximity.getValues()[0];
                }
                
                return envData;
            }
        ).subscribeOn(Schedulers.io());
    }

    public Observable<MotionData> getMotionData() {
        return Observable.combineLatest(
            getSensorData(SensorData.SensorType.ACCELEROMETER).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.GYROSCOPE).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.MAGNETOMETER).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.GRAVITY).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.LINEAR_ACCELERATION).startWith(new SensorData()),
            getSensorData(SensorData.SensorType.ROTATION_VECTOR).startWith(new SensorData()),
            (accel, gyro, mag, gravity, linearAccel, rotation) -> {
                MotionData motionData = new MotionData();
                
                motionData.accelerometer = accel.getValues();
                motionData.gyroscope = gyro.getValues();
                motionData.magnetometer = mag.getValues();
                motionData.gravity = gravity.getValues();
                motionData.linearAcceleration = linearAccel.getValues();
                motionData.rotationVector = rotation.getValues();
                
                return motionData;
            }
        ).subscribeOn(Schedulers.io());
    }

    public void stopSensorListening(SensorData.SensorType sensorType) {
        SensorEventListener listener = sensorListeners.get(sensorType);
        if (listener != null) {
            sensorManager.unregisterListener(listener);
            sensorListeners.remove(sensorType);
            Log.d(TAG, "Stopped listening to sensor: " + sensorType);
        }
    }

    public void stopAllSensors() {
        // Stop all sensor listeners
        for (SensorEventListener listener : sensorListeners.values()) {
            sensorManager.unregisterListener(listener);
        }
        sensorListeners.clear();
        
        // Stop location listener
        if (locationListener != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                Log.e(TAG, "Error stopping location updates", e);
            }
            locationListener = null;
        }
        
        Log.d(TAG, "Stopped all sensor listening");
    }

    public void cleanup() {
        stopAllSensors();
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // Inner classes for combined sensor data
    public static class EnvironmentalData {
        public Float temperature;
        public Float humidity;
        public Float pressure;
        public Float lightLevel;
        public Float proximity;
        public long timestamp = System.currentTimeMillis();
    }

    public static class MotionData {
        public float[] accelerometer = new float[3];
        public float[] gyroscope = new float[3];
        public float[] magnetometer = new float[3];
        public float[] gravity = new float[3];
        public float[] linearAcceleration = new float[3];
        public float[] rotationVector = new float[4];
        public long timestamp = System.currentTimeMillis();
    }
}