package com.example.mariaradioarchivum.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue10,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue40,
    secondary = DarkBlue80,
    onSecondary = DarkBlue10,
    secondaryContainer = DarkBlue30,
    onSecondaryContainer = DarkBlue90,

    tertiary = Pink80,
    onTertiary = Pink10,
    tertiaryContainer = Pink90,
    onTertiaryContainer = Pink10,

    error = Red80,
    onError = Red10,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Grey05,
    onBackground = Grey90,
    surface = BlueGrey30,
    onSurface = BlueGrey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey10,

    surfaceVariant = TransparentBlack,
    onSurfaceVariant = BlueGrey80,

    outline = BlueGrey80
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Blue90,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    inversePrimary = Blue80,

    secondary = DarkBlue40,
    onSecondary = Blue90,
    secondaryContainer = DarkBlue90,
    onSecondaryContainer = DarkBlue10,

    tertiary = Pink40,
    onTertiary = Pink90,
    tertiaryContainer = Pink90,
    onTertiaryContainer = Pink10,

    error = Red40,
    onError = Red90,
    errorContainer = Red90,
    onErrorContainer = Red10,

    background = Grey99,
    onBackground = Grey10,

    surface = Color.White,
    onSurface = BlueGrey05,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,

    surfaceVariant = TransparentWhite,
    onSurfaceVariant = BlueGrey30,

    outline = BlueGrey50
)

@Composable
fun MáriaRádióArchívumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}