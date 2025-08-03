package com.networksensor.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.networksensor.app.R;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // For now, return a simple text view
        android.widget.TextView textView = new android.widget.TextView(getContext());
        textView.setText("Settings\n\n" +
                "App Information:\n" +
                "• Version: 1.0.0\n" +
                "• Build: Debug\n" +
                "• Target SDK: 35\n\n" +
                "Features:\n" +
                "✓ Network Speed Testing (WiFi, LTE, GPRS)\n" +
                "✓ Real-time Sensor Monitoring\n" +
                "✓ GPS Location Tracking\n" +
                "✓ Environmental Sensors\n" +
                "✓ Motion Detection\n" +
                "✓ Material Design 3 UI\n\n" +
                "Permissions:\n" +
                "• Internet Access - For speed testing\n" +
                "• Location Access - For GPS tracking\n" +
                "• Network State - For connection monitoring\n" +
                "• Sensor Access - For device sensors\n" +
                "• Phone State - For cellular info");
        textView.setPadding(48, 48, 48, 48);
        textView.setTextSize(16);
        return textView;
    }
}