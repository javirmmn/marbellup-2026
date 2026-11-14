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
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.marbellup.quedamos.R

/*
 * Estilo: app deportiva luminosa. Fondo gris azulado claro, tarjetas blancas
 * muy redondeadas con sombra suave, azul intenso para la acción principal
 * y fotos de cada deporte.
 * Los estados conservan su significado: verde = comprobado, ámbar = falta algo,
 * rojo = barrera. Siempre van acompañados de texto.
 */

@Immutable
data class ExtraColors(
    val card: Color,
    val chip: Color,
    val onChip: Color,
    val ok: Color,
    val okContainer: Color,
    val onOkContainer: Color,
    val warn: Color,
    val warnContainer: Color,
    val onWarnContainer: Color,
    val barrier: Color,
    val scrim: Color,
    val notification: Color,
    /** Fondo de mosaicos y tarjetas de deporte sin seleccionar. */
    val tile: Color,
    /** Tarjeta de deporte seleccionada: azul más claro que el botón principal. */
    val tileSelected: Color,
    /** Azul del degradado del banner. */
    val hero: Color,
    /** Llama de «Eventos cerca de ti». */
    val flame: Color,
)

/* Paleta tomada de la referencia del equipo. */
private val LightExtra = ExtraColors(
    card = Color(0xFFFFFFFF),
    chip = Color(0xFFE6EDFC),
    onChip = Color(0xFF1B2444),
    ok = Color(0xFF1E8A5A),
    okContainer = Color(0xFFE6EDFC),
    onOkContainer = Color(0xFF1B2444),
    warn = Color(0xFFB4690E),
    warnContainer = Color(0xFFE6EDFC),
    onWarnContainer = Color(0xFF1B2444),
    barrier = Color(0xFFD64545),
    scrim = Color(0x99172446),
    notification = Color(0xFFF2513A),
    tile = Color(0xFFEEF1F8),
    tileSelected = Color(0xFF4A86EC),
    hero = Color(0xFF2C63D6),
    flame = Color(0xFFF2602B),
)

private val DarkExtra = ExtraColors(
    card = Color(0xFF1A2235),
    chip = Color(0xFF243256),
    onChip = Color(0xFFDCE5FF),
    ok = Color(0xFF7AD3A2),
    okContainer = Color(0xFF243256),
    onOkContainer = Color(0xFFDCE5FF),
    warn = Color(0xFFF2B866),
    warnContainer = Color(0xFF243256),
    onWarnContainer = Color(0xFFDCE5FF),
    barrier = Color(0xFFFF8A80),
    scrim = Color(0xA6050A18),
    notification = Color(0xFFFF6B5A),
    tile = Color(0xFF222B40),
    tileSelected = Color(0xFF3F79E0),
    hero = Color(0xFF2456C2),
    flame = Color(0xFFFF7A45),
)

val LocalExtra = staticCompositionLocalOf { LightExtra }

private val LightColors = lightColorScheme(
    primary = Color(0xFF2D6BE4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6EDFC),
    onPrimaryContainer = Color(0xFF1B2444),
    secondary = Color(0xFF4A5576),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEF1F8),
    onSecondaryContainer = Color(0xFF1B2444),
    tertiary = Color(0xFFF2602B),
    onTertiary = Color(0xFFFFFFFF),
    error = Color(0xFFD64545),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFDE4E1),
    onErrorContainer = Color(0xFF5F0A0A),
    background = Color(0xFFE3ECFB),
    onBackground = Color(0xFF1B2444),
    surface = Color(0xFFE3ECFB),
    onSurface = Color(0xFF1B2444),
    surfaceVariant = Color(0xFFEEF1F8),
    onSurfaceVariant = Color(0xFF4A5576),
    outline = Color(0xFF8C94AD),
    outlineVariant = Color(0xFFDFE3EE),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F9FD),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFEEF1F8),
    surfaceContainerHighest = Color(0xFFE6EAF4),
    inverseSurface = Color(0xFF1B2444),
    inverseOnSurface = Color(0xFFF5F6FC),
    inversePrimary = Color(0xFFA9C4FF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DB2FF),
    onPrimary = Color(0xFF00256B),
    primaryContainer = Color(0xFF1C3F94),
    onPrimaryContainer = Color(0xFFDCE6FF),
    secondary = Color(0xFFBCC5DE),
    onSecondary = Color(0xFF263048),
    secondaryContainer = Color(0xFF2E3852),
    onSecondaryContainer = Color(0xFFDCE2F5),
    tertiary = Color(0xFFFFB27A),
    onTertiary = Color(0xFF4A2200),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF5F0A0A),
    errorContainer = Color(0xFF7A1C1C),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0E1424),
    onBackground = Color(0xFFE4E9F5),
    surface = Color(0xFF0E1424),
    onSurface = Color(0xFFE4E9F5),
    surfaceVariant = Color(0xFF283149),
    onSurfaceVariant = Color(0xFFB7C0D6),
    outline = Color(0xFF7D879E),
    outlineVariant = Color(0xFF323C55),
    surfaceContainerLowest = Color(0xFF0A0F1C),
    surfaceContainerLow = Color(0xFF141B2D),
    surfaceContainer = Color(0xFF1A2235),
    surfaceContainerHigh = Color(0xFF222B40),
    surfaceContainerHighest = Color(0xFF2B354C),
    inverseSurface = Color(0xFFE4E9F5),
    inverseOnSurface = Color(0xFF111A2E),
    inversePrimary = Color(0xFF1F5FE0),
)

/** Atkinson Hyperlegible Next: diseñada por el Braille Institute para baja visión. */
@OptIn(ExperimentalTextApi::class)
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
    headlineLarge = Base.headlineLarge.voice(800, -0.02),
    headlineMedium = Base.headlineMedium.voice(800, -0.015),
    headlineSmall = Base.headlineSmall.voice(800, -0.01),
    titleLarge = Base.titleLarge.voice(800, -0.01),
    titleMedium = Base.titleMedium.voice(700),
    titleSmall = Base.titleSmall.voice(700),
    bodyLarge = Base.bodyLarge.voice(400),
    bodyMedium = Base.bodyMedium.voice(400),
    bodySmall = Base.bodySmall.voice(500),
    labelLarge = Base.labelLarge.voice(700),
    labelMedium = Base.labelMedium.voice(700),
    labelSmall = Base.labelSmall.voice(700),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(30.dp),
)

@Composable
fun QuedamosTheme(
    // Siempre claro, aunque el móvil esté en modo oscuro: es el estilo elegido para la demo.
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalExtra provides if (darkTheme) DarkExtra else LightExtra) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
