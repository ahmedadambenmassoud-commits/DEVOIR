import kotlinx.coroutines.*


class Commande(val id:Int ,val plats: List<String>, val total: Double) {

    fun afficherDetails() {
        println("Commande n°$id")
        println("Plats : $plats")
        println("Total : $total")
    }
}

class Serveur(val nom: String) {
    fun prendreCommande(id: Int, prix: List<Double>, plats: List<Double>): Commande {
        return prendreCommande(id,plats,prix)
    }
    fun afficherCommande(commandes: List<Commande>) {
        println("Commandes prises par $nom :")
    }
}
class Cuisinier(val plat: String) {
    fun preparerPlat(plat: Int): String {
        Thread.sleep(3000)
        return "Le plat $plat est prêt"
    }
}

class Cuisine(val cuisiniers: List<Cuisinier>) {
   fun gererPreparationCommande(commande: Commande) = coroutineScope {
        cuisiniers.mapIndexed { index, cuisinier ->
            async(Dispatchers.IO) {
                for (i in commande.plats.indices) {
                    if (i % cuisiniers.size == index) {
                        println(cuisinier.preparerPlat(commande.plats[i].length))
                    }
                }
            }
        }.awaitAll()
    }
}
class Caisse {
    suspend fun traiterPaiement(commande: Commande): Boolean {
        delay(500)
        if (Random.nextBoolean()) throw Exception("Paiement refusé")
        println("Paiement accepté : ${commande.total} DH")
        return true
    }
}

    class Restaurant(val cuisine: Cuisine, val caisse: Caisse) {
        val commandes = mutableListOf<Commande>()

        fun prendreCommandeEtTraiter(serveur: Serveur, plats: List<String>, prix: List<Double>) {
            val commande = serveur.prendreCommande(plats, prix)
            commandes.add(commande)
            commande.afficherDetails()

            try {
                cuisine.preparerCommande(commande)
                caisse.traiterPaiement(commande)
            } catch (e: Exception) {
                println("Erreur : ${e.message}")
            }
        }

    fun afficherCommandesEnCours() {
        commandes.forEach { it.afficherDetails() }
    }       
}

fun main() = runBlocking {
    val restaurant = Restaurant(
        Cuisine(listOf(Cuisinier("Ahemd"), Cuisinier("Salma"))),
        Caisse()
    )

    restaurant.prendreCommandeEtTraiter(
        Serveur("Fatima"),
        listOf("Pizza", "Salade"),
        listOf(60.0, 25.0)
    )

    restaurant.afficherCommandesEnCours()
}
