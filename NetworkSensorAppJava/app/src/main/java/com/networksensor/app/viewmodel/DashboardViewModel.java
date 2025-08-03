package com.networksensor.app.viewmodel;

import android.content.Context;
import android.util.Log;

import com.networksensor.app.model.NetworkInfo;
import com.networksensor.app.model.SensorData;
import com.networksensor.app.repository.NetworkRepository;
import com.networksensor.app.repository.SensorRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Observable;

public class DashboardViewModel {
    private static final String TAG = "DashboardViewModel";
    
    private final NetworkRepository networkRepository;
    private final SensorRepository sensorRepository;
    private final AtomicBoolean isSpeedTestRunning = new AtomicBoolean(false);

    public DashboardViewModel(Context context) {
        this.networkRepository = new NetworkRepository(context);
        this.sensorRepository = new SensorRepository(context);
    }

    public Observable<NetworkInfo> getCurrentNetworkInfo() {
        return networkRepository.getCurrentNetworkInfo();
    }

    public Observable<NetworkRepository.SpeedTestResult> performSpeedTest() {
        if (isSpeedTestRunning.get()) {
            return Observable.empty();
        }
        
        isSpeedTestRunning.set(true);
        return networkRepository.performSpeedTest()
                .doFinally(() -> isSpeedTestRunning.set(false));
    }

    public Observable<List<SensorData.SensorType>> getAvailableSensors() {
        return Observable.fromCallable(() -> sensorRepository.getAvailableSensors());
    }

    public Observable<SensorData> getSensorData(SensorData.SensorType sensorType) {
        return sensorRepository.getSensorData(sensorType);
    }

    public Observable<SensorRepository.EnvironmentalData> getEnvironmentalData() {
        return sensorRepository.getEnvironmentalData();
    }

    public Observable<SensorRepository.MotionData> getMotionData() {
        return sensorRepository.getMotionData();
    }

    public boolean isSpeedTestRunning() {
        return isSpeedTestRunning.get();
    }

    public void cleanup() {
        try {
            networkRepository.cleanup();
            sensorRepository.cleanup();
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }
}