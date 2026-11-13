package com.marbellup.quedamos.data

/*
 * TODOS LOS DATOS DE ESTE ARCHIVO SON DE EJEMPLO.
 * Personas, instalaciones, fechas y cifras son ficticias y sirven solo para la demo.
 * Antes de un piloto hay que sustituirlas por instalaciones reales comprobadas.
 */

enum class NeedGroup(val label: String) {
    Movilidad("Para moverte"),
    Sentidos("Para ver y oír"),
    Comprension("Para entender"),
    Apoyo("Apoyo durante la actividad"),
}

/** Lo que una persona necesita. Nunca se pregunta por un diagnóstico. */
enum class Need(val group: NeedGroup, val label: String) {
    SinEscalones(NeedGroup.Movilidad, "Acceso sin escalones"),
    Aparcamiento(NeedGroup.Movilidad, "Aparcamiento reservado"),
    Vestuario(NeedGroup.Movilidad, "Vestuario y baño adaptados"),
    Grua(NeedGroup.Movilidad, "Grúa de piscina"),
    Transporte(NeedGroup.Movilidad, "Transporte hasta allí"),
    Signos(NeedGroup.Sentidos, "Lengua de signos"),
    Bucle(NeedGroup.Sentidos, "Bucle magnético"),
    Guia(NeedGroup.Sentidos, "Guía para personas ciegas"),
    Tranquilo(NeedGroup.Sentidos, "Entorno tranquilo"),
    LecturaFacil(NeedGroup.Comprension, "Explicaciones sencillas"),
    Pictogramas(NeedGroup.Comprension, "Pictogramas"),
    GrupoReducido(NeedGroup.Comprension, "Grupo pequeño"),
    Material(NeedGroup.Apoyo, "Material adaptado"),
    Monitor(NeedGroup.Apoyo, "Monitor de apoyo"),
    Acompanante(NeedGroup.Apoyo, "Alguien que me acompañe"),
}

enum class Seal(val label: String) {
    Ayuntamiento("Sello del Ayuntamiento"),
    Asociacion("Sello de una asociación"),
    Ninguno("Sin sello oficial todavía"),
}

data class Review(val author: String, val date: String, val text: String)

data class GroupMember(val name: String, val how: String)

data class Volunteer(
    val id: String,
    val name: String,
    val backedBy: String,
    val training: String,
    val sessions: Int,
)

enum class Sport(val label: String) {
    Natacion("Natación"),
    Playa("Playa"),
    Baloncesto("Baloncesto"),
    Padel("Pádel"),
    Yoga("Yoga"),
    Senderismo("Senderismo"),
}

data class Activity(
    val id: String,
    val sport: Sport,
    val shortDate: String,
    val description: String,
    val title: String,
    val place: String,
    val zone: String,
    val schedule: String,
    val nextSession: String,
    val features: Set<Need>,
    val seal: Seal,
    val verifiedOn: String,
    val confirmations: Int,
    val reviews: List<Review>,
    val group: List<GroupMember>,
    val volunteers: List<Volunteer>,
    val spotsLeft: Int,
    val offPeak: Boolean,
    val howToArrive: String,
    val meetingNote: String,
)

data class Match(val activity: Activity, val covered: List<Need>, val missing: List<Need>)

fun matchActivities(needs: Set<Need>): List<Match> =
    SampleData.all
        .map { a ->
            val ordered = needs.sortedBy { it.ordinal }
            Match(a, ordered.filter { it in a.features }, ordered.filterNot { it in a.features })
        }
        .sortedWith(compareBy<Match> { it.missing.size }.thenByDescending { it.activity.confirmations })

data class Barrier(val place: String, val what: String, val reports: Int, val status: BarrierStatus)

enum class BarrierStatus(val label: String) {
    SinResolver("Sin resolver"),
    EnReparacion("En reparación"),
    Resuelta("Resuelta"),
}

data class UnmetDemand(val what: String, val requests: Int)

object SampleData {

    const val USER_NAME = "Lucía"

    private val rocio = Volunteer("v1", "Rocío", "Asociación de ejemplo", "Formación en apoyo en el agua", 14)
    private val andres = Volunteer("v2", "Andrés", "Club de natación de ejemplo", "Formación en apoyo en el agua", 6)
    private val marta = Volunteer("v3", "Marta", "Asociación de ejemplo", "Formación en guía de montaña", 9)
    private val samir = Volunteer("v4", "Samir", "Club deportivo de ejemplo", "Formación en deporte inclusivo", 21)

