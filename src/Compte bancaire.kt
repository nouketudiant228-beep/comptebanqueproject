import kotlin.random.Random

fun main(){

    println("============ COMPTE BANCAIRE =============")

    // Création d'un compte pour commencer.
    println("Pour commencer, vous devez créer un compte.")

    //// Nom du titulaire du compte
    println("Nom du titulaire : ")
    val nomTitulaire = readln()

    //// Prénom du titulaire du compte
    println("Prénom du titulaire : ")
    val prenomTitulaire = readln()

    //// Date de naissance du titulaire du compte
    println("Date de naissance du titulaire (sous forme jj/mm/aaaa) : ")
    val dateNaissanceTitulaire = readln()

    //// Adresse du titulaire du compte
    println("Adresse du titulaire : ")
    val adresseTitulaire = readln()

    //// Instanciation (création du compte)
    val compteCree = CompteBancaire(nomTitulaire, prenomTitulaire, dateNaissanceTitulaire, adresseTitulaire)

    //// Validation de la création du compte
    println("Compte créé avec succès !")
    println("")
    println("""
        :: :: :: Voici les informations concernant votre compte :: :: ::
        Nom du titulaire : $nomTitulaire
        Prénom du titulaire : $prenomTitulaire
        Date de naissance du titulaire : $dateNaissanceTitulaire
        Adresse du Titulaire : $adresseTitulaire
        Numéro de compte : ${compteCree.numeroCompte}
        Code PIN (à garder jalousement) : ${compteCree.codeSecret}
    """.trimIndent())

    do{
        var codeIntrusion = false

        // Affichage des fonctionnalités concernant le compte bancaire ET demande d'action d'utilisateur.
        println("")
        println("Voici les fonctionnalités proposées par la banque : ")

        afficherFonctions()

        println("Veuillez entrer le numéro correspondant à l'action à réaliser :")

        // Exécution des fonctions selon l'action choisie par l'utilisateur.
        var listeActions = mutableListOf<String>("0","1","2","3","4","5")
        val action = readln()
        check(action in listeActions) {"Choix invalide !"}


        try {
            // Cas 1 : Dépôt de somme sur le compte.
            if (action == "1"){

                println(" === Dépot de somme sur le compte bancaire === ")

                // Authentification du compte par vérification du mot de passe.
                if (compteCree.authentifierCompte()) {
                    println("Somme entière à déposer : ")
                    var depot = readln().toInt()
                    compteCree.deposer(depot)
                    compteCree.ajoutHistorique("Retrait de $depot euros !")

                } else {
                    compteCree.gestionIntrusion()
                    codeIntrusion = true
                }

            }
            else if (action == "2"){

                println(" === Retrait de somme du compte bancaire === ")

                if (compteCree.authentifierCompte()) {
                    println("Somme entière à retirer : ")
                    var retrait = readln().toInt()
                    compteCree.retirer(retrait)
                    compteCree.ajoutHistorique("Retrait de $retrait euros !")

                } else {
                    compteCree.gestionIntrusion()
                    codeIntrusion = true
                }
            }
            else if (action == "3"){

                println(" === Consultation du solde compte bancaire === ")

                if (compteCree.authentifierCompte()) {
                    compteCree.consulterSolde()
                    compteCree.ajoutHistorique("Consultation de solde : ${compteCree.solde}")

                } else {
                    compteCree.gestionIntrusion()
                    codeIntrusion = true
                }
            }
            else if (action == "4"){
                println(" ::: Historique du compte bancaire ::: ")
                compteCree.afficherHistorique()
            }
            else if (action == "5"){

                println(" ==== RELEVE DE COMPTE =====")

                if (compteCree.authentifierCompte()) {
                    compteCree.afficherReleve()
                    compteCree.ajoutHistorique("Affichage du relevé banquaire !")

                } else {
                    compteCree.gestionIntrusion()
                    codeIntrusion = true
                }
            }
            else{
                println("A bientôt !")
            }

        }
        catch (e: IllegalArgumentException){
            println(e.message)
        }
        catch (e: IllegalStateException){
            println(e.message)
        }
        catch (e: NumberFormatException) {
            println("Nombre invalide !")
        }
    } while (action != "0" || codeIntrusion)
}

