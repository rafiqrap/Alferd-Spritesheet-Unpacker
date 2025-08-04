package com.networksensor.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.networksensor.app.adapters.DashboardAdapter;
import com.networksensor.app.models.DashboardItem;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up the main layout
        setContentView(R.layout.activity_main);
        
        // Initialize views
        initializeViews();
        setupNavigation();
        setupRecyclerView();
        setupSwipeRefresh();
        setupFloatingActionButton();
        
        // Load initial data
        loadDashboardData();
    }

    private void initializeViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                showDashboard();
                return true;
            } else if (itemId == R.id.navigation_network) {
                showNetworkMonitor();
                return true;
            } else if (itemId == R.id.navigation_sensors) {
                showSensorHub();
                return true;
            } else if (itemId == R.id.navigation_settings) {
                showSettings();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        dashboardAdapter = new DashboardAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dashboardAdapter);
        
        // Add item decoration for spacing
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));
        
        // Set click listener
        dashboardAdapter.setOnItemClickListener((item, position) -> {
            handleItemClick(item, position);
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh data
            loadDashboardData();
            swipeRefreshLayout.setRefreshing(false);
        });
        
        // Set refreshing colors
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_blue,
                R.color.primary_teal,
                R.color.primary_purple
        );
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            // Show quick actions or start monitoring
            Toast.makeText(this, "Starting network scan...", Toast.LENGTH_SHORT).show();
            startNetworkScan();
        });
    }

    private void loadDashboardData() {
        List<DashboardItem> items = new ArrayList<>();
        
        // Add network status item
        items.add(new DashboardItem(
                "Network Status",
                "Excellent",
                "WiFi Connected - 150 Mbps",
                R.drawable.ic_wifi,
                DashboardItem.ItemType.NETWORK_STATUS,
                R.color.network_excellent
        ));
        
        // Add sensor status item
        items.add(new DashboardItem(
                "Sensor Hub",
                "Active",
                "5 sensors monitoring",
                R.drawable.ic_sensors,
                DashboardItem.ItemType.SENSOR_STATUS,
                R.color.sensor_accelerometer
        ));
        
        // Add recent activity items
        items.add(new DashboardItem(
                "Recent Activity",
                "Network scan completed",
                "All systems operational",
                R.drawable.ic_check_circle,
                DashboardItem.ItemType.ACTIVITY
        ));
        
        items.add(new DashboardItem(
                "GPS Location",
                "Updated",
                "Accuracy: ±3 meters",
                R.drawable.ic_location_on,
                DashboardItem.ItemType.ACTIVITY
        ));
        
        dashboardAdapter.updateItems(items);
    }

    private void handleItemClick(DashboardItem item, int position) {
        switch (item.getItemType()) {
            case NETWORK_STATUS:
                showNetworkMonitor();
                break;
            case SENSOR_STATUS:
                showSensorHub();
                break;
            case ACTIVITY:
                // Handle activity item click
                Toast.makeText(this, "Activity: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case FEATURE_CARD:
                // Handle feature card click
                Toast.makeText(this, "Feature: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showDashboard() {
        // Show dashboard view
        findViewById(R.id.dashboardLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.networkLayout).setVisibility(View.GONE);
        findViewById(R.id.sensorsLayout).setVisibility(View.GONE);
        findViewById(R.id.settingsLayout).setVisibility(View.GONE);
    }

    private void showNetworkMonitor() {
        // Launch NetworkMonitorActivity
        Intent intent = new Intent(this, NetworkMonitorActivity.class);
        startActivity(intent);
    }

    private void showSensorHub() {
        // Launch SensorHubActivity
        Intent intent = new Intent(this, SensorHubActivity.class);
        startActivity(intent);
    }

    private void showSettings() {
        // Show settings view (placeholder for now)
        findViewById(R.id.dashboardLayout).setVisibility(View.GONE);
        findViewById(R.id.networkLayout).setVisibility(View.GONE);
        findViewById(R.id.sensorsLayout).setVisibility(View.GONE);
        findViewById(R.id.settingsLayout).setVisibility(View.VISIBLE);
        
        Toast.makeText(this, "Settings - Coming Soon!", Toast.LENGTH_SHORT).show();
    }

    private void startNetworkScan() {
        // Start network scanning in background
        // This would typically involve starting a service or using WorkManager
        Toast.makeText(this, "Network scan started", Toast.LENGTH_SHORT).show();
    }
}