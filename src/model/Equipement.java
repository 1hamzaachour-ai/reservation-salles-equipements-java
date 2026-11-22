package model;

/**
 * Un équipement réservable (vidéoprojecteur, PC portable, caméra...).
 * Il hérite de Ressource (id, nom) et ajoute son type et le nombre d'exemplaires possédés.
 *
 * Chaque réservation d'un équipement correspond à UNE unité.
 */
public class Equipement extends Ressource {

    private String type;
    private int quantiteDisponible;

    public Equipement(String id, String nom, String type, int quantiteDisponible) {
        super(id, nom);   // la classe mère Ressource enregistre l'identifiant et le nom
        this.type = type;
        this.quantiteDisponible = quantiteDisponible;
    }

    @Override
    public String getCategorie() {
        return CATEGORIE_EQUIPEMENT;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantiteDisponible() {
        return quantiteDisponible;
    }

    public void setQuantiteDisponible(int quantiteDisponible) {
        this.quantiteDisponible = quantiteDisponible;
    }

    @Override
    public String toString() {
        return super.toString() + " (type : " + type + ", quantité : " + quantiteDisponible + ")";
    }
}
