# Network Sensor Monitor - Pure Java Android App

A modern, beautiful Android application built entirely in **Java** for monitoring network performance and device sensors in real-time. This app features a stunning Material Design 3 interface with gradient cards, smooth animations, and comprehensive network analysis tools.

## 🌟 Features

### 🎨 Beautiful Modern Design
- **Material Design 3** with custom color schemes
- **Gradient cards** and modern UI components
- **Smooth animations** and transitions
- **Professional UI/UX** with modern components
- **Custom typography** and spacing

### 📊 Network Monitoring
- **Real-time network status** monitoring
- **WiFi analysis** with signal strength
- **Cellular network** detection (2G/3G/4G/5G)
- **Speed testing** functionality
- **Network type** identification
- **IP address** and connection details

### 📱 Device Sensors
- **Accelerometer** monitoring
- **Gyroscope** data tracking
- **Magnetometer** readings
- **Environmental sensors** (temperature, humidity, pressure)
- **Real-time sensor** data visualization

### 🔧 Technical Features
- **Pure Java** Android development
- **Latest Android APIs** (API 35)
- **Modern architecture** with MVVM pattern
- **Dependency injection** with Hilt
- **Room database** for data persistence
- **Retrofit** for network operations
- **Material Components** for UI

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 35
- Java 17
- Minimum Android version: API 24 (Android 7.0)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/NetworkSensorApp.git
   cd NetworkSensorApp
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the NetworkSensorApp folder
   - Click "OK"

3. **Sync and Build**
   - Wait for Gradle sync to complete
   - Build the project (Build → Make Project)
   - Run on device or emulator

### Permissions Required

The app requires the following permissions:
- `INTERNET` - For network monitoring
- `ACCESS_NETWORK_STATE` - Network status checking
- `ACCESS_WIFI_STATE` - WiFi information
- `ACCESS_FINE_LOCATION` - GPS and location services
- `BODY_SENSORS` - Sensor data access

## 📱 App Structure

### Main Components

#### Activities
- **MainActivity.java** - Main dashboard with navigation
- **NetworkMonitorActivity.java** - Network monitoring interface
- **SensorHubActivity.java** - Sensor data visualization

#### Adapters
- **DashboardAdapter.java** - RecyclerView adapter for dashboard items

#### Models
- **DashboardItem.java** - Dashboard data model

#### Application
- **NetworkSensorApplication.java** - Application class with Hilt setup

### Architecture

```
app/
├── src/main/
│   ├── java/com/networksensor/app/
│   │   ├── MainActivity.java              # Main dashboard
│   │   ├── NetworkMonitorActivity.java    # Network monitoring
│   │   ├── SensorHubActivity.java         # Sensor monitoring
│   │   ├── NetworkSensorApplication.java  # Application class
│   │   ├── adapters/                      # RecyclerView adapters
│   │   │   └── DashboardAdapter.java
│   │   └── models/                        # Data models
│   │       └── DashboardItem.java
│   └── res/
│       ├── layout/                        # XML layouts
│       │   ├── activity_main.xml
│       │   ├── activity_network_monitor.xml
│       │   ├── activity_sensor_hub.xml
│       │   ├── item_feature_card.xml
│       │   ├── item_activity.xml
│       │   └── item_status_card.xml
│       ├── values/                        # Resources
│       │   ├── colors.xml
│       │   ├── strings.xml
│       │   └── themes.xml
│       ├── drawable/                      # Icons and graphics
│       │   ├── ic_dashboard.xml
│       │   ├── ic_network.xml
│       │   ├── ic_sensors.xml
│       │   ├── ic_settings.xml
│       │   ├── ic_wifi.xml
│       │   ├── ic_speed.xml
│       │   ├── ic_arrow_forward.xml
│       │   ├── ic_play_arrow.xml
│       │   ├── ic_check_circle.xml
│       │   └── ic_location_on.xml
│       ├── menu/                          # Navigation menus
│       │   └── bottom_nav_menu.xml
│       └── color/                         # Color selectors
│           └── bottom_nav_item_color.xml
```

