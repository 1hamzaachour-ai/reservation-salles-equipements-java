package gestion;

import exceptions.ConflitReservationException;
import exceptions.RessourceIntrouvableException;
import model.Equipement;
import model.Reservation;
import model.Ressource;
import model.Salle;
import model.Utilisateur;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Toute la logique métier de l'application :
 * gestion des salles, des équipements, des utilisateurs et des réservations,
 * détection des conflits de créneaux, plannings et recherche de disponibilités.
 *
 * Les données vivent en mémoire dans quatre ArrayList. GestionFichiers sait les lire
 * et les écrire dans les fichiers texte ; cette classe ne fait AUCUN affichage :
 * quand quelque chose ne va pas, elle lance une exception avec un message clair,
 * et c'est Main qui l'affiche à l'utilisateur.
 */
public class GestionReservations {

    // ===================== Attributs =====================

    private ArrayList<Salle> salles = new ArrayList<Salle>();
    private ArrayList<Equipement> equipements = new ArrayList<Equipement>();
    private ArrayList<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
    private ArrayList<Reservation> reservations = new ArrayList<Reservation>();

    // Dernier numéro attribué pour chaque type d'identifiant (S12 -> 12). Ils ne diminuent JAMAIS :
    // un identifiant supprimé n'est donc jamais réattribué, et on ne se base pas sur size().
    private int dernierNumeroSalle = 0;
    private int dernierNumeroEquipement = 0;
    private int dernierNumeroUtilisateur = 0;
    private int dernierNumeroReservation = 0;

    private GestionFichiers fichiers;

    public GestionReservations(GestionFichiers fichiers) {
        this.fichiers = fichiers;
    }

    // =====================================================================
    //  Chargement et sauvegarde
    // =====================================================================

    /**
     * Charge les quatre fichiers au démarrage (en les créant s'ils n'existent pas).
     * Chaque donnée est contrôlée avec les mêmes règles que lors d'un ajout au clavier.
     * Une ligne incorrecte est ignorée ; la liste renvoyée explique chaque problème trouvé.
     */
    public ArrayList<String> chargerDonnees() throws IOException {
        fichiers.preparerFichiers();
        ArrayList<String> problemes = new ArrayList<String>();

        // Les numéros mémorisés dans compteurs.txt : le prochain identifiant ne sera jamais plus petit
        int[] compteurs = fichiers.chargerCompteurs();
        dernierNumeroSalle = compteurs[0];
        dernierNumeroEquipement = compteurs[1];
        dernierNumeroUtilisateur = compteurs[2];
        dernierNumeroReservation = compteurs[3];

        // 1. Les salles
        for (Salle salle : fichiers.chargerSalles()) {
            try {
                verifierIdentifiant(salle.getId(), 'S', chercherSalle(salle.getId()) != null);
                controlerSalle(salle.getNom(), salle.getCapacite(), salle.getBatiment());
                salles.add(salle);
                dernierNumeroSalle = Math.max(dernierNumeroSalle, numero(salle.getId()));
            } catch (IllegalArgumentException e) {
                problemes.add("salles.txt : la salle \"" + salle.getId() + "\" est ignorée : " + e.getMessage());
            }
        }

        // 2. Les équipements
        for (Equipement equipement : fichiers.chargerEquipements()) {
            try {
                verifierIdentifiant(equipement.getId(), 'E', chercherEquipement(equipement.getId()) != null);
                controlerEquipement(equipement.getNom(), equipement.getType(), equipement.getQuantiteDisponible());
                equipements.add(equipement);
                dernierNumeroEquipement = Math.max(dernierNumeroEquipement, numero(equipement.getId()));
            } catch (IllegalArgumentException e) {
                problemes.add("equipements.txt : l'équipement \"" + equipement.getId() + "\" est ignoré : " + e.getMessage());
            }
        }

        // 3. Les utilisateurs
        for (Utilisateur utilisateur : fichiers.chargerUtilisateurs()) {
            try {
                verifierIdentifiant(utilisateur.getId(), 'U', chercherUtilisateur(utilisateur.getId()) != null);
                controlerUtilisateur(utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getRole());
                utilisateurs.add(utilisateur);
                dernierNumeroUtilisateur = Math.max(dernierNumeroUtilisateur, numero(utilisateur.getId()));
            } catch (IllegalArgumentException e) {
                problemes.add("utilisateurs.txt : l'utilisateur \"" + utilisateur.getId() + "\" est ignoré : " + e.getMessage());
            }
        }

        // 4. Les réservations (en dernier : elles s'appuient sur les ressources et utilisateurs chargés)
        for (Reservation reservation : fichiers.chargerReservations()) {
            try {
                verifierIdentifiant(reservation.getId(), 'R', chercherReservation(reservation.getId()) != null);
                controlerReservationChargee(reservation);
                reservations.add(reservation);
                dernierNumeroReservation = Math.max(dernierNumeroReservation, numero(reservation.getId()));
            } catch (IllegalArgumentException | RessourceIntrouvableException | ConflitReservationException e) {
                problemes.add("reservations.txt : la réservation \"" + reservation.getId() + "\" est ignorée : " + e.getMessage());
            }
        }

        // D'abord les lignes mal formées repérées par GestionFichiers, puis nos propres contrôles
        ArrayList<String> tous = fichiers.getAvertissements();
        tous.addAll(problemes);
        return tous;
    }

