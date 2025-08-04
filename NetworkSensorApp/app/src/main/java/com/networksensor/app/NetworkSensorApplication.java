package com.networksensor.app;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class NetworkSensorApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize any app-wide configurations here
    }
}