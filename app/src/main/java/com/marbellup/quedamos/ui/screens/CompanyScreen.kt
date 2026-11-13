package com.marbellup.quedamos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.AppState
import com.marbellup.quedamos.data.SampleData
import com.marbellup.quedamos.ui.CourtScreen
import com.marbellup.quedamos.ui.ExampleNote
import com.marbellup.quedamos.ui.Mark
import com.marbellup.quedamos.ui.MarkedLine
import com.marbellup.quedamos.ui.Rule
import com.marbellup.quedamos.ui.SectionTitle
import com.marbellup.quedamos.ui.say

private const val WITH_GROUP = "Con el grupo"
private const val WITH_MINE = "Con alguien mío"

@Composable
fun CompanyScreen(state: AppState, activityId: String) {
    val a = SampleData.activity(activityId)
    var choice by rememberSaveable { mutableStateOf(WITH_GROUP) }

    CourtScreen(
        title = "¿Con quién quieres ir?",
        subtitle = "${a.title} · ${a.nextSession}",
        step = 3,
        onBack = { state.back() },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                Column(Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Button(
                        onClick = { state.book(a.id, choice) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    ) { Text("Reservar mi plaza", style = MaterialTheme.typography.titleMedium) }
                    Text(
                        "Gratis. Puedes cancelar hasta dos horas antes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                }
            }
        },
    ) {
        SectionTitle("Ya van ${a.group.size}")
        Text(
            say(
                "Personas con y sin discapacidad, en el mismo grupo.",
                "Va gente con discapacidad y gente sin discapacidad. Todos juntos.",
            ),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
        )
        Rule(strong = true)
        for (member in a.group) {
            MarkedLine(Mark.Person, member.name, member.how)
            Rule()
        }

        SectionTitle("Tú eliges")
        Column(Modifier.selectableGroup()) {
            Rule(strong = true)
            Option(
                title = WITH_GROUP,
                detail = "Voy por mi cuenta y me uno a quienes ya van.",
                selected = choice == WITH_GROUP,
                onSelect = { choice = WITH_GROUP },
            )
            Rule()
            Option(
                title = WITH_MINE,
                detail = "Viene conmigo un familiar o un amigo. Su plaza también es gratis.",
                selected = choice == WITH_MINE,
                onSelect = { choice = WITH_MINE },
            )
            Rule()
            for (v in a.volunteers) {
                val key = "Con ${v.name}, voluntariado"
                Option(
                    title = "Con ${v.name}",
                    detail = "Voluntariado avalado por ${v.backedBy}. ${v.training}. " +
                        "Ha acompañado ${v.sessions} veces.",
                    selected = choice == key,
                    onSelect = { choice = key },
                )
                Rule()
            }
        }
        if (a.volunteers.isEmpty()) {
            Text(
                "Esta actividad aún no tiene voluntariado avalado. Se lo hemos apuntado al Ayuntamiento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
        ExampleNote("Personas de ejemplo para la demo")
    }
}

@Composable
private fun Option(title: String, detail: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
            .heightIn(min = 64.dp)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Column(Modifier.padding(start = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun ConfirmedScreen(state: AppState, activityId: String) {
    val a = SampleData.activity(activityId)
    val companion = state.booking?.companion ?: WITH_GROUP

    CourtScreen(
        title = "Plaza reservada",
        subtitle = say("Has cruzado la pista entera. Nos vemos allí.", "Ya tienes tu plaza."),
        step = 4,
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                Column(Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    OutlinedButton(
                        onClick = state::closeFlow,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    ) { Text("Volver a mis actividades", style = MaterialTheme.typography.titleMedium) }
                }
            }
        },
    ) {
        Text(
            a.nextSession,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 28.dp),
        )
        Text(
            "${a.title} · ${a.place}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
        )
        Rule(strong = true)
        Fact("Vas", companion)
        Rule()
        Fact("Dónde quedáis", a.meetingNote)
        Rule()
        Fact("Cómo llegar", a.howToArrive)
        Rule()
        Fact("Aviso", "Te lo recordaremos el día antes.")
        Rule()
        ExampleNote("Reserva de demostración: no se ha enviado a ninguna instalación real")
    }
}

@Composable
private fun Fact(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
