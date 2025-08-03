package com.networksensor.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.networksensor.app.databinding.ActivityMainBinding;
import com.networksensor.app.fragment.DashboardFragment;
import com.networksensor.app.fragment.NetworkFragment;
import com.networksensor.app.fragment.SensorFragment;
import com.networksensor.app.fragment.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private ActivityMainBinding binding;
    private DashboardFragment dashboardFragment;
    private NetworkFragment networkFragment;
    private SensorFragment sensorFragment;
    private SettingsFragment settingsFragment;

    private final String[] REQUIRED_PERMISSIONS = {
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFragments();
        setupBottomNavigation();
        
        // Check and request permissions
        if (!hasRequiredPermissions()) {
            requestPermissions();
        }
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(dashboardFragment);
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
        }
    }

    private void initializeFragments() {
        dashboardFragment = new DashboardFragment();
        networkFragment = new NetworkFragment();
        sensorFragment = new SensorFragment();
        settingsFragment = new SettingsFragment();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                selectedFragment = dashboardFragment;
            } else if (itemId == R.id.navigation_network) {
                selectedFragment = networkFragment;
            } else if (itemId == R.id.navigation_sensors) {
                selectedFragment = sensorFragment;
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = settingsFragment;
            }
            
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }

    private boolean hasRequiredPermissions() {
        return EasyPermissions.hasPermissions(this, REQUIRED_PERMISSIONS);
    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "This app needs permissions to monitor network and sensors",
            PERMISSION_REQUEST_CODE,
            REQUIRED_PERMISSIONS
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "Permissions granted: " + perms.toString());
        Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.w(TAG, "Permissions denied: " + perms.toString());
        Toast.makeText(this, "Some features may not work without permissions", Toast.LENGTH_LONG).show();
        
        // Check if some permissions are permanently denied
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // Show settings dialog
            Toast.makeText(this, "Please enable permissions in Settings", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}