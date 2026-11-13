package com.marbellup.quedamos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.AppState
import com.marbellup.quedamos.data.Need
import com.marbellup.quedamos.data.NeedGroup
import com.marbellup.quedamos.ui.CourtScreen
import com.marbellup.quedamos.ui.Rule
import com.marbellup.quedamos.ui.SectionTitle
import com.marbellup.quedamos.ui.say

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NeedsScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    CourtScreen(
        title = say("¿Qué necesitas para hacer deporte?", "¿Qué necesitas?"),
        subtitle = say(
            "Marca lo que te hace falta. No preguntamos por diagnósticos.",
            "Marca lo que te hace falta.",
        ),
        step = 0,
        bottomBar = {
            if (state.onboarded) bottomBar()
            else Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                Column(Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Button(
                        onClick = state::finishOnboarding,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    ) {
                        Text(
                            if (state.needs.isEmpty()) "Ver todas las actividades"
                            else "Ver qué encaja conmigo",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        },
    ) {
        for (group in NeedGroup.entries) {
            Text(
                group.label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
                    .semantics { heading() },
            )
            FlowRow(
                Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (need in Need.entries.filter { it.group == group }) {
                    val selected = need in state.needs
                    FilterChip(
                        selected = selected,
                        onClick = { state.toggle(need) },
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
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                    )
                }
            }
        }

        SectionTitle("Cómo quieres leer la app")
        Rule()
        SwitchRow(
            title = "Texto grande",
            detail = "Aumenta el tamaño de toda la letra.",
            checked = state.bigText,
            onChange = { state.bigText = it },
        )
        Rule()
        SwitchRow(
            title = "Lectura fácil",
            detail = "Frases más cortas y directas.",
            checked = state.easyRead,
            onChange = { state.easyRead = it },
        )
        Rule()
    }
}

@Composable
private fun SwitchRow(title: String, detail: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChange)
            .heightIn(min = 64.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = null)
    }
}
