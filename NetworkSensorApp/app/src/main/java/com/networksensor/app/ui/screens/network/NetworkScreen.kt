package com.networksensor.app.ui.screens.network

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.networksensor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Network Analysis",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            NetworkStatusCard(
                networkInfo = uiState.networkInfo,
                onRefresh = { viewModel.refreshNetworkInfo() }
            )
        }
        
        item {
            SpeedTestCard(
                speedTestResult = uiState.speedTestResult,
                isRunning = uiState.isSpeedTestRunning,
                onStartTest = { viewModel.startSpeedTest() }
            )
        }
        
        uiState.wifiInfo?.let { wifiInfo ->
            item {
                WifiDetailsCard(wifiInfo = wifiInfo)
            }
        }
        
        uiState.cellularInfo?.let { cellularInfo ->
            item {
                CellularDetailsCard(cellularInfo = cellularInfo)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkStatusCard(
    networkInfo: com.networksensor.app.data.model.NetworkInfo?,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connection Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            networkInfo?.let { info ->
                NetworkInfoRow("Type", info.connectionType.name)
                NetworkInfoRow("Network", info.networkName ?: "Unknown")
                NetworkInfoRow("Signal Strength", "${info.signalStrength}/5")
                NetworkInfoRow("Download Speed", "${String.format("%.2f", info.downloadSpeed)} Mbps")
                NetworkInfoRow("Upload Speed", "${String.format("%.2f", info.uploadSpeed)} Mbps")
                NetworkInfoRow("Latency", "${info.latency} ms")
            } ?: run {
                Text(
                    text = "No network information available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun NetworkInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedTestCard(
    speedTestResult: com.networksensor.app.data.model.SpeedTestResult?,
    isRunning: Boolean,
    onStartTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Speed Test",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            speedTestResult?.let { result ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SpeedMetric(
                        label = "Download",
                        value = "${String.format("%.2f", result.downloadSpeed)} Mbps",
                        icon = Icons.Default.Download,
                        color = NetworkGreen
                    )
                    
                    SpeedMetric(
                        label = "Upload",
                        value = "${String.format("%.2f", result.uploadSpeed)} Mbps",
                        icon = Icons.Default.Upload,
                        color = NetworkBlue
                    )
                    
                    SpeedMetric(
                        label = "Ping",
                        value = "${result.latency} ms",
                        icon = Icons.Default.Speed,
                        color = NetworkOrange
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Button(
                onClick = onStartTest,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRunning
            ) {
                if (isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Running Test...")
                } else {
                    Icon(Icons.Default.Speed, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Speed Test")
                }
            }
        }
    }
}

@Composable
fun SpeedMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiDetailsCard(wifiInfo: com.networksensor.app.data.model.WifiInfo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Wifi,
                    contentDescription = "WiFi",
                    tint = NetworkBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "WiFi Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NetworkInfoRow("SSID", wifiInfo.ssid ?: "Unknown")
            NetworkInfoRow("BSSID", wifiInfo.bssid ?: "Unknown")
            NetworkInfoRow("Frequency", "${wifiInfo.frequency} MHz")
            NetworkInfoRow("Link Speed", "${wifiInfo.linkSpeed} Mbps")
            NetworkInfoRow("RSSI", "${wifiInfo.rssi} dBm")
            NetworkInfoRow("IP Address", wifiInfo.ipAddress ?: "Unknown")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellularDetailsCard(cellularInfo: com.networksensor.app.data.model.CellularInfo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SignalCellular4Bar,
                    contentDescription = "Cellular",
                    tint = NetworkGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cellular Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NetworkInfoRow("Network Type", cellularInfo.networkType)
            NetworkInfoRow("Operator", cellularInfo.operatorName ?: "Unknown")
            NetworkInfoRow("MCC", cellularInfo.mcc ?: "Unknown")
            NetworkInfoRow("MNC", cellularInfo.mnc ?: "Unknown")
            NetworkInfoRow("Signal Strength", "${cellularInfo.signalStrength}/5")
        }
    }
}