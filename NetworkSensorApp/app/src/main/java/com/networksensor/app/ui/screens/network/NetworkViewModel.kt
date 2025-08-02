package com.networksensor.app.ui.screens.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networksensor.app.data.model.CellularInfo
import com.networksensor.app.data.model.NetworkInfo
import com.networksensor.app.data.model.SpeedTestResult
import com.networksensor.app.data.model.WifiInfo
import com.networksensor.app.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkUiState(
    val networkInfo: NetworkInfo? = null,
    val speedTestResult: SpeedTestResult? = null,
    val wifiInfo: WifiInfo? = null,
    val cellularInfo: CellularInfo? = null,
    val isSpeedTestRunning: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()
    
    init {
        loadNetworkData()
    }
    
    private fun loadNetworkData() {
        viewModelScope.launch {
            try {
                // Load current network info
                networkRepository.getCurrentNetworkInfo().collect { networkInfo ->
                    _uiState.value = _uiState.value.copy(
                        networkInfo = networkInfo,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
        
        viewModelScope.launch {
            try {
                // Load WiFi details if available
                networkRepository.getWifiDetails().collect { wifiInfo ->
                    _uiState.value = _uiState.value.copy(wifiInfo = wifiInfo)
                }
            } catch (e: Exception) {
                // WiFi might not be available
            }
        }
        
        viewModelScope.launch {
            try {
                // Load cellular details if available
                networkRepository.getCellularDetails().collect { cellularInfo ->
                    _uiState.value = _uiState.value.copy(cellularInfo = cellularInfo)
                }
            } catch (e: Exception) {
                // Cellular might not be available
            }
        }
    }
    
    fun refreshNetworkInfo() {
        viewModelScope.launch {
            try {
                networkRepository.getCurrentNetworkInfo().collect { networkInfo ->
                    _uiState.value = _uiState.value.copy(networkInfo = networkInfo)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun startSpeedTest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSpeedTestRunning = true)
            
            try {
                networkRepository.performSpeedTest().collect { speedTestResult ->
                    _uiState.value = _uiState.value.copy(
                        speedTestResult = speedTestResult,
                        isSpeedTestRunning = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSpeedTestRunning = false,
                    error = "Speed test failed: ${e.message}"
                )
            }
        }
    }
}