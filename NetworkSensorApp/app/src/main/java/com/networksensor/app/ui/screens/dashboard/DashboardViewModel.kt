package com.networksensor.app.ui.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networksensor.app.data.model.NetworkInfo
import com.networksensor.app.data.model.SensorType
import com.networksensor.app.data.repository.NetworkRepository
import com.networksensor.app.data.repository.SensorRepository
import com.networksensor.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val networkInfo: NetworkInfo? = null,
    val isSpeedTestRunning: Boolean = false,
    val quickStats: List<QuickStat> = emptyList(),
    val sensorStatuses: List<SensorStatus> = emptyList(),
    val recentActivities: List<RecentActivity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class QuickStat(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

data class SensorStatus(
    val name: String,
    val isActive: Boolean,
    val icon: ImageVector
)

data class RecentActivity(
    val title: String,
    val description: String,
    val timestamp: String,
    val icon: ImageVector,
    val color: Color
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val sensorRepository: SensorRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private val recentActivitiesList = mutableListOf<RecentActivity>()
    
    init {
        loadInitialData()
        startDataCollection()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Load network info
                networkRepository.getCurrentNetworkInfo().collect { networkInfo ->
                    _uiState.value = _uiState.value.copy(
                        networkInfo = networkInfo,
                        isLoading = false
                    )
                    
                    addRecentActivity(
                        RecentActivity(
                            title = "Network Connected",
                            description = "${networkInfo.connectionType.name} - ${networkInfo.networkName}",
                            timestamp = getCurrentTime(),
                            icon = Icons.Default.NetworkCheck,
                            color = NetworkGreen
                        )
                    )
                    
                    updateQuickStats()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    
    private fun startDataCollection() {
        // Update sensor statuses
        updateSensorStatuses()
        
        // Start collecting sensor data for quick stats
        viewModelScope.launch {
            try {
                sensorRepository.getEnvironmentalData().collect { environmentalData ->
                    updateQuickStats()
                    
                    environmentalData.temperature?.let { temp ->
                        addRecentActivity(
                            RecentActivity(
                                title = "Temperature Reading",
                                description = "${temp}°C",
                                timestamp = getCurrentTime(),
                                icon = Icons.Default.Thermostat,
                                color = SensorTemp
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle sensor data collection errors
            }
        }
        
        viewModelScope.launch {
            try {
                sensorRepository.getLocationData().collect { locationData ->
                    addRecentActivity(
                        RecentActivity(
                            title = "Location Update",
                            description = "Lat: ${String.format("%.4f", locationData.latitude)}, Lng: ${String.format("%.4f", locationData.longitude)}",
                            timestamp = getCurrentTime(),
                            icon = Icons.Default.LocationOn,
                            color = NetworkBlue
                        )
                    )
                }
            } catch (e: Exception) {
                // Handle location data collection errors
            }
        }
    }
    
    fun startSpeedTest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSpeedTestRunning = true)
            
            try {
                networkRepository.performSpeedTest().collect { speedTestResult ->
                    val updatedNetworkInfo = _uiState.value.networkInfo?.copy(
                        downloadSpeed = speedTestResult.downloadSpeed,
                        uploadSpeed = speedTestResult.uploadSpeed,
                        latency = speedTestResult.latency
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        networkInfo = updatedNetworkInfo,
                        isSpeedTestRunning = false
                    )
                    
                    addRecentActivity(
                        RecentActivity(
                            title = "Speed Test Completed",
                            description = "↓${String.format("%.1f", speedTestResult.downloadSpeed)}Mbps ↑${String.format("%.1f", speedTestResult.uploadSpeed)}Mbps",
                            timestamp = getCurrentTime(),
                            icon = Icons.Default.Speed,
                            color = NetworkGreen
                        )
                    )
                    
                    updateQuickStats()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSpeedTestRunning = false,
                    error = "Speed test failed: ${e.message}"
                )
                
                addRecentActivity(
                    RecentActivity(
                        title = "Speed Test Failed",
                        description = e.message ?: "Unknown error",
                        timestamp = getCurrentTime(),
                        icon = Icons.Default.Error,
                        color = NetworkRed
                    )
                )
            }
        }
    }
    
    private fun updateQuickStats() {
        val networkInfo = _uiState.value.networkInfo
        val quickStats = listOf(
            QuickStat(
                label = "Signal",
                value = "${networkInfo?.signalStrength ?: 0}/5",
                icon = Icons.Default.SignalCellular4Bar,
                color = NetworkBlue
            ),
            QuickStat(
                label = "Speed",
                value = "${networkInfo?.downloadSpeed?.let { String.format("%.1f", it) } ?: "0.0"}M",
                icon = Icons.Default.Speed,
                color = NetworkGreen
            ),
            QuickStat(
                label = "Sensors",
                value = "${_uiState.value.sensorStatuses.count { it.isActive }}",
                icon = Icons.Default.Sensors,
                color = SensorAccel
            ),
            QuickStat(
                label = "Uptime",
                value = "24h",
                icon = Icons.Default.Schedule,
                color = NetworkOrange
            )
        )
        
        _uiState.value = _uiState.value.copy(quickStats = quickStats)
    }
    
    private fun updateSensorStatuses() {
        val availableSensors = sensorRepository.getAvailableSensors()
        
        val sensorStatuses = listOf(
            SensorStatus(
                name = "Accelerometer",
                isActive = availableSensors.contains(SensorType.ACCELEROMETER),
                icon = Icons.Default.Speed
            ),
            SensorStatus(
                name = "Gyroscope",
                isActive = availableSensors.contains(SensorType.GYROSCOPE),
                icon = Icons.Default.RotateRight
            ),
            SensorStatus(
                name = "GPS",
                isActive = true, // Assume GPS is available
                icon = Icons.Default.LocationOn
            ),
            SensorStatus(
                name = "Temperature",
                isActive = availableSensors.contains(SensorType.TEMPERATURE),
                icon = Icons.Default.Thermostat
            ),
            SensorStatus(
                name = "Pressure",
                isActive = availableSensors.contains(SensorType.PRESSURE),
                icon = Icons.Default.Compress
            ),
            SensorStatus(
                name = "Light",
                isActive = availableSensors.contains(SensorType.LIGHT),
                icon = Icons.Default.LightMode
            )
        )
        
        _uiState.value = _uiState.value.copy(sensorStatuses = sensorStatuses)
    }
    
    private fun addRecentActivity(activity: RecentActivity) {
        recentActivitiesList.add(0, activity)
        if (recentActivitiesList.size > 10) {
            recentActivitiesList.removeAt(recentActivitiesList.size - 1)
        }
        
        _uiState.value = _uiState.value.copy(recentActivities = recentActivitiesList.toList())
    }
    
    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(Date())
    }
}