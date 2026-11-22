# Guide pour présenter le projet devant le professeur

Aide-mémoire personnel pour l'oral. Tous les extraits de code ci-dessous viennent du projet (les numéros de ligne indiquent où les
retrouver). Ce fichier peut être supprimé avant de rendre le projet.

Le support de présentation est dans le dossier `presentation/` : `Presentation_Projet_Java.pptx` (12 diapositives, notes orales
incluses), `Script_Presentation.md` (le discours, diapositive par diapositive) et `Apercu_Presentation.pdf`.

---

## Le projet en 30 secondes

> « C'est une application console qui gère des salles, des équipements, des utilisateurs et des réservations. Les données sont
> stockées dans des fichiers texte. Le cœur du projet est la règle qui empêche de réserver deux fois la même salle sur un créneau
> qui se chevauche, et qui limite les équipements à leur quantité en stock. »

Architecture en trois couches (cherche à la dire avec tes mots) :

```text
Main  ──lit au clavier──▶  Saisie            (saisies contrôlées, redemandées tant qu'elles sont invalides)
 │
 └──appelle──▶  GestionReservations  ──contient──▶  4 ArrayList (salles, équipements, utilisateurs, réservations)
                       │
                       └──utilise──▶  GestionFichiers  ──lit / écrit──▶  data/*.txt
```

* `Main` n'a **aucune règle métier** : il pose des questions, appelle `GestionReservations`, affiche le résultat.
* `GestionReservations` ne fait **aucun affichage** : si quelque chose ne va pas, elle lance une exception avec un message clair.
* `GestionFichiers` ne connaît **que les fichiers**.

---

## Démonstration conseillée (≈ 5 minutes, avec les données fournies)

1. **Lancer** le programme : montrer le menu (identique au PDF) et le chemin du dossier `data/` affiché au démarrage.
2. **Salle** — menu 4 ▸ 1 : salle `S2`, utilisateur `U1`, date `07/12/2026`, `09:00` → `11:00` : *réservation confirmée (R6)*.
   Ouvrir `data/reservations.txt` : **la ligne R6 est déjà écrite** (sauvegarde immédiate).
3. **Chevauchement** — même salle, `10:00` → `12:00` : *refusé*. Puis `11:00` → `12:00` : *accepté* (« un créneau qui finit à 11h n'empêche pas d'en commencer un à 11h »).
4. **Équipement** — menu 4 ▸ 2 : `E1` (3 vidéoprojecteurs, 2 déjà réservés de 09:00 à 11:00) : `U3`, `07/12/2026`, `09:00` → `11:00` : *accepté* (3ᵉ).
   Un 4ᵉ (`U4`, `09:30` → `10:30`) : *quantité disponible insuffisante*.
5. **Planning** — menu 6 ▸ 3 (par date, `07/12/2026`) : toutes les réservations du jour **triées par heure**.
6. **Disponibilités** — menu 7 : `07/12/2026`, `09:00` → `11:00` : `S1` et `S2` n'apparaissent plus, `E1` non plus (0 unité libre), `E2` affiche « 5 / 5 ».
7. **Annuler** — menu 5 ▸ 3 : `R6`, confirmer `O` ; refaire la recherche : `S2` est de nouveau disponible.
8. **Robustesse** — taper `abc` dans un menu, la date `31/02/2027`, l'heure `25:00`, l'identifiant `S99` : message clair à chaque fois, le programme continue.
9. **Suppression refusée** — menu 1 ▸ 3 ▸ `S1` : « suppression impossible : S1 a encore 2 réservation(s) ».
10. **Persistance** — quitter (0), relancer : les données sont toujours là.

---

## Les 10 points à savoir expliquer

### 1. Pourquoi `Ressource` est une classe abstraite

*En une phrase :* une « ressource » toute seule n'existe pas, c'est toujours **une salle ou un équipement** ; la classe regroupe ce qu'ils ont en commun
et oblige chaque sous-classe à dire de quelle catégorie elle est.

`src/model/Ressource.java` :

```java
public abstract class Ressource {
    private String id;
    private String nom;
    ...
    /** Chaque sous-classe donne sa catégorie : "SALLE" ou "EQUIPEMENT". */
    public abstract String getCategorie();
```

