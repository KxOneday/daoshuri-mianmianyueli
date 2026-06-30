package com.timecalendar.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Pink40,
    onPrimary = Color.White,
    primaryContainer = Pink90,
    onPrimaryContainer = Pink10,
    secondary = Rose40,
    onSecondary = Color.White,
    secondaryContainer = Rose90,
    onSecondaryContainer = Pink10,
    tertiary = Purple40,
    onTertiary = Color.White,
    tertiaryContainer = Purple90,
    onTertiaryContainer = Pink10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Pink10,
    background = Gray99,
    onBackground = Gray10,
    surface = Gray99,
    onSurface = Gray10,
    surfaceVariant = Gray95,
    onSurfaceVariant = Gray20
)

private val DarkColorScheme = darkColorScheme(
    primary = Pink80,
    onPrimary = Pink20,
    primaryContainer = Pink30,
    onPrimaryContainer = Pink90,
    secondary = Rose90,
    onSecondary = Pink20,
    secondaryContainer = Rose40,
    onSecondaryContainer = Pink90,
    tertiary = Purple90,
    onTertiary = Pink20,
    tertiaryContainer = Purple40,
    onTertiaryContainer = Pink90,
    error = Red90,
    onError = Pink20,
    errorContainer = Red40,
    onErrorContainer = Red90,
    background = Gray10,
    onBackground = Gray90,
    surface = Gray10,
    onSurface = Gray90,
    surfaceVariant = Gray20,
    onSurfaceVariant = Gray90
)

@Composable
fun TimeCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
