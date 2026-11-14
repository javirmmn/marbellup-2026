package com.marbellup.fitin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marbellup.fitin.AppState
import com.marbellup.fitin.Step
import com.marbellup.fitin.Tab
import com.marbellup.fitin.data.matchActivities
import com.marbellup.fitin.ui.AppCard
import com.marbellup.fitin.ui.AppScreen
import com.marbellup.fitin.ui.BigButton
import com.marbellup.fitin.ui.SectionHeader
import com.marbellup.fitin.ui.theme.LocalExtra

@Composable
fun PlansScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    val booking = state.booking
    AppScreen(
        title = "Mis planes",
        subtitle = if (booking == null) "Aquí verás las actividades que reserves." else "Tu próxima actividad.",
        bottomBar = bottomBar,
    ) {
        if (booking != null) {
            val match = matchActivities(state.needs).first { it.activity.id == booking.activityId }
            SectionHeader("Próximamente")
            Column(Modifier.padding(horizontal = 16.dp)) {
                EventCard(match, onOpen = { state.open(Step.Confirmed(booking.activityId)) }, showButton = false)
            }
            Text(
                "Vas ${booking.companion.lowercase()}.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
        } else {
            Spacer(Modifier.height(24.dp))
            AppCard(Modifier.padding(horizontal = 16.dp)) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val x = LocalExtra.current
                    Box(
                        Modifier.size(72.dp).clip(CircleShape).background(x.chip),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.EventAvailable, contentDescription = null, tint = x.onChip, modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Todavía no tienes planes", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    Text(
                        "Elige una actividad y decide con quién vas.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
                    )
                    BigButton("Buscar actividades", Icons.Filled.Search) { state.tab = Tab.Inicio }
                }
            }
        }
    }
}
