package model;

/**
 * Une personne qui peut réserver : enseignant, étudiant, etc.
 */
public class Utilisateur {

    private String id;
    private String nom;
    private String prenom;
    private String role;

    public Utilisateur(String id, String nom, String prenom, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /** Le nom complet, dans l'ordre habituel : "Sara Benali". */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return id + " - " + getNomComplet() + " (" + role + ")";
    }
}
