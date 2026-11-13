package com.marbellup.quedamos.ui

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.ui.theme.LocalPista

/** Lectura fácil: frases más cortas y directas. */
val LocalEasyRead = compositionLocalOf { false }

@Composable
fun say(normal: String, easy: String): String = if (LocalEasyRead.current) easy else normal

val FlowSteps = listOf("Qué necesitas", "Para ti", "El sitio", "Con quién", "Reservado")

@Composable
private fun reduceMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/**
 * La firma de la app: una franja de pista vista desde arriba.
 * El recorrido de la persona es la línea amarilla; cada paso la lleva más lejos
 * por la pista, y al reservar llega a la línea de fondo contraria.
 * Sin paso (panel municipal) se dibuja solo la pista.
 */
@Composable
fun CourtBand(step: Int?, modifier: Modifier = Modifier) {
    val pista = LocalPista.current
    val still = reduceMotion()
    val target = (step ?: 0).toFloat()
    val progress = remember { Animatable((target - 1f).coerceAtLeast(0f)) }
    LaunchedEffect(target) {
        if (still) progress.snapTo(target)
        else progress.animateTo(target, spring(dampingRatio = 0.82f, stiffness = 90f))
    }
    val description = step?.let { "Paso ${it + 1} de ${FlowSteps.size}: ${FlowSteps[it]}" }

    Canvas(
        modifier
            .fillMaxWidth()
            .height(72.dp)
            .clipToBounds()
            .then(
                if (description != null) Modifier.semantics { contentDescription = description }
                else Modifier.clearAndSetSemantics { }
            )
    ) {
        val w = size.width
        val h = size.height
        val length = w * 2.4f
        val inset = 20.dp.toPx()
        val stepX = { i: Float -> inset + (length - 2 * inset) * (0.06f + 0.88f * i / (FlowSteps.size - 1)) }
        val markerX = stepX(progress.value)
        val scroll = if (step == null) (length - w) / 2f else (markerX - w * 0.7f).coerceIn(0f, length - w)

        translate(left = -scroll) {
            drawCourt(length, h, inset, pista.onFloor, pista.lineVerified, pista.lineBarrier)
            if (step != null) {
                val mid = h / 2f
                drawLine(
                    pista.lineYou, Offset(inset, mid), Offset(markerX, mid),
                    strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round,
                )
                for (i in FlowSteps.indices) {
                    val x = stepX(i.toFloat())
                    if (i <= progress.value) continue
                    drawCircle(pista.floor, 6.dp.toPx(), Offset(x, mid))
                    drawCircle(pista.onFloor, 6.dp.toPx(), Offset(x, mid), style = Stroke(2.dp.toPx()))
                }
                drawCircle(pista.floor, 13.dp.toPx(), Offset(markerX, mid))
                drawCircle(pista.lineYou, 10.dp.toPx(), Offset(markerX, mid))
            }
        }
    }
}

/** Pista polideportiva: las líneas de varios deportes, cada uno en su color, sobre el mismo suelo. */
private fun DrawScope.drawCourt(length: Float, h: Float, inset: Float, white: Color, green: Color, red: Color) {
    val line = Stroke(2.dp.toPx())
    val thin = Stroke(1.5.dp.toPx())
    val mid = h / 2f
    val main = white.copy(alpha = 0.55f)
    val second = Color(0xFF9FE0BA).copy(alpha = 0.5f)
    val third = Color(0xFFFFB199).copy(alpha = 0.45f)

    // Balonmano y fútbol sala: líneas de fondo, medio campo, círculo central y áreas.
    drawLine(main, Offset(inset, -h), Offset(inset, 2 * h), line.width)
    drawLine(main, Offset(length - inset, -h), Offset(length - inset, 2 * h), line.width)
    drawLine(main, Offset(length / 2f, -h), Offset(length / 2f, 2 * h), line.width)
    drawCircle(main, h * 0.95f, Offset(length / 2f, mid), style = line)
    for (goalX in listOf(inset, length - inset)) {
        drawCircle(main, h * 1.7f, Offset(goalX, mid), style = line)
        drawCircle(
            main, h * 2.5f, Offset(goalX, mid),
            style = Stroke(line.width, pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f))),
        )
    }

    // Baloncesto: la zona y su semicírculo de tiros libres.
    val key = h * 2.1f
    for ((x0, dir) in listOf(inset to 1f, length - inset to -1f)) {
        drawLine(second, Offset(x0, h * 0.14f), Offset(x0 + dir * key, h * 0.14f), thin.width)
        drawLine(second, Offset(x0, h * 0.86f), Offset(x0 + dir * key, h * 0.86f), thin.width)
        drawLine(second, Offset(x0 + dir * key, h * 0.14f), Offset(x0 + dir * key, h * 0.86f), thin.width)
        drawArc(
            second, startAngle = if (dir > 0) -90f else 90f, sweepAngle = 180f, useCenter = false,
            topLeft = Offset(x0 + dir * key - h * 0.36f, mid - h * 0.36f),
            size = Size(h * 0.72f, h * 0.72f), style = thin,
        )
    }

    // Voleibol: las dos líneas de ataque.
    for (x in listOf(length / 2f - h * 1.9f, length / 2f + h * 1.9f)) {
        drawLine(third, Offset(x, -h), Offset(x, 2 * h), thin.width)
    }
}

