package exceptions;

/**
 * Exception personnalisée : la réservation demandée est impossible.
 *
 * Elle est lancée quand le créneau est déjà pris (cas d'une salle) ou quand toutes les
 * unités de l'équipement sont déjà réservées (disponibilité insuffisante).
 */
public class ConflitReservationException extends Exception {

    // Numéro de version : les exceptions sont "Serializable", Java demande donc de le préciser
    private static final long serialVersionUID = 1L;

    public ConflitReservationException(String message) {
        super(message);
    }
}
