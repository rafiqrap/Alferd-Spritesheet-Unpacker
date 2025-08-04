package com.networksensor.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SensorHubActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView accelerometerText;
    private TextView gyroscopeText;
    private TextView magnetometerText;
    private TextView temperatureText;
    private TextView humidityText;
    private TextView pressureText;
    private MaterialCardView accelerometerCard;
    private MaterialCardView gyroscopeCard;
    private MaterialCardView magnetometerCard;
    private MaterialCardView temperatureCard;
    private MaterialCardView humidityCard;
    private MaterialCardView pressureCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_hub);
        
        initializeViews();
        initializeSensors();
    }

    private void initializeViews() {
        accelerometerText = findViewById(R.id.accelerometerText);
        gyroscopeText = findViewById(R.id.gyroscopeText);
        magnetometerText = findViewById(R.id.magnetometerText);
        temperatureText = findViewById(R.id.temperatureText);
        humidityText = findViewById(R.id.humidityText);
        pressureText = findViewById(R.id.pressureText);
        
        accelerometerCard = findViewById(R.id.accelerometerCard);
        gyroscopeCard = findViewById(R.id.gyroscopeCard);
        magnetometerCard = findViewById(R.id.magnetometerCard);
        temperatureCard = findViewById(R.id.temperatureCard);
        humidityCard = findViewById(R.id.humidityCard);
        pressureCard = findViewById(R.id.pressureCard);
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        if (sensorManager != null) {
            // Register accelerometer
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                accelerometerCard.setVisibility(android.view.View.VISIBLE);
            } else {
                accelerometerCard.setVisibility(android.view.View.GONE);
            }
            
            // Register gyroscope
            Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                gyroscopeCard.setVisibility(android.view.View.VISIBLE);
            } else {
                gyroscopeCard.setVisibility(android.view.View.GONE);
            }
            
            // Register magnetometer
            Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (magnetometer != null) {
                sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
                magnetometerCard.setVisibility(android.view.View.VISIBLE);
            } else {
                magnetometerCard.setVisibility(android.view.View.GONE);
            }
            
            // Register temperature sensor
            Sensor temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            if (temperature != null) {
                sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
                temperatureCard.setVisibility(android.view.View.VISIBLE);
            } else {
                temperatureCard.setVisibility(android.view.View.GONE);
            }
            
            // Register humidity sensor
            Sensor humidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            if (humidity != null) {
                sensorManager.registerListener(this, humidity, SensorManager.SENSOR_DELAY_NORMAL);
                humidityCard.setVisibility(android.view.View.VISIBLE);
            } else {
                humidityCard.setVisibility(android.view.View.GONE);
            }
            
            // Register pressure sensor
            Sensor pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if (pressure != null) {
                sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
                pressureCard.setVisibility(android.view.View.VISIBLE);
            } else {
                pressureCard.setVisibility(android.view.View.GONE);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                accelerometerText.setText(String.format("X: %.2f, Y: %.2f, Z: %.2f m/s²", x, y, z));
                break;
                
            case Sensor.TYPE_GYROSCOPE:
                float gx = event.values[0];
                float gy = event.values[1];
                float gz = event.values[2];
                gyroscopeText.setText(String.format("X: %.2f, Y: %.2f, Z: %.2f rad/s", gx, gy, gz));
                break;
                
            case Sensor.TYPE_MAGNETIC_FIELD:
                float mx = event.values[0];
                float my = event.values[1];
                float mz = event.values[2];
                magnetometerText.setText(String.format("X: %.2f, Y: %.2f, Z: %.2f μT", mx, my, mz));
                break;
                
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                float temp = event.values[0];
                temperatureText.setText(String.format("%.1f°C", temp));
                break;
                
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                float hum = event.values[0];
                humidityText.setText(String.format("%.1f%%", hum));
                break;
                
            case Sensor.TYPE_PRESSURE:
                float press = event.values[0];
                pressureText.setText(String.format("%.1f hPa", press));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}