    val activities = listOf(
        Activity(
            id = "natacion",
            sport = Sport.Natacion,
            shortDate = "Mar 17/11 · 11:00",
            description = "Natación tranquila en calle reservada. Cada uno va a su ritmo y el monitor ayuda a entrar y salir del agua.",
            title = "Natación en grupo",
            place = "Piscina cubierta municipal",
            zone = "Zona centro",
            schedule = "Martes y jueves, 11:00",
            nextSession = "Martes 17 de noviembre, 11:00",
            features = setOf(
                Need.SinEscalones, Need.Aparcamiento, Need.Vestuario, Need.Grua,
                Need.Monitor, Need.Acompanante, Need.GrupoReducido, Need.Material,
            ),
            seal = Seal.Ayuntamiento,
            verifiedOn = "28 de octubre de 2026",
            confirmations = 12,
            reviews = listOf(
                Review("Paco", "5 de noviembre", "La grúa funciona y el socorrista sabe usarla. Entré al agua en dos minutos."),
                Review("Inés", "22 de octubre", "El vestuario adaptado tiene camilla. La puerta pesa un poco."),
            ),
            group = listOf(
                GroupMember("Paco", "Viene todas las semanas"),
                GroupMember("Inés y su hija Carla", "Vienen juntas"),
                GroupMember("Tomás", "Primera vez, como tú"),
                GroupMember("Elena", "Vecina del barrio"),
            ),
            volunteers = listOf(rocio, andres),
            spotsLeft = 3,
            offPeak = true,
            howToArrive = "Entrada por la puerta lateral, a nivel de calle. Dos plazas reservadas junto a la entrada.",
            meetingNote = "El grupo queda en la entrada diez minutos antes.",
        ),
        Activity(
            id = "playa",
            sport = Sport.Playa,
            shortDate = "Sáb 14/11 · 12:00",
            description = "Baño en el mar con silla anfibia y apoyo del socorrista. Para empezar el fin de semana en buena compañía.",
            title = "Baño asistido en la playa",
            place = "Punto de playa accesible",
            zone = "Zona este",
            schedule = "Sábados, 12:00",
            nextSession = "Sábado 14 de noviembre, 12:00",
            features = setOf(
                Need.SinEscalones, Need.Aparcamiento, Need.Vestuario, Need.Material,
                Need.Monitor, Need.Acompanante,
            ),
            seal = Seal.Ayuntamiento,
            verifiedOn = "15 de octubre de 2026",
            confirmations = 9,
            reviews = listOf(
                Review("Nuria", "1 de noviembre", "Pasarela hasta la orilla y silla anfibia disponible. Hay sombra."),
            ),
            group = listOf(
                GroupMember("Nuria", "Viene con su pareja"),
                GroupMember("Álvaro", "Viene con dos amigos"),
            ),
            volunteers = listOf(rocio),
            spotsLeft = 4,
            offPeak = false,
            howToArrive = "Pasarela de madera desde el paseo marítimo hasta la orilla.",
            meetingNote = "El grupo queda junto al puesto de socorrismo.",
        ),
        Activity(
            id = "baloncesto",
            sport = Sport.Baloncesto,
            shortDate = "Vie 20/11 · 19:00",
            description = "Partido de baloncesto en silla abierto a todo el mundo. Prestan sillas deportivas: juega quien quiera, la use a diario o no.",
            title = "Baloncesto en silla, equipo mixto",
            place = "Pabellón polideportivo",
            zone = "Zona oeste",
            schedule = "Viernes, 19:00",
            nextSession = "Viernes 20 de noviembre, 19:00",
            features = setOf(
                Need.SinEscalones, Need.Aparcamiento, Need.Vestuario, Need.Material, Need.Signos,
            ),
            seal = Seal.Asociacion,
            verifiedOn = "2 de noviembre de 2026",
            confirmations = 7,
            reviews = listOf(
                Review("Dani", "8 de noviembre", "Prestan sillas deportivas. Juega quien quiera, use silla a diario o no."),
            ),
            group = listOf(
                GroupMember("Dani", "Capitán del equipo"),
                GroupMember("Lola y Kike", "Hermanos, juegan juntos"),
                GroupMember("Sergio", "Intérprete de lengua de signos"),
            ),
            volunteers = listOf(samir),
            spotsLeft = 5,
            offPeak = false,
            howToArrive = "Rampa en la entrada principal. Ascensor hasta la pista.",
            meetingNote = "El equipo queda en la pista quince minutos antes.",
        ),
        Activity(
            id = "padel",
            sport = Sport.Padel,
            shortDate = "Sáb 14/11 · 10:00",
            description = "Pádel por parejas con monitor. Pista ancha y pelotas de presión baja para quien las necesite.",
            title = "Pádel para todos",
            place = "Pistas de pádel municipales",
            zone = "Zona centro",
            schedule = "Sábados, 10:00",
            nextSession = "Sábado 14 de noviembre, 10:00",
            features = setOf(Need.SinEscalones, Need.Aparcamiento, Need.Material, Need.Monitor),
            seal = Seal.Asociacion,
            verifiedOn = "20 de octubre de 2026",
            confirmations = 4,
            reviews = listOf(
                Review("Carmen", "25 de octubre", "Pista ancha y monitor atento. El baño adaptado está en otro edificio."),
            ),
            group = listOf(GroupMember("Carmen", "Juega en pareja"), GroupMember("Luis", "Busca pareja")),
            volunteers = listOf(samir),
            spotsLeft = 2,
            offPeak = true,
            howToArrive = "Acceso a nivel desde el aparcamiento.",
            meetingNote = "Se queda en la pista 2.",
        ),
        Activity(
            id = "yoga",
            sport = Sport.Yoga,
            shortDate = "Mié 18/11 · 18:00",
            description = "Yoga suave en grupo pequeño, con luz tenue y sin música alta. La profesora explica cada postura con dibujos.",
            title = "Yoga tranquilo",
            place = "Sala del centro cívico",
            zone = "Zona norte",
            schedule = "Miércoles, 18:00",
            nextSession = "Miércoles 18 de noviembre, 18:00",
            features = setOf(
                Need.SinEscalones, Need.Tranquilo, Need.GrupoReducido, Need.LecturaFacil,
                Need.Pictogramas, Need.Bucle,
            ),
            seal = Seal.Ninguno,
            verifiedOn = "",
            confirmations = 3,
            reviews = listOf(
                Review("Sofía", "29 de octubre", "Poca gente, luz suave y sin música alta. La profesora explica con dibujos."),
            ),
            group = listOf(GroupMember("Sofía", "Viene con su madre"), GroupMember("Hugo", "Viene solo")),
            volunteers = emptyList(),
            spotsLeft = 6,
            offPeak = true,
            howToArrive = "Planta baja, sin escalones.",
            meetingNote = "La sala abre diez minutos antes.",
        ),
        Activity(
            id = "senderismo",
            sport = Sport.Senderismo,
            shortDate = "Dom 22/11 · 9:00",
            description = "Ruta por la sierra con silla de montaña. El grupo se turna para llevarla y todos llegan juntos al mirador.",
            title = "Senderismo con silla de montaña",
            place = "Sendero de la sierra",
            zone = "Salida en autobús",
            schedule = "Un domingo al mes, 9:00",
            nextSession = "Domingo 22 de noviembre, 9:00",
            features = setOf(Need.Material, Need.Acompanante, Need.Transporte, Need.Guia),
            seal = Seal.Asociacion,
            verifiedOn = "10 de octubre de 2026",
            confirmations = 6,
            reviews = listOf(
                Review("Julián", "12 de octubre", "Cuatro personas llevan la silla por turnos. Llegamos al mirador todos."),
            ),
            group = listOf(GroupMember("Julián", "Viene con su hijo"), GroupMember("Pilar", "Camina con guía")),
            volunteers = listOf(marta),
            spotsLeft = 2,
            offPeak = false,
            howToArrive = "Autobús adaptado desde el centro. El sendero no tiene baño.",
            meetingNote = "El autobús sale a las 9:00 en punto.",
        ),
    )

