# Network & Sensor Monitor App

A modern Android application built with Jetpack Compose that provides comprehensive network monitoring and sensor data collection capabilities targeting Android API 35.

## Features

### 🌐 Network Monitoring
- **Real-time Network Analysis**: Monitor WiFi, LTE, 4G, 5G, 3G, 2G, and GPRS connections
- **Speed Testing**: Comprehensive download/upload speed tests with latency measurement
- **Connection Details**: 
  - WiFi: SSID, BSSID, frequency, link speed, RSSI, IP address
  - Cellular: Network type, operator, MCC/MNC, signal strength, cell info
- **Network Type Detection**: Automatic detection and classification of connection types

### 📱 Sensor Monitoring
- **Motion Sensors**: Accelerometer, gyroscope, magnetometer, gravity, linear acceleration
- **Environmental Sensors**: Temperature, humidity, pressure, light, proximity
- **Location Services**: GPS tracking with latitude, longitude, altitude, accuracy, speed
- **Real-time Data**: Live sensor readings with high-frequency sampling
- **Sensor Availability**: Automatic detection of available device sensors

### 🎨 Modern UI/UX
- **Material Design 3**: Latest Material You design principles
- **Jetpack Compose**: Fully declarative UI with modern Android development
- **Dynamic Theming**: Supports system-wide dynamic colors (Android 12+)
- **Responsive Design**: Optimized for different screen sizes and orientations
- **Real-time Updates**: Live data visualization with smooth animations

### 📊 Dashboard & Analytics
- **Overview Dashboard**: Quick stats and network/sensor status at a glance
- **Recent Activity**: Timeline of network changes and sensor events
- **Quick Actions**: One-tap speed tests and sensor monitoring
- **Visual Indicators**: Color-coded status indicators for easy interpretation

## Architecture

### Modern Android Architecture
- **MVVM Pattern**: Model-View-ViewModel architecture with clean separation
- **Repository Pattern**: Centralized data management with single source of truth
- **Dependency Injection**: Hilt for dependency management
- **Reactive Programming**: Kotlin Coroutines and Flow for asynchronous operations

### Key Components
```
├── data/
│   ├── model/          # Data classes for network and sensor data
│   └── repository/     # Data repositories for network and sensor operations
├── di/                 # Dependency injection modules
├── ui/
│   ├── navigation/     # Navigation component setup
│   ├── screens/        # UI screens (Dashboard, Network, Sensors, Settings)
│   └── theme/          # Material Design 3 theming
└── MainActivity.kt     # Main entry point
```

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Hilt
- **Async Programming**: Coroutines + Flow
- **Networking**: OkHttp + Retrofit
- **Navigation**: Navigation Compose
- **Material Design**: Material 3 Components

## Permissions

The app requires the following permissions for full functionality:

### Network Permissions
- `INTERNET` - For speed testing and network requests
- `ACCESS_NETWORK_STATE` - For monitoring network connections
- `ACCESS_WIFI_STATE` - For WiFi network information
- `CHANGE_WIFI_STATE` - For WiFi operations
- `READ_PHONE_STATE` - For cellular network information

### Location Permissions
- `ACCESS_FINE_LOCATION` - For precise GPS tracking
- `ACCESS_COARSE_LOCATION` - For approximate location
- `ACCESS_BACKGROUND_LOCATION` - For background location updates

### Sensor Permissions
- `BODY_SENSORS` - For health-related sensors
- `HIGH_SAMPLING_RATE_SENSORS` - For high-frequency sensor data

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 35
- Kotlin 1.9.22 or later
- Gradle 8.2.0 or later

### Installation
1. Clone the repository:
```bash
git clone <repository-url>
cd NetworkSensorApp
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the app on a device or emulator

### Building
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Usage

### Dashboard Screen
- View overall network and sensor status
- Quick access to speed tests
- Monitor recent activity and system health
- Real-time updates of key metrics

### Network Screen
- Detailed network analysis and monitoring
- Run comprehensive speed tests
- View WiFi and cellular connection details
- Monitor signal strength and connection quality

### Sensors Screen
- Real-time sensor data visualization
- GPS location tracking
- Motion sensor readings (accelerometer, gyroscope)
- Environmental data (temperature, humidity, pressure)

### Settings Screen
- App information and version details
- Feature overview
- Permission requirements

## Key Features Implementation

### Network Speed Testing
```kotlin
// Real-time speed testing with multiple endpoints
suspend fun performSpeedTest(): Flow<SpeedTestResult> = flow {
    val testUrls = listOf(
        "https://speed.cloudflare.com/__down?bytes=25000000",
        "https://httpbin.org/bytes/10000000"
    )
    // Implementation details...
}
```

### Sensor Data Collection
```kotlin
// Real-time sensor monitoring
fun getSensorReading(sensorType: SensorType): Flow<SensorReading> = callbackFlow {
    val sensor = getSensorBySensorType(sensorType)
    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            // Process sensor data...
        }
    }
    // Implementation details...
}
```

### Modern UI with Compose
```kotlin
@Composable
fun NetworkOverviewCard(
    networkInfo: NetworkInfo?,
    onSpeedTestClick: () -> Unit,
    isSpeedTestRunning: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        // Modern Material Design 3 UI implementation...
    }
}
```

## Device Compatibility

### Minimum Requirements
- **Android Version**: API 24 (Android 7.0)
- **Target Version**: API 35 (Android 15)
- **RAM**: 2GB minimum, 4GB recommended
- **Storage**: 100MB for app installation

### Sensor Support
The app automatically detects available sensors and adapts functionality accordingly:
- Accelerometer, Gyroscope, Magnetometer
- GPS/Location Services
- Temperature, Humidity, Pressure sensors
- Light and Proximity sensors
- Network connectivity sensors

### Network Support
- WiFi (2.4GHz and 5GHz)
- Cellular: 5G, LTE, 4G, 3G, 2G, GPRS
- Ethernet connections
- VPN and tethered connections

## Performance Optimizations

- **Efficient Sensor Sampling**: Configurable sampling rates to balance accuracy and battery life
- **Background Processing**: Coroutines for non-blocking operations
- **Memory Management**: Proper lifecycle management and resource cleanup
- **Battery Optimization**: Smart sensor polling and network request batching

## Security & Privacy

- **Permission Management**: Runtime permission requests with clear explanations
- **Data Privacy**: All sensor and network data stays on device
- **Secure Networking**: HTTPS for all network requests
- **No Data Collection**: App doesn't collect or transmit personal data

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is open source and available under the [MIT License](LICENSE).

## Support

For issues, feature requests, or questions:
- Open an issue on GitHub
- Check existing documentation
- Review the code comments for implementation details

---

**Built with ❤️ using modern Android development practices**