package com.marbellup.quedamos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.AppState
import com.marbellup.quedamos.Step
import com.marbellup.quedamos.Tab
import com.marbellup.quedamos.data.Match
import com.marbellup.quedamos.data.SampleData
import com.marbellup.quedamos.data.Seal
import com.marbellup.quedamos.data.matchActivities
import com.marbellup.quedamos.ui.CourtScreen
import com.marbellup.quedamos.ui.ExampleNote
import com.marbellup.quedamos.ui.LineMark
import com.marbellup.quedamos.ui.Mark
import com.marbellup.quedamos.ui.Rule
import com.marbellup.quedamos.ui.SectionTitle
import com.marbellup.quedamos.ui.say

@Composable
fun ForYouScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    val matches = matchActivities(state.needs)
    val full = matches.count { it.missing.isEmpty() }
    CourtScreen(
        title = "Hola, ${SampleData.USER_NAME}",
        subtitle = when {
            state.needs.isEmpty() -> "Estas son las actividades de tu ciudad."
            full == 0 -> "Ninguna actividad cubre todo todavía. Estas son las que más se acercan."
            full == 1 -> "1 actividad cubre todo lo que necesitas."
            else -> "$full actividades cubren todo lo que necesitas."
        },
        step = 1,
        bottomBar = bottomBar,
    ) {
        state.booking?.let { booking ->
            val a = SampleData.activity(booking.activityId)
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable(role = Role.Button) { state.open(Step.Confirmed(a.id)) }
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    "Tu próxima sesión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    "${a.title} · ${a.nextSession}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }

        ExampleNote()
        Rule(strong = true)
        matches.forEachIndexed { index, match ->
            ActivityRow(
                match = match,
                best = index == 0 && state.needs.isNotEmpty() && match.missing.isEmpty(),
                onOpen = { state.open(Step.Place(match.activity.id)) },
            )
            Rule()
        }

        if (state.needs.isEmpty()) {
            SectionTitle("Aún no has marcado nada")
            Text(
                say(
                    "Dinos qué necesitas y ordenaremos las actividades por lo que cubren.",
                    "Dinos qué necesitas. Así te enseñamos primero lo que te sirve.",
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            TextButton(
                onClick = { state.tab = Tab.Necesidades },
                modifier = Modifier.padding(horizontal = 8.dp),
            ) { Text("Marcar lo que necesito") }
        }
    }
}

@Composable
private fun ActivityRow(match: Match, best: Boolean, onOpen: () -> Unit) {
    val a = match.activity
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxWidth()
            .then(if (best) Modifier.background(colors.primaryContainer) else Modifier)
            .clickable(role = Role.Button, onClickLabel = "Ver el sitio", onClick = onOpen)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        val ink = if (best) colors.onPrimaryContainer else colors.onSurface
        val soft = if (best) colors.onPrimaryContainer else colors.onSurfaceVariant
        Text(
            a.title,
            style = if (best) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
            color = ink,
            modifier = Modifier.semantics { heading() },
        )
        Text("${a.place} · ${a.zone}", style = MaterialTheme.typography.bodyLarge, color = ink)
        Text(a.schedule, style = MaterialTheme.typography.bodyLarge, color = ink)

        val total = match.covered.size + match.missing.size
        if (total > 0) {
            Row(
                Modifier.padding(top = 8.dp).semantics(mergeDescendants = true) { },
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(match.covered.size) { LineMark(Mark.Verified) }
                repeat(match.missing.size) { LineMark(Mark.Missing) }
            }
            Text(
                if (match.missing.isEmpty()) "Cubre las $total cosas que necesitas"
                else "Cubre ${match.covered.size} de $total. Falta: " +
                    match.missing.joinToString { it.label.lowercase() },
                style = MaterialTheme.typography.titleMedium,
                color = ink,
            )
        }
        Text(
            when (a.seal) {
                Seal.Ninguno -> "${a.seal.label} · ${a.confirmations} personas lo confirman"
                else -> "${a.seal.label} · comprobado el ${a.verifiedOn}"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = soft,
        )
        Text(
            "Van ${a.group.size} · quedan ${a.spotsLeft} plazas",
            style = MaterialTheme.typography.bodyMedium,
            color = soft,
        )
    }
}
