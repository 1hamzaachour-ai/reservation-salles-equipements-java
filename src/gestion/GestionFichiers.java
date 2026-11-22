package gestion;

import model.Equipement;
import model.Reservation;
import model.Salle;
import model.Utilisateur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

/**
 * Lecture et écriture des fichiers texte du dossier data/.
 *
 * Un enregistrement par ligne, champs séparés par un point-virgule, encodage UTF-8 :
 *   salles.txt         id;nom;capacite;batiment
 *   equipements.txt    id;nom;type;quantite
 *   utilisateurs.txt   id;nom;prenom;role
 *   reservations.txt   id;idRessource;categorie;idUtilisateur;date;debut;fin
 *
 * En plus des quatre fichiers du cahier des charges, un petit fichier technique compteurs.txt
 * garde le dernier numéro attribué à chaque type d'identifiant (S, E, U, R) afin de ne JAMAIS
 * réutiliser un identifiant, même après la suppression du plus grand et un redémarrage.
 *
 * Cette classe ne vérifie que la SYNTAXE des lignes (nombre de champs, nombres, dates, heures).
 * Les règles métier (capacité positive, ressource existante, conflits...) sont contrôlées
 * ensuite par GestionReservations. Une ligne incorrecte est ignorée et signalée dans la liste
 * des avertissements : elle ne fait jamais planter le programme.
 */
public class GestionFichiers {

    private static final String SEPARATEUR = ";";

    // Heure écrite/lue dans les fichiers : HH:mm (STRICT : refuse 24:00 ou 9h30)
    private static final DateTimeFormatter FORMAT_HEURE =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private File dossier;
    private File fichierSalles;
    private File fichierEquipements;
    private File fichierUtilisateurs;
    private File fichierReservations;
    private File fichierCompteurs;

    // Messages sur les lignes ignorées pendant le chargement
    private ArrayList<String> avertissements = new ArrayList<String>();

    public GestionFichiers(String cheminDossier) {
        dossier = new File(cheminDossier);
        fichierSalles = new File(dossier, "salles.txt");
        fichierEquipements = new File(dossier, "equipements.txt");
        fichierUtilisateurs = new File(dossier, "utilisateurs.txt");
        fichierReservations = new File(dossier, "reservations.txt");
        fichierCompteurs = new File(dossier, "compteurs.txt");
    }

    /** Chemin complet du dossier de données (affiché au démarrage). */
    public String getCheminDossier() {
        return dossier.getAbsolutePath();
    }

    /** Les messages sur les lignes ignorées lors des chargements. */
    public ArrayList<String> getAvertissements() {
        return new ArrayList<String>(avertissements);
    }

    // =====================================================================
    //  Préparation
    // =====================================================================

    /**
     * Crée le dossier data/ et les fichiers qui n'existent pas encore (vides).
     * Un fichier qui existe déjà n'est JAMAIS modifié ni écrasé ici.
     */
    public void preparerFichiers() throws IOException {
        if (dossier.exists() && !dossier.isDirectory()) {
            throw new IOException(dossier.getAbsolutePath() + " existe mais n'est pas un dossier");
        }
        if (!dossier.exists() && !dossier.mkdirs()) {
            throw new IOException("impossible de créer le dossier " + dossier.getAbsolutePath());
        }
        creerSiAbsent(fichierSalles);
        creerSiAbsent(fichierEquipements);
        creerSiAbsent(fichierUtilisateurs);
        creerSiAbsent(fichierReservations);
        creerSiAbsent(fichierCompteurs);
    }

    private void creerSiAbsent(File fichier) throws IOException {
        if (!fichier.exists()) {
            fichier.createNewFile();
        }
    }

