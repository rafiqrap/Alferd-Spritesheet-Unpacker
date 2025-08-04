package com.networksensor.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlue.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryBlue,
    
    secondary = PrimaryTeal,
    onSecondary = Color.White,
    secondaryContainer = PrimaryTeal.copy(alpha = 0.2f),
    onSecondaryContainer = PrimaryTeal,
    
    tertiary = PrimaryPurple,
    onTertiary = Color.White,
    tertiaryContainer = PrimaryPurple.copy(alpha = 0.2f),
    onTertiaryContainer = PrimaryPurple,
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondaryDark,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    
    outline = TextSecondaryDark.copy(alpha = 0.5f),
    outlineVariant = TextSecondaryDark.copy(alpha = 0.3f)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlue.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryBlue,
    
    secondary = PrimaryTeal,
    onSecondary = Color.White,
    secondaryContainer = PrimaryTeal.copy(alpha = 0.1f),
    onSecondaryContainer = PrimaryTeal,
    
    tertiary = PrimaryPurple,
    onTertiary = Color.White,
    tertiaryContainer = PrimaryPurple.copy(alpha = 0.1f),
    onTertiaryContainer = PrimaryPurple,
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondaryLight,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,
    
    outline = TextSecondaryLight.copy(alpha = 0.5f),
    outlineVariant = TextSecondaryLight.copy(alpha = 0.3f)
)

@Composable
fun NetworkSensorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use transparent status bar for modern look
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}