import exceptions.ConflitReservationException;
import exceptions.RessourceIntrouvableException;
import gestion.GestionFichiers;
import gestion.GestionReservations;
import model.Equipement;
import model.Reservation;
import model.Ressource;
import model.Salle;
import model.Utilisateur;
import util.Saisie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Point d'entrée de l'application : le menu principal et les sous-menus.
 *
 * Main ne contient aucune règle métier : il pose les questions (avec Saisie), appelle
 * GestionReservations, puis affiche le résultat ou le message de l'exception.
 * Après chaque opération qui modifie les données, il demande la sauvegarde des fichiers.
 */
public class Main {

    // L'objet qui contient toute la logique métier et les données en mémoire
    private static GestionReservations gestion;

    // Le texte des menus (le numéro est ajouté automatiquement ; 0 est toujours Quitter ou Retour)
    private static final String[] MENU_PRINCIPAL = {
        "Gérer les salles",
        "Gérer les équipements",
        "Gérer les utilisateurs",
        "Réserver une ressource",
        "Annuler / modifier une réservation",
        "Consulter le planning",
        "Rechercher une ressource disponible"
    };
    private static final String[] MENU_SALLES = {
        "Ajouter une salle", "Modifier une salle", "Supprimer une salle", "Afficher toutes les salles"
    };
    private static final String[] MENU_EQUIPEMENTS = {
        "Ajouter un équipement", "Modifier un équipement", "Supprimer un équipement", "Afficher tous les équipements"
    };
    private static final String[] MENU_UTILISATEURS = {
        "Ajouter un utilisateur", "Modifier un utilisateur", "Supprimer un utilisateur", "Afficher tous les utilisateurs"
    };
    private static final String[] MENU_RESERVER = {
        "Réserver une salle", "Réserver un équipement"
    };
    private static final String[] MENU_RESERVATIONS = {
        "Lister les réservations", "Rechercher par identifiant", "Annuler une réservation", "Modifier une réservation"
    };
    private static final String[] MENU_PLANNING = {
        "Planning par salle", "Planning par équipement", "Planning par date", "Planning par utilisateur"
    };

    // =====================================================================
    //  Programme principal
    // =====================================================================

    public static void main(String[] args) {
        GestionFichiers fichiers = new GestionFichiers("data");   // dossier data/ à la racine du projet
        gestion = new GestionReservations(fichiers);

        // 1. Chargement des données au démarrage
        try {
            ArrayList<String> problemes = gestion.chargerDonnees();
            System.out.println("Données chargées depuis : " + fichiers.getCheminDossier());
            afficherProblemes(problemes);
        } catch (IOException e) {
            System.out.println("ERREUR : impossible de lire les fichiers de données (" + e.getMessage() + ").");
            System.out.println("Le programme s'arrête pour ne pas écraser vos fichiers.");
            return;
        }

        // 2. Boucle du menu principal : on réaffiche le menu après chaque action, jusqu'à "Quitter"
        boolean continuer = true;
        while (continuer) {
            afficherMenu("RESERVATION DE SALLES ET D'EQUIPEMENTS", MENU_PRINCIPAL, "Quitter");
            try {
                int choix = Saisie.lireChoixMenu(0, 7);
                switch (choix) {
                    case 1:
                        menuSalles();
                        break;
                    case 2:
                        menuEquipements();
                        break;
                    case 3:
                        menuUtilisateurs();
                        break;
                    case 4:
                        menuReserver();
                        break;
                    case 5:
                        menuReservations();
                        break;
                    case 6:
                        menuPlanning();
                        break;
                    case 7:
                        rechercherDisponibilites();
                        break;
                    default:   // 0 : quitter
                        continuer = false;
                        break;
                }
            } catch (NoSuchElementException e) {
                // Le clavier est fermé (Ctrl+D / Ctrl+Z) : on ne peut plus rien lire, on quitte proprement
                System.out.println();
                System.out.println("Fin de la saisie : fermeture du programme.");
                continuer = false;
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            } catch (RuntimeException e) {
                // Filet de sécurité : une erreur imprévue ne doit jamais fermer le programme
                System.out.println("Erreur inattendue : " + e);
            }
        }

        // 3. Sauvegarde à la fermeture
        sauvegarderALaFermeture();
        System.out.println("Au revoir !");
    }

