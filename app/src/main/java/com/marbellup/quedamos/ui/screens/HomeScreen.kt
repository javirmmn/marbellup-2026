package com.marbellup.quedamos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.AppState
import com.marbellup.quedamos.R
import com.marbellup.quedamos.Step
import com.marbellup.quedamos.Tab
import com.marbellup.quedamos.data.Match
import com.marbellup.quedamos.data.SampleData
import com.marbellup.quedamos.data.Seal
import com.marbellup.quedamos.data.Sport
import com.marbellup.quedamos.data.matchActivities
import com.marbellup.quedamos.ui.AppCard
import com.marbellup.quedamos.ui.ExampleNote
import com.marbellup.quedamos.ui.Logo
import com.marbellup.quedamos.ui.Pill
import com.marbellup.quedamos.ui.SectionHeader
import com.marbellup.quedamos.ui.icon
import com.marbellup.quedamos.ui.photo
import com.marbellup.quedamos.ui.say
import com.marbellup.quedamos.ui.theme.LocalExtra
import com.marbellup.quedamos.ui.tint
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val matches = matchActivities(state.needs).filter { state.sport == null || it.activity.sport == state.sport }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = bottomBar,
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState()),
        ) {
            // Cabecera: logo, nombre y avisos.
            Row(
                Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Logo()
                Spacer(Modifier.width(12.dp))
                Text(
                    "Quedamos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f),
                )
                val booking = state.booking
                IconButton(onClick = {
                    scope.launch {
                        snackbar.showSnackbar(
                            if (booking == null) "No tienes avisos nuevos."
                            else "Recuerda: ${SampleData.activity(booking.activityId).nextSession}."
                        )
                    }
                }) {
                    Box {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = if (booking == null) "Avisos" else "Avisos, 1 nuevo",
                            modifier = Modifier.size(28.dp),
                        )
                        if (booking != null) {
                            Box(
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(LocalExtra.current.notification)
                            )
                        }
                    }
                }
            }

            Hero(onAction = { state.tab = Tab.Perfil })

            // Deportes.
            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { SportChip(null, state.sport == null) { state.sport = null } }
                items(Sport.entries) { s -> SportChip(s, state.sport == s) { state.sport = s } }
            }

            SectionHeader(
                title = if (state.needs.isEmpty()) "Actividades cerca de ti" else "Encajan contigo",
                leading = Icons.Filled.LocalFireDepartment,
                action = if (state.sport != null) "Ver todos" else null,
                onAction = { state.sport = null },
                modifier = Modifier.padding(top = 0.dp),
            )

            Column(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                for (m in matches) {
                    EventCard(m, onOpen = { state.open(Step.Detail(m.activity.id)) })
                }
            }
            ExampleNote("Actividades de ejemplo para la demo · Fotos: Unsplash")
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Hero(onAction: () -> Unit) {
    val deep = LocalExtra.current.hero
    Box(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(232.dp)
            .clip(MaterialTheme.shapes.large),
    ) {
        Image(
            painter = painterResource(R.drawable.foto_baloncesto),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0f to deep.copy(alpha = 0.95f),
                        0.5f to deep.copy(alpha = 0.7f),
                        1f to deep.copy(alpha = 0.05f),
                    )
                )
        )
        Column(
            Modifier.fillMaxHeight().padding(22.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                say("Haz deporte con quien quieras", "Haz deporte acompañado"),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.widthIn(max = 230.dp).semantics { heading() },
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Sitios accesibles y gente con quien ir.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.widthIn(max = 230.dp),
            )
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Text("Lo que necesito", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun SportChip(sport: Sport?, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = if (selected) LocalExtra.current.tileSelected else LocalExtra.current.card,
        shadowElevation = if (selected) 3.dp else 1.dp,
        modifier = Modifier
            .width(98.dp)
            .heightIn(min = 88.dp)
            .semantics {
                this.selected = selected
                role = Role.Tab
            },
    ) {
        Column(
            Modifier.padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                sport?.icon ?: Icons.Filled.Apps,
                contentDescription = null,
                tint = if (selected) colors.onPrimary else sport?.tint ?: colors.primary,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                sport?.label ?: "Todos",
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) colors.onPrimary else colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun EventCard(match: Match, onOpen: () -> Unit, showButton: Boolean = true) {
    val a = match.activity
    val x = LocalExtra.current
    AppCard(onClick = onOpen) {
        Row(Modifier.height(IntrinsicSize.Min).heightIn(min = 168.dp)) {
            Box(
                Modifier
                    .width(132.dp)
                    .fillMaxHeight(),
            ) {
                Image(
                    painter = painterResource(a.sport.photo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                Pill(
                    text = a.shortDate.substringBefore(" ·"),
                    icon = Icons.Filled.CalendarMonth,
                    container = x.scrim,
                    content = Color.White,
                    iconTint = Color.White,
                    modifier = Modifier.padding(8.dp),
                )
            }
            Column(
                Modifier.weight(1f).padding(start = 14.dp, end = 12.dp, top = 14.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    a.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Place, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        a.place,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    a.shortDate.substringAfter("· "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Groups, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${a.group.size}/${a.group.size + a.spotsLeft}",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        MatchPill(match)
                    }
                    if (showButton) {
                        Button(
                            onClick = onOpen,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        ) { Text("Unirme", style = MaterialTheme.typography.titleSmall) }
                    }
                }
            }
        }
    }
}

/** Equivale al chip «Nivel» de la referencia: dice cuánto de lo que necesitas cubre la actividad. */
@Composable
fun MatchPill(match: Match) {
    val total = match.covered.size + match.missing.size
    val text = when {
        total == 0 && match.activity.seal != Seal.Ninguno -> "Con sello"
        total == 0 -> "Sin sello"
        match.missing.isEmpty() -> "Cubre ${match.covered.size}/$total"
        else -> "Falta ${match.missing.size}"
    }
    Pill(text, icon = Icons.AutoMirrored.Filled.Accessible)
}
