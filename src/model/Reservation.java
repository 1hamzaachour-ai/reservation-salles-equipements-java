package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Une réservation : un utilisateur réserve une ressource (salle ou équipement)
 * à une date, de heureDebut à heureFin.
 *
 * La réservation ne garde que l'identifiant de la ressource et celui de l'utilisateur
 * (comme dans le fichier reservations.txt) ; la catégorie vaut "SALLE" ou "EQUIPEMENT".
 *
 * Elle implémente Comparable pour pouvoir être triée dans l'ordre chronologique
 * (Collections.sort utilise la méthode compareTo).
 */
public class Reservation implements Comparable<Reservation> {

    // Formats d'AFFICHAGE à l'écran (le format des fichiers est géré par GestionFichiers)
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMAT_HEURE = DateTimeFormatter.ofPattern("HH:mm");

    private String id;
    private String idRessource;
    private String categorie;
    private String idUtilisateur;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    public Reservation(String id, String idRessource, String categorie, String idUtilisateur,
                       LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        this.id = id;
        this.idRessource = idRessource;
        this.categorie = categorie;
        this.idUtilisateur = idUtilisateur;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRessource() {
        return idRessource;
    }

    public void setIdRessource(String idRessource) {
        this.idRessource = idRessource;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    /** La date au format de la console : 23/11/2026. */
    public String getDateTexte() {
        return date.format(FORMAT_DATE);
    }

    /** L'heure de début au format HH:mm. */
    public String getHeureDebutTexte() {
        return heureDebut.format(FORMAT_HEURE);
    }

    /** L'heure de fin au format HH:mm. */
    public String getHeureFinTexte() {
        return heureFin.format(FORMAT_HEURE);
    }

    /**
     * Ordre chronologique : d'abord la date, puis l'heure de début, puis l'heure de fin.
     * Si deux réservations ont exactement le même créneau, on les classe par identifiant
     * (le plus court d'abord, pour que R2 passe avant R10).
     */
    @Override
    public int compareTo(Reservation autre) {
        int resultat = date.compareTo(autre.date);
        if (resultat == 0) {
            resultat = heureDebut.compareTo(autre.heureDebut);
        }
        if (resultat == 0) {
            resultat = heureFin.compareTo(autre.heureFin);
        }
        if (resultat == 0) {
            if (id.length() != autre.id.length()) {
                resultat = id.length() - autre.id.length();
            } else {
                resultat = id.compareTo(autre.id);
            }
        }
        return resultat;
    }

    @Override
    public String toString() {
        return id + " | " + categorie + " " + idRessource + " | " + idUtilisateur + " | "
                + getDateTexte() + " | " + getHeureDebutTexte() + " - " + getHeureFinTexte();
    }
}