fun afficherFonctions(){
    println("""
        1. Déposer de l'argent
        2. Retirer de l'argent
        3. Consulter le solde
        4. Afficher l'historique (bonus)
        5. Afficher le relevé du compte
        0. Quitter
    """.trimIndent())
}

class CompteBancaire (val nomtitulaire:String, val prenomtitulaire:String, val dateNaissanceTitulaire:String, val adresseTitulaire:String) {

    val numeroCompte: String = genererNumeroCompte()
    val codeSecret: String = genererCodeSecret()
    var solde = 0
    var historique = mutableListOf<String>("Création du compte")

    init {
        require(prenomtitulaire.isNotEmpty()) {"Le prenom du titulaire du compte n'est pas valide !"}
        require(prenomtitulaire.isNotBlank()) {"Le prenom du titulaire du compte n'est pas valide !"}
        require(nomtitulaire.isNotEmpty()) {"Le nom du titulaire du compte n'est pas valide !"}
        require(nomtitulaire.isNotBlank()) {"Le nom du titulaire du compte n'est pas valide !"}
        require(dateNaissanceTitulaire.isNotEmpty()) {"La date de naissance du titulaire du compte n'est pas valide !"}
        require(dateNaissanceTitulaire.isNotBlank()) {"La date de naissance du titulaire du compte n'est pas valide !"}
        require(adresseTitulaire.isNotEmpty()) {"L'adresse du titulaire n'est pas valide !"}
        require(adresseTitulaire.isNotBlank()) {"L'adresse du titulaire n'est pas valide !"}
        check(codeSecret.length == 4) {"Erreur de génération Mot de Passe !"}
        check(numeroCompte.length == 16) {"Erreur de génération Numéro de Compte !"}

    }

    fun deposer(sommeDépot:Int){
        check(sommeDépot > 0) {"La somme à déposer n'est pas valide (Doit être strictement supérieur à 0) !"}
        solde += sommeDépot
        println("Dépôt de $sommeDépot euros !")
    }

    fun retirer(sommeRetrait:Int){
        check(sommeRetrait > 0) {"La somme à retirer n'est pas valide (Doit être strictement supérieur à 0) !"}
        check (sommeRetrait <= this.solde) {"Retrait impossible ! Solde insuffisant !"}
        solde -= sommeRetrait
        println("Retrait de $sommeRetrait euros !")
    }

    fun consulterSolde(){
        println("""
          Numéro de compte : ${this.numeroCompte}
          Solde actuel : ${this.solde} euros
            """.trimIndent())
    }

    fun authentifierCompte():Boolean{
        println("Code secret : ")
        val mdpUser = readln()
        return mdpUser == this.codeSecret
    }

    fun gestionIntrusion(){
        var counter = 0
        while (counter < 5 && !this.authentifierCompte()){
            println("Code Secret invalide !")
            counter++
        }
        println("Impossible de raliser des actions sur ce compte car une tentative de fraude a été repérée !")
        this.ajoutHistorique("Activité suspecte repérée !")
    }


    fun afficherReleve(){
        println("""
            Numéro de compte : ${this.numeroCompte}
            Nom du titulaire : ${this.nomtitulaire}
            Prénom du titulaire : ${this.prenomtitulaire}
            Date de naissance du titulaire : ${this.dateNaissanceTitulaire}
            Solde actuel : ${this.solde} euros
        """.trimIndent())


    }

    fun ajoutHistorique(actionRealisee:String){
        this.historique.add(actionRealisee)
    }

    fun afficherHistorique(){
        for (i in this.historique) {
            println(i)
            println("")
        }
    }

    fun transferer(compteDestinataire:CompteBancaire, montant:Int){
        compteDestinataire.deposer(montant)
        this.solde -= montant
    }

    fun genererNumeroCompte():String{
        var chaineNumeroCompte = ""
        for (i in 0 until 16){
            chaineNumeroCompte+= Random.nextInt(0,10).toString()
        }
        return chaineNumeroCompte
    }

    fun genererCodeSecret():String{
        var chaineCodeSecret = ""
        for (i in 0 until 4){
            chaineCodeSecret += Random.nextInt(0,4).toString()
        }
        return chaineCodeSecret
    }

}


class Banque (val nomBanque:String, val adresseBanque: Banque){

}