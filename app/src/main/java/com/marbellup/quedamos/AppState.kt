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

enum class Tab(val label: String) { ParaTi("Para ti"), Necesidades("Mi perfil"), Panel("Ayuntamiento") }

sealed interface Step {
    val activityId: String

    data class Place(override val activityId: String) : Step
    data class Company(override val activityId: String) : Step
    data class Confirmed(override val activityId: String) : Step
}

data class Booking(val activityId: String, val companion: String)

/** Estado de la demo. Vive en memoria: no hay servidor ni cuenta. */
class AppState : ViewModel() {
    var needs by mutableStateOf(emptySet<Need>())
        private set
    var bigText by mutableStateOf(false)
    var easyRead by mutableStateOf(false)
    var onboarded by mutableStateOf(false)
        private set
    var tab by mutableStateOf(Tab.ParaTi)
    var booking by mutableStateOf<Booking?>(null)
        private set

    val steps = mutableStateListOf<Step>()
    val reported = mutableStateListOf<Barrier>()

    fun toggle(need: Need) {
        needs = if (need in needs) needs - need else needs + need
    }

    fun finishOnboarding() {
        onboarded = true
        tab = Tab.ParaTi
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

    fun closeFlow() {
        steps.clear()
        tab = Tab.ParaTi
    }

    fun reportBarrier(activityId: String, what: String) {
        val place = SampleData.activity(activityId).place
        reported.add(0, Barrier(place, what, 1, BarrierStatus.SinResolver))
    }
}