    // =====================================================================
    //  Menus
    // =====================================================================

    /** Affiche un menu : le titre, les options numérotées à partir de 1, puis l'option 0. */
    private static void afficherMenu(String titre, String[] options, String texteZero) {
        System.out.println();
        System.out.println("=== " + titre + " ===");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println("0. " + texteZero);
    }

    private static void menuSalles() {
        int choix;
        do {
            afficherMenu("GESTION DES SALLES", MENU_SALLES, "Retour");
            choix = Saisie.lireChoixMenu(0, 4);
            try {
                switch (choix) {
                    case 1:
                        ajouterSalle();
                        break;
                    case 2:
                        modifierSalle();
                        break;
                    case 3:
                        supprimerSalle();
                        break;
                    case 4:
                        afficherSalles();
                        break;
                    default:   // 0 : retour au menu principal
                        break;
                }
            } catch (RessourceIntrouvableException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } while (choix != 0);
    }

    private static void menuEquipements() {
        int choix;
        do {
            afficherMenu("GESTION DES EQUIPEMENTS", MENU_EQUIPEMENTS, "Retour");
            choix = Saisie.lireChoixMenu(0, 4);
            try {
                switch (choix) {
                    case 1:
                        ajouterEquipement();
                        break;
                    case 2:
                        modifierEquipement();
                        break;
                    case 3:
                        supprimerEquipement();
                        break;
                    case 4:
                        afficherEquipements();
                        break;
                    default:
                        break;
                }
            } catch (RessourceIntrouvableException | ConflitReservationException
                    | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } while (choix != 0);
    }

    private static void menuUtilisateurs() {
        int choix;
        do {
            afficherMenu("GESTION DES UTILISATEURS", MENU_UTILISATEURS, "Retour");
            choix = Saisie.lireChoixMenu(0, 4);
            try {
                switch (choix) {
                    case 1:
                        ajouterUtilisateur();
                        break;
                    case 2:
                        modifierUtilisateur();
                        break;
                    case 3:
                        supprimerUtilisateur();
                        break;
                    case 4:
                        afficherUtilisateurs();
                        break;
                    default:
                        break;
                }
            } catch (RessourceIntrouvableException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } while (choix != 0);
    }

    private static void menuReserver() {
        int choix;
        do {
            afficherMenu("RESERVER UNE RESSOURCE", MENU_RESERVER, "Retour");
            choix = Saisie.lireChoixMenu(0, 2);
            try {
                switch (choix) {
                    case 1:
                        reserver(Ressource.CATEGORIE_SALLE);
                        break;
                    case 2:
                        reserver(Ressource.CATEGORIE_EQUIPEMENT);
                        break;
                    default:
                        break;
                }
            } catch (RessourceIntrouvableException | ConflitReservationException
                    | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } while (choix != 0);
    }

    private static void menuReservations() {
        int choix;
        do {
            afficherMenu("ANNULER / MODIFIER UNE RESERVATION", MENU_RESERVATIONS, "Retour");
            choix = Saisie.lireChoixMenu(0, 4);
            try {
                switch (choix) {
                    case 1:
                        listerReservations();
                        break;
                    case 2:
                        rechercherReservation();
                        break;
                    case 3:
                        annulerReservation();
                        break;
                    case 4:
                        modifierReservation();
                        break;
                    default:
                        break;
                }
            } catch (RessourceIntrouvableException | ConflitReservationException
                    | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } while (choix != 0);
    }

    private static void menuPlanning() {
        int choix;
        do {
            afficherMenu("CONSULTER LE PLANNING", MENU_PLANNING, "Retour");
            choix = Saisie.lireChoixMenu(0, 4);
            try {
                switch (choix) {
                    case 1:
                        planningParSalle();
                        break;
                    case 2:
                        planningParEquipement();
                        break;
                    case 3:
                        planningParDate();
                        break;
                    case 4:
                        planningParUtilisateur();
                        break;
                    default:
                        break;
                }
            } catch (RessourceIntrouvableException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } while (choix != 0);
    }

    // =====================================================================
    //  Salles
    // =====================================================================

    private static void ajouterSalle() {
        System.out.println("--- Ajouter une salle ---");
        String nom = Saisie.lireTexte("Nom de la salle : ");
        int capacite = Saisie.lireEntierPositif("Capacité (nombre de places) : ");
        String batiment = Saisie.lireTexte("Bâtiment : ");

        Salle salle = gestion.ajouterSalle(nom, capacite, batiment);
        System.out.println("Salle ajoutée : " + salle);
        sauvegarder();
    }

    private static void modifierSalle() throws RessourceIntrouvableException {
        System.out.println("--- Modifier une salle ---");
        afficherSalles();
        String id = Saisie.lireIdentifiant("Identifiant de la salle à modifier (ex : S1) : ");
        Salle salle = gestion.trouverSalle(id);

        System.out.println("Salle actuelle : " + salle);
        System.out.println("(Appuyez sur Entrée pour conserver la valeur actuelle.)");
        String nom = Saisie.lireTexte("Nom [" + salle.getNom() + "] : ", salle.getNom());
        int capacite = Saisie.lireEntierPositif("Capacité [" + salle.getCapacite() + "] : ", salle.getCapacite());
        String batiment = Saisie.lireTexte("Bâtiment [" + salle.getBatiment() + "] : ", salle.getBatiment());

        gestion.modifierSalle(id, nom, capacite, batiment);
        System.out.println("Salle modifiée : " + salle);
        sauvegarder();
    }

    private static void supprimerSalle() throws RessourceIntrouvableException {
        System.out.println("--- Supprimer une salle ---");
        afficherSalles();
        String id = Saisie.lireIdentifiant("Identifiant de la salle à supprimer (ex : S1) : ");
        Salle salle = gestion.trouverSalle(id);
        gestion.verifierSuppressionPossible(id);   // on refuse tout de suite si elle a des réservations
        if (Saisie.lireConfirmation("Supprimer définitivement " + salle + " ? (O/N) : ")) {
            gestion.supprimerSalle(id);
            System.out.println("Salle supprimée : " + salle);
            sauvegarder();
        } else {
            System.out.println("Suppression abandonnée : la salle est conservée.");
        }
    }

    private static void afficherSalles() {
        ArrayList<Salle> salles = gestion.getSalles();
        if (salles.isEmpty()) {
            System.out.println("Aucune salle enregistrée.");
            return;
        }
        System.out.println(String.format("%-6s %-22s %-10s %s", "ID", "Nom", "Capacité", "Bâtiment"));
        for (Salle salle : salles) {
            System.out.println(String.format("%-6s %-22s %-10s %s",
                    salle.getId(), salle.getNom(), salle.getCapacite(), salle.getBatiment()));
        }
        System.out.println(salles.size() + " salle(s).");
    }

    // =====================================================================
    //  Équipements
    // =====================================================================

    private static void ajouterEquipement() {
        System.out.println("--- Ajouter un équipement ---");
        String nom = Saisie.lireTexte("Nom de l'équipement : ");
        String type = Saisie.lireTexte("Type (ex : VIDEOPROJECTEUR, PC PORTABLE, CAMERA) : ");
        int quantite = Saisie.lireEntierPositif("Quantité disponible : ");

        Equipement equipement = gestion.ajouterEquipement(nom, type, quantite);
        System.out.println("Équipement ajouté : " + equipement);
        sauvegarder();
    }

    private static void modifierEquipement() throws RessourceIntrouvableException, ConflitReservationException {
        System.out.println("--- Modifier un équipement ---");
        afficherEquipements();
        String id = Saisie.lireIdentifiant("Identifiant de l'équipement à modifier (ex : E1) : ");
        Equipement equipement = gestion.trouverEquipement(id);

        System.out.println("Équipement actuel : " + equipement);
        System.out.println("(Appuyez sur Entrée pour conserver la valeur actuelle.)");
        String nom = Saisie.lireTexte("Nom [" + equipement.getNom() + "] : ", equipement.getNom());
        String type = Saisie.lireTexte("Type [" + equipement.getType() + "] : ", equipement.getType());
        int quantite = Saisie.lireEntierPositif("Quantité [" + equipement.getQuantiteDisponible() + "] : ",
                equipement.getQuantiteDisponible());

        gestion.modifierEquipement(id, nom, type, quantite);   // refusé si la quantité est trop basse
        System.out.println("Équipement modifié : " + equipement);
        sauvegarder();
    }

    private static void supprimerEquipement() throws RessourceIntrouvableException {
        System.out.println("--- Supprimer un équipement ---");
        afficherEquipements();
        String id = Saisie.lireIdentifiant("Identifiant de l'équipement à supprimer (ex : E1) : ");
        Equipement equipement = gestion.trouverEquipement(id);
        gestion.verifierSuppressionPossible(id);
        if (Saisie.lireConfirmation("Supprimer définitivement " + equipement + " ? (O/N) : ")) {
            gestion.supprimerEquipement(id);
            System.out.println("Équipement supprimé : " + equipement);
            sauvegarder();
        } else {
            System.out.println("Suppression abandonnée : l'équipement est conservé.");
        }
    }

    private static void afficherEquipements() {
        ArrayList<Equipement> equipements = gestion.getEquipements();
        if (equipements.isEmpty()) {
            System.out.println("Aucun équipement enregistré.");
            return;
        }
        System.out.println(String.format("%-6s %-26s %-18s %s", "ID", "Nom", "Type", "Quantité"));
        for (Equipement equipement : equipements) {
            System.out.println(String.format("%-6s %-26s %-18s %s",
                    equipement.getId(), equipement.getNom(), equipement.getType(), equipement.getQuantiteDisponible()));
        }
        System.out.println(equipements.size() + " équipement(s).");
    }

    // =====================================================================
    //  Utilisateurs
    // =====================================================================

    private static void ajouterUtilisateur() {
        System.out.println("--- Ajouter un utilisateur ---");
        String nom = Saisie.lireTexte("Nom : ");
        String prenom = Saisie.lireTexte("Prénom : ");
        String role = Saisie.lireTexte("Rôle (ex : ENSEIGNANT, ETUDIANT) : ");

        Utilisateur utilisateur = gestion.ajouterUtilisateur(nom, prenom, role);
        System.out.println("Utilisateur ajouté : " + utilisateur);
        sauvegarder();
    }

    private static void modifierUtilisateur() throws RessourceIntrouvableException {
        System.out.println("--- Modifier un utilisateur ---");
        afficherUtilisateurs();
        String id = Saisie.lireIdentifiant("Identifiant de l'utilisateur à modifier (ex : U1) : ");
        Utilisateur utilisateur = gestion.trouverUtilisateur(id);

        System.out.println("Utilisateur actuel : " + utilisateur);
        System.out.println("(Appuyez sur Entrée pour conserver la valeur actuelle.)");
        String nom = Saisie.lireTexte("Nom [" + utilisateur.getNom() + "] : ", utilisateur.getNom());
        String prenom = Saisie.lireTexte("Prénom [" + utilisateur.getPrenom() + "] : ", utilisateur.getPrenom());
        String role = Saisie.lireTexte("Rôle [" + utilisateur.getRole() + "] : ", utilisateur.getRole());

        gestion.modifierUtilisateur(id, nom, prenom, role);
        System.out.println("Utilisateur modifié : " + utilisateur);
        sauvegarder();
    }

    private static void supprimerUtilisateur() throws RessourceIntrouvableException {
        System.out.println("--- Supprimer un utilisateur ---");
        afficherUtilisateurs();
        String id = Saisie.lireIdentifiant("Identifiant de l'utilisateur à supprimer (ex : U1) : ");
        Utilisateur utilisateur = gestion.trouverUtilisateur(id);
        gestion.verifierSuppressionPossible(id);
        if (Saisie.lireConfirmation("Supprimer définitivement " + utilisateur + " ? (O/N) : ")) {
            gestion.supprimerUtilisateur(id);
            System.out.println("Utilisateur supprimé : " + utilisateur);
            sauvegarder();
        } else {
            System.out.println("Suppression abandonnée : l'utilisateur est conservé.");
        }
    }

    private static void afficherUtilisateurs() {
        ArrayList<Utilisateur> utilisateurs = gestion.getUtilisateurs();
        if (utilisateurs.isEmpty()) {
            System.out.println("Aucun utilisateur enregistré.");
            return;
        }
        System.out.println(String.format("%-6s %-18s %-18s %s", "ID", "Nom", "Prénom", "Rôle"));
        for (Utilisateur utilisateur : utilisateurs) {
            System.out.println(String.format("%-6s %-18s %-18s %s",
                    utilisateur.getId(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getRole()));
        }
        System.out.println(utilisateurs.size() + " utilisateur(s).");
    }

    // =====================================================================
    //  Réservations
    // =====================================================================

    /** Réserve une salle ou un équipement (selon la catégorie) : on pose les questions puis on réserve. */
    private static void reserver(String categorie) throws RessourceIntrouvableException, ConflitReservationException {
        boolean estUneSalle = categorie.equals(Ressource.CATEGORIE_SALLE);
        if (estUneSalle) {
            System.out.println("--- Réserver une salle ---");
            afficherSalles();
        } else {
            System.out.println("--- Réserver un équipement ---");
            afficherEquipements();
        }

        String idRessource = Saisie.lireIdentifiant(estUneSalle
                ? "Identifiant de la salle (ex : S1) : "
                : "Identifiant de l'équipement (ex : E1) : ");
        gestion.trouverRessource(idRessource, categorie);   // on s'arrête tout de suite si elle n'existe pas
        String idUtilisateur = Saisie.lireIdentifiant("Identifiant de l'utilisateur (ex : U1) : ");
        gestion.trouverUtilisateur(idUtilisateur);
        LocalDate date = Saisie.lireDate("Date (JJ/MM/AAAA) : ");
        LocalTime heureDebut = Saisie.lireHeure("Heure de début (HH:mm) : ");
        LocalTime heureFin = Saisie.lireHeure("Heure de fin (HH:mm) : ");

        // Toutes les règles de gestion sont vérifiées ici ; une exception est lancée si quelque chose cloche
        Reservation reservation = gestion.reserver(categorie, idRessource, idUtilisateur, date, heureDebut, heureFin);

        System.out.println();
        System.out.println("=== RESERVATION CONFIRMEE ===");
        System.out.println();
        afficherDetailsReservation(reservation);
        sauvegarder();
    }

    private static void listerReservations() {
        afficherReservations(gestion.getToutesLesReservations(),
                "Toutes les réservations", "Aucune réservation enregistrée.");
    }

    private static void rechercherReservation() throws RessourceIntrouvableException {
        String id = Saisie.lireIdentifiant("Identifiant de la réservation (ex : R1) : ");
        Reservation reservation = gestion.trouverReservation(id);
        System.out.println();
        afficherDetailsReservation(reservation);
    }

    private static void annulerReservation() throws RessourceIntrouvableException {
        System.out.println("--- Annuler une réservation ---");
        String id = Saisie.lireIdentifiant("Identifiant de la réservation à annuler (ex : R1) : ");
        Reservation reservation = gestion.trouverReservation(id);
        System.out.println();
        afficherDetailsReservation(reservation);
        if (Saisie.lireConfirmation("Voulez-vous vraiment annuler cette réservation ? (O/N) : ")) {
            gestion.annulerReservation(id);
            System.out.println("Réservation " + id + " annulée : le créneau est de nouveau libre.");
            sauvegarder();
        } else {
            System.out.println("Annulation abandonnée : la réservation est conservée.");
        }
    }

    private static void modifierReservation() throws RessourceIntrouvableException, ConflitReservationException {
        System.out.println("--- Modifier une réservation ---");
        String id = Saisie.lireIdentifiant("Identifiant de la réservation à modifier (ex : R1) : ");
        Reservation reservation = gestion.trouverReservation(id);
        System.out.println();
        afficherDetailsReservation(reservation);

        System.out.println();
        System.out.println("(Appuyez sur Entrée pour conserver la valeur actuelle.)");
        String idRessource = Saisie.lireTexte("Identifiant de la ressource [" + reservation.getIdRessource() + "] : ",
                reservation.getIdRessource()).toUpperCase();
        String idUtilisateur = Saisie.lireTexte("Identifiant de l'utilisateur [" + reservation.getIdUtilisateur() + "] : ",
                reservation.getIdUtilisateur()).toUpperCase();
        LocalDate date = Saisie.lireDate("Date [" + reservation.getDateTexte() + "] (JJ/MM/AAAA) : ",
                reservation.getDate());
        LocalTime heureDebut = Saisie.lireHeure("Heure de début [" + reservation.getHeureDebutTexte() + "] (HH:mm) : ",
                reservation.getHeureDebut());
        LocalTime heureFin = Saisie.lireHeure("Heure de fin [" + reservation.getHeureFinTexte() + "] (HH:mm) : ",
                reservation.getHeureFin());

        // Si une vérification échoue (conflit...), l'exception est lancée et la réservation reste intacte
        gestion.modifierReservation(id, idRessource, idUtilisateur, date, heureDebut, heureFin);

        System.out.println();
        System.out.println("=== RESERVATION MODIFIEE ===");
        System.out.println();
        afficherDetailsReservation(reservation);
        sauvegarder();
    }

    /** Affiche une réservation dans le format du récapitulatif demandé. */
    private static void afficherDetailsReservation(Reservation reservation) {
        System.out.println("Identifiant : " + reservation.getId());
        System.out.println("Ressource : " + reservation.getIdRessource() + " (" + gestion.getNomRessource(reservation) + ")");
        System.out.println("Utilisateur : " + reservation.getIdUtilisateur() + " (" + gestion.getNomUtilisateur(reservation) + ")");
        System.out.println("Date : " + reservation.getDateTexte());
        System.out.println("Début : " + reservation.getHeureDebutTexte());
        System.out.println("Fin : " + reservation.getHeureFinTexte());
    }

    /** Affiche une liste de réservations sous forme de tableau (ou le message adapté si elle est vide). */
    private static void afficherReservations(ArrayList<Reservation> liste, String titre, String messageSiVide) {
        System.out.println();
        System.out.println("--- " + titre + " ---");
        if (liste.isEmpty()) {
            System.out.println(messageSiVide);
            return;
        }
        System.out.println(String.format("%-5s %-11s %-6s %-6s %-26s %s",
                "ID", "Date", "Début", "Fin", "Ressource", "Utilisateur"));
        for (Reservation r : liste) {
            String ressource = r.getIdRessource() + " " + gestion.getNomRessource(r);
            String utilisateur = r.getIdUtilisateur() + " " + gestion.getNomUtilisateur(r);
            System.out.println(String.format("%-5s %-11s %-6s %-6s %-26s %s",
                    r.getId(), r.getDateTexte(), r.getHeureDebutTexte(), r.getHeureFinTexte(), ressource, utilisateur));
        }
        System.out.println(liste.size() + " réservation(s).");
    }

    // =====================================================================
    //  Planning
    // =====================================================================

    private static void planningParSalle() throws RessourceIntrouvableException {
        afficherSalles();
        String id = Saisie.lireIdentifiant("Identifiant de la salle (ex : S1) : ");
        Salle salle = gestion.trouverSalle(id);
        afficherReservations(gestion.planningParSalle(id), "Planning de la salle " + salle.getId() + " - " + salle.getNom(),
                "Aucune réservation pour la salle " + salle.getId() + " - " + salle.getNom() + ".");
    }

    private static void planningParEquipement() throws RessourceIntrouvableException {
        afficherEquipements();
        String id = Saisie.lireIdentifiant("Identifiant de l'équipement (ex : E1) : ");
        Equipement equipement = gestion.trouverEquipement(id);
        afficherReservations(gestion.planningParEquipement(id),
                "Planning de l'équipement " + equipement.getId() + " - " + equipement.getNom(),
                "Aucune réservation pour l'équipement " + equipement.getId() + " - " + equipement.getNom() + ".");
    }

    private static void planningParDate() {
        LocalDate date = Saisie.lireDate("Date (JJ/MM/AAAA) : ");
        String texteDate = date.format(Reservation.FORMAT_DATE);
        afficherReservations(gestion.planningParDate(date), "Planning du " + texteDate,
                "Aucune réservation le " + texteDate + ".");
    }

    private static void planningParUtilisateur() throws RessourceIntrouvableException {
        afficherUtilisateurs();
        String id = Saisie.lireIdentifiant("Identifiant de l'utilisateur (ex : U1) : ");
        Utilisateur utilisateur = gestion.trouverUtilisateur(id);
        afficherReservations(gestion.planningParUtilisateur(id),
                "Planning de " + utilisateur.getNomComplet() + " (" + utilisateur.getId() + ")",
                "Aucune réservation pour " + utilisateur.getNomComplet() + " (" + utilisateur.getId() + ").");
    }

    // =====================================================================
    //  Recherche de ressources disponibles
    // =====================================================================

    private static void rechercherDisponibilites() {
        System.out.println("--- Rechercher une ressource disponible ---");
        LocalDate date = Saisie.lireDate("Date (JJ/MM/AAAA) : ");
        LocalTime heureDebut = Saisie.lireHeure("Heure de début (HH:mm) : ");
        LocalTime heureFin = Saisie.lireHeure("Heure de fin (HH:mm) : ");

        // Lance IllegalArgumentException si le créneau est invalide (début >= fin, date passée)
        ArrayList<Salle> sallesLibres = gestion.sallesDisponibles(date, heureDebut, heureFin);
        ArrayList<Equipement> equipementsLibres = gestion.equipementsDisponibles(date, heureDebut, heureFin);

        System.out.println();
        System.out.println("Disponibilités le " + date.format(Reservation.FORMAT_DATE)
                + " de " + heureDebut.format(Reservation.FORMAT_HEURE)
                + " à " + heureFin.format(Reservation.FORMAT_HEURE) + " :");

        System.out.println();
        System.out.println("Salles disponibles :");
        if (sallesLibres.isEmpty()) {
            System.out.println("  Aucune salle disponible sur ce créneau.");
        } else {
            System.out.println(String.format("  %-6s %-22s %-10s %s", "ID", "Nom", "Capacité", "Bâtiment"));
            for (Salle salle : sallesLibres) {
                System.out.println(String.format("  %-6s %-22s %-10s %s",
                        salle.getId(), salle.getNom(), salle.getCapacite(), salle.getBatiment()));
            }
        }

        System.out.println();
        System.out.println("Équipements disponibles :");
        if (equipementsLibres.isEmpty()) {
            System.out.println("  Aucun équipement disponible sur ce créneau.");
        } else {
            System.out.println(String.format("  %-6s %-26s %-18s %-16s %s",
                    "ID", "Nom", "Type", "Quantité totale", "Unités disponibles"));
            for (Equipement equipement : equipementsLibres) {
                int libres = gestion.unitesDisponibles(equipement, date, heureDebut, heureFin);
                System.out.println(String.format("  %-6s %-26s %-18s %-16s %s",
                        equipement.getId(), equipement.getNom(), equipement.getType(),
                        equipement.getQuantiteDisponible(), libres));
            }
        }
    }

    // =====================================================================
    //  Sauvegarde et messages communs
    // =====================================================================

    /** Demande la sauvegarde des fichiers. On n'affiche "sauvegardées" QUE si l'écriture a réussi. */
    private static void sauvegarder() {
        try {
            gestion.sauvegarderTout();
            System.out.println("Données sauvegardées.");
        } catch (IOException e) {
            System.out.println("ERREUR DE SAUVEGARDE : " + e.getMessage());
            System.out.println("Les modifications sont gardées en mémoire mais ne sont PAS enregistrées dans les fichiers.");
            System.out.println("Une nouvelle tentative aura lieu à la prochaine modification et à la fermeture.");
        }
    }

    /** Dernière sauvegarde à la fermeture ; si elle échoue, on propose de réessayer. */
    private static void sauvegarderALaFermeture() {
        boolean termine = false;
        while (!termine) {
            try {
                gestion.sauvegarderTout();
                System.out.println("Données sauvegardées.");
                termine = true;
            } catch (IOException e) {
                System.out.println("ERREUR DE SAUVEGARDE : " + e.getMessage());
                try {
                    termine = !Saisie.lireConfirmation("Réessayer la sauvegarde ? (O = réessayer, N = quitter sans sauvegarder) : ");
                } catch (NoSuchElementException fin) {
                    termine = true;   // plus de clavier : impossible de poser la question
                }
                if (termine) {
                    System.out.println("ATTENTION : les dernières modifications n'ont PAS été enregistrées.");
                }
            }
        }
    }

    private static void afficherProblemes(ArrayList<String> problemes) {
        if (problemes.isEmpty()) {
            return;
        }
        System.out.println();
        System.out.println("ATTENTION : " + problemes.size() + " problème(s) dans les fichiers de données :");
        for (String probleme : problemes) {
            System.out.println(" - " + probleme);
        }
        System.out.println("Ces lignes ne sont pas chargées et disparaîtront des fichiers à la prochaine sauvegarde.");
        try {
            gestion.copierFichiersOriginaux();
            System.out.println("Une copie des fichiers d'origine a été enregistrée (fichiers *.bak dans le dossier data).");
        } catch (IOException e) {
            System.out.println("La copie de secours (*.bak) n'a pas pu être créée : " + e.getMessage());
        }
    }
}
