package com.networksensor.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class SensorData implements Parcelable {
    private SensorType sensorType;
    private float[] values;
    private int accuracy;
    private long timestamp;

    public SensorData() {
        this.timestamp = System.currentTimeMillis();
    }

    public SensorData(SensorType sensorType, float[] values, int accuracy) {
        this.sensorType = sensorType;
        this.values = values != null ? values.clone() : new float[0];
        this.accuracy = accuracy;
        this.timestamp = System.currentTimeMillis();
    }

    protected SensorData(Parcel in) {
        sensorType = SensorType.valueOf(in.readString());
        values = in.createFloatArray();
        accuracy = in.readInt();
        timestamp = in.readLong();
    }

    public static final Creator<SensorData> CREATOR = new Creator<SensorData>() {
        @Override
        public SensorData createFromParcel(Parcel in) {
            return new SensorData(in);
        }

        @Override
        public SensorData[] newArray(int size) {
            return new SensorData[size];
        }
    };

    // Getters and Setters
    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public float[] getValues() {
        return values != null ? values.clone() : new float[0];
    }

    public void setValues(float[] values) {
        this.values = values != null ? values.clone() : new float[0];
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sensorType.name());
        dest.writeFloatArray(values);
        dest.writeInt(accuracy);
        dest.writeLong(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorData that = (SensorData) o;
        return accuracy == that.accuracy &&
                timestamp == that.timestamp &&
                sensorType == that.sensorType &&
                Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        int result = sensorType != null ? sensorType.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(values);
        result = 31 * result + accuracy;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    public enum SensorType {
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
}

// Location Data class
class LocationData implements Parcelable {
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;
    private float speed;
    private float bearing;
    private String provider;
    private long timestamp;

    public LocationData() {
        this.timestamp = System.currentTimeMillis();
    }

    public LocationData(double latitude, double longitude, double altitude, float accuracy,
                       float speed, float bearing, String provider) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.bearing = bearing;
        this.provider = provider;
        this.timestamp = System.currentTimeMillis();
    }

    protected LocationData(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        accuracy = in.readFloat();
        speed = in.readFloat();
        bearing = in.readFloat();
        provider = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<LocationData> CREATOR = new Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel in) {
            return new LocationData(in);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };

    // Getters and Setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public double getAltitude() { return altitude; }
    public void setAltitude(double altitude) { this.altitude = altitude; }
    
    public float getAccuracy() { return accuracy; }
    public void setAccuracy(float accuracy) { this.accuracy = accuracy; }
    
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    
    public float getBearing() { return bearing; }
    public void setBearing(float bearing) { this.bearing = bearing; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeFloat(accuracy);
        dest.writeFloat(speed);
        dest.writeFloat(bearing);
        dest.writeString(provider);
        dest.writeLong(timestamp);
    }
}