*Exemple concret :* `new Ressource(...)` ne compile pas. Dans `GestionReservations.trouverRessource(id, categorie)` on renvoie une `Ressource` qui est, selon le cas, une
`Salle` ou un `Equipement` : c'est le **polymorphisme**.

### 2. Comment `Salle` et `Equipement` héritent de `Ressource`

`src/model/Salle.java` :

```java
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
```

*À dire :* `extends` = « une salle **est une** ressource » ; `super(id, nom)` appelle le constructeur de la classe mère ; une `Salle` possède `getId()`
et `getNom()` sans les avoir réécrits. `Equipement` fait pareil avec `type` et `quantiteDisponible`.

### 3. Pourquoi `ArrayList`

*En une phrase :* on ne sait pas à l'avance combien il y aura de salles ou de réservations : un tableau a une taille fixe, une `ArrayList` grandit et rétrécit toute seule
(`add`, `remove`, `size`) et le type entre chevrons (`<Salle>`) garantit qu'on n'y range que des salles.

`src/gestion/GestionReservations.java` (ligne 31) :

```java
private ArrayList<Salle> salles = new ArrayList<Salle>();
```

*Exemples :* `salles.add(salle);` (ajout) · `salles.remove(salle);` (suppression) · `for (Salle salle : salles) { ... }` (parcours).

### 4. La génération automatique des identifiants

*Piège à connaître :* `"S" + (salles.size() + 1)` est faux. Avec S1, S2, S3, on supprime S2 : il reste 2 salles, la suivante s'appellerait `S3` et on aurait **deux S3**.

*Solution :* un compteur par type, qui ne diminue **jamais** (attributs lignes 38 à 41, utilisés dans `ajouterSalle`, ligne 263) :

```java
Salle salle = new Salle("S" + (dernierNumeroSalle + 1), nom.trim(), capacite, batiment.trim());
dernierNumeroSalle++;
salles.add(salle);
```

