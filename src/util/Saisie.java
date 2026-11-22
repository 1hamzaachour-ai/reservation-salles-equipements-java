package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/**
 * Saisies sécurisées au clavier : chaque méthode REDEMANDE tant que la valeur est invalide,
 * et affiche un message d'erreur clair. Le programme ne plante donc jamais sur une faute de frappe.
 *
 * Deux règles importantes :
 *  - un SEUL Scanner pour toute l'application (plusieurs Scanner sur System.in se volent les données) ;
 *  - on lit toujours une ligne entière avec nextLine() (jamais nextInt()), ce qui évite le piège
 *    du retour à la ligne oublié dans le tampon.
 *
 * Quand le clavier est fermé (Ctrl+D / Ctrl+Z), nextLine() lance NoSuchElementException :
 * on ne l'attrape pas ici, c'est Main qui ferme alors le programme proprement.
 */
public class Saisie {

    private static final Scanner SCANNER = new Scanner(System.in);

    // Mode STRICT : refuse les dates qui n'existent pas (31/02/2026) et les heures comme 24:00.
    // En mode STRICT, l'année s'écrit "uuuu" (et non "yyyy").
    // "d/M/uuuu" accepte aussi bien 05/12/2026 que 5/12/2026 ; "H:mm" accepte 09:30 comme 9:30.
    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMAT_HEURE =
            DateTimeFormatter.ofPattern("H:mm").withResolverStyle(ResolverStyle.STRICT);

    // ===================== Lecture de base =====================

    /** Affiche le message puis lit une ligne (sans les espaces au début et à la fin). */
    public static String lireLigne(String message) {
        System.out.print(message);
        System.out.flush();
        return SCANNER.nextLine().trim();
    }

    // ===================== Texte =====================

    /** Lit un texte : il ne doit pas être vide ni contenir de point-virgule (séparateur des fichiers). */
    public static String lireTexte(String message) {
        return lireTexteOuDefaut(message, null);
    }

    /** Comme lireTexte, mais si l'utilisateur appuie seulement sur Entrée, on garde la valeur actuelle. */
    public static String lireTexte(String message, String valeurActuelle) {
        return lireTexteOuDefaut(message, valeurActuelle);
    }

    private static String lireTexteOuDefaut(String message, String defaut) {
        while (true) {
            String texte = lireLigne(message);
            if (texte.isEmpty() && defaut != null) {
                return defaut;
            }
            if (texte.isEmpty()) {
                System.out.println("Erreur : cette saisie ne peut pas être vide.");
            } else if (texte.contains(";")) {
                System.out.println("Erreur : le caractère point-virgule (;) est interdit.");
            } else {
                return texte;
            }
        }
    }

    // ===================== Nombres =====================

    /** Lit un nombre entier (positif, nul ou négatif). */
    public static int lireEntier(String message) {
        while (true) {
            String texte = lireLigne(message);
            try {
                return Integer.parseInt(texte);
            } catch (NumberFormatException e) {
                System.out.println("Erreur : veuillez saisir un nombre valide.");
            }
        }
    }

    /** Lit un nombre entier strictement positif (1, 2, 3...). */
    public static int lireEntierPositif(String message) {
        return lireEntierPositifOuDefaut(message, 0);
    }

    /** Comme lireEntierPositif, mais Entrée seule garde la valeur actuelle. */
    public static int lireEntierPositif(String message, int valeurActuelle) {
        return lireEntierPositifOuDefaut(message, valeurActuelle);
    }

    // defaut = 0 signifie "pas de valeur par défaut" (0 n'est jamais une réponse valide ici)
    private static int lireEntierPositifOuDefaut(String message, int defaut) {
        while (true) {
            String texte = lireLigne(message);
            if (texte.isEmpty() && defaut > 0) {
                return defaut;
            }
            try {
                int nombre = Integer.parseInt(texte);
                if (nombre > 0) {
                    return nombre;
                }
                System.out.println("Erreur : le nombre doit être supérieur à 0.");
            } catch (NumberFormatException e) {
                System.out.println("Erreur : veuillez saisir un nombre valide.");
            }
        }
    }

    /** Lit un choix de menu compris entre min et max (inclus). */
    public static int lireChoixMenu(int min, int max) {
        while (true) {
            int choix = lireEntier("Votre choix : ");
            if (choix >= min && choix <= max) {
                return choix;
            }
            System.out.println("Erreur : choix invalide. Entrez un nombre entre " + min + " et " + max + ".");
        }
    }

    // ===================== Identifiant =====================

    /** Lit un identifiant (S1, E2, U3, R4...). Il est mis en majuscules : "s1" devient "S1". */
    public static String lireIdentifiant(String message) {
        while (true) {
            String id = lireLigne(message).toUpperCase();
            if (!id.isEmpty()) {
                return id;
            }
            System.out.println("Erreur : l'identifiant ne peut pas être vide.");
        }
    }

    // ===================== Date et heure =====================

    /** Lit une date au format JJ/MM/AAAA. */
    public static LocalDate lireDate(String message) {
        return lireDateOuDefaut(message, null);
    }

    /** Comme lireDate, mais Entrée seule garde la date actuelle. */
    public static LocalDate lireDate(String message, LocalDate dateActuelle) {
        return lireDateOuDefaut(message, dateActuelle);
    }

    private static LocalDate lireDateOuDefaut(String message, LocalDate defaut) {
        while (true) {
            String texte = lireLigne(message);
            if (texte.isEmpty() && defaut != null) {
                return defaut;
            }
            try {
                return LocalDate.parse(texte, FORMAT_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("Erreur : format de date incorrect. Utilisez JJ/MM/AAAA.");
            }
        }
    }

    /** Lit une heure au format HH:mm. */
    public static LocalTime lireHeure(String message) {
        return lireHeureOuDefaut(message, null);
    }

    /** Comme lireHeure, mais Entrée seule garde l'heure actuelle. */
    public static LocalTime lireHeure(String message, LocalTime heureActuelle) {
        return lireHeureOuDefaut(message, heureActuelle);
    }

    private static LocalTime lireHeureOuDefaut(String message, LocalTime defaut) {
        while (true) {
            String texte = lireLigne(message);
            if (texte.isEmpty() && defaut != null) {
                return defaut;
            }
            try {
                return LocalTime.parse(texte, FORMAT_HEURE);
            } catch (DateTimeParseException e) {
                System.out.println("Erreur : heure invalide. Utilisez HH:mm.");
            }
        }
    }

    // ===================== Confirmation =====================

    /** Pose une question fermée : true pour O / OUI, false pour N / NON. */
    public static boolean lireConfirmation(String message) {
        while (true) {
            String reponse = lireLigne(message).toUpperCase();
            if (reponse.equals("O") || reponse.equals("OUI")) {
                return true;
            }
            if (reponse.equals("N") || reponse.equals("NON")) {
                return false;
            }
            System.out.println("Erreur : répondez par O (oui) ou N (non).");
        }
    }
}
