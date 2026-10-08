package DevoirN1

open class Personne(val nom: String, val prenom: String, val email: String) {
    fun afficherInfos() {
        println("Nom: $nom | Prenom: $prenom | Email: $email")
    }
}

class Utilisateur(
    nom: String,
    prenom: String,
    email: String,
    val idUtilisateur: Int
) : Personne(nom, prenom, email) {
    val emprunts = mutableListOf<Emprunt>()

    fun emprunterLivre(livre: Livre, dateEmprunt: String) {
        if (livre.disponiblePourEmprunt()) {
            emprunts.add(Emprunt(this, livre, dateEmprunt))
            livre.mettreAJourStock(livre.nombreExemplaires - 1)
        } else {
            println("Livre indisponible : ${livre.titre}")
        }
    }

    fun afficherEmprunts() {
        emprunts.forEach { it.afficherDetails() }
    }
}

class Livre(
    val titre: String,
    val auteur: String,
    val isbn: String,
    var nombreExemplaires: Int
) {
    fun afficherDetails() {
        println("Titre: $titre | Auteur: $auteur | ISBN: $isbn | Exemplaires: $nombreExemplaires")
    }

    fun disponiblePourEmprunt(): Boolean = nombreExemplaires > 0

    fun mettreAJourStock(nouveauStock: Int) {
        nombreExemplaires = nouveauStock
    }
}

class Emprunt(
    val utilisateur: Utilisateur,
    val livre: Livre,
    val dateEmprunt: String,
    var dateRetour: String? = null
) {
    fun afficherDetails() {
        println("Utilisateur: ${utilisateur.nom} | Livre: ${livre.titre} " +
                "| Emprunt: $dateEmprunt | Retour: ${dateRetour ?: "pas encore retourne"}")
    }

    fun retournerLivre() {
        dateRetour = java.time.LocalDate.now().toString()
        livre.mettreAJourStock(livre.nombreExemplaires + 1)
    }
}

abstract class GestionBibliotheque {
    val utilisateurs = mutableListOf<Utilisateur>()
    val livres = mutableListOf<Livre>()

    abstract fun ajouterUtilisateur(utilisateur: Utilisateur)
    abstract fun ajouterLivre(livre: Livre)
    abstract fun afficherTousLesLivres()
}

class Bibliotheque : GestionBibliotheque() {
    override fun ajouterUtilisateur(utilisateur: Utilisateur) {
        utilisateurs.add(utilisateur)
    }

    override fun ajouterLivre(livre: Livre) {
        livres.add(livre)
    }

    override fun afficherTousLesLivres() {
        livres.forEach { it.afficherDetails() }
    }

    fun rechercherLivreParTitre(titre: String): Livre? =
        livres.find { it.titre == titre }
}

fun main() {
    val l1 = Livre("Kotlin Basics", "Jetbrains", "111", 2)
    val l2 = Livre("Clean Code", "Robert Martin", "222", 1)
    val u1 = Utilisateur("Alami", "Sara", "sara@mail.com", 1)
    val u2 = Utilisateur("Benani", "Omar", "omar@mail.com", 2)
    val biblio = Bibliotheque()
    biblio.ajouterLivre(l1)
    biblio.ajouterLivre(l2)
    biblio.ajouterUtilisateur(u1)
    biblio.ajouterUtilisateur(u2)

    u1.emprunterLivre(l1, "2025-01-10")
    u2.emprunterLivre(l2, "2025-01-11")
    biblio.afficherTousLesLivres()
    u1.afficherInfos()
    u2.afficherInfos()
    u1.afficherEmprunts()
    u2.afficherEmprunts()

    u1.emprunts[0].retournerLivre()
    u1.afficherEmprunts()
    biblio.rechercherLivreParTitre("Kotlin Basics")?.afficherDetails()
}