package com.networksensor.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NetworkInfo implements Parcelable {
    private ConnectionType connectionType;
    private String networkName;
    private int signalStrength;
    private double downloadSpeed; // Mbps
    private double uploadSpeed; // Mbps
    private long latency; // ms
    private long timestamp;

    public NetworkInfo() {
        this.timestamp = System.currentTimeMillis();
    }

    public NetworkInfo(ConnectionType connectionType, String networkName, int signalStrength,
                      double downloadSpeed, double uploadSpeed, long latency) {
        this.connectionType = connectionType;
        this.networkName = networkName;
        this.signalStrength = signalStrength;
        this.downloadSpeed = downloadSpeed;
        this.uploadSpeed = uploadSpeed;
        this.latency = latency;
        this.timestamp = System.currentTimeMillis();
    }

    protected NetworkInfo(Parcel in) {
        connectionType = ConnectionType.valueOf(in.readString());
        networkName = in.readString();
        signalStrength = in.readInt();
        downloadSpeed = in.readDouble();
        uploadSpeed = in.readDouble();
        latency = in.readLong();
        timestamp = in.readLong();
    }

    public static final Creator<NetworkInfo> CREATOR = new Creator<NetworkInfo>() {
        @Override
        public NetworkInfo createFromParcel(Parcel in) {
            return new NetworkInfo(in);
        }

        @Override
        public NetworkInfo[] newArray(int size) {
            return new NetworkInfo[size];
        }
    };

    // Getters and Setters
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    public double getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(double downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public double getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(double uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
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
        dest.writeString(connectionType.name());
        dest.writeString(networkName);
        dest.writeInt(signalStrength);
        dest.writeDouble(downloadSpeed);
        dest.writeDouble(uploadSpeed);
        dest.writeLong(latency);
        dest.writeLong(timestamp);
    }

    public enum ConnectionType {
        WIFI,
        MOBILE_LTE,
        MOBILE_4G,
        MOBILE_3G,
        MOBILE_2G,
        MOBILE_5G,
        ETHERNET,
        UNKNOWN
    }
}