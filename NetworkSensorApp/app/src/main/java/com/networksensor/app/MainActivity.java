package com.networksensor.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.compose.setContent;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.foundation.layout.fillMaxSize;
import androidx.compose.foundation.layout.padding;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.Scaffold;
import androidx.compose.material3.Surface;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.navigation.compose.rememberNavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.networksensor.app.ui.navigation.NetworkSensorNavigation;
import com.networksensor.app.ui.theme.NetworkSensorTheme;
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
        enableEdgeToEdge();
        
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
                DashboardItem.ItemType.NETWORK_STATUS
        ));
        
        // Add sensor status item
        items.add(new DashboardItem(
                "Sensor Hub",
                "Active",
                "5 sensors monitoring",
                R.drawable.ic_sensors,
                DashboardItem.ItemType.SENSOR_STATUS
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

    private void showDashboard() {
        // Show dashboard view
        findViewById(R.id.dashboardLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.networkLayout).setVisibility(View.GONE);
        findViewById(R.id.sensorsLayout).setVisibility(View.GONE);
        findViewById(R.id.settingsLayout).setVisibility(View.GONE);
    }

    private void showNetworkMonitor() {
        // Show network monitoring view
        findViewById(R.id.dashboardLayout).setVisibility(View.GONE);
        findViewById(R.id.networkLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.sensorsLayout).setVisibility(View.GONE);
        findViewById(R.id.settingsLayout).setVisibility(View.GONE);
    }

    private void showSensorHub() {
        // Show sensor hub view
        findViewById(R.id.dashboardLayout).setVisibility(View.GONE);
        findViewById(R.id.networkLayout).setVisibility(View.GONE);
        findViewById(R.id.sensorsLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.settingsLayout).setVisibility(View.GONE);
    }

    private void showSettings() {
        // Show settings view
        findViewById(R.id.dashboardLayout).setVisibility(View.GONE);
        findViewById(R.id.networkLayout).setVisibility(View.GONE);
        findViewById(R.id.sensorsLayout).setVisibility(View.GONE);
        findViewById(R.id.settingsLayout).setVisibility(View.VISIBLE);
    }

    private void startNetworkScan() {
        // Start network scanning in background
        // This would typically involve starting a service or using WorkManager
        Toast.makeText(this, "Network scan started", Toast.LENGTH_SHORT).show();
    }

    // Compose version for modern UI components
    @Composable
    public static void NetworkSensorApp() {
        NetworkSensorTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController();
                
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NetworkSensorNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    );
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    public static void NetworkSensorAppPreview() {
        NetworkSensorTheme {
            NetworkSensorApp();
        }
    }
}