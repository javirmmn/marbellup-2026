package com.marbellup.quedamos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.marbellup.quedamos.R

/*
 * Mundo visual: líneas de pista de un polideportivo.
 * Un suelo de color (azul para quien hace deporte, verde para el Ayuntamiento)
 * y líneas pintadas encima. Cada color de línea significa siempre lo mismo:
 *   amarillo = tú y lo que necesitas
 *   verde    = comprobado
 *   rojo     = barrera
 * El color nunca va solo: cada marca lleva su texto.
 */

@Immutable
data class PistaColors(
    val floor: Color,
    val floorCouncil: Color,
    val onFloor: Color,
    val onFloorMuted: Color,
    val lineYou: Color,
    val lineVerified: Color,
    val lineBarrier: Color,
    val markOutline: Color,
)

private val LightPista = PistaColors(
    floor = Color(0xFF0B4F8A),
    floorCouncil = Color(0xFF17603D),
    onFloor = Color(0xFFFFFFFF),
    onFloorMuted = Color(0xFFD3E4F4),
    lineYou = Color(0xFFF4C430),
    lineVerified = Color(0xFF1B6B45),
    lineBarrier = Color(0xFFB3261E),
    markOutline = Color(0xFF14202B),
)

private val DarkPista = PistaColors(
    floor = Color(0xFF0F3D66),
    floorCouncil = Color(0xFF16482F),
    onFloor = Color(0xFFFFFFFF),
    onFloorMuted = Color(0xFFC6D9EA),
    lineYou = Color(0xFFF4C430),
    lineVerified = Color(0xFF8FD4AB),
    lineBarrier = Color(0xFFFFB4A9),
    markOutline = Color(0xFFE3EAF0),
)

val LocalPista = staticCompositionLocalOf { LightPista }

private val LightColors = lightColorScheme(
    primary = Color(0xFF0B4F8A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD5E6F7),
    onPrimaryContainer = Color(0xFF062E52),
    secondary = Color(0xFF1B6B45),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD3EBDC),
    onSecondaryContainer = Color(0xFF0B3B24),
    tertiary = Color(0xFF6B5200),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE89A),
    onTertiaryContainer = Color(0xFF2A2000),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFADAD5),
    onErrorContainer = Color(0xFF5C0F0A),
    background = Color(0xFFF5F7F9),
    onBackground = Color(0xFF14202B),
    surface = Color(0xFFF5F7F9),
    onSurface = Color(0xFF14202B),
    surfaceVariant = Color(0xFFDDE4EB),
    onSurfaceVariant = Color(0xFF3F4B57),
    outline = Color(0xFF6B7885),
    outlineVariant = Color(0xFFC3CDD6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF3F6),
    surfaceContainer = Color(0xFFE9EEF2),
    surfaceContainerHigh = Color(0xFFE3E9EE),
    surfaceContainerHighest = Color(0xFFDDE4EA),
    inverseSurface = Color(0xFF14202B),
    inverseOnSurface = Color(0xFFF5F7F9),
    inversePrimary = Color(0xFF9CCBF5),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9CCBF5),
    onPrimary = Color(0xFF003256),
    primaryContainer = Color(0xFF11426E),
    onPrimaryContainer = Color(0xFFD5E6F7),
    secondary = Color(0xFF8FD4AB),
    onSecondary = Color(0xFF00391F),
    secondaryContainer = Color(0xFF1B5237),
    onSecondaryContainer = Color(0xFFC4EBD2),
    tertiary = Color(0xFFF4C430),
    onTertiary = Color(0xFF2A2000),
    tertiaryContainer = Color(0xFF5A4700),
    onTertiaryContainer = Color(0xFFFFE9A0),
    error = Color(0xFFFFB4A9),
    onError = Color(0xFF690003),
    errorContainer = Color(0xFF7A1D15),
    onErrorContainer = Color(0xFFFFDAD4),
    background = Color(0xFF0C1722),
    onBackground = Color(0xFFE3EAF0),
    surface = Color(0xFF0C1722),
    onSurface = Color(0xFFE3EAF0),
    surfaceVariant = Color(0xFF2A3642),
    onSurfaceVariant = Color(0xFFBCC7D2),
    outline = Color(0xFF8693A0),
    outlineVariant = Color(0xFF364451),
    surfaceContainerLowest = Color(0xFF08111A),
    surfaceContainerLow = Color(0xFF121E2A),
    surfaceContainer = Color(0xFF16232F),
    surfaceContainerHigh = Color(0xFF1F2C39),
    surfaceContainerHighest = Color(0xFF293744),
    inverseSurface = Color(0xFFE3EAF0),
    inverseOnSurface = Color(0xFF14202B),
    inversePrimary = Color(0xFF0B4F8A),
)

/** Atkinson Hyperlegible Next: diseñada por el Braille Institute para baja visión. */
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
private fun atkinson(weight: Int) = Font(
    resId = R.font.atkinson_next,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private val Atkinson = FontFamily(atkinson(400), atkinson(500), atkinson(700), atkinson(800))

private fun TextStyle.voice(weight: Int, tracking: Double = 0.0) = copy(
    fontFamily = Atkinson,
    fontWeight = FontWeight(weight),
    letterSpacing = tracking.em,
)

private val Base = Typography()

private val AppTypography = Typography(
    displayLarge = Base.displayLarge.voice(800, -0.02),
    displayMedium = Base.displayMedium.voice(800, -0.02),
    displaySmall = Base.displaySmall.voice(800, -0.02),
    headlineLarge = Base.headlineLarge.voice(800, -0.015),
    headlineMedium = Base.headlineMedium.voice(800, -0.015),
    headlineSmall = Base.headlineSmall.voice(700, -0.01),
    titleLarge = Base.titleLarge.voice(700, -0.005),
    titleMedium = Base.titleMedium.voice(700),
    titleSmall = Base.titleSmall.voice(700),
    bodyLarge = Base.bodyLarge.voice(400),
    bodyMedium = Base.bodyMedium.voice(400),
    bodySmall = Base.bodySmall.voice(400),
    labelLarge = Base.labelLarge.voice(700),
    labelMedium = Base.labelMedium.voice(500),
    labelSmall = Base.labelSmall.voice(500),
)

/* Una pista se hace con rectas y arcos: esquinas casi vivas y círculos completos. */
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(12.dp),
)

@Composable
fun QuedamosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalPista provides if (darkTheme) DarkPista else LightPista) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
