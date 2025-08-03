package com.networksensor.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NetworkSensorApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}