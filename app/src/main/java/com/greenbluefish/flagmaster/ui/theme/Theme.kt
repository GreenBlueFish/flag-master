package com.greenbluefish.flagmaster.ui.theme

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

private val LightColors = lightColorScheme(
    primary = OceanBlue,
    primaryContainer = OceanBlueContainer,
    onPrimaryContainer = OnOceanBlueContainer,
    secondary = TealGlobe,
    secondaryContainer = TealGlobeContainer,
    onSecondaryContainer = OnTealGlobeContainer,
    tertiary = CompassAmber,
    tertiaryContainer = CompassAmberContainer,
    onTertiaryContainer = OnCompassAmberContainer,
    background = PaperSurface,
    surface = PaperSurface,
    surfaceVariant = PaperSurfaceVariant,
    onBackground = OnPaper,
    onSurface = OnPaper,
    onSurfaceVariant = OnPaperVariant,
)

private val DarkColors = darkColorScheme(
    primary = OceanBlueDark,
    primaryContainer = OceanBlueContainerDark,
    onPrimaryContainer = OnOceanBlueContainerDark,
    secondary = TealGlobeDark,
    secondaryContainer = TealGlobeContainerDark,
    onSecondaryContainer = OnTealGlobeContainerDark,
    tertiary = CompassAmberDark,
    tertiaryContainer = CompassAmberContainerDark,
    onTertiaryContainer = OnCompassAmberContainerDark,
    background = PaperSurfaceDark,
    surface = PaperSurfaceDark,
    surfaceVariant = PaperSurfaceVariantDark,
    onBackground = OnPaperDark,
    onSurface = OnPaperDark,
    onSurfaceVariant = OnPaperVariantDark,
)

/**
 * App-wide Material 3 theme. Provides both light and dark color schemes and
 * opts into dynamic color on Android 12+ (falling back to the hand-tuned
 * flag/globe palette on older devices).
 */
@Composable
fun FlagMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
