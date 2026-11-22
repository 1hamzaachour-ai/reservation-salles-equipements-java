# Réservation de salles et d'équipements

Application **Java en mode console** qui permet à un établissement de gérer ses salles, son matériel
(vidéoprojecteurs, PC portables, caméras…), ses utilisateurs et les réservations, en garantissant
qu'une ressource n'est **jamais réservée deux fois sur un même créneau**.

Projet réalisé pour le contrôle pratique de fin de semestre (cahier des charges :
`Projet-Java-reservation-salles-equipements.pdf`). Les données sont stockées dans des **fichiers texte**
(ni base de données, ni interface graphique) et l'application n'utilise que la bibliothèque standard de Java
(Java 8 ou plus récent).

---

## 1. Fonctionnalités

| Menu | Ce que fait l'application |
|---|---|
| **1. Gérer les salles** | Ajouter, modifier, supprimer, afficher les salles (id, nom, capacité, bâtiment) |
| **2. Gérer les équipements** | Ajouter, modifier, supprimer, afficher les équipements (id, nom, type, quantité) |
| **3. Gérer les utilisateurs** | Ajouter, modifier, supprimer, afficher les utilisateurs (id, nom, prénom, rôle) |
| **4. Réserver une ressource** | Réserver une salle ou un équipement après vérification de toutes les règles de gestion |
| **5. Annuler / modifier une réservation** | Lister, rechercher par identifiant, annuler (avec confirmation), modifier |
| **6. Consulter le planning** | Par salle, par équipement, par date ou par utilisateur, **trié dans l'ordre chronologique** |
| **7. Rechercher une ressource disponible** | Pour un créneau donné : salles libres et équipements avec le nombre d'unités encore disponibles |
| **0. Quitter** | Sauvegarde finale puis fermeture |

Autres points :

* les **identifiants sont attribués automatiquement** (S1, E1, U1, R1…) : l'utilisateur ne les saisit jamais à la création ;
* chaque saisie est contrôlée : une erreur de frappe affiche un message clair et la question est reposée,
  le programme ne plante jamais ;
* les données sont **chargées au démarrage** et **sauvegardées après chaque modification** et à la fermeture ;
* si une sauvegarde échoue, le programme le dit clairement (il n'affiche « Données sauvegardées » que si l'écriture a réussi).

---

## 2. Organisation des dossiers

```
(racine du projet)
├── src/
│   ├── Main.java                          menu principal et sous-menus
│   ├── model/
│   │   ├── Ressource.java                 classe abstraite (id, nom)
│   │   ├── Salle.java                     hérite de Ressource (+ capacité, bâtiment)
│   │   ├── Equipement.java                hérite de Ressource (+ type, quantité disponible)
│   │   ├── Utilisateur.java
│   │   └── Reservation.java
│   ├── gestion/
│   │   ├── GestionReservations.java       toute la logique métier
│   │   └── GestionFichiers.java           lecture / écriture des fichiers texte
│   ├── util/
│   │   └── Saisie.java                    saisies sécurisées au clavier
│   └── exceptions/
│       ├── ConflitReservationException.java
│       └── RessourceIntrouvableException.java
├── data/
│   ├── salles.txt
│   ├── equipements.txt
│   ├── utilisateurs.txt
│   ├── reservations.txt
│   └── compteurs.txt                      créé automatiquement au premier lancement (voir § 7)
├── nbproject/ et build.xml                fichiers du projet NetBeans (facultatifs : les autres éditeurs les ignorent)
├── README.md
├── presentation/                          présentation PowerPoint, script oral et aperçu PDF (facultatif)
├── GUIDE_PRESENTATION.md                  aide-mémoire pour l'oral (peut être supprimé avant de rendre le projet)
├── Projet-Java-reservation-salles-equipements.pdf   le cahier des charges
└── .gitignore
```

Les packages Java correspondent aux dossiers (`package model;` pour `src/model/…`). `Main` est dans `src/`,
sans package.

---

## 3. Rôle de chaque classe