*Après un redémarrage :* au chargement, `dernierNumeroSalle` = le plus grand numéro trouvé dans le fichier (ou la valeur de `compteurs.txt` si elle est plus grande).
Ce petit fichier existe pour ne jamais réutiliser un identifiant, **même si on a supprimé le plus grand** (sinon, après avoir supprimé S5 et relancé, la prochaine salle
s'appellerait de nouveau S5).

### 5. `LocalDate` et `LocalTime`

*En une phrase :* ce sont des classes de `java.time` qui représentent une **date** (sans heure) et une **heure** (sans date) ; elles se comparent avec `isBefore`, `isAfter`, `equals`
(jamais avec `==` ni `<`).

`GestionReservations.verifierHeures` (ligne 715) et `verifierCreneau` (ligne 725) :

```java
if (!debut.isBefore(fin)) {
    throw new IllegalArgumentException("l'heure de début doit être antérieure à l'heure de fin.");
}
...
if (date.isBefore(LocalDate.now())) {
    throw new IllegalArgumentException("la date ne peut pas être dans le passé.");
}
```

*Lecture de la saisie* (`src/util/Saisie.java`, ligne 29) :

```java
DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
```

`STRICT` refuse `31/02/2027` (sans lui, Java corrigerait silencieusement en 28/02) ; en mode strict l'année s'écrit `uuuu`.

### 6. Comment détecter un chevauchement

`GestionReservations.creneauxSeChevauchent` (ligne 473) :

```java
return date1.equals(date2) && debut1.isBefore(fin2) && debut2.isBefore(fin1);
```

*À dire :* deux créneaux se chevauchent s'ils sont **le même jour** et si **chacun commence avant la fin de l'autre**. Raisonnement par l'absurde : s'ils ne se chevauchent pas, l'un
finit avant (ou quand) l'autre commence.

| 09–11 contre | `debut1 < fin2` ? | `debut2 < fin1` ? | conflit |
|---|---|---|---|
| 10–12 | 09 < 12 ✔ | 10 < 11 ✔ | **oui** |
| 11–12 | 09 < 12 ✔ | 11 < 11 ✘ | non |
| 08–09 | 09 < 09 ✘ | 08 < 11 ✔ | non |

*Pourquoi `isBefore` et pas `<=` :* avec `<=`, 09–11 et 11–12 seraient en conflit alors que le cahier des charges dit explicitement que non.

### 7. Comment gérer les trois vidéoprojecteurs

*Idée :* chaque réservation d'un équipement prend **une unité**. On accepte tant qu'**à chaque instant du créneau demandé** il reste au moins une unité libre.

`GestionReservations.verifierDisponibilite` (ligne 482) :

```java
Equipement equipement = (Equipement) ressource;
int utilisees = unitesUtilisees(equipement.getId(), date, debut, fin, idReservationIgnoree);
if (utilisees >= equipement.getQuantiteDisponible()) {
    throw new ConflitReservationException("quantité disponible insuffisante : ...");
}
```

`unitesUtilisees` (ligne 528) calcule le **nombre maximal d'unités utilisées en même temps** pendant le créneau :

1. on garde les réservations de cet équipement qui chevauchent le créneau (avec la règle du point 6) ;
2. le maximum est atteint à un instant où une réservation commence : on teste le début du créneau et le début de chaque réservation voisine ;
3. à chaque instant on compte les réservations en cours (`!debut.isAfter(instant) && fin.isAfter(instant)`).

*Exemples :* trois réservations 09–11 → à 09:00 il y a 3 unités utilisées ; stock 3 → la 4ᵉ est refusée (3 ≥ 3). Avec un stock de 2, les réservations 08–10 et 10–12 n'utilisent
jamais 2 unités ensemble, donc une 3ᵉ réservation 08–12 est acceptée (c'est pour cela qu'on ne se contente pas de **compter** les réservations qui touchent le créneau).

### 8. Comment lire et écrire les fichiers

*Format :* une ligne = un enregistrement, champs séparés par `;`.

Lecture (`GestionFichiers`, lignes 323 et suivantes) :

```java
try (BufferedReader lecteur = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), decodeur))) {
    String ligne = lecteur.readLine();
    while (ligne != null) {
        lignes.add(ligne);
        ligne = lecteur.readLine();
    }
}
```

Transformation d'une ligne en objet (`chargerSalles`, ligne 131) :

```java
String[] champs = decouper(ligne, 4);                       // "S1;A101;30;A" -> {"S1","A101","30","A"}
int capacite = lireEntier(champs[2], "la capacité");        // Integer.parseInt avec un message clair
salles.add(new Salle(champs[0], champs[1], capacite, champs[3]));
```

Écriture (ligne 337) :

```java
try (BufferedWriter ecrivain = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(fichier), StandardCharsets.UTF_8))) {
    for (String ligne : lignes) {
        ecrivain.write(ligne);
        ecrivain.newLine();
    }
}
```

*À dire :* `BufferedReader` lit ligne par ligne ; **try-with-resources** ferme le fichier automatiquement même en cas d'erreur ; on précise **UTF-8** (avec `InputStreamReader` /
`OutputStreamWriter`) parce que `FileReader` / `FileWriter` utilisent l'encodage de la machine et abîment les accents ; le fichier est **réécrit en entier** à chaque sauvegarde.
Une ligne incorrecte est signalée et ignorée (numéro de ligne, raison, contenu), elle ne fait pas planter le programme.

### 9. Comment fonctionnent les exceptions personnalisées

`src/exceptions/ConflitReservationException.java` :

```java
public class ConflitReservationException extends Exception {
    private static final long serialVersionUID = 1L;
    public ConflitReservationException(String message) {
        super(message);
    }
}
```

*Où on la lance* (`GestionReservations.verifierDisponibilite`) : `throw new ConflitReservationException("ce créneau est déjà réservé : ...");`
*Où on l'attrape* (`Main`, dans chaque sous-menu) :

```java
} catch (RessourceIntrouvableException | ConflitReservationException
        | IllegalArgumentException | IllegalStateException e) {
    System.out.println("Erreur : " + e.getMessage());
}
```

*À dire :* elles étendent `Exception` (exceptions **contrôlées**) : le compilateur oblige à les déclarer (`throws`) ou à les traiter (`try / catch`), on ne peut donc pas les oublier.
`RessourceIntrouvableException` : identifiant inconnu (salle, équipement, utilisateur ou réservation). `ConflitReservationException` : créneau déjà pris ou stock insuffisant.
Pour une donnée invalide (nom vide, capacité négative…) on utilise l'exception standard `IllegalArgumentException`, pour une suppression interdite `IllegalStateException`,
et `IOException` pour les erreurs de fichier.