    /** Copie les fichiers d'origine (*.bak) : à faire quand des lignes ont été ignorées au chargement. */
    public void copierFichiersOriginaux() throws IOException {
        fichiers.copierFichiersOriginaux();
    }

    /**
     * Réécrit tous les fichiers. On essaie les cinq fichiers même si l'un échoue,
     * puis on lance UNE exception qui liste ce qui n'a pas pu être écrit.
     */
    public void sauvegarderTout() throws IOException {
        StringBuilder erreurs = new StringBuilder();
        try {
            fichiers.sauvegarderSalles(salles);
        } catch (IOException e) {
            noter(erreurs, e);
        }
        try {
            fichiers.sauvegarderEquipements(equipements);
        } catch (IOException e) {
            noter(erreurs, e);
        }
        try {
            fichiers.sauvegarderUtilisateurs(utilisateurs);
        } catch (IOException e) {
            noter(erreurs, e);
        }
        try {
            fichiers.sauvegarderReservations(reservations);
        } catch (IOException e) {
            noter(erreurs, e);
        }
        try {
            fichiers.sauvegarderCompteurs(dernierNumeroSalle, dernierNumeroEquipement,
                    dernierNumeroUtilisateur, dernierNumeroReservation);
        } catch (IOException e) {
            noter(erreurs, e);
        }
        if (erreurs.length() > 0) {
            throw new IOException(erreurs.toString());
        }
    }

    private static void noter(StringBuilder erreurs, IOException e) {
        if (erreurs.length() > 0) {
            erreurs.append(" | ");
        }
        erreurs.append(e.getMessage());
    }

    // =====================================================================
    //  Recherche par identifiant
    //  chercherXxx renvoie null si rien n'est trouvé ; trouverXxx lance RessourceIntrouvableException.
    // =====================================================================

    private Salle chercherSalle(String id) {
        for (Salle salle : salles) {
            if (salle.getId().equals(id)) {
                return salle;
            }
        }
        return null;
    }

    private Equipement chercherEquipement(String id) {
        for (Equipement equipement : equipements) {
            if (equipement.getId().equals(id)) {
                return equipement;
            }
        }
        return null;
    }

    private Utilisateur chercherUtilisateur(String id) {
        for (Utilisateur utilisateur : utilisateurs) {
            if (utilisateur.getId().equals(id)) {
                return utilisateur;
            }
        }
        return null;
    }

