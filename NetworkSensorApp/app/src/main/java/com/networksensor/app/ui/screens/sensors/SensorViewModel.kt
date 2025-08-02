package com.networksensor.app.ui.screens.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networksensor.app.data.model.*
import com.networksensor.app.data.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SensorUiState(
    val availableSensors: List<SensorType> = emptyList(),
    val activeSensors: List<SensorType> = emptyList(),
    val sensorReadings: List<SensorReading> = emptyList(),
    val locationData: LocationData? = null,
    val deviceMotion: DeviceMotion? = null,
    val environmentalData: EnvironmentalData? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val sensorRepository: SensorRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SensorUiState())
    val uiState: StateFlow<SensorUiState> = _uiState.asStateFlow()
    
    init {
        loadAvailableSensors()
        startSensorDataCollection()
    }
    
    private fun loadAvailableSensors() {
        val availableSensors = sensorRepository.getAvailableSensors()
        _uiState.value = _uiState.value.copy(
            availableSensors = availableSensors,
            activeSensors = availableSensors, // Assume all available sensors are active for demo
            isLoading = false
        )
    }
    
    private fun startSensorDataCollection() {
        // Collect location data
        viewModelScope.launch {
            try {
                sensorRepository.getLocationData().collect { locationData ->
                    _uiState.value = _uiState.value.copy(locationData = locationData)
                }
            } catch (e: Exception) {
                // Handle location permission issues
            }
        }
        
        // Collect device motion data
        viewModelScope.launch {
            try {
                sensorRepository.getDeviceMotion().collect { deviceMotion ->
                    _uiState.value = _uiState.value.copy(deviceMotion = deviceMotion)
                }
            } catch (e: Exception) {
                // Handle motion sensor issues
            }
        }
        
        // Collect environmental data
        viewModelScope.launch {
            try {
                sensorRepository.getEnvironmentalData().collect { environmentalData ->
                    _uiState.value = _uiState.value.copy(environmentalData = environmentalData)
                }
            } catch (e: Exception) {
                // Handle environmental sensor issues
            }
        }
        
        // Collect individual sensor readings for available sensors
        _uiState.value.availableSensors.forEach { sensorType ->
            viewModelScope.launch {
                try {
                    sensorRepository.getSensorReading(sensorType).collect { reading ->
                        val currentReadings = _uiState.value.sensorReadings.toMutableList()
                        val existingIndex = currentReadings.indexOfFirst { it.sensorType == sensorType }
                        
                        if (existingIndex >= 0) {
                            currentReadings[existingIndex] = reading
                        } else {
                            currentReadings.add(reading)
                        }
                        
                        _uiState.value = _uiState.value.copy(sensorReadings = currentReadings)
                    }
                } catch (e: Exception) {
                    // Handle individual sensor reading issues
                }
            }
        }
    }
}