    /**
     * Copie les quatre fichiers de données tels qu'ils sont (salles.txt.bak, etc.).
     * Main l'appelle quand des lignes ont été ignorées au chargement : ces lignes disparaîtront
     * du fichier à la prochaine sauvegarde, la copie permet de les récupérer.
     */
    public void copierFichiersOriginaux() throws IOException {
        File[] fichiers = {fichierSalles, fichierEquipements, fichierUtilisateurs, fichierReservations};
        for (File fichier : fichiers) {
            File copie = new File(dossier, fichier.getName() + ".bak");
            Files.copy(fichier.toPath(), copie.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // =====================================================================
    //  Chargement
    // =====================================================================

    /** Lit salles.txt. Les lignes mal formées sont ignorées et signalées. */
    public ArrayList<Salle> chargerSalles() throws IOException {
        ArrayList<Salle> salles = new ArrayList<Salle>();
        ArrayList<String> lignes = lireLignes(fichierSalles);
        for (int i = 0; i < lignes.size(); i++) {
            String ligne = lignes.get(i);
            if (ligne.trim().isEmpty()) {
                continue;   // une ligne vide n'est pas une erreur
            }
            try {
                String[] champs = decouper(ligne, 4);
                int capacite = lireEntier(champs[2], "la capacité");
                salles.add(new Salle(champs[0], champs[1], capacite, champs[3]));
            } catch (IllegalArgumentException e) {
                avertir(fichierSalles, i + 1, ligne, e);
            }
        }
        return salles;
    }

    /** Lit equipements.txt. Les lignes mal formées sont ignorées et signalées. */
    public ArrayList<Equipement> chargerEquipements() throws IOException {
        ArrayList<Equipement> equipements = new ArrayList<Equipement>();
        ArrayList<String> lignes = lireLignes(fichierEquipements);
        for (int i = 0; i < lignes.size(); i++) {
            String ligne = lignes.get(i);
            if (ligne.trim().isEmpty()) {
                continue;
            }
            try {
                String[] champs = decouper(ligne, 4);
                int quantite = lireEntier(champs[3], "la quantité");
                equipements.add(new Equipement(champs[0], champs[1], champs[2], quantite));
            } catch (IllegalArgumentException e) {
                avertir(fichierEquipements, i + 1, ligne, e);
            }
        }
        return equipements;
    }

    /** Lit utilisateurs.txt. Les lignes mal formées sont ignorées et signalées. */
    public ArrayList<Utilisateur> chargerUtilisateurs() throws IOException {
        ArrayList<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
        ArrayList<String> lignes = lireLignes(fichierUtilisateurs);
        for (int i = 0; i < lignes.size(); i++) {
            String ligne = lignes.get(i);
            if (ligne.trim().isEmpty()) {
                continue;
            }
            try {
                String[] champs = decouper(ligne, 4);
                utilisateurs.add(new Utilisateur(champs[0], champs[1], champs[2], champs[3]));
            } catch (IllegalArgumentException e) {
                avertir(fichierUtilisateurs, i + 1, ligne, e);
            }
        }
        return utilisateurs;
    }

    /** Lit reservations.txt. Les lignes mal formées sont ignorées et signalées. */
    public ArrayList<Reservation> chargerReservations() throws IOException {
        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        ArrayList<String> lignes = lireLignes(fichierReservations);
        for (int i = 0; i < lignes.size(); i++) {
            String ligne = lignes.get(i);
            if (ligne.trim().isEmpty()) {
                continue;
            }
            try {
                String[] champs = decouper(ligne, 7);
                LocalDate date = lireDate(champs[4]);
                LocalTime debut = lireHeure(champs[5]);
                LocalTime fin = lireHeure(champs[6]);
                reservations.add(new Reservation(champs[0], champs[1], champs[2], champs[3], date, debut, fin));
            } catch (IllegalArgumentException e) {
                avertir(fichierReservations, i + 1, ligne, e);
            }
        }
        return reservations;
    }

    /**
     * Lit compteurs.txt (lignes "S;4", "E;3", "U;5", "R;12").
     * Renvoie {dernier n° de salle, d'équipement, d'utilisateur, de réservation} ; 0 si absent.
     */
    public int[] chargerCompteurs() throws IOException {
        int[] compteurs = new int[4];   // quatre cases initialisées à 0
        ArrayList<String> lignes = lireLignes(fichierCompteurs);
        for (int i = 0; i < lignes.size(); i++) {
            String ligne = lignes.get(i);
            if (ligne.trim().isEmpty()) {
                continue;
            }
            try {
                String[] champs = decouper(ligne, 2);
                int position = champs[0].length() == 1 ? "SEUR".indexOf(champs[0]) : -1;
                if (position < 0) {
                    throw new IllegalArgumentException("lettre inconnue : \"" + champs[0] + "\" (S, E, U ou R attendu)");
                }
                int valeur = lireEntier(champs[1], "le numéro");
                if (valeur < 0) {
                    throw new IllegalArgumentException("le numéro ne peut pas être négatif");
                }
                compteurs[position] = valeur;
            } catch (IllegalArgumentException e) {
                avertir(fichierCompteurs, i + 1, ligne, e);
            }
        }
        return compteurs;
    }

    // =====================================================================
    //  Sauvegarde (le fichier est entièrement réécrit à chaque fois)
    // =====================================================================

    public void sauvegarderSalles(ArrayList<Salle> salles) throws IOException {
        ArrayList<String> lignes = new ArrayList<String>();
        for (Salle salle : salles) {
            lignes.add(salle.getId() + SEPARATEUR + salle.getNom() + SEPARATEUR
                    + salle.getCapacite() + SEPARATEUR + salle.getBatiment());
        }
        ecrireLignes(fichierSalles, lignes);
    }

    public void sauvegarderEquipements(ArrayList<Equipement> equipements) throws IOException {
        ArrayList<String> lignes = new ArrayList<String>();
        for (Equipement equipement : equipements) {
            lignes.add(equipement.getId() + SEPARATEUR + equipement.getNom() + SEPARATEUR
                    + equipement.getType() + SEPARATEUR + equipement.getQuantiteDisponible());
        }
        ecrireLignes(fichierEquipements, lignes);
    }

    public void sauvegarderUtilisateurs(ArrayList<Utilisateur> utilisateurs) throws IOException {
        ArrayList<String> lignes = new ArrayList<String>();
        for (Utilisateur utilisateur : utilisateurs) {
            lignes.add(utilisateur.getId() + SEPARATEUR + utilisateur.getNom() + SEPARATEUR
                    + utilisateur.getPrenom() + SEPARATEUR + utilisateur.getRole());
        }
        ecrireLignes(fichierUtilisateurs, lignes);
    }

    public void sauvegarderReservations(ArrayList<Reservation> reservations) throws IOException {
        ArrayList<String> lignes = new ArrayList<String>();
        for (Reservation r : reservations) {
            lignes.add(r.getId() + SEPARATEUR + r.getIdRessource() + SEPARATEUR + r.getCategorie()
                    + SEPARATEUR + r.getIdUtilisateur() + SEPARATEUR + r.getDate()   // date : aaaa-mm-jj
                    + SEPARATEUR + r.getHeureDebut().format(FORMAT_HEURE)             // heure : HH:mm
                    + SEPARATEUR + r.getHeureFin().format(FORMAT_HEURE));
        }
        ecrireLignes(fichierReservations, lignes);
    }

    public void sauvegarderCompteurs(int salles, int equipements, int utilisateurs, int reservations)
            throws IOException {
        ArrayList<String> lignes = new ArrayList<String>();
        lignes.add("S" + SEPARATEUR + salles);
        lignes.add("E" + SEPARATEUR + equipements);
        lignes.add("U" + SEPARATEUR + utilisateurs);
        lignes.add("R" + SEPARATEUR + reservations);
        ecrireLignes(fichierCompteurs, lignes);
    }

    // =====================================================================
    //  Outils de lecture / écriture
    // =====================================================================

    /**
     * Lit toutes les lignes d'un fichier en UTF-8.
     * FileReader utilise l'encodage par défaut de la machine (Windows-1252 avant Java 18), ce qui
     * abîme les accents : on précise donc UTF-8 avec InputStreamReader.
     * Si le fichier n'est pas en UTF-8 (cas du Bloc-notes en mode "ANSI"), on le relit en
     * Windows-1252 pour ne pas perdre les accents ; il sera réenregistré en UTF-8.
     */
    private ArrayList<String> lireLignes(File fichier) throws IOException {
        ArrayList<String> lignes;
        try {
            lignes = lireLignes(fichier, StandardCharsets.UTF_8.newDecoder());   // strict : signale les octets invalides
        } catch (CharacterCodingException e) {
            CharsetDecoder ansi = Charset.forName("windows-1252").newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
            lignes = lireLignes(fichier, ansi);
            avertissements.add(fichier.getName() + " n'est pas encodé en UTF-8 : il a été relu en Windows-1252 "
                    + "et sera réenregistré en UTF-8.");
        }
        // Un fichier UTF-8 enregistré "avec BOM" commence par un caractère invisible : on l'enlève
        if (!lignes.isEmpty() && lignes.get(0).startsWith("﻿")) {
            lignes.set(0, lignes.get(0).substring(1));
        }
        return lignes;
    }

    private ArrayList<String> lireLignes(File fichier, CharsetDecoder decodeur) throws IOException {
        ArrayList<String> lignes = new ArrayList<String>();
        // try-with-resources : le fichier est fermé automatiquement, même en cas d'erreur
        try (BufferedReader lecteur = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), decodeur))) {
            String ligne = lecteur.readLine();
            while (ligne != null) {
                lignes.add(ligne);
                ligne = lecteur.readLine();
            }
        }
        return lignes;
    }