| Classe | Rôle |
|---|---|
| `Ressource` | **Classe abstraite** : regroupe ce qui est commun aux salles et aux équipements (identifiant, nom) et impose à ses sous-classes de donner leur catégorie (`getCategorie()` : `SALLE` ou `EQUIPEMENT`). |
| `Salle` | Hérite de `Ressource` ; ajoute `capacite` et `batiment`. |
| `Equipement` | Hérite de `Ressource` ; ajoute `type` et `quantiteDisponible`. Chaque réservation d'équipement correspond à **une unité**. |
| `Utilisateur` | Identifiant, nom, prénom, rôle (enseignant, étudiant…). |
| `Reservation` | Identifiant, `idRessource`, `categorie`, `idUtilisateur`, `date`, `heureDebut`, `heureFin`. Implémente `Comparable` pour être triée par ordre chronologique. |
| `GestionReservations` | **Toute la logique métier** : quatre `ArrayList` (salles, équipements, utilisateurs, réservations), ajout / modification / suppression, réservation, détection des conflits, plannings, recherche de disponibilités, génération des identifiants. Elle ne fait **aucun affichage** : en cas de problème elle lance une exception avec un message clair. |
| `GestionFichiers` | Lit et écrit les fichiers texte (UTF-8). Vérifie la syntaxe de chaque ligne et signale les lignes incorrectes. |
| `Saisie` | Méthodes de saisie qui **redemandent tant que la valeur est invalide** (entier, texte, date, heure, choix de menu, identifiant, confirmation). Un seul `Scanner` pour toute l'application. |
| `Main` | Menu principal et sous-menus : pose les questions, appelle `GestionReservations`, affiche les résultats et les messages d'erreur, déclenche la sauvegarde. |
| `ConflitReservationException` | Exception personnalisée : créneau déjà réservé ou quantité disponible insuffisante. |
| `RessourceIntrouvableException` | Exception personnalisée : un identifiant ne correspond à rien (salle, équipement, utilisateur ou réservation). |

**Comment les classes communiquent :** `Main` → `Saisie` (lire) → `GestionReservations` (agir) → `GestionFichiers` (lire / écrire les fichiers).
`GestionReservations` manipule des objets `Salle`, `Equipement`, `Utilisateur` et `Reservation`.

---

## 4. Notions Java utilisées

* **Classes et objets**, constructeurs, **encapsulation** (attributs `private`, accès par getters / setters) ;
* **Héritage** (`extends`, `super(...)`) et **classe abstraite** (`abstract`), `@Override` ;
* **Constantes** (`static final`) ;
* **Collections** : `ArrayList<Salle>`, `ArrayList<Equipement>`, `ArrayList<Utilisateur>`, `ArrayList<Reservation>` ;
* **Tri** : `Collections.sort` avec l'interface `Comparable` (méthode `compareTo` de `Reservation`) ;
* **Boucles** `for`, *for-each*, `while`, `do … while`, conditions `if / else`, `switch` ;
* **Chaînes** : `trim`, `split`, `contains`, `toUpperCase`, `String.format` ;
* **`Scanner`** et `nextLine()` pour les saisies ;
* **`LocalDate`, `LocalTime`, `DateTimeFormatter`** pour les dates et les heures ;
* **Exceptions** : `try / catch`, `throws`, `throw`, exceptions personnalisées, `try-with-resources` (fermeture automatique des fichiers) ;
* **Fichiers texte** : `BufferedReader`, `BufferedWriter`, `InputStreamReader`, `OutputStreamWriter`, `FileInputStream`,
  `FileOutputStream`, `File`, encodage UTF-8 (`StandardCharsets.UTF_8`).

> `FileReader` / `FileWriter` utilisent l'encodage par défaut de la machine (Windows-1252 avant Java 18), ce qui abîme
> les accents. On passe donc par `InputStreamReader` / `OutputStreamWriter` en précisant UTF-8.

---

## 5. Fonctionnement des réservations

Quand l'utilisateur réserve (menu 4), `GestionReservations.reserver(...)` vérifie **dans cet ordre** :

1. la catégorie (`SALLE` ou `EQUIPEMENT`) ;
2. la ressource existe (sinon `RessourceIntrouvableException`) ;
3. l'utilisateur existe ;
4. la date et les heures sont valides, **l'heure de début est strictement antérieure à l'heure de fin** ;
5. **la date n'est pas dans le passé** ;
6. la ressource est **réellement disponible** (sinon `ConflitReservationException`).

Si une seule condition échoue, **rien n'est enregistré**. Sinon la réservation reçoit son identifiant
(R1, R2…), est ajoutée à la liste, puis les fichiers sont sauvegardés immédiatement et un récapitulatif s'affiche.

Autres règles de gestion :

* **Modifier une réservation** : l'identifiant est conservé ; tous les contrôles sont refaits sur les nouvelles valeurs en
  **ignorant la réservation en cours de modification** (elle peut donc chevaucher son ancienne version). Si un contrôle échoue,
  l'ancienne réservation reste intacte. La nouvelle ressource doit être de la même catégorie (une salle reste une salle).
