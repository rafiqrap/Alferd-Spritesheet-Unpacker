package com.networksensor.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.networksensor.app.R;

public class NetworkFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // For now, return a simple text view
        android.widget.TextView textView = new android.widget.TextView(getContext());
        textView.setText("Network Analysis\n\nDetailed network monitoring features will be displayed here including:\n\n• Real-time speed testing\n• Connection details\n• WiFi information\n• Cellular network data\n• Signal strength monitoring");
        textView.setPadding(48, 48, 48, 48);
        textView.setTextSize(16);
        return textView;
    }
}