    /** Réécrit entièrement le fichier en UTF-8, une ligne par enregistrement. */
    private void ecrireLignes(File fichier, ArrayList<String> lignes) throws IOException {
        try (BufferedWriter ecrivain = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fichier), StandardCharsets.UTF_8))) {
            for (String ligne : lignes) {
                ecrivain.write(ligne);
                ecrivain.newLine();
            }
        } catch (IOException e) {
            throw new IOException("écriture impossible dans " + fichier.getName() + " (" + e.getMessage() + ")", e);
        }
    }

    // =====================================================================
    //  Outils d'analyse des lignes
    // =====================================================================

    /** Découpe une ligne sur les points-virgules ; il faut exactement nombreChamps champs. */
    private String[] decouper(String ligne, int nombreChamps) {
        String[] champs = ligne.split(SEPARATEUR, -1);   // -1 : garde les champs vides en fin de ligne
        if (champs.length != nombreChamps) {
            throw new IllegalArgumentException(nombreChamps + " champs attendus, " + champs.length + " trouvé(s)");
        }
        for (int i = 0; i < champs.length; i++) {
            champs[i] = champs[i].trim();
        }
        return champs;
    }

    private int lireEntier(String texte, String nomChamp) {
        try {
            return Integer.parseInt(texte);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nomChamp + " n'est pas un nombre entier : \"" + texte + "\"");
        }
    }

    private LocalDate lireDate(String texte) {
        try {
            return LocalDate.parse(texte);   // format ISO : aaaa-mm-jj
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("date invalide (format aaaa-mm-jj attendu) : \"" + texte + "\"");
        }
    }

    private LocalTime lireHeure(String texte) {
        try {
            return LocalTime.parse(texte, FORMAT_HEURE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("heure invalide (format HH:mm attendu) : \"" + texte + "\"");
        }
    }

    private void avertir(File fichier, int numeroLigne, String ligne, IllegalArgumentException e) {
        avertissements.add(fichier.getName() + ", ligne " + numeroLigne + " ignorée (" + e.getMessage() + ") : " + ligne);
    }
}
