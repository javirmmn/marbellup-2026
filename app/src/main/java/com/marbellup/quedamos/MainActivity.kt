package com.marbellup.quedamos

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.marbellup.quedamos.ui.LocalEasyRead
import com.marbellup.quedamos.ui.screens.CompanyScreen
import com.marbellup.quedamos.ui.screens.ConfirmedScreen
import com.marbellup.quedamos.ui.screens.CouncilScreen
import com.marbellup.quedamos.ui.screens.ForYouScreen
import com.marbellup.quedamos.ui.screens.NeedsScreen
import com.marbellup.quedamos.ui.screens.PlaceScreen
import com.marbellup.quedamos.ui.theme.QuedamosTheme

class MainActivity : ComponentActivity() {
    private val state: AppState by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La cabecera siempre es suelo de pista oscuro: iconos de estado claros.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
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

@Composable
private fun App(state: AppState) {
    BackHandler(enabled = state.steps.isNotEmpty()) { state.back() }

    val tabs: @Composable () -> Unit = {
        NavigationBar {
            for (tab in Tab.entries) {
                NavigationBarItem(
                    selected = state.tab == tab,
                    onClick = { state.tab = tab },
                    icon = {
                        Icon(
                            when (tab) {
                                Tab.ParaTi -> Icons.Filled.Home
                                Tab.Necesidades -> Icons.Filled.Person
                                Tab.Panel -> Icons.Filled.Info
                            },
                            contentDescription = null,
                        )
                    },
                    label = { Text(tab.label) },
                )
            }
        }
    }

    val step = state.steps.lastOrNull()
    when {
        !state.onboarded -> NeedsScreen(state, tabs)
        step is Step.Place -> PlaceScreen(state, step.activityId)
        step is Step.Company -> CompanyScreen(state, step.activityId)
        step is Step.Confirmed -> ConfirmedScreen(state, step.activityId)
        state.tab == Tab.ParaTi -> ForYouScreen(state, tabs)
        state.tab == Tab.Necesidades -> NeedsScreen(state, tabs)
        else -> CouncilScreen(state, tabs)
    }
}