    private Reservation chercherReservation(String id) {
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(id)) {
                return reservation;
            }
        }
        return null;
    }

    public Salle trouverSalle(String id) throws RessourceIntrouvableException {
        Salle salle = chercherSalle(id);
        if (salle == null) {
            throw new RessourceIntrouvableException("cette ressource n'existe pas (salle introuvable : " + id + ").");
        }
        return salle;
    }

    public Equipement trouverEquipement(String id) throws RessourceIntrouvableException {
        Equipement equipement = chercherEquipement(id);
        if (equipement == null) {
            throw new RessourceIntrouvableException("cette ressource n'existe pas (équipement introuvable : " + id + ").");
        }
        return equipement;
    }

    public Utilisateur trouverUtilisateur(String id) throws RessourceIntrouvableException {
        Utilisateur utilisateur = chercherUtilisateur(id);
        if (utilisateur == null) {
            throw new RessourceIntrouvableException("cet utilisateur n'existe pas (identifiant introuvable : " + id + ").");
        }
        return utilisateur;
    }

    public Reservation trouverReservation(String id) throws RessourceIntrouvableException {
        Reservation reservation = chercherReservation(id);
        if (reservation == null) {
            throw new RessourceIntrouvableException("cette réservation n'existe pas (identifiant introuvable : " + id + ").");
        }
        return reservation;
    }

    /** Renvoie la salle ou l'équipement selon la catégorie ("SALLE" ou "EQUIPEMENT"). */
    public Ressource trouverRessource(String id, String categorie) throws RessourceIntrouvableException {
        verifierCategorie(categorie);
        if (categorie.equals(Ressource.CATEGORIE_SALLE)) {
            return trouverSalle(id);
        }
        return trouverEquipement(id);
    }

    // =====================================================================
    //  Salles
    // =====================================================================

    /** Une copie de la liste : l'appelant ne peut pas modifier la vraie liste par accident. */
    public ArrayList<Salle> getSalles() {
        return new ArrayList<Salle>(salles);
    }

    public Salle ajouterSalle(String nom, int capacite, String batiment) {
        controlerSalle(nom, capacite, batiment);
        Salle salle = new Salle("S" + (dernierNumeroSalle + 1), nom.trim(), capacite, batiment.trim());
        dernierNumeroSalle++;
        salles.add(salle);
        return salle;
    }

    public void modifierSalle(String id, String nom, int capacite, String batiment)
            throws RessourceIntrouvableException {
        Salle salle = trouverSalle(id);
        controlerSalle(nom, capacite, batiment);   // on vérifie TOUT avant de changer quoi que ce soit
        salle.setNom(nom.trim());
        salle.setCapacite(capacite);
        salle.setBatiment(batiment.trim());
    }

    public void supprimerSalle(String id) throws RessourceIntrouvableException {
        Salle salle = trouverSalle(id);
        verifierSuppressionPossible(id);
        salles.remove(salle);
    }

    // =====================================================================
    //  Équipements
    // =====================================================================

    public ArrayList<Equipement> getEquipements() {
        return new ArrayList<Equipement>(equipements);
    }

    public Equipement ajouterEquipement(String nom, String type, int quantite) {
        controlerEquipement(nom, type, quantite);
        Equipement equipement = new Equipement("E" + (dernierNumeroEquipement + 1),
                nom.trim(), type.trim().toUpperCase(), quantite);
        dernierNumeroEquipement++;
        equipements.add(equipement);
        return equipement;
    }

    /**
     * Modifie un équipement. Si la nouvelle quantité est plus petite que le nombre d'unités
     * déjà réservées en même temps, la modification est refusée (sinon les réservations
     * existantes dépasseraient le stock).
     */
    public void modifierEquipement(String id, String nom, String type, int quantite)
            throws RessourceIntrouvableException, ConflitReservationException {
        Equipement equipement = trouverEquipement(id);
        controlerEquipement(nom, type, quantite);
        int dejaReserveesEnMemeTemps = utilisationMaximale(id);
        if (quantite < dejaReserveesEnMemeTemps) {
            throw new ConflitReservationException("quantité disponible insuffisante : " + dejaReserveesEnMemeTemps
                    + " unité(s) de " + id + " sont déjà réservées en même temps, "
                    + "impossible de descendre à " + quantite + ".");
        }
        equipement.setNom(nom.trim());
        equipement.setType(type.trim().toUpperCase());
        equipement.setQuantiteDisponible(quantite);
    }

    public void supprimerEquipement(String id) throws RessourceIntrouvableException {
        Equipement equipement = trouverEquipement(id);
        verifierSuppressionPossible(id);
        equipements.remove(equipement);
    }

    // =====================================================================
    //  Utilisateurs
    // =====================================================================

    public ArrayList<Utilisateur> getUtilisateurs() {
        return new ArrayList<Utilisateur>(utilisateurs);
    }

    public Utilisateur ajouterUtilisateur(String nom, String prenom, String role) {
        controlerUtilisateur(nom, prenom, role);
        Utilisateur utilisateur = new Utilisateur("U" + (dernierNumeroUtilisateur + 1),
                nom.trim(), prenom.trim(), role.trim().toUpperCase());
        dernierNumeroUtilisateur++;
        utilisateurs.add(utilisateur);
        return utilisateur;
    }

    public void modifierUtilisateur(String id, String nom, String prenom, String role)
            throws RessourceIntrouvableException {
        Utilisateur utilisateur = trouverUtilisateur(id);
        controlerUtilisateur(nom, prenom, role);
        utilisateur.setNom(nom.trim());
        utilisateur.setPrenom(prenom.trim());
        utilisateur.setRole(role.trim().toUpperCase());
    }

    public void supprimerUtilisateur(String id) throws RessourceIntrouvableException {
        Utilisateur utilisateur = trouverUtilisateur(id);
        verifierSuppressionPossible(id);
        utilisateurs.remove(utilisateur);
    }

    /**
     * Règle de gestion : une salle, un équipement ou un utilisateur qui a encore des réservations
     * ne peut pas être supprimé. On ne supprime JAMAIS les réservations à sa place.
     * (Les identifiants S, E et U sont tous différents : un seul test suffit pour les trois cas.)
     */
    public void verifierSuppressionPossible(String id) {
        ArrayList<Reservation> liees = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getIdRessource().equals(id) || r.getIdUtilisateur().equals(id)) {
                liees.add(r);
            }
        }
        if (!liees.isEmpty()) {
            String identifiants = "";
            for (int i = 0; i < liees.size(); i++) {
                if (i > 0) {
                    identifiants += ", ";
                }
                identifiants += liees.get(i).getId();
            }
            throw new IllegalStateException("suppression impossible : " + id + " a encore " + liees.size()
                    + " réservation(s) (" + identifiants + "). Annulez d'abord ces réservations.");
        }
    }

    // =====================================================================
    //  Réservations : réserver, modifier, annuler
    // =====================================================================

    /**
     * Réserve une ressource. Tout est vérifié AVANT d'enregistrer : si une seule condition
     * n'est pas respectée, une exception est lancée et rien n'est enregistré.
     */
    public Reservation reserver(String categorie, String idRessource, String idUtilisateur,
                                LocalDate date, LocalTime heureDebut, LocalTime heureFin)
            throws RessourceIntrouvableException, ConflitReservationException {
        // 1. La catégorie, la ressource et l'utilisateur existent
        verifierCategorie(categorie);
        Ressource ressource = trouverRessource(idRessource, categorie);
        trouverUtilisateur(idUtilisateur);
        // 2. La date et les heures sont valides (début < fin, pas dans le passé)
        verifierCreneau(date, heureDebut, heureFin);
        // 3. La ressource est réellement disponible
        verifierDisponibilite(ressource, date, heureDebut, heureFin, null);

        // Tout est bon : on crée et on enregistre
        Reservation reservation = new Reservation("R" + (dernierNumeroReservation + 1),
                idRessource, categorie, idUtilisateur, date, heureDebut, heureFin);
        dernierNumeroReservation++;
        reservations.add(reservation);
        return reservation;
    }

    /**
     * Modifie une réservation (elle garde son identifiant et sa catégorie).
     * La nouvelle ressource doit être de la même catégorie.
     * Pour les conflits, on ignore la réservation en cours de modification elle-même.
     * Si une vérification échoue, l'ancienne réservation reste intacte.
     */
    public Reservation modifierReservation(String id, String idRessource, String idUtilisateur,
                                           LocalDate date, LocalTime heureDebut, LocalTime heureFin)
            throws RessourceIntrouvableException, ConflitReservationException {
        Reservation reservation = trouverReservation(id);
        Ressource ressource = trouverRessource(idRessource, reservation.getCategorie());
        trouverUtilisateur(idUtilisateur);
        verifierCreneau(date, heureDebut, heureFin);
        verifierDisponibilite(ressource, date, heureDebut, heureFin, id);

        // Toutes les vérifications ont réussi : on applique les changements
        reservation.setIdRessource(idRessource);
        reservation.setIdUtilisateur(idUtilisateur);
        reservation.setDate(date);
        reservation.setHeureDebut(heureDebut);
        reservation.setHeureFin(heureFin);
        return reservation;
    }

    public void annulerReservation(String id) throws RessourceIntrouvableException {
        Reservation reservation = trouverReservation(id);
        reservations.remove(reservation);
    }

    /** Toutes les réservations, dans l'ordre chronologique. */
    public ArrayList<Reservation> getToutesLesReservations() {
        return trier(new ArrayList<Reservation>(reservations));
    }

    /** Le nom de la ressource réservée (ou "(introuvable)" si le fichier a été modifié à la main). */
    public String getNomRessource(Reservation reservation) {
        if (reservation.getCategorie().equals(Ressource.CATEGORIE_SALLE)) {
            Salle salle = chercherSalle(reservation.getIdRessource());
            return salle == null ? "(introuvable)" : salle.getNom();
        }
        Equipement equipement = chercherEquipement(reservation.getIdRessource());
        return equipement == null ? "(introuvable)" : equipement.getNom();
    }

    /** Le nom complet de l'utilisateur qui a réservé. */
    public String getNomUtilisateur(Reservation reservation) {
        Utilisateur utilisateur = chercherUtilisateur(reservation.getIdUtilisateur());
        return utilisateur == null ? "(introuvable)" : utilisateur.getNomComplet();
    }

    // =====================================================================
    //  Détection des conflits
    // =====================================================================

    /**
     * Règle du cahier des charges : deux créneaux se chevauchent s'ils ont lieu le MÊME JOUR
     * et si CHACUN COMMENCE AVANT LA FIN DE L'AUTRE.
     * La comparaison est stricte (isBefore, jamais <=) : 9h-11h et 11h-12h ne se chevauchent pas.
     */
    public static boolean creneauxSeChevauchent(LocalDate date1, LocalTime debut1, LocalTime fin1,
                                                LocalDate date2, LocalTime debut2, LocalTime fin2) {
        return date1.equals(date2) && debut1.isBefore(fin2) && debut2.isBefore(fin1);
    }

    /**
     * Vérifie que la ressource est libre sur le créneau demandé, sinon lance ConflitReservationException.
     * idReservationIgnoree : réservation à ne pas compter (celle qu'on est en train de modifier), ou null.
     */
    private void verifierDisponibilite(Ressource ressource, LocalDate date, LocalTime debut, LocalTime fin,
                                       String idReservationIgnoree) throws ConflitReservationException {
        if (ressource instanceof Salle) {
            // Une salle : aucune autre réservation ne doit chevaucher le créneau
            Reservation conflit = chercherConflitSalle(ressource.getId(), date, debut, fin, idReservationIgnoree);
            if (conflit != null) {
                throw new ConflitReservationException("ce créneau est déjà réservé : la salle " + ressource.getId()
                        + " (" + ressource.getNom() + ") est occupée le " + conflit.getDateTexte()
                        + " de " + conflit.getHeureDebutTexte() + " à " + conflit.getHeureFinTexte()
                        + " (réservation " + conflit.getId() + ").");
            }
        } else {
            // Un équipement : il doit rester au moins une unité libre pendant tout le créneau
            Equipement equipement = (Equipement) ressource;
            int utilisees = unitesUtilisees(equipement.getId(), date, debut, fin, idReservationIgnoree);
            if (utilisees >= equipement.getQuantiteDisponible()) {
                throw new ConflitReservationException("quantité disponible insuffisante : les "
                        + equipement.getQuantiteDisponible() + " unité(s) de " + equipement.getId()
                        + " (" + equipement.getNom() + ") sont déjà réservées sur ce créneau.");
            }
        }
    }

    /** Cherche une réservation de cette salle qui chevauche le créneau ; renvoie null s'il n'y en a pas. */
    private Reservation chercherConflitSalle(String idSalle, LocalDate date, LocalTime debut, LocalTime fin,
                                             String idReservationIgnoree) {
        for (Reservation r : reservations) {
            if (r.getCategorie().equals(Ressource.CATEGORIE_SALLE)
                    && r.getIdRessource().equals(idSalle)
                    && !r.getId().equals(idReservationIgnoree)
                    && creneauxSeChevauchent(date, debut, fin, r.getDate(), r.getHeureDebut(), r.getHeureFin())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Nombre MAXIMAL d'unités de l'équipement utilisées EN MÊME TEMPS pendant le créneau [debut, fin[.
     *
     * Chaque réservation d'un équipement représente une unité. Il ne suffit pas de compter les
     * réservations qui touchent le créneau : 8h-10h et 10h-12h touchent toutes les deux 8h-12h,
     * mais n'utilisent jamais deux unités à la fois. Le maximum est atteint à un instant où une
     * réservation commence : on teste donc le début du créneau et le début de chaque réservation
     * voisine, et on compte à chaque fois les réservations en cours.
     */
    private int unitesUtilisees(String idEquipement, LocalDate date, LocalTime debut, LocalTime fin,
                                String idReservationIgnoree) {
        // Les réservations de cet équipement qui chevauchent le créneau
        ArrayList<Reservation> voisines = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getCategorie().equals(Ressource.CATEGORIE_EQUIPEMENT)
                    && r.getIdRessource().equals(idEquipement)
                    && !r.getId().equals(idReservationIgnoree)
                    && creneauxSeChevauchent(date, debut, fin, r.getDate(), r.getHeureDebut(), r.getHeureFin())) {
                voisines.add(r);
            }
        }

        // Les instants à tester : début du créneau + début de chaque voisine qui commence plus tard
        ArrayList<LocalTime> instants = new ArrayList<LocalTime>();
        instants.add(debut);
        for (Reservation r : voisines) {
            if (r.getHeureDebut().isAfter(debut)) {
                instants.add(r.getHeureDebut());
            }
        }

        // À chaque instant, on compte les réservations en cours (commencées et pas encore terminées)
        int maximum = 0;
        for (LocalTime instant : instants) {
            int enCours = 0;
            for (Reservation r : voisines) {
                if (!r.getHeureDebut().isAfter(instant) && r.getHeureFin().isAfter(instant)) {
                    enCours++;
                }
            }
            if (enCours > maximum) {
                maximum = enCours;
            }
        }
        return maximum;
    }

    /** Plus grand nombre d'unités de cet équipement réservées en même temps, tous jours confondus. */
    private int utilisationMaximale(String idEquipement) {
        int maximum = 0;
        for (Reservation r : reservations) {
            if (r.getCategorie().equals(Ressource.CATEGORIE_EQUIPEMENT) && r.getIdRessource().equals(idEquipement)) {
                int utilisees = unitesUtilisees(idEquipement, r.getDate(), r.getHeureDebut(), r.getHeureFin(), null);
                if (utilisees > maximum) {
                    maximum = utilisees;
                }
            }
        }
        return maximum;
    }

    // =====================================================================
    //  Plannings (toujours triés dans l'ordre chronologique)
    // =====================================================================

    public ArrayList<Reservation> planningParSalle(String idSalle) throws RessourceIntrouvableException {
        trouverSalle(idSalle);   // lance une exception si la salle n'existe pas
        ArrayList<Reservation> resultat = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getCategorie().equals(Ressource.CATEGORIE_SALLE) && r.getIdRessource().equals(idSalle)) {
                resultat.add(r);
            }
        }
        return trier(resultat);
    }

    public ArrayList<Reservation> planningParEquipement(String idEquipement) throws RessourceIntrouvableException {
        trouverEquipement(idEquipement);
        ArrayList<Reservation> resultat = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getCategorie().equals(Ressource.CATEGORIE_EQUIPEMENT) && r.getIdRessource().equals(idEquipement)) {
                resultat.add(r);
            }
        }
        return trier(resultat);
    }

    public ArrayList<Reservation> planningParDate(LocalDate date) {
        ArrayList<Reservation> resultat = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getDate().equals(date)) {
                resultat.add(r);
            }
        }
        return trier(resultat);
    }

    public ArrayList<Reservation> planningParUtilisateur(String idUtilisateur) throws RessourceIntrouvableException {
        trouverUtilisateur(idUtilisateur);
        ArrayList<Reservation> resultat = new ArrayList<Reservation>();
        for (Reservation r : reservations) {
            if (r.getIdUtilisateur().equals(idUtilisateur)) {
                resultat.add(r);
            }
        }
        return trier(resultat);
    }

    /** Trie la liste dans l'ordre défini par Reservation.compareTo (date, puis heure de début). */
    private ArrayList<Reservation> trier(ArrayList<Reservation> liste) {
        Collections.sort(liste);
        return liste;
    }

    // =====================================================================
    //  Recherche de disponibilités sur un créneau
    // =====================================================================

    /** Les salles qu'aucune réservation ne chevauche sur ce créneau. */
    public ArrayList<Salle> sallesDisponibles(LocalDate date, LocalTime debut, LocalTime fin) {
        verifierCreneau(date, debut, fin);
        ArrayList<Salle> libres = new ArrayList<Salle>();
        for (Salle salle : salles) {
            if (chercherConflitSalle(salle.getId(), date, debut, fin, null) == null) {
                libres.add(salle);
            }
        }
        return libres;
    }

    /** Les équipements dont il reste au moins une unité libre pendant tout le créneau. */
    public ArrayList<Equipement> equipementsDisponibles(LocalDate date, LocalTime debut, LocalTime fin) {
        verifierCreneau(date, debut, fin);
        ArrayList<Equipement> libres = new ArrayList<Equipement>();
        for (Equipement equipement : equipements) {
            if (unitesDisponibles(equipement, date, debut, fin) > 0) {
                libres.add(equipement);
            }
        }
        return libres;
    }

    /** Nombre d'unités encore libres sur ce créneau = quantité totale - unités utilisées en même temps. */
    public int unitesDisponibles(Equipement equipement, LocalDate date, LocalTime debut, LocalTime fin) {
        int libres = equipement.getQuantiteDisponible()
                - unitesUtilisees(equipement.getId(), date, debut, fin, null);
        return Math.max(libres, 0);
    }

    // =====================================================================
    //  Contrôles de validité (utilisés à la saisie ET au chargement des fichiers)
    //  Ils lancent IllegalArgumentException avec un message explicatif.
    // =====================================================================

    /** Un texte ne doit pas être vide ni contenir le séparateur des fichiers (le point-virgule). */
    private static void verifierTexte(String valeur, String champ) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new IllegalArgumentException(champ + " ne peut pas être vide.");
        }
        if (valeur.contains(";")) {
            throw new IllegalArgumentException(champ + " ne peut pas contenir de point-virgule (;).");
        }
    }

    private static void verifierPositif(int valeur, String champ) {
        if (valeur <= 0) {
            throw new IllegalArgumentException(champ + " doit être supérieure à 0.");
        }
    }

    private static void controlerSalle(String nom, int capacite, String batiment) {
        verifierTexte(nom, "le nom de la salle");
        verifierPositif(capacite, "la capacité");
        verifierTexte(batiment, "le bâtiment");
    }

    private static void controlerEquipement(String nom, String type, int quantite) {
        verifierTexte(nom, "le nom de l'équipement");
        verifierTexte(type, "le type");
        verifierPositif(quantite, "la quantité");
    }

    private static void controlerUtilisateur(String nom, String prenom, String role) {
        verifierTexte(nom, "le nom");
        verifierTexte(prenom, "le prénom");
        verifierTexte(role, "le rôle");
    }

    private static void verifierCategorie(String categorie) {
        if (!Ressource.CATEGORIE_SALLE.equals(categorie) && !Ressource.CATEGORIE_EQUIPEMENT.equals(categorie)) {
            throw new IllegalArgumentException("catégorie incorrecte : \"" + categorie
                    + "\" (SALLE ou EQUIPEMENT attendu).");
        }
    }

    /** L'heure de début doit être strictement antérieure à l'heure de fin. */
    private static void verifierHeures(LocalTime debut, LocalTime fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("les heures de début et de fin sont obligatoires.");
        }
        if (!debut.isBefore(fin)) {
            throw new IllegalArgumentException("l'heure de début doit être antérieure à l'heure de fin.");
        }
    }

    /** Créneau valide pour une NOUVELLE réservation : heures cohérentes et date qui n'est pas dans le passé. */
    private static void verifierCreneau(LocalDate date, LocalTime debut, LocalTime fin) {
        if (date == null) {
            throw new IllegalArgumentException("la date est obligatoire.");
        }
        verifierHeures(debut, fin);
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("la date ne peut pas être dans le passé.");
        }
    }

    /**
     * Contrôle d'une réservation lue dans le fichier. Mêmes règles qu'une nouvelle réservation
     * SAUF la date : une réservation passée a le droit d'exister (elle était valide quand elle a été faite).
     */
    private void controlerReservationChargee(Reservation r)
            throws RessourceIntrouvableException, ConflitReservationException {
        verifierCategorie(r.getCategorie());
        Ressource ressource = trouverRessource(r.getIdRessource(), r.getCategorie());
        trouverUtilisateur(r.getIdUtilisateur());
        verifierHeures(r.getHeureDebut(), r.getHeureFin());
        verifierDisponibilite(ressource, r.getDate(), r.getHeureDebut(), r.getHeureFin(), null);
    }

    // =====================================================================
    //  Identifiants
    // =====================================================================

    /** Un identifiant est une lettre suivie d'un nombre sans zéro au début (ex : S12). */
    private static boolean identifiantValide(String id, char lettre) {
        if (id == null || id.length() < 2 || id.length() > 10 || id.charAt(0) != lettre || id.charAt(1) == '0') {
            return false;
        }
        for (int i = 1; i < id.length(); i++) {
            if (id.charAt(i) < '0' || id.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    private static void verifierIdentifiant(String id, char lettre, boolean dejaUtilise) {
        if (!identifiantValide(id, lettre)) {
            throw new IllegalArgumentException("identifiant invalide (une lettre " + lettre
                    + " suivie d'un nombre est attendue, par exemple " + lettre + "1).");
        }
        if (dejaUtilise) {
            throw new IllegalArgumentException("cet identifiant est déjà utilisé (doublon).");
        }
    }

    /** Le nombre contenu dans un identifiant valide : "S12" donne 12. */
    private static int numero(String id) {
        return Integer.parseInt(id.substring(1));
    }
}
