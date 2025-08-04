package com.networksensor.app.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.networksensor.app.ui.components.GradientCard
import com.networksensor.app.ui.components.GlassCard
import com.networksensor.app.ui.theme.*
import androidx.compose.foundation.clickable

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onNavigateToNetwork: () -> Unit = {},
    onNavigateToSensors: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Network Sensor",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Monitor your network and device sensors in real-time",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Quick Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Wifi,
                title = "Network",
                value = "Excellent",
                color = NetworkExcellent,
                onClick = onNavigateToNetwork
            )
            
            QuickStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Sensors,
                title = "Sensors",
                value = "Active",
                color = SensorAccelerometer,
                onClick = onNavigateToSensors
            )
        }
        
        // Main Feature Cards
        FeatureCard(
            icon = Icons.Default.Speed,
            title = "Network Monitor",
            description = "Real-time network speed, WiFi analysis, and cellular data monitoring",
            gradientColors = listOf(GradientBlueStart, GradientBlueEnd),
            onClick = onNavigateToNetwork
        )
        
        FeatureCard(
            icon = Icons.Default.Explore,
            title = "Sensor Hub",
            description = "Monitor accelerometer, gyroscope, GPS, and environmental sensors",
            gradientColors = listOf(GradientPurpleStart, GradientPurpleEnd),
            onClick = onNavigateToSensors
        )
        
        // Recent Activity
        GlassCard {
            Column {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ActivityItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Network scan completed",
                    subtitle = "All systems operational",
                    time = "2 min ago",
                    color = SuccessGreen
                )
                
                ActivityItem(
                    icon = Icons.Default.LocationOn,
                    title = "GPS location updated",
                    subtitle = "Accuracy: ±3 meters",
                    time = "5 min ago",
                    color = InfoBlue
                )
                
                ActivityItem(
                    icon = Icons.Default.Sensors,
                    title = "Sensor calibration",
                    subtitle = "Accelerometer calibrated",
                    time = "10 min ago",
                    color = WarningOrange
                )
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    GradientCard(
        gradientColors = gradientColors,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ActivityItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    time: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}