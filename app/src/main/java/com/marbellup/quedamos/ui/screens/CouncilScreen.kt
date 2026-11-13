package com.marbellup.quedamos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.AppState
import com.marbellup.quedamos.data.BarrierStatus
import com.marbellup.quedamos.data.SampleData
import com.marbellup.quedamos.ui.CourtScreen
import com.marbellup.quedamos.ui.ExampleNote
import com.marbellup.quedamos.ui.Mark
import com.marbellup.quedamos.ui.MarkedLine
import com.marbellup.quedamos.ui.Rule
import com.marbellup.quedamos.ui.SectionTitle
import com.marbellup.quedamos.ui.theme.LocalPista

@Composable
fun CouncilScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    CourtScreen(
        title = "Panel del Ayuntamiento",
        subtitle = "Deporte inclusivo en la ciudad. Datos simulados.",
        step = null,
        council = true,
        bottomBar = bottomBar,
    ) {
        SectionTitle("Este mes")
        Rule(strong = true)
        val newPeople = SampleData.NEW_PARTICIPANTS + if (state.booking != null) 1 else 0
        Figure("Personas que hacen deporte por primera vez", "$newPeople")
        Rule()
        Figure("Instalaciones con sello", "${SampleData.SEALED_PLACES} de ${SampleData.TOTAL_PLACES}")
        Rule()
        Figure("Voluntariado avalado", "${SampleData.BACKED_VOLUNTEERS}")
        Rule()

        SectionTitle("Ocupación por horas")
        Text(
            "Línea hueca: antes. Barra llena: con la app. Las horas vacías de la mañana se están llenando.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
        )
        OccupancyChart()
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)) {
            Text("8:00", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
            Text("21:00", style = MaterialTheme.typography.labelMedium)
        }

        SectionTitle("Barreras avisadas")
        Rule(strong = true)
        for (b in state.reported + SampleData.barriers) {
            MarkedLine(
                mark = if (b.status == BarrierStatus.Resuelta) Mark.Verified else Mark.Barrier,
                text = "${b.what} · ${b.place}",
                detail = "${b.status.label} · ${if (b.reports == 1) "1 aviso" else "${b.reports} avisos"}",
            )
            Rule()
        }

        SectionTitle("Lo que la gente pide y no hay")
        Rule(strong = true)
        for (d in SampleData.unmetDemand) {
            Figure(d.what, "${d.requests}")
            Rule()
        }
        ExampleNote("Todas las cifras son simuladas para la demo")
    }
}

@Composable
private fun Figure(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f).padding(end = 16.dp))
        Text(value, style = MaterialTheme.typography.titleLarge)
    }
}

/** Barras como calles de piscina: una por hora, de 8:00 a 21:00. */
@Composable
private fun OccupancyChart() {
    val pista = LocalPista.current
    val outline = MaterialTheme.colorScheme.outline
    val before = SampleData.occupancyBefore
    val now = SampleData.occupancyNow
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 20.dp)
            .semantics {
                contentDescription =
                    "Ocupación por horas. A las 11 de la mañana pasa del ${before[3]} al ${now[3]} por ciento."
            }
    ) {
        val slot = size.width / before.size
        val bar = slot * 0.62f
        for (i in before.indices) {
            val x = i * slot + (slot - bar) / 2f
            val hNow = size.height * now[i] / 100f
            val hBefore = size.height * before[i] / 100f
            drawRect(pista.lineVerified, Offset(x, size.height - hNow), Size(bar, hNow))
            drawRect(outline, Offset(x, size.height - hBefore), Size(bar, hBefore), style = Stroke(2.dp.toPx()))
        }
        drawLine(outline, Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx())
    }
}
