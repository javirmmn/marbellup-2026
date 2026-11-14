package com.marbellup.fitin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marbellup.fitin.AppState
import com.marbellup.fitin.data.Activity
import com.marbellup.fitin.data.GroupMember
import com.marbellup.fitin.data.Need
import com.marbellup.fitin.data.NeedGroup
import com.marbellup.fitin.data.SampleData
import com.marbellup.fitin.data.Seal
import com.marbellup.fitin.data.Sport
import com.marbellup.fitin.ui.AppCard
import com.marbellup.fitin.ui.AppScreen
import com.marbellup.fitin.ui.BigButton
import com.marbellup.fitin.ui.BottomAction
import com.marbellup.fitin.ui.SectionHeader
import com.marbellup.fitin.ui.icon
import com.marbellup.fitin.ui.theme.LocalExtra

/**
 * Crear una actividad. Las necesidades de tu perfil vienen marcadas por defecto
 * y se pueden cambiar antes de confirmar.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateScreen(state: AppState) {
    var title by rememberSaveable { mutableStateOf("") }
    var sport by rememberSaveable { mutableStateOf(Sport.Baloncesto) }
    var place by rememberSaveable { mutableStateOf("") }
    var day by rememberSaveable { mutableStateOf("Sáb 21/11") }
    var hour by rememberSaveable { mutableStateOf("10:00") }
    var spots by rememberSaveable { mutableIntStateOf(6) }
    var description by rememberSaveable { mutableStateOf("") }
    var needs by rememberSaveable { mutableStateOf(state.needs.map { it.name }.toSet()) }

    val ready = title.isNotBlank() && place.isNotBlank() && day.isNotBlank() && hour.isNotBlank()

    AppScreen(
        title = "Nueva actividad",
        subtitle = "Lo que tienes en tu perfil ya viene marcado. Cámbialo si hace falta.",
        onBack = { state.back() },
        bottomBar = {
            BottomAction {
                BigButton("Crear actividad", Icons.Filled.Check, enabled = ready) {
                    state.create(
                        buildActivity(title, sport, place, day, hour, spots, description,
                            needs.map { Need.valueOf(it) }.toSet())
                    )
                }
                if (!ready) {
                    Text(
                        "Falta el nombre, el lugar, el día o la hora.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                }
            }
        },
    ) {
        SectionHeader("Lo básico")
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Field("Nombre de la actividad", title) { title = it }
                Field("Lugar", place) { place = it }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Field("Día", day, Modifier.weight(1f)) { day = it }
                    Field("Hora", hour, Modifier.weight(1f)) { hour = it }
                }
                Field("Descripción (opcional)", description, singleLine = false) { description = it }
            }
        }

        SectionHeader("Deporte")
        FlowRow(
            Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (s in Sport.entries) {
                val selected = s == sport
                FilterChip(
                    selected = selected,
                    onClick = { sport = s },
                    shape = RoundedCornerShape(50),
                    leadingIcon = { Icon(s.icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = { Text(s.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp)) },
                    colors = chipColors(),
                    border = if (selected) null else FilterChipDefaults.filterChipBorder(
                        enabled = true, selected = false, borderColor = LocalExtra.current.chip,
                    ),
                )
            }
        }

        SectionHeader("Plazas")
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 12.dp).semantics { stateDescription = "$spots plazas" },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Personas en total, contigo", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                FilledTonalIconButton(onClick = { if (spots > 2) spots-- }, shape = CircleShape) {
                    Icon(Icons.Filled.Remove, contentDescription = "Una plaza menos")
                }
                Text("$spots", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.width(44.dp))
                FilledTonalIconButton(onClick = { if (spots < 30) spots++ }, shape = CircleShape) {
                    Icon(Icons.Filled.Add, contentDescription = "Una plaza más")
                }
            }
        }

        SectionHeader("Necesidades que cubre")
        Text(
            "Marcadas según tu perfil.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
        )
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            Column(Modifier.padding(14.dp)) {
                for (group in NeedGroup.entries) {
                    Text(
                        group.label,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (need in Need.entries.filter { it.group == group }) {
                            val selected = need.name in needs
                            FilterChip(
                                selected = selected,
                                onClick = { needs = if (selected) needs - need.name else needs + need.name },
                                shape = RoundedCornerShape(50),
                                leadingIcon = if (selected) {
                                    { Icon(Icons.Filled.Check, contentDescription = null) }
                                } else null,
                                label = { Text(need.label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp)) },
                                colors = chipColors(),
                                border = if (selected) null else FilterChipDefaults.filterChipBorder(
                                    enabled = true, selected = false, borderColor = LocalExtra.current.chip,
                                ),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.padding(8.dp))
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    containerColor = LocalExtra.current.card,
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
    iconColor = MaterialTheme.colorScheme.primary,
)

@Composable
private fun Field(
    label: String,
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = true,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedBorderColor = LocalExtra.current.chip,
        ),
        modifier = modifier,
    )
}

private fun buildActivity(
    title: String,
    sport: Sport,
    place: String,
    day: String,
    hour: String,
    spots: Int,
    description: String,
    needs: Set<Need>,
): Activity {
    val when_ = "${day.trim()} · ${hour.trim()}"
    return Activity(
        id = "creada-${System.currentTimeMillis()}",
        sport = sport,
        shortDate = when_,
        description = description.ifBlank { "Actividad creada por ${SampleData.USER_NAME}. ¡Apúntate!" },
        title = title.trim(),
        place = place.trim(),
        zone = "Marbella",
        schedule = when_,
        nextSession = when_,
        features = needs,
        seal = Seal.Ninguno,
        verifiedOn = "",
        confirmations = 0,
        reviews = emptyList(),
        group = listOf(GroupMember("${SampleData.USER_NAME} (tú)", "Organizas la actividad")),
        volunteers = emptyList(),
        spotsLeft = (spots - 1).coerceAtLeast(0),
        offPeak = false,
        howToArrive = place.trim(),
        meetingNote = "Quedáis en la entrada diez minutos antes.",
    )
}
