package com.networksensor.app;

import android.app.Application;

public class NetworkSensorApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize application-wide components here
        // This is where you would initialize crash reporting, analytics, etc.
    }
}