package com.marbellup.quedamos.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.R
import com.marbellup.quedamos.data.Sport
import com.marbellup.quedamos.ui.theme.LocalExtra

/** Lectura fácil: frases más cortas y directas. */
val LocalEasyRead = compositionLocalOf { false }

@Composable
fun say(normal: String, easy: String): String = if (LocalEasyRead.current) easy else normal

/* ---------- Deportes: icono, color y foto ---------- */

val Sport.icon: ImageVector
    get() = when (this) {
        Sport.Natacion -> Icons.Filled.Pool
        Sport.Playa -> Icons.Filled.Waves
        Sport.Baloncesto -> Icons.Filled.SportsBasketball
        Sport.Padel -> Icons.Filled.SportsTennis
        Sport.Yoga -> Icons.Filled.SelfImprovement
        Sport.Senderismo -> Icons.Filled.Hiking
    }

val Sport.tint: Color
    get() = when (this) {
        Sport.Natacion -> Color(0xFF1C9AD6)
        Sport.Playa -> Color(0xFF14A0A0)
        Sport.Baloncesto -> Color(0xFFE8622C)
        Sport.Padel -> Color(0xFF2D6BE4)
        Sport.Yoga -> Color(0xFF5D6B92)
        Sport.Senderismo -> Color(0xFF6AA334)
    }

@get:DrawableRes
val Sport.photo: Int
    get() = when (this) {
        Sport.Natacion -> R.drawable.foto_natacion
        Sport.Playa -> R.drawable.foto_playa
        Sport.Baloncesto -> R.drawable.foto_baloncesto
        Sport.Padel -> R.drawable.foto_padel
        Sport.Yoga -> R.drawable.foto_yoga
        Sport.Senderismo -> R.drawable.foto_senderismo
    }

/* ---------- Superficies ---------- */

/** Tarjeta blanca, muy redondeada y con sombra suave. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.large
    val base = Modifier.fillMaxWidth()
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier.then(base),
            shape = shape,
            color = LocalExtra.current.card,
            shadowElevation = 2.dp,
        ) { Column(content = content) }
    } else {
        Surface(
            modifier = modifier.then(base),
            shape = shape,
            color = LocalExtra.current.card,
            shadowElevation = 2.dp,
        ) { Column(content = content) }
    }
}

/** Píldora pequeña con icono y texto (fecha, plazas, estado). */
@Composable
fun Pill(
    text: String,
    icon: ImageVector? = null,
    container: Color = LocalExtra.current.chip,
    content: Color = LocalExtra.current.onChip,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, color = content, maxLines = 1)
    }
}

/** Botón principal en píldora, a todo lo ancho. */
@Composable
fun BigButton(
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        modifier = modifier.fillMaxWidth().heightIn(min = 60.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
        }
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

/** Cabecera de sección: título a la izquierda y «Ver todos →» a la derecha. */
@Composable
fun SectionHeader(
    title: String,
    leading: ImageVector? = null,
    leadingTint: Color = MaterialTheme.colorScheme.tertiary,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 24.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            Icon(leading, contentDescription = null, tint = leadingTint, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(action, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/* ---------- Personas ---------- */

/* Tonos de la referencia: azules, marino y los acentos naranja y verde de sus iconos. */
private val AvatarColors = listOf(
    Color(0xFF2D6BE4), Color(0xFF1B2444), Color(0xFF4A86EC),
    Color(0xFFE8622C), Color(0xFF3F7D3A), Color(0xFF5D6B92),
)

fun initials(name: String): String =
    name.split(" ", " y ").filter { it.isNotBlank() && it.first().isUpperCase() }
        .take(2).joinToString("") { it.first().toString() }

/** Avatar de iniciales: las personas de la demo son ficticias, así que no llevan foto. */
@Composable
fun Avatar(name: String, size: Dp = 60.dp, modifier: Modifier = Modifier) {
    val color = AvatarColors[(name.hashCode() and 0x7fffffff) % AvatarColors.size]
    Box(
        modifier.size(size).clip(CircleShape).background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials(name),
            style = if (size >= 56.dp) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleSmall,
            color = Color.White,
        )
    }
}

/* ---------- Estados ---------- */

enum class Status { Ok, Unverified, Missing, Barrier }

@Composable
fun StatusIcon(status: Status, modifier: Modifier = Modifier) {
    val x = LocalExtra.current
    val (icon, tint) = when (status) {
        Status.Ok -> Icons.Filled.CheckCircle to x.ok
        Status.Unverified -> Icons.Filled.CheckCircle to MaterialTheme.colorScheme.outline
        Status.Missing -> Icons.Filled.ErrorOutline to x.warn
        Status.Barrier -> Icons.Filled.Block to x.barrier
    }
    Icon(icon, contentDescription = null, tint = tint, modifier = modifier.size(26.dp))
}

@Composable
fun StatusRow(status: Status, text: String, detail: String? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.Top,
    ) {
        StatusIcon(status)
        Column(Modifier.padding(start = 14.dp)) {
            Text(text, style = MaterialTheme.typography.bodyLarge)
            if (detail != null) {
                Text(
                    detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun IconLine(icon: ImageVector, text: String, tint: Color = MaterialTheme.colorScheme.primary) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
        Spacer(Modifier.width(14.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

/* ---------- Pantallas ---------- */

/**
 * Logotipo: dos puntos que se encuentran, sobre un cuadrado con degradado azul.
 * Sobre fondo azul ([onDark]) se invierte: cuadrado blanco y puntos azules.
 */
@Composable
fun Logo(size: Dp = 44.dp, onDark: Boolean = false) {
    val top = Color(0xFF4A86EC)
    val bottom = Color(0xFF1F55C8)
    Canvas(Modifier.size(size)) {
        val corner = androidx.compose.ui.geometry.CornerRadius(this.size.width * 0.3f)
        if (onDark) drawRoundRect(Color.White, cornerRadius = corner)
        else drawRoundRect(androidx.compose.ui.graphics.Brush.linearGradient(listOf(top, bottom)), cornerRadius = corner)
        val r = this.size.width * 0.17f
        drawCircle(if (onDark) bottom else Color.White, r, Offset(this.size.width * 0.37f, this.size.height * 0.5f))
        drawCircle(if (onDark) top.copy(alpha = 0.55f) else Color(0xFFA9C8FF), r, Offset(this.size.width * 0.63f, this.size.height * 0.5f))
    }
}

/** Estructura de pantalla secundaria: barra con «volver», título grande, contenido y barra inferior. */
@Composable
fun AppScreen(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    snackbar: SnackbarHostState? = null,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { if (snackbar != null) SnackbarHost(snackbar) },
        bottomBar = bottomBar,
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
        ) {
            if (onBack != null) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = LocalExtra.current.card,
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp).size(52.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            } else {
                Spacer(Modifier.height(16.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp)
                    .semantics { heading() },
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 6.dp),
                )
            }
            content()
        }
    }
}

/** Zona inferior fija con el botón principal. */
@Composable
fun BottomAction(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = LocalExtra.current.card, shadowElevation = 8.dp) {
        Column(
            Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
            content = content,
        )
    }
}

@Composable
fun ExampleNote(text: String = "Datos de ejemplo para la demo") {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
    )
}

