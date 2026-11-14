package com.marbellup.fitin.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.marbellup.fitin.data.Need
import com.marbellup.fitin.data.NeedGroup
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
import com.marbellup.fitin.AppState
import com.marbellup.fitin.R
import com.marbellup.fitin.Step
import com.marbellup.fitin.Tab
import com.marbellup.fitin.data.Match
import com.marbellup.fitin.data.SampleData
import com.marbellup.fitin.data.Seal
import com.marbellup.fitin.data.Sport
import com.marbellup.fitin.data.matchActivities
import com.marbellup.fitin.ui.AppCard
import com.marbellup.fitin.ui.ExampleNote
import com.marbellup.fitin.ui.Logo
import com.marbellup.fitin.ui.Pill
import com.marbellup.fitin.ui.SectionHeader
import com.marbellup.fitin.ui.icon
import com.marbellup.fitin.ui.photo
import com.marbellup.fitin.ui.say
import com.marbellup.fitin.ui.theme.LocalExtra
import com.marbellup.fitin.ui.tint
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(state: AppState, bottomBar: @Composable () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val matches = matchActivities(state.needs).filter { state.sport == null || it.activity.sport == state.sport }
    var showEvents by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = bottomBar,
    ) { padding ->
        val headerBlue = LocalExtra.current.hero
        LightStatusIcons()
        Box(Modifier.fillMaxSize().padding(padding)) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Encabezado azul: logo, nombre, mis eventos y necesidades.
            Surface(
                color = headerBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            ) {
                Column(Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(bottom = 18.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Logo(onDark = true)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Fit-In",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f),
                        )
                        val booking = state.booking
                        IconButton(onClick = { showEvents = true }) {
                            Box {
                                Icon(
                                    Icons.Outlined.EventAvailable,
                                    contentDescription = if (booking == null) "Mis eventos" else "Mis eventos, 1 pendiente",
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

                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        NeedsPicker(state)
                    }
                }
            }

            // Deportes.
            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { SportChip(null, state.sport == null) { state.sport = null } }
                items(Sport.entries) { s -> SportChip(s, state.sport == s) { state.sport = s } }
            }

            Row(Modifier.padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                SectionHeader(
                    title = if (state.needs.isEmpty()) "Actividades cerca de ti" else "Encajan contigo",
                    leading = Icons.Filled.LocalFireDepartment,
                    action = if (state.sport != null) "Ver todos" else null,
                    onAction = { state.sport = null },
                    modifier = Modifier.weight(1f),
                )
                // Crear una actividad nueva.
                Surface(
                    onClick = { state.open(Step.Create) },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shadowElevation = 3.dp,
                    modifier = Modifier.padding(top = 12.dp, end = 16.dp).size(46.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Add, contentDescription = "Crear actividad", modifier = Modifier.size(28.dp))
                    }
                }
            }

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
        // Franja azul fija bajo la barra de estado, para que al desplazar no se mezcle con las tarjetas.
        Box(Modifier.fillMaxWidth().windowInsetsTopHeight(WindowInsets.statusBars).background(headerBlue))
        }
    }

    if (showEvents) {
        EventsSheet(
            state = state,
            onDismiss = { showEvents = false },
            onOpen = { id ->
                showEvents = false
                state.open(Step.Detail(id))
            },
        )
    }
}

/** Iconos de la barra de estado en blanco mientras se ve el encabezado azul; se restauran al salir. */
@Composable
private fun LightStatusIcons() {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.context as? android.app.Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.isAppearanceLightStatusBars = false
        onDispose { controller?.isAppearanceLightStatusBars = true }
    }
}

/** Panel de eventos a los que te has apuntado: pendientes y recientes. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsSheet(state: AppState, onDismiss: () -> Unit, onOpen: (String) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = LocalExtra.current.card) {
        Column(Modifier.padding(bottom = 24.dp)) {
            Text(
                "Mis eventos",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 20.dp).semantics { heading() },
            )
            SectionHeader("Pendientes", modifier = Modifier.padding(top = 0.dp))
            val booking = state.booking
            if (booking == null) {
                Text(
                    "No te has apuntado a nada todavía.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            } else {
                val a = SampleData.activity(booking.activityId)
                EventRow(a.sport, a.title, a.shortDate, "Vas ${booking.companion.lowercase()}") { onOpen(a.id) }
            }
            SectionHeader("Recientes")
            for ((id, date) in SampleData.recentEvents) {
                val a = SampleData.activity(id)
                EventRow(a.sport, a.title, date, "Ya fuiste") { onOpen(a.id) }
            }
        }
    }
}

@Composable
private fun EventRow(sport: Sport, title: String, date: String, detail: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(sport.photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(60.dp).clip(MaterialTheme.shapes.small),
        )
        Column(Modifier.weight(1f).padding(start = 14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private val NeedGroup.icon
    get() = when (this) {
        NeedGroup.Movilidad -> Icons.AutoMirrored.Filled.Accessible
        NeedGroup.Sentidos -> Icons.Filled.Hearing
        NeedGroup.Comprension -> Icons.Filled.Lightbulb
        NeedGroup.Apoyo -> Icons.Filled.Handshake
    }

private val NeedGroup.short
    get() = when (this) {
        NeedGroup.Movilidad -> "Moverte"
        NeedGroup.Sentidos -> "Ver y oír"
        NeedGroup.Comprension -> "Entender"
        NeedGroup.Apoyo -> "Apoyo"
    }

/** Necesidades en formato compacto: cuatro desplegables en una cuadrícula de 2 × 2. */
@Composable
private fun NeedsPicker(state: AppState) {
    val groups = NeedGroup.entries.chunked(2)
    Column(
        Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (row in groups) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (group in row) {
                    Box(Modifier.weight(1f)) { NeedDropdown(state, group) }
                }
            }
        }
    }
}

@Composable
private fun NeedDropdown(state: AppState, group: NeedGroup) {
    var open by remember { mutableStateOf(false) }
    val options = Need.entries.filter { it.group == group }
    val count = options.count { it in state.needs }
    val x = LocalExtra.current
    Box {
        Surface(
            onClick = { open = true },
            shape = MaterialTheme.shapes.medium,
            color = if (count > 0) x.chip else x.card,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .semantics {
                    contentDescription = "${group.label}, " +
                        if (count == 0) "nada seleccionado" else "$count seleccionadas"
                },
        ) {
            Row(
                Modifier.padding(start = 12.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(group.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    group.short,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (count > 0) {
                    Box(
                        Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("$count", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false },
            containerColor = LocalExtra.current.card,
            shape = MaterialTheme.shapes.medium,
        ) {
            for (need in options) {
                val checked = need in state.needs
                DropdownMenuItem(
                    text = { Text(need.label, style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = { Checkbox(checked = checked, onCheckedChange = null) },
                    onClick = { state.toggle(need) },
                    modifier = Modifier.heightIn(min = 52.dp),
                )
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
    when {
        // Sin necesidades marcadas: solo el icono, y solo si la actividad tiene sello.
        total == 0 && match.activity.seal != Seal.Ninguno -> SealIcon()
        total == 0 -> Unit
        match.missing.isEmpty() -> Pill("Cubre ${match.covered.size}/$total", icon = Icons.AutoMirrored.Filled.Accessible)
        else -> Pill("Falta ${match.missing.size}", icon = Icons.AutoMirrored.Filled.Accessible)
    }
}

@Composable
private fun SealIcon() {
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(LocalExtra.current.chip)
            .semantics { contentDescription = "Con sello de accesibilidad" },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Accessible,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
    }
}
