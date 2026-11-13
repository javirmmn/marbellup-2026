package com.marbellup.quedamos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.marbellup.quedamos.ui.LocalEasyRead
import com.marbellup.quedamos.ui.screens.CompanyScreen
import com.marbellup.quedamos.ui.screens.ConfirmedScreen
import com.marbellup.quedamos.ui.screens.CouncilScreen
import com.marbellup.quedamos.ui.screens.CreateScreen
import com.marbellup.quedamos.ui.screens.DetailScreen
import com.marbellup.quedamos.ui.screens.HomeScreen
import com.marbellup.quedamos.ui.screens.NeedsScreen
import com.marbellup.quedamos.ui.screens.PlansScreen
import com.marbellup.quedamos.ui.theme.LocalExtra
import com.marbellup.quedamos.ui.theme.QuedamosTheme

class MainActivity : ComponentActivity() {
    private val state: AppState by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fondo siempre claro: iconos de las barras del sistema en oscuro.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
        )
        setContent {
            QuedamosTheme {
                val density = LocalDensity.current
                val scaled = Density(density.density, density.fontScale * if (state.bigText) 1.3f else 1f)
                CompositionLocalProvider(
                    LocalDensity provides scaled,
                    LocalEasyRead provides state.easyRead,
                ) { App(state) }
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun App(state: AppState) {
    BackHandler(enabled = state.steps.isNotEmpty()) { state.back() }

    val tabs: @Composable () -> Unit = {
        NavigationBar(containerColor = LocalExtra.current.card, tonalElevation = 0.dp) {
            for (tab in Tab.entries) {
                val selected = state.tab == tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { state.tab = tab },
                    icon = {
                        Icon(
                            when (tab) {
                                Tab.Inicio -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
                                Tab.Planes -> if (selected) Icons.Filled.EventNote else Icons.Outlined.EventNote
                                Tab.Ayuntamiento -> if (selected) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance
                                Tab.Perfil -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
                            },
                            contentDescription = null,
                        )
                    },
                    label = { Text(tab.label, style = MaterialTheme.typography.labelMedium, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }
    }

    val step = state.steps.lastOrNull()
    when {
        !state.onboarded -> NeedsScreen(state, tabs)
        step is Step.Detail -> DetailScreen(state, step.activityId)
        step is Step.Company -> CompanyScreen(state, step.activityId)
        step is Step.Confirmed -> ConfirmedScreen(state, step.activityId)
        step is Step.Create -> CreateScreen(state)
        state.tab == Tab.Inicio -> HomeScreen(state, tabs)
        state.tab == Tab.Planes -> PlansScreen(state, tabs)
        state.tab == Tab.Ayuntamiento -> CouncilScreen(state, tabs)
        else -> NeedsScreen(state, tabs)
    }
}
