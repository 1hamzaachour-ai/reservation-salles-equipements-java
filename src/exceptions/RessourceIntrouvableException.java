package exceptions;

/**
 * Exception personnalisée : l'élément recherché n'existe pas.
 *
 * Elle est lancée quand un identifiant ne correspond à aucune salle, aucun équipement,
 * aucun utilisateur ou aucune réservation (on prend "ressource" au sens large :
 * tout ce qui est retrouvé grâce à son identifiant).
 */
public class RessourceIntrouvableException extends Exception {

    // Numéro de version : les exceptions sont "Serializable", Java demande donc de le préciser
    private static final long serialVersionUID = 1L;

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