* **Annuler** : confirmation demandée ; le créneau redevient immédiatement disponible.
* **Supprimer** une salle, un équipement ou un utilisateur qui a encore des réservations est **refusé** (le message liste les
  réservations concernées). Le programme ne supprime jamais les réservations à votre place.
* **Diminuer la quantité** d'un équipement est refusé si la nouvelle quantité est inférieure au nombre d'unités déjà réservées en même temps.

---

## 6. La règle de détection des conflits

Deux créneaux se chevauchent s'ils ont lieu **le même jour** et si **chacun commence avant la fin de l'autre**
(`GestionReservations.creneauxSeChevauchent`) :

```java
return date1.equals(date2) && debut1.isBefore(fin2) && debut2.isBefore(fin1);
```

La comparaison est **stricte** (`isBefore`, jamais `<=`) : une réservation qui finit à 11h n'empêche pas d'en commencer une autre à 11h.

Exemples avec une salle déjà réservée de **09:00 à 11:00** :

| Nouvelle demande | Résultat | Pourquoi |
|---|---|---|
| 10:00 – 12:00 | refusée | 10:00 est avant 11:00 **et** 09:00 est avant 12:00 |
| 11:00 – 12:00 | **acceptée** | 11:00 n'est pas avant 11:00 : elle commence quand l'autre se termine |
| 08:00 – 09:00 | **acceptée** | 09:00 n'est pas avant 08:00 |
| 08:00 – 09:30 | refusée | chevauchement de 09:00 à 09:30 |
| 09:30 – 10:00 | refusée | contenue dans la réservation existante |
| même créneau un autre jour | acceptée | pas le même jour |

**Cas des équipements.** Un équipement a une quantité (par exemple 3 vidéoprojecteurs). Chaque réservation utilise une unité ;
on accepte tant qu'il reste une unité libre **à chaque instant du créneau demandé**. On regarde donc le nombre d'unités
utilisées *en même temps* (et non le simple nombre de réservations qui touchent le créneau) :

* trois réservations 09:00–11:00 sont acceptées, la **quatrième est refusée** ;
* avec un stock de 2, les réservations 08:00–10:00 et 10:00–12:00 n'utilisent jamais deux unités à la fois : une
  troisième réservation 08:00–12:00 est acceptée (deux unités au maximum à tout instant).

Les trois cas d'échec ont un message dédié, par exemple :

```
Erreur : ce créneau est déjà réservé : la salle S2 (A102) est occupée le 07/12/2026 de 09:00 à 11:00 (réservation R6).
Erreur : quantité disponible insuffisante : les 3 unité(s) de E1 (Videoprojecteur Epson) sont déjà réservées sur ce créneau.
Erreur : la date ne peut pas être dans le passé.
```

---

## 7. Fonctionnement des fichiers texte

Les fichiers sont dans le dossier **`data/`**, à la racine du projet. Un enregistrement par ligne, champs séparés par un
**point-virgule**, encodage **UTF-8**, date au format `aaaa-mm-jj`, heure au format `HH:mm`.

| Fichier | Format d'une ligne | Exemple |
|---|---|---|
| `salles.txt` | `id;nom;capacité;bâtiment` | `S1;A101;30;A` |
| `equipements.txt` | `id;nom;type;quantité` | `E1;Videoprojecteur Epson;VIDEOPROJECTEUR;3` |
| `utilisateurs.txt` | `id;nom;prénom;rôle` | `U1;Benali;Sara;ENSEIGNANT` |
| `reservations.txt` | `id;idRessource;catégorie;idUtilisateur;date;début;fin` | `R1;S1;SALLE;U1;2026-12-07;09:00;11:00` |

À la saisie dans la console, la date s'écrit `jj/mm/aaaa`. La catégorie d'une réservation vaut `SALLE` ou `EQUIPEMENT`.

**Au démarrage** : le dossier `data/` et les fichiers manquants sont créés (vides) ; un fichier existant n'est jamais écrasé ;
chaque ligne est lue puis contrôlée avec **les mêmes règles qu'un ajout au clavier** (champs vides, `;` dans un nom, nombres négatifs ou nuls,
identifiants en double, ressource ou utilisateur inconnu, dates et heures invalides, conflits…).
Une ligne incorrecte est **ignorée et signalée** par un message (numéro de ligne, raison, contenu) : elle ne fait pas planter le programme.
Une copie des fichiers d'origine est alors enregistrée dans `data/*.bak`, car ces lignes disparaîtront à la prochaine sauvegarde.
Les réservations passées sont acceptées au chargement (elles étaient valides quand elles ont été faites).

