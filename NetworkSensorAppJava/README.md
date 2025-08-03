# Network & Sensor Monitor App (Java)

A comprehensive Android application written in **Java** that provides real-time network monitoring and sensor data collection capabilities, targeting Android API 35 with support for the latest devices.

## 🚀 Features

### 🌐 Network Monitoring
- **Real-time Network Analysis**: Monitor WiFi, LTE, 4G, 5G, 3G, 2G, and GPRS connections
- **Speed Testing**: Comprehensive download/upload speed tests with latency measurement
- **Connection Details**: 
  - WiFi: SSID, BSSID, frequency, link speed, RSSI, IP address
  - Cellular: Network type, operator, MCC/MNC, signal strength
- **Network Type Detection**: Automatic detection and classification of connection types

### 📱 Sensor Monitoring
- **Motion Sensors**: Accelerometer, gyroscope, magnetometer, gravity, linear acceleration
- **Environmental Sensors**: Temperature, humidity, pressure, light, proximity
- **Location Services**: GPS tracking with latitude, longitude, altitude, accuracy, speed
- **Real-time Data**: Live sensor readings with high-frequency sampling
- **Sensor Availability**: Automatic detection of available device sensors

### 🎨 Modern UI/UX
- **Material Design 3**: Latest Material You design principles
- **View Binding**: Type-safe view references with modern Android development
- **Reactive Programming**: RxJava3 for smooth asynchronous operations
- **Bottom Navigation**: Intuitive navigation between different app sections
- **Real-time Updates**: Live data visualization with smooth UI updates

### 📊 Dashboard & Analytics
- **Overview Dashboard**: Quick stats and network/sensor status at a glance
- **Recent Activity**: Timeline of network changes and sensor events
- **Quick Actions**: One-tap speed tests and sensor monitoring
- **Visual Indicators**: Progress bars and color-coded status indicators

## 🏗️ Architecture

### Modern Android Architecture (Java)
- **MVVM Pattern**: Model-View-ViewModel architecture with clean separation
- **Repository Pattern**: Centralized data management with single source of truth
- **Reactive Programming**: RxJava3 for asynchronous operations and data streams
- **View Binding**: Type-safe view access without findViewById()

### Key Components
```
├── model/              # Data classes for network and sensor data
├── repository/         # Data repositories for network and sensor operations
├── viewmodel/          # ViewModels for business logic and state management
├── fragment/           # UI fragments (Dashboard, Network, Sensors, Settings)
├── adapter/            # RecyclerView adapters for lists and grids
└── MainActivity.java   # Main entry point with navigation
```

### Technology Stack
- **Language**: Java 8+
- **UI Framework**: Android Views + View Binding
- **Architecture**: MVVM + Repository Pattern
- **Reactive Programming**: RxJava3 + RxAndroid
- **Networking**: OkHttp + Retrofit
- **Navigation**: Fragment-based with Bottom Navigation
- **Material Design**: Material Components for Android

## 🔐 Permissions

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

## 🛠️ Setup Instructions

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **Android SDK**: API 35 (Android 15)
- **Java**: JDK 8 or later
- **Gradle**: 8.2.0 or later

### Installation Steps

1. **Clone the repository:**
```bash
git clone <repository-url>
cd NetworkSensorAppJava
```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `NetworkSensorAppJava` folder
   - Click "OK"

3. **Sync Project:**
   - Android Studio will automatically sync Gradle files
   - Wait for the sync to complete
   - Resolve any dependency issues if prompted

