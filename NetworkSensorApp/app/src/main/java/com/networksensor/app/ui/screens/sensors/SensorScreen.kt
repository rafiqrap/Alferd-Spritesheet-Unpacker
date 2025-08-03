package com.networksensor.app.ui.screens.sensors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.networksensor.app.data.model.SensorType
import com.networksensor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(
    viewModel: SensorViewModel = hiltViewModel()
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
                text = "Sensor Monitor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            SensorOverviewCard(
                availableSensors = uiState.availableSensors,
                activeSensors = uiState.activeSensors
            )
        }
        
        uiState.locationData?.let { locationData ->
            item {
                LocationCard(locationData = locationData)
            }
        }
        
        uiState.deviceMotion?.let { motion ->
            item {
                MotionSensorCard(motion = motion)
            }
        }
        
        uiState.environmentalData?.let { environmental ->
            item {
                EnvironmentalCard(environmental = environmental)
            }
        }
        
        items(uiState.sensorReadings) { reading ->
            SensorReadingCard(reading = reading)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorOverviewCard(
    availableSensors: List<SensorType>,
    activeSensors: List<SensorType>
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
            Text(
                text = "Sensor Status",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SensorMetric(
                    label = "Available",
                    value = availableSensors.size.toString(),
                    icon = Icons.Default.Sensors,
                    color = NetworkBlue
                )
                
                SensorMetric(
                    label = "Active",
                    value = activeSensors.size.toString(),
                    icon = Icons.Default.PlayArrow,
                    color = NetworkGreen
                )
                
                SensorMetric(
                    label = "Inactive",
                    value = (availableSensors.size - activeSensors.size).toString(),
                    icon = Icons.Default.Pause,
                    color = NetworkOrange
                )
            }
        }
    }
}

@Composable
fun SensorMetric(
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
            style = MaterialTheme.typography.titleMedium,
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
fun LocationCard(locationData: com.networksensor.app.data.model.LocationData) {
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
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = NetworkBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GPS Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SensorDataRow("Latitude", String.format("%.6f°", locationData.latitude))
            SensorDataRow("Longitude", String.format("%.6f°", locationData.longitude))
            SensorDataRow("Altitude", String.format("%.1f m", locationData.altitude))
            SensorDataRow("Accuracy", String.format("%.1f m", locationData.accuracy))
            SensorDataRow("Speed", String.format("%.1f m/s", locationData.speed))
            SensorDataRow("Provider", locationData.provider)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotionSensorCard(motion: com.networksensor.app.data.model.DeviceMotion) {
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
                    Icons.Default.Speed,
                    contentDescription = "Motion",
                    tint = SensorAccel
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Motion Sensors",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Accelerometer",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = SensorAccel
            )
            SensorDataRow("X", String.format("%.3f m/s²", motion.accelerometer[0]))
            SensorDataRow("Y", String.format("%.3f m/s²", motion.accelerometer[1]))
            SensorDataRow("Z", String.format("%.3f m/s²", motion.accelerometer[2]))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Gyroscope",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = SensorGyro
            )
            SensorDataRow("X", String.format("%.3f rad/s", motion.gyroscope[0]))
            SensorDataRow("Y", String.format("%.3f rad/s", motion.gyroscope[1]))
            SensorDataRow("Z", String.format("%.3f rad/s", motion.gyroscope[2]))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentalCard(environmental: com.networksensor.app.data.model.EnvironmentalData) {
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
                    Icons.Default.Thermostat,
                    contentDescription = "Environmental",
                    tint = SensorTemp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Environmental",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            environmental.temperature?.let { temp ->
                SensorDataRow("Temperature", String.format("%.1f°C", temp))
            }
            
            environmental.humidity?.let { humidity ->
                SensorDataRow("Humidity", String.format("%.1f%%", humidity))
            }
            
            environmental.pressure?.let { pressure ->
                SensorDataRow("Pressure", String.format("%.1f hPa", pressure))
            }
            
            environmental.lightLevel?.let { light ->
                SensorDataRow("Light", String.format("%.1f lux", light))
            }
            
            environmental.proximity?.let { proximity ->
                SensorDataRow("Proximity", String.format("%.1f cm", proximity))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorReadingCard(reading: com.networksensor.app.data.model.SensorReading) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = reading.sensorType.name.replace("_", " "),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            reading.values.forEachIndexed { index, value ->
                SensorDataRow("Value $index", String.format("%.3f", value))
            }
            
            SensorDataRow("Accuracy", reading.accuracy.toString())
        }
    }
}

@Composable
fun SensorDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
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