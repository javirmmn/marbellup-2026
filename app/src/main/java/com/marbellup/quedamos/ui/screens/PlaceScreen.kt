package com.marbellup.quedamos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.AppState
import com.marbellup.quedamos.Step
import com.marbellup.quedamos.data.SampleData
import com.marbellup.quedamos.data.Seal
import com.marbellup.quedamos.ui.CourtScreen
import com.marbellup.quedamos.ui.ExampleNote
import com.marbellup.quedamos.ui.Mark
import com.marbellup.quedamos.ui.MarkedLine
import com.marbellup.quedamos.ui.Rule
import com.marbellup.quedamos.ui.SectionTitle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceScreen(state: AppState, activityId: String) {
    val a = SampleData.activity(activityId)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var reporting by remember { mutableStateOf(false) }
    val needs = state.needs.sortedBy { it.ordinal }
    val official = a.seal != Seal.Ninguno

    CourtScreen(
        title = a.place,
        subtitle = "${a.title} · ${a.schedule}",
        step = 2,
        onBack = { state.back() },
        snackbar = snackbar,
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                Column(Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Button(
                        onClick = { state.open(Step.Company(a.id)) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    ) { Text("Elegir con quién voy", style = MaterialTheme.typography.titleMedium) }
                }
            }
        },
    ) {
        if (needs.isNotEmpty()) {
            SectionTitle("Lo que necesitas, una por una")
            Rule(strong = true)
            for (need in needs) {
                val has = need in a.features
                MarkedLine(
                    mark = when {
                        !has -> Mark.Missing
                        official -> Mark.Verified
                        else -> Mark.Unchecked
                    },
                    text = need.label,
                    detail = when {
                        !has -> "Aquí no lo hay"
                        official -> "Comprobado el ${a.verifiedOn}"
                        else -> "Lo dicen ${a.confirmations} personas, sin comprobar por una entidad"
                    },
                )
                Rule()
            }
        }

        val others = a.features.filterNot { it in state.needs }.sortedBy { it.ordinal }
        if (others.isNotEmpty()) {
            SectionTitle(if (needs.isEmpty()) "Qué tiene este sitio" else "También tiene")
            Text(
                others.joinToString(separator = ", ") { it.label.lowercase() }
                    .replaceFirstChar { it.uppercase() } + ".",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        SectionTitle("Quién lo ha comprobado")
        MarkedLine(
            mark = if (official) Mark.Verified else Mark.Unchecked,
            text = a.seal.label,
            detail = if (official) "Visita presencial el ${a.verifiedOn}" else "Pendiente de visita",
        )
        MarkedLine(
            mark = Mark.You,
            text = "${a.confirmations} personas que han ido lo confirman",
            detail = "Cada opinión lleva su fecha",
        )

        SectionTitle("Lo último que cuentan")
        Rule(strong = true)
        for (review in a.reviews) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(review.text, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${review.author} · ${review.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Rule()
        }

        SectionTitle("Cómo llegar")
        Text(
            a.howToArrive,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(28.dp))
        OutlinedButton(
            onClick = { reporting = true },
            modifier = Modifier.padding(horizontal = 20.dp).heightIn(min = 48.dp),
        ) { Text("Avisar de una barrera") }
        ExampleNote("Sitio y opiniones de ejemplo para la demo")
    }

    if (reporting) {
        ModalBottomSheet(onDismissRequest = { reporting = false }) {
            Text(
                "¿Qué barrera has encontrado?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .semantics { heading() },
            )
            for (option in SampleData.barrierOptions) {
                Text(
                    option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button) {
                            state.reportBarrier(a.id, option)
                            reporting = false
                            scope.launch { snackbar.showSnackbar("Aviso enviado. El Ayuntamiento ya lo ve en su panel.") }
                        }
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