    /** Actividades que crea la persona usuaria durante la demo (solo en memoria). */
    val created = androidx.compose.runtime.mutableStateListOf<Activity>()

    /** Las creadas primero, luego las de ejemplo. */
    val all: List<Activity> get() = created + activities

    fun activity(id: String): Activity = all.first { it.id == id }

    /** Eventos a los que Lucía ya fue (ejemplo), para el apartado «Recientes». */
    val recentEvents = listOf(
        "yoga" to "Mié 4/11 · 18:00",
        "padel" to "Sáb 31/10 · 10:00",
    )

    val barrierOptions = listOf(
        "Ascensor o grúa averiados",
        "Plaza reservada ocupada",
        "Escalón o rampa en mal estado",
        "Baño adaptado cerrado",
    )

    val barriers = listOf(
        Barrier("Pabellón polideportivo", "Ascensor averiado", 6, BarrierStatus.EnReparacion),
        Barrier("Pistas de pádel municipales", "Baño adaptado en otro edificio", 4, BarrierStatus.SinResolver),
        Barrier("Punto de playa accesible", "Pasarela con tablas sueltas", 3, BarrierStatus.Resuelta),
    )

    val unmetDemand = listOf(
        UnmetDemand("Lengua de signos en natación", 7),
        UnmetDemand("Transporte hasta la piscina", 5),
        UnmetDemand("Actividades por la tarde en zona norte", 4),
        UnmetDemand("Guía para personas ciegas en pádel", 2),
    )

    /** Ocupación (%) por hora, de 8:00 a 21:00, antes y con la app. */
    val hours = (8..21).toList()
    val occupancyBefore = listOf(35, 22, 15, 12, 14, 18, 20, 16, 30, 62, 85, 92, 88, 60)
    val occupancyNow = listOf(38, 34, 41, 47, 39, 30, 26, 29, 41, 66, 86, 92, 88, 61)

    const val NEW_PARTICIPANTS = 38
    const val SEALED_PLACES = 9
    const val TOTAL_PLACES = 14
    const val BACKED_VOLUNTEERS = 23
}
