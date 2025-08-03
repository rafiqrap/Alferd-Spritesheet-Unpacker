package com.networksensor.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.networksensor.app.R;

public class SensorFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // For now, return a simple text view
        android.widget.TextView textView = new android.widget.TextView(getContext());
        textView.setText("Sensor Monitor\n\nReal-time sensor data will be displayed here including:\n\n• Accelerometer readings\n• Gyroscope data\n• GPS location tracking\n• Environmental sensors\n• Motion detection\n• Temperature & humidity\n• Light & proximity sensors");
        textView.setPadding(48, 48, 48, 48);
        textView.setTextSize(16);
        return textView;
    }
}