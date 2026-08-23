package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
  primary = Indigo600,
  onPrimary = SurfaceWhite,
  primaryContainer = Indigo50,
  onPrimaryContainer = Indigo700,
  secondary = Slate700,
  onSecondary = SurfaceWhite,
  secondaryContainer = Slate100,
  onSecondaryContainer = Slate800,
  tertiary = Emerald500,
  onTertiary = SurfaceWhite,
  tertiaryContainer = Emerald50,
  onTertiaryContainer = Emerald600,
  background = BackgroundCanvas,
  onBackground = Slate900,
  surface = SurfaceWhite,
  onSurface = Slate900,
  surfaceVariant = Slate50,
  onSurfaceVariant = Slate600,
  outline = BorderSubtle,
  outlineVariant = BorderLight,
  error = Rose500,
  onError = SurfaceWhite,
  errorContainer = Rose50,
  onErrorContainer = Rose500
)

private val DarkColorScheme = darkColorScheme(
  primary = Indigo500,
  onPrimary = SurfaceWhite,
  primaryContainer = Indigo700,
  onPrimaryContainer = Indigo100,
  secondary = Slate300,
  onSecondary = Slate900,
  background = Slate900,
  onBackground = Slate50,
  surface = Slate800,
  onSurface = Slate50,
  outline = Slate700
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.surface.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
