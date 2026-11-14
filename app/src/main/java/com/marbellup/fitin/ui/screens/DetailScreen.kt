package com.marbellup.fitin.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marbellup.fitin.AppState
import com.marbellup.fitin.Step
import com.marbellup.fitin.data.Activity
import com.marbellup.fitin.data.Match
import com.marbellup.fitin.data.SampleData
import com.marbellup.fitin.data.Seal
import com.marbellup.fitin.data.matchActivities
import com.marbellup.fitin.ui.Avatar
import com.marbellup.fitin.ui.BigButton
import com.marbellup.fitin.ui.ExampleNote
import com.marbellup.fitin.ui.IconLine
import com.marbellup.fitin.ui.Pill
import com.marbellup.fitin.ui.SectionHeader
import com.marbellup.fitin.ui.Status
import com.marbellup.fitin.ui.StatusRow
import com.marbellup.fitin.ui.icon
import com.marbellup.fitin.ui.photo
import com.marbellup.fitin.ui.theme.LocalExtra
import com.marbellup.fitin.ui.tint
import kotlinx.coroutines.launch

private val DetailTabs = listOf("Detalles", "Accesibilidad", "Ubicación")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(state: AppState, activityId: String) {
    val a = SampleData.activity(activityId)
    val match = matchActivities(state.needs).first { it.activity.id == activityId }
    val x = LocalExtra.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var saved by rememberSaveable { mutableStateOf(false) }
    var reporting by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = x.card,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val scroll = rememberScrollState()
            Column(Modifier.fillMaxSize().verticalScroll(scroll).navigationBarsPadding()) {
                Image(
                    painter = painterResource(a.sport.photo),
                    contentDescription = "Foto de ${a.sport.label.lowercase()}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(320.dp),
                )
                Surface(
                    color = x.card,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    modifier = Modifier.offset(y = (-32).dp),
                ) {
                    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Pill(a.shortDate, Icons.Filled.CalendarMonth)
                            Spacer(Modifier.weight(1f))
                            Pill("${a.group.size}/${a.group.size + a.spotsLeft}", Icons.Filled.Groups)
                        }
                        Text(
                            a.title,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 14.dp).semantics { heading() },
                        )
                        Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Place, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(a.place, style = MaterialTheme.typography.bodyLarge)
                        }

                        Row(
                            Modifier.fillMaxWidth().padding(top = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            InfoTile(a.sport.icon, MaterialTheme.colorScheme.onSurface, "Deporte", a.sport.label)
                            InfoTile(Icons.AutoMirrored.Filled.Accessible, MaterialTheme.colorScheme.primary, "Accesible", accessLabel(match))
                            InfoTile(Icons.Filled.EventSeat, MaterialTheme.colorScheme.onSurface, "Plazas", "Quedan ${a.spotsLeft}")
                        }

                        BigButton(
                            text = "Unirme a la actividad",
                            icon = Icons.Filled.PersonAdd,
                            modifier = Modifier.padding(top = 20.dp),
                            onClick = { state.open(Step.Company(a.id)) },
                        )
                    }
                }

                Column(Modifier.offset(y = (-32).dp)) {
                    SectionHeader(
                        title = "¿Quién viene?",
                        action = "Ver todos",
                        onAction = { state.open(Step.Company(a.id)) },
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Row(
                        Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val shown = a.group.take(4)
                        for (m in shown) Avatar(m.name, size = 64.dp)
                        val more = a.group.size - shown.size + a.volunteers.size
                        if (more > 0) {
                            Box(
                                Modifier.size(64.dp).background(x.chip, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("+$more", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    SecondaryTabRow(
                        selectedTabIndex = tab,
                        containerColor = Color.Transparent,
                        modifier = Modifier.padding(top = 20.dp, start = 8.dp, end = 8.dp),
                        indicator = {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tab),
                                height = 3.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        },
                    ) {
                        DetailTabs.forEachIndexed { i, label ->
                            Tab(
                                selected = tab == i,
                                onClick = { tab = i },
                                text = {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                selectedContentColor = MaterialTheme.colorScheme.primary,
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        when (tab) {
                            0 -> DetailsTab(a)
                            1 -> AccessTab(state, a, match, onReport = { reporting = true })
                            else -> LocationTab(a)
                        }
                    }
                    ExampleNote("Actividad de ejemplo para la demo")
                }
            }

            // Fondo bajo la barra de estado en cuanto la foto sale de pantalla.
            if (scroll.value > 700) {
                Box(Modifier.fillMaxWidth().windowInsetsTopHeight(WindowInsets.statusBars).background(x.card))
            }

            // Botones sobre la foto.
            Row(
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                PhotoButton(Icons.AutoMirrored.Filled.ArrowBack, "Volver") { state.back() }
                Spacer(Modifier.weight(1f))
                PhotoButton(
                    if (saved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    if (saved) "Quitar de guardados" else "Guardar",
                    tint = if (saved) Color(0xFFFF4F9A) else Color.White,
                ) {
                    saved = !saved
                    scope.launch { snackbar.showSnackbar(if (saved) "Guardada en tus planes." else "Quitada de guardados.") }
                }
            }
        }
    }

    if (reporting) {
        ModalBottomSheet(onDismissRequest = { reporting = false }, containerColor = x.card) {
            Text(
                "¿Qué barrera has encontrado?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).semantics { heading() },
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

private fun accessLabel(m: Match): String {
    val total = m.covered.size + m.missing.size
    return when {
        total > 0 -> "Cubre ${m.covered.size}/$total"
        m.activity.seal == Seal.Ayuntamiento -> "Sello Ayto."
        m.activity.seal == Seal.Asociacion -> "Sello asoc."
        else -> "Sin sello"
    }
}

@Composable
private fun RowScope.InfoTile(icon: ImageVector, tint: Color, label: String, value: String) {
    Column(
        Modifier
            .weight(1f)
            .heightIn(min = 96.dp)
            .background(LocalExtra.current.tile, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) { },
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        Text(value, style = MaterialTheme.typography.labelLarge, maxLines = 2)
    }
}

@Composable
private fun PhotoButton(icon: ImageVector, description: String, tint: Color = Color.White, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color(0x80101828),
        contentColor = Color.White,
        modifier = Modifier.size(52.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = description, tint = tint, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun DetailsTab(a: Activity) {
    Text(a.description, style = MaterialTheme.typography.bodyLarge)
    Spacer(Modifier.height(10.dp))
    IconLine(Icons.Filled.Groups, "Grupo mixto, con y sin discapacidad", Color(0xFF3F7D3A))
    IconLine(Icons.Filled.AccessTime, a.schedule, MaterialTheme.colorScheme.primary)
    if (a.volunteers.isNotEmpty()) {
        IconLine(Icons.Filled.Handshake, "Voluntariado avalado disponible", MaterialTheme.colorScheme.primary)
    }
    if (a.offPeak) IconLine(Icons.Filled.Nightlight, "Hora tranquila, sin aglomeraciones", MaterialTheme.colorScheme.primary)
}

@Composable
private fun AccessTab(state: AppState, a: Activity, m: Match, onReport: () -> Unit) {
    val official = a.seal != Seal.Ninguno
    if (state.needs.isEmpty()) {
        Text(
            "Marca lo que necesitas en tu perfil y aquí verás, una por una, si este sitio lo cumple.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
    for (need in m.covered) {
        StatusRow(
            if (official) Status.Ok else Status.Unverified,
            need.label,
            if (official) "Comprobado el ${a.verifiedOn}" else "Lo dicen ${a.confirmations} personas",
        )
    }
    for (need in m.missing) StatusRow(Status.Missing, need.label, "Aquí no lo hay")

    Text(
        "Quién lo ha comprobado",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 18.dp, bottom = 4.dp).semantics { heading() },
    )
    StatusRow(
        if (official) Status.Ok else Status.Unverified,
        a.seal.label,
        if (official) "Visita presencial el ${a.verifiedOn}" else "Pendiente de visita",
    )
    StatusRow(Status.Unverified, "${a.confirmations} personas que han ido lo confirman")

    Text(
        "Lo último que cuentan",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 18.dp, bottom = 4.dp).semantics { heading() },
    )
    for (r in a.reviews) {
        Row(Modifier.padding(vertical = 8.dp)) {
            Avatar(r.author, size = 40.dp)
            Column(Modifier.padding(start = 12.dp)) {
                Text(r.text, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${r.author} · ${r.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
    OutlinedButton(
        onClick = onReport,
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(top = 14.dp).heightIn(min = 48.dp),
    ) {
        Icon(Icons.Filled.ReportProblem, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text("Avisar de una barrera")
    }
}

@Composable
private fun LocationTab(a: Activity) {
    IconLine(Icons.Filled.Place, "${a.place} · ${a.zone}")
    IconLine(Icons.Filled.Directions, a.howToArrive)
    IconLine(Icons.Filled.Groups, a.meetingNote)
}
