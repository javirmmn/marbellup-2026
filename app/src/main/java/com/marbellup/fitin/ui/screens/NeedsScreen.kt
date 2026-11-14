package com.marbellup.fitin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.marbellup.fitin.AppState
import com.marbellup.fitin.data.Need
import com.marbellup.fitin.data.NeedGroup
import com.marbellup.fitin.ui.AppCard
import com.marbellup.fitin.ui.AppScreen
import com.marbellup.fitin.ui.BigButton
import com.marbellup.fitin.ui.BottomAction
import com.marbellup.fitin.ui.SectionHeader
import com.marbellup.fitin.ui.say
import com.marbellup.fitin.ui.theme.LocalExtra

private val NeedGroup.icon
    get() = when (this) {
        NeedGroup.Movilidad -> Icons.AutoMirrored.Filled.Accessible
        NeedGroup.Sentidos -> Icons.Filled.Hearing
        NeedGroup.Comprension -> Icons.Filled.Lightbulb
        NeedGroup.Apoyo -> Icons.Filled.Handshake
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NeedsScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    val x = LocalExtra.current
    AppScreen(
        title = if (state.onboarded) "Tu perfil" else say("¿Qué necesitas para hacer deporte?", "¿Qué necesitas?"),
        subtitle = say(
            "Marca lo que te hace falta. No preguntamos por diagnósticos.",
            "Marca lo que te hace falta.",
        ),
        bottomBar = {
            if (state.onboarded) bottomBar()
            else BottomAction {
                BigButton(
                    if (state.needs.isEmpty()) "Ver todas las actividades" else "Ver qué encaja conmigo",
                    Icons.Filled.Search,
                    onClick = state::finishOnboarding,
                )
            }
        },
    ) {
        for (group in NeedGroup.entries) {
            SectionHeader(group.label, leading = group.icon, leadingTint = MaterialTheme.colorScheme.primary)
            AppCard(Modifier.padding(horizontal = 16.dp)) {
                FlowRow(
                    Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (need in Need.entries.filter { it.group == group }) {
                        val selected = need in state.needs
                        FilterChip(
                            selected = selected,
                            onClick = { state.toggle(need) },
                            shape = RoundedCornerShape(50),
                            label = {
                                Text(
                                    need.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                )
                            },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Filled.Check, contentDescription = null) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            border = if (selected) null else FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = false, borderColor = x.chip,
                            ),
                        )
                    }
                }
            }
        }

        SectionHeader("Cómo quieres leer la app")
        AppCard(Modifier.padding(horizontal = 16.dp)) {
            SwitchRow("Texto grande", "Aumenta el tamaño de toda la letra.", state.bigText) { state.bigText = it }
            HorizontalDivider(Modifier.padding(horizontal = 18.dp), color = MaterialTheme.colorScheme.outlineVariant)
            SwitchRow("Lectura fácil", "Frases más cortas y directas.", state.easyRead) { state.easyRead = it }
        }
    }
}

@Composable
private fun SwitchRow(title: String, detail: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChange)
            .heightIn(min = 72.dp)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = null)
    }
}
