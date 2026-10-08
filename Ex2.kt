package DevoirN1

class VehiculeIndisponibleException(immatriculation: String) :
    Exception("Le vehicule $immatriculation n'est pas disponible")

class VehiculeNonTrouveException(immatriculation: String) :
    Exception("Le vehicule avec l'immatriculation $immatriculation n'a pas ete trouve")

abstract class Vehicule(
    val immatriculation: String,
    val marque: String,
    val modele: String,
    kilometrage: Int,
    disponible: Boolean = true
) {
    var kilometrage = kilometrage
        private set
    var disponible = disponible
        private set

    open fun afficherDetails() {
        println("Immatriculation: $immatriculation | $marque $modele " +
                "| Km: $kilometrage | Disponible: $disponible") }

    fun estDisponible(): Boolean = disponible
    fun marquerIndisponible() {
        disponible = false }
    fun marquerDisponible() {
        disponible = true }
    fun mettreAJourKilometrage(km: Int) {
        kilometrage = km }
}

class Voiture(
    immatriculation: String,
    marque: String,
    modele: String,
    kilometrage: Int,
    val nombrePortes: Int,
    val typeCarburant: String
) : Vehicule(immatriculation, marque, modele, kilometrage) {
    override fun afficherDetails() {
        super.afficherDetails()
        println("   Voiture | Portes: $nombrePortes | Carburant: $typeCarburant")
    }
}

class Moto(
    immatriculation: String,
    marque: String,
    modele: String,
    kilometrage: Int,
    val cylindree: Int
) : Vehicule(immatriculation, marque, modele, kilometrage) {
    override fun afficherDetails() {
        super.afficherDetails()
        println("   Moto | Cylindree: $cylindree cm3")
    }
}

class Conducteur(val nom: String, val prenom: String, val numeroPermis: String) {
    fun afficherDetails() {
        println("Conducteur: $nom $prenom | Permis: $numeroPermis")
    }
}

class Reservation(
    val vehicule: Vehicule,
    val conducteur: Conducteur,
    val dateDebut: String,
    val dateFin: String,
    val kilometrageDebut: Int,
    var kilometrageFin: Int? = null
) {
    fun cloturerReservation(kilometrageRetour: Int) {
        kilometrageFin = kilometrageRetour
        vehicule.mettreAJourKilometrage(kilometrageRetour)
        vehicule.marquerDisponible()
    }

    fun afficherDetails() {
        println("Reservation: ${vehicule.immatriculation} | ${conducteur.nom} " +
                "${conducteur.prenom} | $dateDebut -> $dateFin " +
                "| Km debut: $kilometrageDebut | Km fin: ${kilometrageFin ?: "en cours"}")
    }
}

class ParcAutomobile {
    val vehicules = mutableListOf<Vehicule>()
    val reservations = mutableListOf<Reservation>()

    fun ajouterVehicule(vehicule: Vehicule) {
        vehicules.add(vehicule)
    }
    fun supprimerVehicule(immatriculation: String) {
        vehicules.removeIf { it.immatriculation == immatriculation }
    }
    fun reserverVehicule(immatriculation: String, conducteur: Conducteur,
                         dateDebut: String, dateFin: String) {
        val vehicule = vehicules.find { it.immatriculation == immatriculation }
            ?: throw VehiculeNonTrouveException(immatriculation)
        if (!vehicule.estDisponible()) {
            throw VehiculeIndisponibleException(immatriculation) }
        vehicule.marquerIndisponible()
        reservations.add(Reservation(vehicule, conducteur, dateDebut, dateFin,
            vehicule.kilometrage)) }
    fun afficherVehiculesDisponibles() {
        vehicules.filter { it.estDisponible() }.forEach { it.afficherDetails() } }
    fun afficherReservations() {
        reservations.filter { it.kilometrageFin == null }.forEach { it.afficherDetails() } }
}

fun main() {
    val parc = ParcAutomobile()
    parc.ajouterVehicule(Voiture("A-1234", "Dacia",
        "Logan", 50000, 4, "diesel"))
    parc.ajouterVehicule(Voiture("B-5678", "Renault",
        "Clio", 30000, 5, "essence"))
    parc.ajouterVehicule(Moto("C-9012", "Yamaha",
        "MT-07", 12000, 700))

    val c1 = Conducteur("Alami", "Sara", "P001")
    val c2 = Conducteur("Benani", "Omar", "P002")
    c1.afficherDetails()
    c2.afficherDetails()

    try {
        parc.reserverVehicule("A-1234", c1,
            "2025-02-01", "2025-02-05")
        parc.reserverVehicule("C-9012", c2,
            "2025-02-02", "2025-02-06")
        parc.reserverVehicule("A-1234", c2,
            "2025-02-03", "2025-02-04")
    } catch (e: VehiculeIndisponibleException) {
        println("Erreur : ${e.message}")
    } catch (e: VehiculeNonTrouveException) {
        println("Erreur : ${e.message}")
    }
    try {
        parc.reserverVehicule("Z-0000", c1,
            "2025-02-01", "2025-02-05")
    } catch (e: VehiculeIndisponibleException) {
        println("Erreur : ${e.message}")
    } catch (e: VehiculeNonTrouveException) {
        println("Erreur : ${e.message}")
    }
    parc.afficherVehiculesDisponibles()
    parc.afficherReservations()
}