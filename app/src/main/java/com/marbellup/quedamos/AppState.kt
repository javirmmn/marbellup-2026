package com.marbellup.quedamos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.marbellup.quedamos.data.Barrier
import com.marbellup.quedamos.data.BarrierStatus
import com.marbellup.quedamos.data.Need
import com.marbellup.quedamos.data.SampleData
import com.marbellup.quedamos.data.Sport

enum class Tab(val label: String) {
    Inicio("Inicio"),
    Planes("Mis planes"),
    Ayuntamiento("Ayuntamiento"),
    Perfil("Perfil"),
}

sealed interface Step {
    val activityId: String

    data class Detail(override val activityId: String) : Step
    data class Company(override val activityId: String) : Step
    data class Confirmed(override val activityId: String) : Step
    data object Create : Step {
        override val activityId = ""
    }
}

data class Booking(val activityId: String, val companion: String)

/** Estado de la demo. Vive en memoria: no hay servidor ni cuenta. */
class AppState : ViewModel() {
    var needs by mutableStateOf(emptySet<Need>())
        private set
    var bigText by mutableStateOf(false)
    var easyRead by mutableStateOf(false)
    // Sin intro: la app abre directamente en el panel principal.
    var onboarded by mutableStateOf(true)
        private set
    var tab by mutableStateOf(Tab.Inicio)
    var sport by mutableStateOf<Sport?>(null)
    var booking by mutableStateOf<Booking?>(null)
        private set

    val steps = mutableStateListOf<Step>()
    val reported = mutableStateListOf<Barrier>()

    fun toggle(need: Need) {
        needs = if (need in needs) needs - need else needs + need
    }

    fun finishOnboarding() {
        onboarded = true
        tab = Tab.Inicio
    }

    fun open(step: Step) {
        steps.add(step)
    }

    fun back(): Boolean {
        if (steps.isEmpty()) return false
        steps.removeAt(steps.lastIndex)
        return true
    }

    fun book(activityId: String, companion: String) {
        booking = Booking(activityId, companion)
        steps.clear()
        steps.add(Step.Confirmed(activityId))
    }

    /** Guarda la actividad nueva y abre su ficha. */
    fun create(activity: com.marbellup.quedamos.data.Activity) {
        SampleData.created.add(0, activity)
        sport = null
        steps.clear()
        steps.add(Step.Detail(activity.id))
    }

    fun closeFlow() {
        steps.clear()
        tab = Tab.Planes
    }

    fun reportBarrier(activityId: String, what: String) {
        val place = SampleData.activity(activityId).place
        reported.add(0, Barrier(place, what, 1, BarrierStatus.SinResolver))
    }
}