### 10. Comment les classes communiquent entre elles

*Déroulé de « Réserver une salle » :*

1. `Main.reserver(...)` pose les questions avec **`Saisie`** (`lireIdentifiant`, `lireDate`, `lireHeure`, redemandées tant qu'elles sont invalides) ;
2. il appelle **`gestion.reserver(...)`** qui enchaîne les contrôles (`GestionReservations`, ligne 394) :

```java
verifierCategorie(categorie);
Ressource ressource = trouverRessource(idRessource, categorie);   // RessourceIntrouvableException
trouverUtilisateur(idUtilisateur);                                // RessourceIntrouvableException
verifierCreneau(date, heureDebut, heureFin);                      // IllegalArgumentException
verifierDisponibilite(ressource, date, heureDebut, heureFin, null); // ConflitReservationException
```

3. si tout est bon, elle crée la `Reservation`, l'ajoute à l'`ArrayList` et la renvoie ;
4. `Main` affiche le récapitulatif puis appelle `sauvegarder()` (ligne 701) → `gestion.sauvegarderTout()` → **`GestionFichiers`** réécrit les fichiers.

*Rien n'est enregistré si une vérification échoue* : l'exception est lancée avant la création de la réservation.

---

## Questions probables du jury

| Question | Réponse courte |
|---|---|
| Pourquoi pas de base de données ? | Imposé par le sujet : les données sont dans des fichiers texte. |
| Pourquoi `Comparable` sur `Reservation` ? | Pour trier avec `Collections.sort` : `compareTo` compare la date, puis l'heure de début, puis l'heure de fin. |
| Que se passe-t-il si on modifie une réservation sans changer l'heure ? | Le contrôle de conflit **ignore la réservation en cours de modification** ; sinon elle serait en conflit avec elle-même. |
| Et si la modification échoue ? | Tous les contrôles sont faits **avant** d'appliquer les setters : l'ancienne réservation reste intacte. |
| Où validez-vous les données ? | Dans `GestionReservations` (une seule fois) : `Saisie` redemande à l'écran, mais c'est la logique métier qui a le dernier mot, y compris pour les lignes lues dans les fichiers. |
| Que se passe-t-il si un fichier est modifié à la main avec une erreur ? | La ligne est ignorée, signalée au démarrage (numéro, raison) et une copie `.bak` des fichiers d'origine est faite. |
| Et si l'écriture d'un fichier échoue ? | Le programme affiche « ERREUR DE SAUVEGARDE » (il n'affiche jamais « sauvegardé » à tort), garde les modifications en mémoire et réessaie à la modification suivante et à la fermeture. |
| Pourquoi `InputStreamReader` et pas `FileReader` ? | Pour imposer UTF-8 et garder les accents sur n'importe quelle machine. |
| À quoi sert `compteurs.txt` ? | À ne jamais réutiliser un identifiant supprimé, même le plus grand après un redémarrage. Il est facultatif (recréé s'il manque). |
| Pourquoi `static` dans `Main` et `Saisie` ? | Il n'y a qu'un seul menu et qu'un seul `Scanner` pour toute l'application ; pas besoin de créer d'objet `Main`. |
| Pourquoi un seul `Scanner` ? | Plusieurs `Scanner` sur `System.in` se volent les données ; et on lit toujours avec `nextLine()` pour éviter le piège du retour à la ligne oublié après `nextInt()`. |
| Peut-on supprimer une salle qui a des réservations ? | Non : `verifierSuppressionPossible` refuse et liste les réservations concernées ; on ne les supprime pas automatiquement. |
| Peut-on réserver dans le passé ? | Non (la date est comparée à `LocalDate.now()`). Aujourd'hui est accepté : le cahier des charges ne parle que de la **date**. |

---

## Choix à assumer (au cas où on te les demande)

* La règle des équipements compte les unités utilisées **au même instant** (et pas le nombre de réservations qui touchent le créneau).
* Une réservation modifiée garde sa catégorie : une salle reste une salle.
* `compteurs.txt` est un cinquième fichier, **technique et facultatif**, ajouté uniquement pour ne jamais réutiliser un identifiant.
* Les suppressions demandent une confirmation, comme l'annulation d'une réservation.
