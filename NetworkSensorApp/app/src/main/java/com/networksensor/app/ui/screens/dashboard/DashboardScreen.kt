package com.networksensor.app.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.networksensor.app.data.model.ConnectionType
import com.networksensor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
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
                text = "Network & Sensor Monitor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            NetworkOverviewCard(
                networkInfo = uiState.networkInfo,
                onSpeedTestClick = { viewModel.startSpeedTest() },
                isSpeedTestRunning = uiState.isSpeedTestRunning
            )
        }
        
        item {
            Text(
                text = "Quick Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.quickStats) { stat ->
                    QuickStatCard(stat = stat)
                }
            }
        }
        
        item {
            Text(
                text = "Active Sensors",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        item {
            SensorStatusGrid(
                sensorStatuses = uiState.sensorStatuses
            )
        }
        
        item {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        items(uiState.recentActivities) { activity ->
            ActivityCard(activity = activity)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkOverviewCard(
    networkInfo: com.networksensor.app.data.model.NetworkInfo?,
    onSpeedTestClick: () -> Unit,
    isSpeedTestRunning: Boolean
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
                Column {
                    Text(
                        text = "Network Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = networkInfo?.networkName ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Icon(
                    imageVector = getConnectionIcon(networkInfo?.connectionType),
                    contentDescription = "Connection Type",
                    tint = getConnectionColor(networkInfo?.connectionType)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NetworkMetric(
                    label = "Download",
                    value = "${networkInfo?.downloadSpeed?.let { "%.1f".format(it) } ?: "0.0"} Mbps",
                    icon = Icons.Default.Download
                )
                
                NetworkMetric(
                    label = "Upload",
                    value = "${networkInfo?.uploadSpeed?.let { "%.1f".format(it) } ?: "0.0"} Mbps",
                    icon = Icons.Default.Upload
                )
                
                NetworkMetric(
                    label = "Ping",
                    value = "${networkInfo?.latency ?: 0}ms",
                    icon = Icons.Default.Speed
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onSpeedTestClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSpeedTestRunning
            ) {
                if (isSpeedTestRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Running Speed Test...")
                } else {
                    Icon(Icons.Default.Speed, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Run Speed Test")
                }
            }
        }
    }
}

@Composable
fun NetworkMetric(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
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
fun QuickStatCard(stat: QuickStat) {
    Card(
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.label,
                tint = stat.color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stat.value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorStatusGrid(sensorStatuses: List<SensorStatus>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            sensorStatuses.chunked(2).forEach { rowSensors ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowSensors.forEach { sensor ->
                        SensorStatusItem(
                            sensor = sensor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowSensors.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if (sensorStatuses.indexOf(rowSensors.first()) < sensorStatuses.size - 2) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SensorStatusItem(
    sensor: SensorStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = sensor.icon,
            contentDescription = sensor.name,
            tint = if (sensor.isActive) NetworkGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = sensor.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (sensor.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.bodySmall,
                color = if (sensor.isActive) NetworkGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCard(activity: RecentActivity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = activity.icon,
                contentDescription = activity.title,
                tint = activity.color
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                text = activity.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

private fun getConnectionIcon(connectionType: ConnectionType?): ImageVector {
    return when (connectionType) {
        ConnectionType.WIFI -> Icons.Default.Wifi
        ConnectionType.MOBILE_5G, ConnectionType.MOBILE_LTE, ConnectionType.MOBILE_4G -> Icons.Default.SignalCellular4Bar
        ConnectionType.MOBILE_3G, ConnectionType.MOBILE_2G -> Icons.Default.SignalCellular3Bar
        ConnectionType.ETHERNET -> Icons.Default.Cable
        else -> Icons.Default.SignalCellularOff
    }
}

private fun getConnectionColor(connectionType: ConnectionType?): androidx.compose.ui.graphics.Color {
    return when (connectionType) {
        ConnectionType.WIFI -> NetworkBlue
        ConnectionType.MOBILE_5G, ConnectionType.MOBILE_LTE -> NetworkGreen
        ConnectionType.MOBILE_4G, ConnectionType.MOBILE_3G -> NetworkOrange
        ConnectionType.MOBILE_2G -> NetworkRed
        ConnectionType.ETHERNET -> NetworkGreen
        else -> androidx.compose.ui.graphics.Color.Gray
    }
}