4. **Build and Run:**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`
   - The app will install and launch automatically

### Building from Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## 📱 Usage

### Dashboard Screen
- **Network Overview**: Current connection status and speed metrics
- **Quick Stats**: Signal strength, speed, active sensors, uptime
- **Speed Test**: One-tap network speed testing
- **Sensor Status**: Overview of available and active sensors
- **Recent Activity**: Timeline of recent network and sensor events

### Network Screen
- **Detailed Analysis**: Comprehensive network monitoring
- **Speed Testing**: Multi-endpoint speed tests with progress tracking
- **Connection Info**: WiFi and cellular connection details
- **Signal Monitoring**: Real-time signal strength tracking

### Sensors Screen
- **Real-time Data**: Live sensor readings and visualizations
- **Motion Sensors**: Accelerometer, gyroscope, magnetometer data
- **Environmental**: Temperature, humidity, pressure, light readings
- **GPS Tracking**: Location data with accuracy and speed information

### Settings Screen
- **App Information**: Version, build info, target SDK
- **Feature Overview**: List of available app capabilities
- **Permissions**: Required permissions and their purposes

## 🔧 Key Implementation Details

### Network Speed Testing (Java)
```java
public Observable<SpeedTestResult> performSpeedTest() {
    return Observable.fromCallable(() -> {
        String[] testUrls = {
            "https://httpbin.org/bytes/1000000",
            "https://httpbin.org/bytes/5000000"
        };
        // Perform speed tests and calculate results
        return new SpeedTestResult(downloadSpeed, uploadSpeed, latency);
    }).subscribeOn(Schedulers.io());
}
```

### Sensor Data Collection (Java)
```java
public Observable<SensorData> getSensorData(SensorType sensorType) {
    return Observable.create(emitter -> {
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                SensorData data = new SensorData(sensorType, event.values, event.accuracy);
                emitter.onNext(data);
            }
        };
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }).subscribeOn(Schedulers.io());
}
```

### Modern UI with View Binding (Java)
```java
public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void updateNetworkInfo(NetworkInfo networkInfo) {
        binding.textNetworkName.setText(networkInfo.getNetworkName());
        binding.textDownloadSpeed.setText(String.format("%.1f Mbps", networkInfo.getDownloadSpeed()));
    }
}
```

## 📊 Device Compatibility

### Minimum Requirements
- **Android Version**: API 24 (Android 7.0)
- **Target Version**: API 35 (Android 15)
- **RAM**: 2GB minimum, 4GB recommended
- **Storage**: 100MB for app installation

### Sensor Support
The app automatically detects and adapts to available sensors:
- ✅ **Motion**: Accelerometer, Gyroscope, Magnetometer
- ✅ **Location**: GPS, Network-based location
- ✅ **Environmental**: Temperature, Humidity, Pressure
- ✅ **Ambient**: Light sensor, Proximity sensor
- ✅ **Health**: Heart rate sensor (if available)

### Network Support
- ✅ **WiFi**: 2.4GHz and 5GHz networks
- ✅ **Cellular**: 5G, LTE, 4G, 3G, 2G, GPRS
- ✅ **Other**: Ethernet, VPN connections

## ⚡ Performance Optimizations

- **Efficient Threading**: RxJava3 schedulers for background operations
- **Memory Management**: Proper lifecycle handling and resource cleanup
- **Battery Optimization**: Smart sensor sampling rates
- **Network Efficiency**: Connection pooling and request batching
- **UI Responsiveness**: Non-blocking operations with reactive streams

## 🔒 Security & Privacy

- **Runtime Permissions**: Proper permission handling with user explanations
- **Data Privacy**: All data stays on device, no external transmission
- **Secure Networking**: HTTPS for all network requests
- **Permission Management**: EasyPermissions library for smooth UX

## 🐛 Troubleshooting

### Common Issues

1. **Permissions Denied**
   - Go to Settings → Apps → Network Sensor Monitor → Permissions
   - Enable required permissions manually

2. **Sensors Not Working**
   - Ensure device has the required sensors
   - Check if other apps can access sensors
   - Restart the app

3. **Speed Test Fails**
   - Check internet connection
   - Try different network (WiFi/Mobile)
   - Ensure firewall/VPN isn't blocking requests

4. **Build Errors**
   - Clean and rebuild project: `Build → Clean Project`
   - Sync Gradle files: `File → Sync Project with Gradle Files`
   - Update Android Studio and SDK components

## 📋 Development Notes

### Adding New Features
1. Create data models in `model/` package
2. Implement repository methods for data access
3. Create ViewModel for business logic
4. Design UI layouts with Material Design
5. Implement Fragment with View Binding
6. Add navigation and update MainActivity

### Testing
- Unit tests for repositories and ViewModels
- UI tests for fragments and activities
- Permission testing on different Android versions
- Performance testing with various sensor combinations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For issues, questions, or feature requests:
- 📧 Create an issue on GitHub
- 📖 Check the documentation
- 🔍 Review existing issues and discussions

---

**🎯 Ready to monitor your network and sensors with this powerful Java Android app!**

### Quick Start Checklist
- [ ] Clone the repository
- [ ] Open in Android Studio
- [ ] Sync Gradle files
- [ ] Connect device/emulator
- [ ] Run the app
- [ ] Grant required permissions
- [ ] Start monitoring!

**Built with ❤️ using modern Java Android development practices**