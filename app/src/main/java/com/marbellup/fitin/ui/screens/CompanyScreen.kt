package com.marbellup.fitin.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marbellup.fitin.AppState
import com.marbellup.fitin.data.SampleData
import com.marbellup.fitin.ui.AppCard
import com.marbellup.fitin.ui.AppScreen
import com.marbellup.fitin.ui.Avatar
import com.marbellup.fitin.ui.BigButton
import com.marbellup.fitin.ui.BottomAction
import com.marbellup.fitin.ui.ExampleNote
import com.marbellup.fitin.ui.IconLine
import com.marbellup.fitin.ui.SectionHeader
import com.marbellup.fitin.ui.photo
import com.marbellup.fitin.ui.say
import com.marbellup.fitin.ui.theme.LocalExtra

private const val WITH_GROUP = "Con el grupo"
private const val WITH_MINE = "Con alguien mío"

@Composable
fun CompanyScreen(state: AppState, activityId: String) {
    val a = SampleData.activity(activityId)
    var choice by rememberSaveable { mutableStateOf(WITH_GROUP) }

    AppScreen(
        title = "¿Con quién quieres ir?",
        subtitle = "${a.title} · ${a.shortDate}",
        onBack = { state.back() },
        bottomBar = {
            BottomAction {
                BigButton("Reservar mi plaza", Icons.Filled.Check) { state.book(a.id, choice) }
                Text(
                    "Gratis. Puedes cancelar hasta dos horas antes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        },
    ) {
        SectionHeader("Ya van ${a.group.size}")
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Text(
                say(
                    "Personas con y sin discapacidad, en el mismo grupo.",
                    "Va gente con discapacidad y gente sin discapacidad. Todos juntos.",
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 4.dp),
            )
            for (member in a.group) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .semantics(mergeDescendants = true) { },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(member.name, size = 48.dp)
                    Column(Modifier.padding(start = 14.dp)) {
                        Text(member.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            member.how,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        SectionHeader("Tú eliges")
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.selectableGroup()) {
                Option(Icons.Filled.Groups, WITH_GROUP, "Voy por mi cuenta y me uno a quienes ya van.",
                    choice == WITH_GROUP) { choice = WITH_GROUP }
                Divider()
                Option(Icons.Filled.PersonAdd, WITH_MINE, "Viene conmigo un familiar o un amigo. Su plaza también es gratis.",
                    choice == WITH_MINE) { choice = WITH_MINE }
                for (v in a.volunteers) {
                    val key = "Con ${v.name}, voluntariado"
                    Divider()
                    Option(
                        Icons.Filled.VolunteerActivism,
                        "Con ${v.name}",
                        "Voluntariado avalado por ${v.backedBy}. ${v.training}. Ha acompañado ${v.sessions} veces.",
                        choice == key,
                    ) { choice = key }
                }
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
private fun Divider() =
    HorizontalDivider(Modifier.padding(horizontal = 18.dp), color = MaterialTheme.colorScheme.outlineVariant)

@Composable
private fun Option(icon: ImageVector, title: String, detail: String, selected: Boolean, onSelect: () -> Unit) {
    val x = LocalExtra.current
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
            .heightIn(min = 72.dp)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(x.chip),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, contentDescription = null, tint = x.onChip) }
        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        RadioButton(selected = selected, onClick = null)
    }
}

@Composable
fun ConfirmedScreen(state: AppState, activityId: String) {
    val a = SampleData.activity(activityId)
    val x = LocalExtra.current
    val companion = state.booking?.companion ?: WITH_GROUP

    AppScreen(
        title = "¡Plaza reservada!",
        subtitle = say("Te esperan. Nos vemos allí.", "Ya tienes tu plaza."),
        bottomBar = {
            BottomAction {
                OutlinedButton(
                    onClick = state::closeFlow,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                ) { Text("Ver mis planes", style = MaterialTheme.typography.titleMedium) }
            }
        },
    ) {
        Spacer(Modifier.height(20.dp))
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Box {
                Image(
                    painter = painterResource(a.sport.photo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(170.dp),
                )
                Box(
                    Modifier
                        .padding(14.dp)
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(x.ok),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = x.card, modifier = Modifier.size(32.dp))
                }
            }
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(a.title, style = MaterialTheme.typography.titleLarge)
                Text(a.place, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                IconLine(Icons.Filled.CalendarMonth, a.nextSession)
                IconLine(Icons.Filled.Person, "Vas: ${companion.lowercase()}")
                IconLine(Icons.Filled.Groups, a.meetingNote)
                IconLine(Icons.Filled.Directions, a.howToArrive)
                IconLine(Icons.Filled.NotificationsActive, "Te lo recordaremos el día antes.")
            }
        }
        ExampleNote("Reserva de demostración: no se ha enviado a ninguna instalación real")
        Spacer(Modifier.width(1.dp))
    }
}
