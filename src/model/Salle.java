package model;

/**
 * Une salle réservable. Elle hérite de Ressource (id, nom)
 * et ajoute ses attributs propres : la capacité et le bâtiment.
 */
public class Salle extends Ressource {

    private int capacite;
    private String batiment;

    public Salle(String id, String nom, int capacite, String batiment) {
        super(id, nom);   // la classe mère Ressource enregistre l'identifiant et le nom
        this.capacite = capacite;
        this.batiment = batiment;
    }

    @Override
    public String getCategorie() {
        return CATEGORIE_SALLE;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getBatiment() {
        return batiment;
    }

    public void setBatiment(String batiment) {
        this.batiment = batiment;
    }

    @Override
    public String toString() {
        return super.toString() + " (capacité : " + capacite + ", bâtiment : " + batiment + ")";
    }
}