enum class Mark { You, Verified, Unchecked, Missing, Barrier, Person }

/**
 * Marca de estado en una celda fija: llena si se cumple, hueca si no.
 * Siempre va junto a un texto que dice lo mismo.
 */
@Composable
fun LineMark(mark: Mark, modifier: Modifier = Modifier) {
    val pista = LocalPista.current
    val surface = MaterialTheme.colorScheme.surface
    Canvas(modifier.size(20.dp).clearAndSetSemantics { }) {
        val r = size.minDimension / 2f
        val ring = Stroke(2.dp.toPx())
        val c = center
        when (mark) {
            Mark.You -> {
                drawCircle(pista.lineYou, r - ring.width / 2)
                drawCircle(pista.markOutline, r - ring.width / 2, style = ring)
            }
            Mark.Verified -> {
                drawCircle(pista.lineVerified, r)
                val s = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(surface, Offset(c.x - r * 0.45f, c.y), Offset(c.x - r * 0.1f, c.y + r * 0.35f), s.width, s.cap)
                drawLine(surface, Offset(c.x - r * 0.1f, c.y + r * 0.35f), Offset(c.x + r * 0.5f, c.y - r * 0.35f), s.width, s.cap)
            }
            Mark.Unchecked -> drawCircle(pista.markOutline, r - ring.width / 2, style = ring)
            Mark.Person -> drawCircle(pista.floor, r * 0.42f)
            Mark.Missing -> {
                drawCircle(
                    pista.markOutline, r - ring.width / 2,
                    style = Stroke(ring.width, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f))),
                )
            }
            Mark.Barrier -> {
                drawCircle(pista.lineBarrier, r)
                drawLine(surface, Offset(c.x - r * 0.5f, c.y), Offset(c.x + r * 0.5f, c.y), 3.dp.toPx(), StrokeCap.Round)
            }
        }
    }
}

/** Estructura común: cabecera de pista, contenido con desplazamiento y barra inferior. */
@Composable
fun CourtScreen(
    title: String,
    subtitle: String?,
    step: Int?,
    onBack: (() -> Unit)? = null,
    council: Boolean = false,
    snackbar: SnackbarHostState? = null,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val pista = LocalPista.current
    val floor = if (council) pista.floorCouncil else pista.floor
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { if (snackbar != null) SnackbarHost(snackbar) },
        bottomBar = bottomBar,
        topBar = {
            Surface(color = floor, contentColor = pista.onFloor) {
                Column(Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    } else {
                        Spacer(Modifier.height(20.dp))
                    }
                    Text(
                        title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .semantics { heading() },
                    )
                    if (subtitle != null) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = pista.onFloorMuted,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 6.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    CourtBand(step)
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
                content = content,
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 12.dp)
            .semantics { heading() },
    )
}

/** Raya de pista: separa bloques sin encerrarlos en tarjetas. */
@Composable
fun Rule(strong: Boolean = false) {
    HorizontalDivider(
        thickness = if (strong) 2.dp else 1.dp,
        color = if (strong) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}

@Composable
fun MarkedLine(mark: Mark, text: String, detail: String? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.Top,
    ) {
        LineMark(mark, Modifier.padding(top = 2.dp))
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
fun ExampleNote(text: String = "Datos de ejemplo para la demo") {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
}
