package com.networksensor.app.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.networksensor.app.R;
import com.networksensor.app.adapter.QuickStatsAdapter;
import com.networksensor.app.adapter.RecentActivityAdapter;
import com.networksensor.app.adapter.SensorStatusAdapter;
import com.networksensor.app.databinding.FragmentDashboardBinding;
import com.networksensor.app.model.NetworkInfo;
import com.networksensor.app.repository.NetworkRepository;
import com.networksensor.app.repository.SensorRepository;
import com.networksensor.app.viewmodel.DashboardViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private CompositeDisposable disposables = new CompositeDisposable();
    
    private QuickStatsAdapter quickStatsAdapter;
    private SensorStatusAdapter sensorStatusAdapter;
    private RecentActivityAdapter recentActivityAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViewModel();
        setupRecyclerViews();
        setupClickListeners();
        loadData();
    }

    private void initializeViewModel() {
        viewModel = new DashboardViewModel(requireContext());
    }

    private void setupRecyclerViews() {
        // Quick Stats RecyclerView
        quickStatsAdapter = new QuickStatsAdapter();
        binding.recyclerQuickStats.setLayoutManager(
            new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.recyclerQuickStats.setAdapter(quickStatsAdapter);

        // Sensor Status RecyclerView
        sensorStatusAdapter = new SensorStatusAdapter();
        binding.recyclerSensorStatus.setLayoutManager(
            new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        );
        binding.recyclerSensorStatus.setAdapter(sensorStatusAdapter);

        // Recent Activity RecyclerView
        recentActivityAdapter = new RecentActivityAdapter();
        binding.recyclerRecentActivity.setLayoutManager(
            new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        );
        binding.recyclerRecentActivity.setAdapter(recentActivityAdapter);
    }

    private void setupClickListeners() {
        binding.buttonSpeedTest.setOnClickListener(v -> {
            if (!viewModel.isSpeedTestRunning()) {
                startSpeedTest();
            }
        });

        binding.buttonRefresh.setOnClickListener(v -> {
            loadData();
        });
    }

    private void loadData() {
        loadNetworkInfo();
        loadSensorStatus();
        loadQuickStats();
        loadRecentActivity();
    }

    private void loadNetworkInfo() {
        disposables.add(
            viewModel.getCurrentNetworkInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::updateNetworkInfo,
                    throwable -> {
                        Log.e(TAG, "Error loading network info", throwable);
                        showError(getString(R.string.error_network));
                    }
                )
        );
    }

    private void updateNetworkInfo(NetworkInfo networkInfo) {
        if (binding == null) return;

        // Update network status card
        binding.textNetworkName.setText(networkInfo.getNetworkName() != null ? 
            networkInfo.getNetworkName() : getString(R.string.unknown));
        binding.textConnectionType.setText(networkInfo.getConnectionType().name());
        
        // Update speed metrics
        binding.textDownloadSpeed.setText(
            String.format(Locale.getDefault(), "%.1f %s", 
                networkInfo.getDownloadSpeed(), getString(R.string.mbps))
        );
        binding.textUploadSpeed.setText(
            String.format(Locale.getDefault(), "%.1f %s", 
                networkInfo.getUploadSpeed(), getString(R.string.mbps))
        );
        binding.textLatency.setText(
            String.format(Locale.getDefault(), "%d %s", 
                networkInfo.getLatency(), getString(R.string.ms))
        );
        
        // Update signal strength
        updateSignalStrength(networkInfo.getSignalStrength());
    }

    private void updateSignalStrength(int signalStrength) {
        // Update signal strength indicator
        binding.progressSignalStrength.setProgress(signalStrength * 20); // Convert 0-5 to 0-100
        binding.textSignalStrength.setText(
            String.format(Locale.getDefault(), "%d/5", signalStrength)
        );
    }

    private void loadSensorStatus() {
        disposables.add(
            viewModel.getAvailableSensors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    sensorTypes -> {
                        List<SensorStatusItem> statusItems = new ArrayList<>();
                        for (var sensorType : sensorTypes) {
                            statusItems.add(new SensorStatusItem(
                                sensorType.name().replace("_", " "),
                                true, // Assume available sensors are active
                                getSensorIcon(sensorType)
                            ));
                        }
                        sensorStatusAdapter.updateData(statusItems);
                        
                        // Update sensor count
                        binding.textSensorCount.setText(String.valueOf(statusItems.size()));
                    },
                    throwable -> {
                        Log.e(TAG, "Error loading sensor status", throwable);
                        showError(getString(R.string.error_sensor));
                    }
                )
        );
    }

    private void loadQuickStats() {
        List<QuickStatItem> quickStats = new ArrayList<>();
        quickStats.add(new QuickStatItem("Signal", "4/5", R.drawable.ic_signal_24));
        quickStats.add(new QuickStatItem("Speed", "25.3M", R.drawable.ic_speed_24));
        quickStats.add(new QuickStatItem("Sensors", "12", R.drawable.ic_sensors_24));
        quickStats.add(new QuickStatItem("Uptime", "24h", R.drawable.ic_schedule_24));
        
        quickStatsAdapter.updateData(quickStats);
    }

    private void loadRecentActivity() {
        List<RecentActivityItem> activities = new ArrayList<>();
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        
        activities.add(new RecentActivityItem(
            "Network Connected", 
            "WiFi - Home Network", 
            currentTime,
            R.drawable.ic_wifi_24
        ));
        activities.add(new RecentActivityItem(
            "Speed Test Completed", 
            "↓25.3Mbps ↑12.1Mbps", 
            currentTime,
            R.drawable.ic_speed_24
        ));
        activities.add(new RecentActivityItem(
            "Location Update", 
            "GPS accuracy: 3m", 
            currentTime,
            R.drawable.ic_location_24
        ));
        
        recentActivityAdapter.updateData(activities);
    }

    private void startSpeedTest() {
        binding.buttonSpeedTest.setEnabled(false);
        binding.buttonSpeedTest.setText(getString(R.string.running_speed_test));
        binding.progressSpeedTest.setVisibility(View.VISIBLE);
        
        disposables.add(
            viewModel.performSpeedTest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    speedTestResult -> {
                        // Update UI with speed test results
                        binding.textDownloadSpeed.setText(
                            String.format(Locale.getDefault(), "%.1f %s", 
                                speedTestResult.downloadSpeed, getString(R.string.mbps))
                        );
                        binding.textUploadSpeed.setText(
                            String.format(Locale.getDefault(), "%.1f %s", 
                                speedTestResult.uploadSpeed, getString(R.string.mbps))
                        );
                        binding.textLatency.setText(
                            String.format(Locale.getDefault(), "%d %s", 
                                speedTestResult.latency, getString(R.string.ms))
                        );
                        
                        resetSpeedTestButton();
                        
                        // Add to recent activity
                        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        recentActivityAdapter.addActivity(new RecentActivityItem(
                            "Speed Test Completed",
                            String.format(Locale.getDefault(), "↓%.1fMbps ↑%.1fMbps", 
                                speedTestResult.downloadSpeed, speedTestResult.uploadSpeed),
                            time,
                            R.drawable.ic_speed_24
                        ));
                    },
                    throwable -> {
                        Log.e(TAG, "Speed test failed", throwable);
                        showError("Speed test failed");
                        resetSpeedTestButton();
                    }
                )
        );
    }

    private void resetSpeedTestButton() {
        if (binding != null) {
            binding.buttonSpeedTest.setEnabled(true);
            binding.buttonSpeedTest.setText(getString(R.string.run_speed_test));
            binding.progressSpeedTest.setVisibility(View.GONE);
        }
    }

    private int getSensorIcon(com.networksensor.app.model.SensorData.SensorType sensorType) {
        switch (sensorType) {
            case ACCELEROMETER:
            case GYROSCOPE:
            case MAGNETOMETER:
                return R.drawable.ic_motion_24;
            case TEMPERATURE:
                return R.drawable.ic_thermostat_24;
            case HUMIDITY:
                return R.drawable.ic_humidity_24;
            case PRESSURE:
                return R.drawable.ic_pressure_24;
            case LIGHT:
                return R.drawable.ic_light_24;
            case PROXIMITY:
                return R.drawable.ic_proximity_24;
            default:
                return R.drawable.ic_sensors_24;
        }
    }

    private void showError(String message) {
        if (binding != null) {
            binding.textError.setText(message);
            binding.textError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        if (viewModel != null) {
            viewModel.cleanup();
        }
        binding = null;
    }

    // Data classes for adapters
    public static class QuickStatItem {
        public final String label;
        public final String value;
        public final int iconRes;

        public QuickStatItem(String label, String value, int iconRes) {
            this.label = label;
            this.value = value;
            this.iconRes = iconRes;
        }
    }

    public static class SensorStatusItem {
        public final String name;
        public final boolean isActive;
        public final int iconRes;

        public SensorStatusItem(String name, boolean isActive, int iconRes) {
            this.name = name;
            this.isActive = isActive;
            this.iconRes = iconRes;
        }
    }

    public static class RecentActivityItem {
        public final String title;
        public final String description;
        public final String timestamp;
        public final int iconRes;

        public RecentActivityItem(String title, String description, String timestamp, int iconRes) {
            this.title = title;
            this.description = description;
            this.timestamp = timestamp;
            this.iconRes = iconRes;
        }
    }
}