**Sauvegarde** : après chaque ajout, modification, suppression, réservation ou annulation, puis à la fermeture, les fichiers sont réécrits en UTF-8.
Une erreur d'écriture est signalée clairement ; les modifications restent en mémoire et une nouvelle tentative a lieu à la modification suivante
et à la fermeture (où l'on peut aussi réessayer à la demande).

**Accents** : les fichiers sont lus et écrits en UTF-8. Un fichier enregistré en « ANSI » (Windows-1252) par le Bloc-notes est reconnu, relu correctement et
réenregistré en UTF-8 ; un BOM éventuel au début du fichier est ignoré.

**Identifiants** : le prochain numéro est le plus grand numéro **déjà attribué** + 1. Pour ne **jamais réutiliser un identifiant supprimé**,
même s'il s'agissait du plus grand et même après un redémarrage, le petit fichier technique `compteurs.txt` mémorise le dernier numéro attribué
(`S;5`, `E;4`, `U;6`, `R;12`). Il est facultatif : s'il manque, il est recréé à partir des identifiants présents dans les quatre autres fichiers.
Pour repartir d'une base **totalement vide** (identifiants repartant de S1, E1, U1, R1), videz les quatre fichiers **et** supprimez `compteurs.txt`.

Les fichiers fournis dans `data/` contiennent quelques données de démonstration (facultatives : on peut les vider).

---

## 8. Instructions d'exécution

Le programme doit être lancé **depuis la racine du projet** : il y cherche le dossier `data/`. Il affiche au démarrage le chemin
complet du dossier de données utilisé.

### Depuis un terminal (Windows, Linux ou macOS)

```
javac -encoding UTF-8 -d out -sourcepath src src/Main.java
java -cp out Main
```

La première commande compile toutes les classes dans le dossier `out/` ; la seconde lance l'application.
(`-encoding UTF-8` est nécessaire car le code source contient des accents.)

### Depuis un éditeur

* **IntelliJ IDEA** : *File ▸ New ▸ Project from Existing Sources…*, choisir le dossier du projet (créer le projet à partir des sources existantes).
  Si `src` n'est pas reconnu : clic droit sur `src` ▸ *Mark Directory as ▸ Sources Root*. Ouvrir `Main.java` et cliquer sur la flèche verte à côté de `main`.
* **Eclipse** : *File ▸ New ▸ Java Project*, décocher *Use default location* et choisir le dossier du projet ; Eclipse détecte `src`.
  Puis clic droit sur `Main.java` ▸ *Run As ▸ Java Application*. (Si les accents du code apparaissent mal : clic droit sur le projet ▸ *Properties ▸ Resource ▸ Text file encoding ▸ UTF-8*.)
* **Visual Studio Code** : installer *Extension Pack for Java*, ouvrir le dossier du projet (*File ▸ Open Folder*), ouvrir `src/Main.java` et cliquer sur *Run* au-dessus de `main`.
  Les saisies se font dans le terminal intégré.
* **NetBeans** : *File ▸ Open Project…* et choisir le dossier du projet (il contient déjà les fichiers `nbproject/` et `build.xml`), puis *Run ▸ Run Project* (F6).
  Le dossier de travail est la racine du projet et les options UTF-8 sont déjà réglées dans les propriétés du projet (les accents s'affichent correctement dans la fenêtre *Output*).

Dans tous les cas le **dossier de travail** doit être la racine du projet (c'est le réglage par défaut des éditeurs ci-dessus).

### Si les accents s'affichent mal à l'écran

Les fichiers de données sont toujours en UTF-8 ; seul l'**affichage** de la console peut être concerné (c'est un réglage de la console, pas du programme).

* PowerShell ou cmd : lancer d'abord `chcp 65001`, puis `java -Dfile.encoding=UTF-8 -cp out Main` (sous PowerShell, mettre l'option entre guillemets : `java "-Dfile.encoding=UTF-8" -cp out Main`) ;
* dans un éditeur : régler l'encodage de la console sur UTF-8, ou ajouter dans les options de la machine virtuelle (*VM options*) :
  `-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8`.

---

## 9. Exemples d'utilisation

Avec les données de démonstration fournies (les lignes après `:` sont ce que l'on tape) :

**Réserver une salle, puis essayer un créneau qui chevauche**

```
=== RESERVATION DE SALLES ET D'EQUIPEMENTS ===
1. Gérer les salles
2. Gérer les équipements
3. Gérer les utilisateurs
4. Réserver une ressource
5. Annuler / modifier une réservation
6. Consulter le planning
7. Rechercher une ressource disponible
0. Quitter
Votre choix : 4

=== RESERVER UNE RESSOURCE ===
1. Réserver une salle
2. Réserver un équipement
0. Retour
Votre choix : 1
(la liste des salles s'affiche)
Identifiant de la salle (ex : S1) : S2
Identifiant de l'utilisateur (ex : U1) : U1
Date (JJ/MM/AAAA) : 07/12/2026
Heure de début (HH:mm) : 09:00
Heure de fin (HH:mm) : 11:00

=== RESERVATION CONFIRMEE ===

Identifiant : R6
Ressource : S2 (A102)
Utilisateur : U1 (Sara Benali)
Date : 07/12/2026
Début : 09:00
Fin : 11:00
Données sauvegardées.
```

Même salle, même jour, de 10:00 à 12:00 :

```
Erreur : ce créneau est déjà réservé : la salle S2 (A102) est occupée le 07/12/2026 de 09:00 à 11:00 (réservation R6).
```

Même salle, de 11:00 à 12:00 : acceptée (réservation R7).

**Les trois vidéoprojecteurs** : `E1` a déjà deux réservations le 07/12/2026 de 09:00 à 11:00 (R3 et R4). Une troisième est acceptée,
une quatrième refusée :

```
Erreur : quantité disponible insuffisante : les 3 unité(s) de E1 (Videoprojecteur Epson) sont déjà réservées sur ce créneau.
```

**Saisies invalides** (le programme redemande) :

```
Votre choix : abc
Erreur : veuillez saisir un nombre valide.
Votre choix : 9
Erreur : choix invalide. Entrez un nombre entre 0 et 7.
Date (JJ/MM/AAAA) : 31/02/2027
Erreur : format de date incorrect. Utilisez JJ/MM/AAAA.
Heure de début (HH:mm) : 25:00
Erreur : heure invalide. Utilisez HH:mm.
```

**Supprimer une salle réservée** :

```
Erreur : suppression impossible : S1 a encore 2 réservation(s) (R1, R2). Annulez d'abord ces réservations.
```

**Rechercher une ressource disponible** le 07/12/2026 de 09:00 à 11:00 : les salles S2, S3 et S4 sont libres ; le vidéoprojecteur `E1`
affiche « Quantité totale 3 — Unités disponibles 1 ».

---

## 10. Correspondance avec le cahier des charges

| N° | Tâche demandée | Où dans le code |
|---|---|---|
| 1 | Modéliser les classes (`Ressource` abstraite, `Salle`, `Equipement`, `Utilisateur`, `Reservation`) | `src/model/` |
| 2 | Gérer salles, équipements, utilisateurs (ajout, modification, suppression, liste — `ArrayList`) | `GestionReservations` (+ menus de `Main`) |
| 3 | Réserver une ressource en détectant les conflits (`LocalDate`, `LocalTime`) | `GestionReservations.reserver`, `creneauxSeChevauchent`, `verifierDisponibilite` |
| 4 | Annuler ou modifier une réservation | `GestionReservations.annulerReservation`, `modifierReservation` |
| 5 | Planning par salle, équipement, date, utilisateur (parcours et tri) | `GestionReservations.planningParXxx`, `Reservation.compareTo` |
| 6 | Rechercher les ressources disponibles sur un créneau | `GestionReservations.sallesDisponibles`, `equipementsDisponibles`, `unitesDisponibles` |
| 7 | Charger et sauvegarder dans les fichiers texte | `GestionFichiers`, `GestionReservations.chargerDonnees`, `sauvegarderTout` |
| 8 | Exceptions personnalisées | `ConflitReservationException`, `RessourceIntrouvableException` (`src/exceptions/`) |
| 9 | Menu console et saisies sécurisées (`Scanner`, boucles, `switch`) | `Main`, `Saisie` |

---

## 11. Choix de conception et limites

* **« La date ne peut pas être dans le passé »** : seule la date est comparée (comme l'indique le cahier des charges). Une réservation **aujourd'hui** est acceptée,
  même si l'heure de début est déjà passée.
* **Modifier une réservation passée** est refusé (la nouvelle date doit elle aussi ne pas être dans le passé) ; on peut en revanche l'annuler.
* Les heures vont de `00:00` à `23:59` : un créneau ne peut pas traverser minuit.
* Le **rôle** d'un utilisateur et le **type** d'un équipement sont du texte libre, enregistré en majuscules (`ENSEIGNANT`, `VIDEOPROJECTEUR`…).
* Les noms ne peuvent pas contenir de point-virgule (c'est le séparateur des fichiers).
* Les suppressions demandent une confirmation (`O` / `N`), comme les annulations de réservation.
* Si le clavier est fermé (Ctrl+D / Ctrl+Z), le programme sauvegarde et se ferme proprement.
