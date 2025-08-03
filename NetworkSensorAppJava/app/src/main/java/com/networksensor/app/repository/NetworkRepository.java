package com.networksensor.app.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.networksensor.app.model.NetworkInfo;
import com.networksensor.app.model.NetworkInfo.ConnectionType;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkRepository {
    private static final String TAG = "NetworkRepository";
    private final Context context;
    private final OkHttpClient okHttpClient;
    private final ConnectivityManager connectivityManager;
    private final WifiManager wifiManager;
    private final TelephonyManager telephonyManager;
    private final ExecutorService executorService;

    public NetworkRepository(Context context) {
        this.context = context;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public Observable<NetworkInfo> getCurrentNetworkInfo() {
        return Observable.fromCallable(this::getCurrentNetworkInfoSync)
                .subscribeOn(Schedulers.io());
    }

    @SuppressLint("MissingPermission")
    private NetworkInfo getCurrentNetworkInfoSync() {
        try {
            android.net.Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            
            ConnectionType connectionType = determineConnectionType(networkCapabilities);
            String networkName = getNetworkName(connectionType);
            int signalStrength = getSignalStrength(connectionType);
            
            // Perform quick speed test
            SpeedTestResult speedTest = performQuickSpeedTest();
            
            return new NetworkInfo(
                connectionType,
                networkName,
                signalStrength,
                speedTest.downloadSpeed,
                speedTest.uploadSpeed,
                speedTest.latency
            );
        } catch (Exception e) {
            Log.e(TAG, "Error getting network info", e);
            return new NetworkInfo(ConnectionType.UNKNOWN, "Unknown", 0, 0.0, 0.0, 0L);
        }
    }

    private ConnectionType determineConnectionType(NetworkCapabilities networkCapabilities) {
        if (networkCapabilities == null) {
            return ConnectionType.UNKNOWN;
        }

        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return ConnectionType.WIFI;
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return getCellularConnectionType();
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return ConnectionType.ETHERNET;
        }
        
        return ConnectionType.UNKNOWN;
    }

    @SuppressLint("MissingPermission")
    private ConnectionType getCellularConnectionType() {
        try {
            int networkType = telephonyManager.getDataNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_NR:
                    return ConnectionType.MOBILE_5G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return ConnectionType.MOBILE_LTE;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return ConnectionType.MOBILE_3G;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return ConnectionType.MOBILE_2G;
                default:
                    return ConnectionType.MOBILE_4G;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error determining cellular connection type", e);
            return ConnectionType.MOBILE_4G;
        }
    }

    @SuppressLint("MissingPermission")
    private String getNetworkName(ConnectionType connectionType) {
        try {
            if (connectionType == ConnectionType.WIFI) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
                return ssid != null ? ssid.replace("\"", "") : "Unknown WiFi";
            } else {
                String operatorName = telephonyManager.getNetworkOperatorName();
                return operatorName != null && !operatorName.isEmpty() ? operatorName : "Unknown Carrier";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting network name", e);
            return "Unknown";
        }
    }

    @SuppressLint("MissingPermission")
    private int getSignalStrength(ConnectionType connectionType) {
        try {
            if (connectionType == ConnectionType.WIFI) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
            } else {
                // For cellular, we would need more complex implementation
                // For now, return a default value
                return 3;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting signal strength", e);
            return 0;
        }
    }

    public Observable<SpeedTestResult> performSpeedTest() {
        return Observable.fromCallable(() -> {
            String[] testUrls = {
                "https://httpbin.org/bytes/1000000", // 1MB
                "https://httpbin.org/bytes/5000000", // 5MB
                "https://httpbin.org/bytes/10000000" // 10MB
            };

            double totalDownloadSpeed = 0.0;
            long totalLatency = 0L;
            int successfulTests = 0;

            for (String url : testUrls) {
                try {
                    SpeedTestResult result = performSingleSpeedTest(url);
                    totalDownloadSpeed += result.downloadSpeed;
                    totalLatency += result.latency;
                    successfulTests++;
                } catch (Exception e) {
                    Log.w(TAG, "Speed test failed for URL: " + url, e);
                }
            }

            if (successfulTests > 0) {
                return new SpeedTestResult(
                    totalDownloadSpeed / successfulTests,
                    0.0, // Upload speed would require different endpoint
                    totalLatency / successfulTests,
                    0L, // Jitter calculation would require multiple tests
                    0.0, // Packet loss calculation
                    System.currentTimeMillis()
                );
            } else {
                throw new IOException("All speed tests failed");
            }
        }).subscribeOn(Schedulers.io());
    }

    private SpeedTestResult performQuickSpeedTest() {
        try {
            return performSingleSpeedTest("https://httpbin.org/bytes/1000000");
        } catch (Exception e) {
            Log.e(TAG, "Quick speed test failed", e);
            return new SpeedTestResult(0.0, 0.0, 0L, 0L, 0.0, System.currentTimeMillis());
        }
    }

    private SpeedTestResult performSingleSpeedTest(String url) throws IOException {
        long latency = measureLatency("8.8.8.8");
        
        Request request = new Request.Builder().url(url).build();
        
        long startTime = System.currentTimeMillis();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                byte[] bytes = response.body().bytes();
                long downloadTime = System.currentTimeMillis() - startTime;
                
                // Calculate download speed in Mbps
                double fileSizeBytes = bytes.length;
                double downloadSpeedMbps = (fileSizeBytes * 8.0) / (downloadTime * 1000.0);
                
                return new SpeedTestResult(
                    downloadSpeedMbps,
                    0.0, // Upload speed
                    latency,
                    0L, // Jitter
                    0.0, // Packet loss
                    downloadTime
                );
            } else {
                throw new IOException("Response not successful: " + response.code());
            }
        }
    }

    private long measureLatency(String host) {
        try {
            long startTime = System.currentTimeMillis();
            InetAddress.getByName(host).isReachable(5000);
            return System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            Log.w(TAG, "Latency measurement failed for host: " + host, e);
            return 0L;
        }
    }

    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // Inner class for speed test results
    public static class SpeedTestResult {
        public final double downloadSpeed;
        public final double uploadSpeed;
        public final long latency;
        public final long jitter;
        public final double packetLoss;
        public final long testDuration;

        public SpeedTestResult(double downloadSpeed, double uploadSpeed, long latency,
                              long jitter, double packetLoss, long testDuration) {
            this.downloadSpeed = downloadSpeed;
            this.uploadSpeed = uploadSpeed;
            this.latency = latency;
            this.jitter = jitter;
            this.packetLoss = packetLoss;
            this.testDuration = testDuration;
        }
    }
}