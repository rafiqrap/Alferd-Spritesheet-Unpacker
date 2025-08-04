package com.networksensor.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkMonitorActivity extends AppCompatActivity {

    private TextView networkStatusText;
    private TextView connectionTypeText;
    private TextView speedText;
    private TextView signalStrengthText;
    private TextView ipAddressText;
    private TextView ssidText;
    private ProgressBar signalProgressBar;
    private Button speedTestButton;
    private ChipGroup networkChips;
    private MaterialCardView networkCard;
    private MaterialCardView wifiCard;
    private MaterialCardView cellularCard;
    
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;
    private ExecutorService executorService;
    private Handler mainHandler;
    private boolean isSpeedTestRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_monitor);
        
        initializeViews();
        initializeManagers();
        setupClickListeners();
        startNetworkMonitoring();
    }

    private void initializeViews() {
        networkStatusText = findViewById(R.id.networkStatusText);
        connectionTypeText = findViewById(R.id.connectionTypeText);
        speedText = findViewById(R.id.speedText);
        signalStrengthText = findViewById(R.id.signalStrengthText);
        ipAddressText = findViewById(R.id.ipAddressText);
        ssidText = findViewById(R.id.ssidText);
        signalProgressBar = findViewById(R.id.signalProgressBar);
        speedTestButton = findViewById(R.id.speedTestButton);
        networkChips = findViewById(R.id.networkChips);
        networkCard = findViewById(R.id.networkCard);
        wifiCard = findViewById(R.id.wifiCard);
        cellularCard = findViewById(R.id.cellularCard);
    }

    private void initializeManagers() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void setupClickListeners() {
        speedTestButton.setOnClickListener(v -> {
            if (!isSpeedTestRunning) {
                startSpeedTest();
            }
        });
    }

    private void startNetworkMonitoring() {
        // Update network info every 2 seconds
        Runnable networkMonitor = new Runnable() {
            @Override
            public void run() {
                updateNetworkInformation();
                mainHandler.postDelayed(this, 2000);
            }
        };
        mainHandler.post(networkMonitor);
    }

    private void updateNetworkInformation() {
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (capabilities != null) {
                updateConnectionType(capabilities);
                updateNetworkStatus(capabilities);
                updateSignalStrength();
                updateNetworkDetails();
            }
        } else {
            setNetworkDisconnected();
        }
    }

    private void updateConnectionType(NetworkCapabilities capabilities) {
        String connectionType = "Unknown";
        int colorResId = R.color.text_secondary_light;
        
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            connectionType = "WiFi";
            colorResId = R.color.network_excellent;
            showWifiCard();
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            connectionType = getCellularGeneration();
            colorResId = R.color.network_good;
            showCellularCard();
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            connectionType = "Ethernet";
            colorResId = R.color.network_excellent;
            showNetworkCard();
        }
        
        connectionTypeText.setText(connectionType);
        connectionTypeText.setTextColor(ContextCompat.getColor(this, colorResId));
    }

    private String getCellularGeneration() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            int dataNetworkType = telephonyManager.getDataNetworkType();
            switch (dataNetworkType) {
                case TelephonyManager.NETWORK_TYPE_NR:
                    return "5G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G LTE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "2G";
                default:
                    return "Cellular";
            }
        }
        return "Cellular";
    }

    private void updateNetworkStatus(NetworkCapabilities capabilities) {
        String status = "Poor";
        int colorResId = R.color.network_poor;
        int progressValue = 25;
        
        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                status = "Excellent";
                colorResId = R.color.network_excellent;
                progressValue = 100;
            } else {
                status = "Good";
                colorResId = R.color.network_good;
                progressValue = 75;
            }
        }
        
        networkStatusText.setText(status);
        networkStatusText.setTextColor(ContextCompat.getColor(this, colorResId));
        signalProgressBar.setProgress(progressValue);
    }

    private void updateSignalStrength() {
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
                int rssi = wifiInfo.getRssi();
                int level = WifiManager.calculateSignalLevel(rssi, 5);
                int percentage = (level * 100) / 4;
                
                signalStrengthText.setText(percentage + "%");
                signalProgressBar.setProgress(percentage);
                
                int colorResId;
                if (percentage >= 80) {
                    colorResId = R.color.network_excellent;
                } else if (percentage >= 60) {
                    colorResId = R.color.network_good;
                } else if (percentage >= 40) {
                    colorResId = R.color.network_fair;
                } else {
                    colorResId = R.color.network_poor;
                }
                signalStrengthText.setTextColor(ContextCompat.getColor(this, colorResId));
            }
        }
    }

    private void updateNetworkDetails() {
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
                String ssid = wifiInfo.getSSID();
                if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                ssidText.setText(ssid);
                
                int ipAddress = wifiInfo.getIpAddress();
                String ip = String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff));
                ipAddressText.setText(ip);
            }
        }
    }

    private void setNetworkDisconnected() {
        networkStatusText.setText("Disconnected");
        networkStatusText.setTextColor(ContextCompat.getColor(this, R.color.network_poor));
        connectionTypeText.setText("No Connection");
        connectionTypeText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_light));
        signalStrengthText.setText("0%");
        signalProgressBar.setProgress(0);
    }

    private void showWifiCard() {
        wifiCard.setVisibility(View.VISIBLE);
        cellularCard.setVisibility(View.GONE);
        networkCard.setVisibility(View.GONE);
    }

    private void showCellularCard() {
        wifiCard.setVisibility(View.GONE);
        cellularCard.setVisibility(View.VISIBLE);
        networkCard.setVisibility(View.GONE);
    }

    private void showNetworkCard() {
        wifiCard.setVisibility(View.GONE);
        cellularCard.setVisibility(View.GONE);
        networkCard.setVisibility(View.VISIBLE);
    }

    private void startSpeedTest() {
        isSpeedTestRunning = true;
        speedTestButton.setText("Testing...");
        speedTestButton.setEnabled(false);
        
        executorService.execute(() -> {
            // Simulate speed test
            try {
                Thread.sleep(3000);
                
                // Simulate download speed (1-100 Mbps)
                double downloadSpeed = Math.random() * 99 + 1;
                DecimalFormat df = new DecimalFormat("#.#");
                
                mainHandler.post(() -> {
                    speedText.setText(df.format(downloadSpeed) + " Mbps");
                    speedTestButton.setText("Test Speed");
                    speedTestButton.setEnabled(true);
                    isSpeedTestRunning = false;
                    
                    Toast.makeText(NetworkMonitorActivity.this, 
                            "Speed test completed!", Toast.LENGTH_SHORT).show();
                });
            } catch (InterruptedException e) {
                mainHandler.post(() -> {
                    speedTestButton.setText("Test Speed");
                    speedTestButton.setEnabled(true);
                    isSpeedTestRunning = false;
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}