## 🎨 Design System

### Color Palette
- **Primary Blue**: #2196F3
- **Primary Teal**: #009688
- **Primary Purple**: #9C27B0
- **Success Green**: #4CAF50
- **Warning Orange**: #FF9800
- **Error Red**: #F44336

### Typography
- **Display Large**: 32sp, Bold
- **Headline Large**: 22sp, SemiBold
- **Title Large**: 16sp, SemiBold
- **Body Large**: 16sp, Regular
- **Label Medium**: 12sp, Medium

### Components
- **Material Cards** with rounded corners (16dp)
- **Gradient backgrounds** for feature cards
- **Progress bars** with custom colors
- **Chips** for status indicators

## 🔧 Development

### Key Technologies Used

#### Core Android
- **Java 17** - Primary programming language
- **Android SDK 35** - Latest Android APIs
- **Material Design 3** - Modern UI components
- **ViewBinding** - Type-safe view access

#### Architecture & Dependencies
- **MVVM Pattern** - Architecture pattern
- **Hilt** - Dependency injection
- **Room** - Local database
- **Retrofit** - HTTP client
- **LiveData** - Observable data holders
- **ViewModel** - UI state management

#### UI & Design
- **Material Components** - UI components
- **ConstraintLayout** - Flexible layouts
- **RecyclerView** - Efficient list display
- **CardView** - Material cards
- **BottomNavigationView** - Navigation
- **FloatingActionButton** - Quick actions

### Code Quality
- **Clean Architecture** principles
- **SOLID** design patterns
- **Java coding conventions**
- **Comprehensive error handling**
- **Performance optimization**

## 📊 Features in Detail

### Network Monitoring
- **Real-time connectivity** status
- **Network type** detection (WiFi/Cellular/Ethernet)
- **Signal strength** measurement
- **Speed testing** with visual feedback
- **Network details** (SSID, IP, MAC address)
- **Cellular generation** detection (2G/3G/4G/5G)

### Sensor Hub
- **Accelerometer** data visualization
- **Gyroscope** readings
- **Magnetometer** information
- **Environmental sensors** monitoring
- **Real-time data** display

### Dashboard
- **Quick stats** cards
- **Recent activity** feed
- **Feature navigation** cards
- **Status indicators** with colors
- **Swipe refresh** functionality
- **Bottom navigation** menu

## 🚀 Performance

### Optimizations
- **Efficient RecyclerView** usage
- **Background processing** for heavy operations
- **Memory management** best practices
- **Network request** optimization
- **UI thread** protection

### Battery Optimization
- **Smart sensor** polling
- **Background service** optimization
- **Location services** efficiency
- **Network monitoring** intervals

## 🔒 Security

### Data Protection
- **Permission handling** best practices
- **Secure data storage** with Room
- **Network security** with HTTPS
- **User privacy** protection

### Permissions
- **Runtime permission** requests
- **Permission rationale** dialogs
- **Graceful degradation** for denied permissions

## 📈 Future Enhancements

### Planned Features
- **Network history** tracking
- **Advanced charts** and analytics
- **Export functionality** for data
- **Cloud sync** capabilities
- **Widget support** for quick access
- **Notification** system for alerts

### Technical Improvements
- **Advanced animations** and transitions
- **Accessibility** improvements
- **Internationalization** support
- **Background services** for continuous monitoring

## 🤝 Contributing

We welcome contributions! Please see our contributing guidelines:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Material Design** team for design guidelines
- **Android Developer** community
- **Open source** contributors
- **Icon designers** for beautiful graphics

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Email: support@networksensor.app
- Documentation: [Wiki](https://github.com/yourusername/NetworkSensorApp/wiki)

---

**Built with ❤️ using Pure Java and modern Android development practices**