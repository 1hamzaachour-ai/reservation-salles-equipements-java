package model;

/**
 * Classe abstraite : une ressource réservable est soit une Salle, soit un Equipement.
 *
 * Elle regroupe ce qui est commun aux deux (l'identifiant et le nom).
 * On ne peut pas écrire "new Ressource(...)" : il faut créer une Salle ou un Equipement.
 */
public abstract class Ressource {

    // Les deux catégories possibles (ce sont les valeurs écrites dans reservations.txt)
    public static final String CATEGORIE_SALLE = "SALLE";
    public static final String CATEGORIE_EQUIPEMENT = "EQUIPEMENT";

    // Attributs privés (encapsulation) : on y accède uniquement avec les getters et les setters
    private String id;
    private String nom;

    public Ressource(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    /** Chaque sous-classe donne sa catégorie : "SALLE" ou "EQUIPEMENT". */
    public abstract String getCategorie();

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

    @Override
    public String toString() {
        return id + " - " + nom;
    }
}
