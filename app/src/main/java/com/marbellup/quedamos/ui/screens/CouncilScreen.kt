package com.marbellup.quedamos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.AppState
import com.marbellup.quedamos.data.BarrierStatus
import com.marbellup.quedamos.data.SampleData
import com.marbellup.quedamos.ui.AppCard
import com.marbellup.quedamos.ui.AppScreen
import com.marbellup.quedamos.ui.ExampleNote
import com.marbellup.quedamos.ui.Pill
import com.marbellup.quedamos.ui.SectionHeader
import com.marbellup.quedamos.ui.Status
import com.marbellup.quedamos.ui.StatusRow
import com.marbellup.quedamos.ui.theme.LocalExtra

@Composable
fun CouncilScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    val x = LocalExtra.current
    AppScreen(
        title = "Panel del Ayuntamiento",
        subtitle = "Deporte inclusivo en la ciudad este mes.",
        bottomBar = bottomBar,
    ) {
        Pill(
            "Datos simulados",
            container = x.warnContainer,
            content = x.onWarnContainer,
            modifier = Modifier.padding(start = 20.dp, top = 12.dp),
        )
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val newPeople = SampleData.NEW_PARTICIPANTS + if (state.booking != null) 1 else 0
            Stat(Icons.Filled.PersonAdd, MaterialTheme.colorScheme.primary, "$newPeople", "Empiezan a hacer deporte")
            Stat(Icons.Filled.Verified, x.ok, "${SampleData.SEALED_PLACES}/${SampleData.TOTAL_PLACES}", "Sitios con sello")
            Stat(Icons.Filled.Handshake, x.flame, "${SampleData.BACKED_VOLUNTEERS}", "Voluntarios avalados")
        }

        SectionHeader("Ocupación por horas", modifier = Modifier.padding(top = 0.dp))
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "Las horas vacías de la mañana se están llenando.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Row(Modifier.padding(top = 10.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Legend(MaterialTheme.colorScheme.primary, "Con la app")
                    Spacer(Modifier.width(16.dp))
                    Legend(MaterialTheme.colorScheme.outlineVariant, "Antes")
                }
                OccupancyChart()
                Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    Text("8:00", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text("21:00", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        SectionHeader("Barreras avisadas")
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                val all = state.reported + SampleData.barriers
                all.forEachIndexed { i, b ->
                    StatusRow(
                        if (b.status == BarrierStatus.Resuelta) Status.Ok else Status.Barrier,
                        "${b.what} · ${b.place}",
                        "${b.status.label} · ${if (b.reports == 1) "1 aviso" else "${b.reports} avisos"}",
                    )
                    if (i < all.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }

        SectionHeader("Lo que la gente pide y no hay")
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                SampleData.unmetDemand.forEachIndexed { i, d ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 12.dp).semantics(mergeDescendants = true) { },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(d.what, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f).padding(end = 12.dp))
                        Pill("${d.requests} peticiones")
                    }
                    if (i < SampleData.unmetDemand.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
        ExampleNote("Todas las cifras son simuladas para la demo")
    }
}

@Composable
private fun RowScope.Stat(icon: ImageVector, tint: Color, value: String, label: String) {
    Surface(
        modifier = Modifier.weight(1f).heightIn(min = 132.dp).semantics(mergeDescendants = true) { },
        shape = MaterialTheme.shapes.medium,
        color = LocalExtra.current.card,
        shadowElevation = 3.dp,
    ) {
        Column(Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Legend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(14.dp)) { drawRoundRect(color, cornerRadius = CornerRadius(4.dp.toPx())) }
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Dos barras por hora: antes (gris) y con la app (azul). */
@Composable
private fun OccupancyChart() {
    val now = MaterialTheme.colorScheme.primary
    val before = MaterialTheme.colorScheme.outlineVariant
    val b = SampleData.occupancyBefore
    val n = SampleData.occupancyNow
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .semantics {
                contentDescription = "Ocupación por horas. A las 11 de la mañana pasa del ${b[3]} al ${n[3]} por ciento."
            }
    ) {
        val slot = size.width / b.size
        val bar = slot * 0.34f
        val r = CornerRadius(bar / 2f)
        for (i in b.indices) {
            val x0 = i * slot + slot * 0.12f
            val hb = size.height * b[i] / 100f
            val hn = size.height * n[i] / 100f
            drawRoundRect(before, Offset(x0, size.height - hb), Size(bar, hb), r)
            drawRoundRect(now, Offset(x0 + bar + slot * 0.06f, size.height - hn), Size(bar, hn), r)
        }
    }
}
