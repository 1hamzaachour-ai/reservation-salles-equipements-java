# Script de présentation orale

**Projet :** Application de réservation de salles et d'équipements (Java, console) — présentation PowerPoint de 12 diapositives.

**Durée prévue :** environ **7 min 47 s** (1126 mots à 145 mots par minute), soit dans la fourchette de 7 à 10 minutes. Avec les pauses entre les diapositives, comptez environ 8 à 9 minutes.

> Ce texte est aussi dans les **notes** de chaque diapositive du fichier `Presentation_Projet_Java.pptx` (mode Présentateur de PowerPoint). Il est écrit à la première personne du pluriel, pour un binôme.

## Avant de commencer

- Compléter la diapositive 1 : noms du binôme, nom du professeur, établissement (les trois informations entre crochets).
- Ouvrir le projet (NetBeans ou autre) pour pouvoir lancer une démonstration si on la demande ; voir `GUIDE_PRESENTATION.md` à la racine du projet.
- Ne pas lire les diapositives : elles donnent les mots-clés, le texte ci-dessous donne les explications.

## Répartition du temps

| Diapositive | Titre | Durée approximative |
|---|---|---|
| 1 | Page de garde | 0 min 25 s |
| 2 | Contexte et problématique | 0 min 36 s |
| 3 | Objectifs du projet | 0 min 35 s |
| 4 | Technologies utilisées | 0 min 28 s |
| 5 | Architecture du projet | 0 min 39 s |
| 6 | Programmation orientée objet et héritage | 0 min 41 s |
| 7 | Gestion des réservations | 0 min 45 s |
| 8 | Détection des conflits | 1 min 03 s |
| 9 | Gestion des équipements | 0 min 43 s |
| 10 | Stockage des données | 0 min 37 s |
| 11 | Fonctionnalités et tests | 0 min 43 s |
| 12 | Conclusion et perspectives | 0 min 32 s |
| | **Total** | **7 min 47 s** |

---

## Diapositive 1 — Page de garde

**Ce qui apparaît sur la diapositive :** Le titre « Application de réservation de salles et d'équipements », le sous-titre « Projet universitaire – Programmation Java », les informations du binôme (à compléter) et un schéma de réservations sur une frise horaire.

**Ce que je dois expliquer oralement** (environ 0 min 25 s) :

> Bonjour. Nous allons vous présenter notre projet de fin de semestre : une application Java en mode console qui permet de réserver des salles et des équipements, sans jamais créer de conflit. Nous commencerons par le contexte, puis nous expliquerons comment l'application est construite, comment elle évite les doubles réservations, et ce que nous avons testé.

**Points techniques importants :**

- Avant de commencer, compléter les trois informations entre crochets sur la diapositive (noms du binôme, professeur, établissement).

**Transition vers la suite :** « Commençons par le contexte. »

---

## Diapositive 2 — Contexte et problématique

**Ce qui apparaît sur la diapositive :** Quatre risques sous forme de cartes (double réservation, disponibilités floues, gestion manuelle, conflits) et la question posée dans un bloc noir.

**Ce que je dois expliquer oralement** (environ 0 min 36 s) :

> Imaginez un établissement avec plusieurs salles et du matériel : vidéoprojecteurs, PC portables, caméras. Tout le monde veut les utiliser, parfois au même moment. Sans outil, on risque la double réservation, on ne sait pas ce qui est libre, et la gestion à la main devient vite source de conflits. Notre problématique est donc la suivante : comment concevoir une application simple qui gère les ressources et les réservations, tout en empêchant les conflits de créneaux ?

**Points techniques importants :**

- Le sujet impose une application console, des fichiers texte et une interface sans graphisme.

**Transition vers la suite :** « Pour répondre à cette question, nous avons défini sept objectifs. »

---

## Diapositive 3 — Objectifs du projet

**Ce qui apparaît sur la diapositive :** Sept blocs numérotés, un par objectif, et un bloc noir qui rappelle le principe central.

**Ce que je dois expliquer oralement** (environ 0 min 35 s) :

> Nous avons fixé sept objectifs, qui correspondent aux options du menu : gérer les salles, les équipements et les utilisateurs ; créer, modifier et annuler des réservations ; consulter les plannings ; rechercher ce qui est disponible sur un créneau ; et conserver les données dans des fichiers pour qu'elles survivent à la fermeture du programme. Le fil conducteur, c'est le bloc noir : une ressource n'est jamais réservée deux fois sur le même créneau.

**Points techniques importants :**

- Les sept objectifs sont tous réalisés dans le code : on peut les montrer un par un dans le menu principal.

**Transition vers la suite :** « Voyons maintenant avec quels outils Java nous avons travaillé. »

---

## Diapositive 4 — Technologies utilisées

**Ce qui apparaît sur la diapositive :** Un tableau en trois colonnes : la technologie, son utilité, et l'endroit où elle sert dans le projet.

**Ce que je dois expliquer oralement** (environ 0 min 28 s) :

> Techniquement, nous n'utilisons que la bibliothèque standard de Java, sans aucune dépendance externe. La programmation orientée objet sert à modéliser le problème. ArrayList garde les données en mémoire. Scanner lit le clavier. LocalDate et LocalTime permettent de manipuler proprement les dates et les heures. Les fichiers texte conservent les données, et les exceptions signalent les erreurs sans faire planter le programme.

**Points techniques importants :**

- Le code est compatible Java 8 (vérifié en compilant avec l'option --release 8).
- La troisième colonne du tableau indique la classe où chaque technologie est utilisée : on peut ouvrir le fichier correspondant.

**Transition vers la suite :** « Regardons maintenant comment tout cela est organisé. »

---

## Diapositive 5 — Architecture du projet

**Ce qui apparaît sur la diapositive :** Un schéma avec cinq groupes : Application (Main), Utilitaires (Saisie), Gestion (GestionReservations et GestionFichiers), Modèle (cinq classes) et Exceptions (deux classes), reliés par des flèches. Une légende explique les trois types de flèches.

**Ce que je dois expliquer oralement** (environ 0 min 39 s) :

> Le projet compte onze classes, rangées en cinq groupes. Main affiche les menus : il lit les saisies grâce à la classe utilitaire Saisie, puis il appelle GestionReservations, qui contient toute la logique métier. GestionReservations manipule les objets du modèle, c'est-à-dire Ressource, Salle, Equipement, Utilisateur et Reservation, et passe par GestionFichiers pour charger et sauvegarder. Enfin, les deux exceptions personnalisées sont lancées par GestionReservations et attrapées dans les menus. Chaque classe a un rôle unique : Main n'a aucune règle métier, et GestionReservations n'affiche rien.

**Points techniques importants :**

- Packages : model, gestion, util, exceptions ; Main est à la racine de src.
- GestionReservations contient les quatre ArrayList (salles, equipements, utilisateurs, reservations).
- GestionFichiers ne connaît que les fichiers ; il crée les objets du modèle à partir des lignes lues.

**Transition vers la suite :** « Zoomons sur le modèle, et en particulier sur l'héritage. »

---

## Diapositive 6 — Programmation orientée objet et héritage

**Ce qui apparaît sur la diapositive :** Un diagramme de classes (Ressource abstraite, Salle et Equipement), quatre notions clés et un extrait de code réel.

**Ce que je dois expliquer oralement** (environ 0 min 41 s) :

> Une salle et un équipement ont des points communs : un identifiant et un nom. Nous les avons mis dans une classe abstraite, Ressource. On ne peut pas créer une ressource générique, seulement une salle ou un équipement. Salle et Equipement héritent de Ressource avec le mot extends, et appellent super(id, nom) dans leur constructeur. Les attributs sont privés : c'est l'encapsulation, on y accède par des getters et des setters. Et la méthode abstraite getCategorie oblige chaque sous-classe à dire si elle est une salle ou un équipement.

**Points techniques importants :**

- getCategorie() renvoie « SALLE » ou « EQUIPEMENT » : c'est la valeur écrite dans reservations.txt.
- L'extrait vient de Ressource.java et Salle.java ; les trois points remplacent des lignes omises.

**Transition vers la suite :** « Voyons maintenant ce qui se passe quand on réserve. »

---

## Diapositive 7 — Gestion des réservations

**Ce qui apparaît sur la diapositive :** Un processus en sept étapes sur une ligne (avec une branche « refus ») et la vraie sortie du programme après une réservation réussie.

**Ce que je dois expliquer oralement** (environ 0 min 45 s) :

> Voici ce qui se passe quand on réserve. L'utilisateur choisit la ressource, donne son identifiant, la date, puis l'heure de début et de fin : ces saisies sont contrôlées par Saisie, qui redemande tant que la valeur est invalide. Ensuite GestionReservations vérifie tout : la ressource et l'utilisateur existent, les heures sont cohérentes, la date n'est pas passée et la ressource est libre. Si une seule condition échoue, un message s'affiche et rien n'est enregistré. Sinon la réservation reçoit un identifiant et les fichiers sont sauvegardés immédiatement. À droite, la vraie sortie du programme.

**Points techniques importants :**

- Méthode GestionReservations.reserver : les contrôles sont faits avant la création de l'objet Reservation.
- Trois familles d'erreurs : élément introuvable, données invalides (heures, date passée), conflit de créneau.
- Le bloc de droite est un copier-coller d'une exécution réelle (réservation R6, salle S2).

**Transition vers la suite :** « Le contrôle de disponibilité repose sur une règle précise : la détection des conflits. »

---

## Diapositive 8 — Détection des conflits

**Ce qui apparaît sur la diapositive :** La règle en grand, puis trois cas sur une frise horaire de 08:00 à 12:00 : conflit, accepté, accepté.

**Ce que je dois expliquer oralement** (environ 1 min 03 s) :

> C'est le cœur du projet. Deux créneaux se chevauchent s'ils sont le même jour et si chacun commence avant la fin de l'autre : debut1 inférieur à fin2, et debut2 inférieur à fin1. Dans le code, c'est une seule ligne avec isBefore. Prenons une salle déjà réservée de 9 h à 11 h. Une demande de 10 h à 12 h : 9 est avant 12 et 10 est avant 11, donc conflit. Une demande de 11 h à 12 h : 11 n'est pas avant 11, donc elle est acceptée. De même, 8 h à 9 h est acceptée. La comparaison est volontairement stricte : une réservation qui se termine à 11 h libère la salle à 11 h, la suivante peut commencer à cette minute. Avec un « inférieur ou égal », on refuserait des réservations légitimes.

**Points techniques importants :**

- GestionReservations.creneauxSeChevauchent : date1.equals(date2) && debut1.isBefore(fin2) && debut2.isBefore(fin1).
- La comparaison est stricte (isBefore, jamais <=) : c'est ce qui autorise 9 h–11 h puis 11 h–12 h.
- La condition « même jour » est dans la même ligne ; « même ressource » est testé dans la boucle qui appelle cette méthode.

**Transition vers la suite :** « Pour un équipement, la règle est différente, car il existe en plusieurs exemplaires. »

---

## Diapositive 9 — Gestion des équipements

**Ce qui apparaît sur la diapositive :** Quatre réservations d'un même vidéoprojecteur (stock de 3) : trois acceptées, la quatrième refusée, puis la comparaison entre une salle et un équipement.

**Ce que je dois expliquer oralement** (environ 0 min 43 s) :

> Pour un équipement, la logique est différente. Prenons trois vidéoprojecteurs. Chaque réservation utilise une unité : les trois premières sont acceptées en même temps, la quatrième est refusée car il ne reste plus d'unité. Une salle, au contraire, ne peut avoir qu'une réservation à la fois. Un détail important : nous ne comptons pas simplement les réservations qui touchent le créneau, mais le nombre d'unités utilisées au même instant. Une réservation de 8 h à 10 h et une autre de 10 h à 12 h n'utilisent jamais deux unités ensemble.

**Points techniques importants :**

- Méthode unitesUtilisees : on teste le début du créneau et le début de chaque réservation voisine, et on compte les réservations en cours.
- Si le nombre d'unités utilisées est supérieur ou égal au stock, on lance ConflitReservationException (« quantité disponible insuffisante »).
- Diminuer la quantité d'un équipement est refusé si des réservations simultanées dépasseraient le nouveau stock.

**Transition vers la suite :** « Ces réservations doivent survivre à la fermeture du programme : voyons le stockage. »

---

## Diapositive 10 — Stockage des données

**Ce qui apparaît sur la diapositive :** Le cycle Fichiers texte → Chargement → ArrayList → Application → Sauvegarde → Fichiers texte, et les quatre fichiers avec leur format et une vraie ligne d'exemple.

**Ce que je dois expliquer oralement** (environ 0 min 37 s) :

> Les données sont dans quatre fichiers texte, dans le dossier data : salles, équipements, utilisateurs et réservations. Une ligne par enregistrement, champs séparés par un point-virgule, comme cette ligne de réservation. Au démarrage, GestionFichiers lit les fichiers et remplit les ArrayList. Pendant l'utilisation, tout se passe en mémoire, puis les fichiers sont réécrits après chaque modification et à la fermeture. Ainsi les données persistent. Nous avons ajouté un petit fichier technique, compteurs point txt, pour ne jamais réutiliser un identifiant supprimé.

**Points techniques importants :**

- Encodage UTF-8 (InputStreamReader et OutputStreamWriter), pour garder les accents.
- Une ligne incorrecte dans un fichier est ignorée et signalée au démarrage : le programme ne plante pas.
- Si une écriture échoue, le programme le dit clairement ; il n'affiche « Données sauvegardées » que si l'écriture a réussi.
- compteurs.txt est facultatif : s'il manque, il est recréé à partir des identifiants existants.

**Transition vers la suite :** « Que fait concrètement l'application, et qu'avons-nous vérifié ? »

---

## Diapositive 11 — Fonctionnalités et tests

**Ce qui apparaît sur la diapositive :** Trois chiffres sur les tests, la liste des sept fonctionnalités et la liste des six familles de tests réussis.

**Ce que je dois expliquer oralement** (environ 0 min 43 s) :

> Côté fonctionnalités : la gestion complète des salles, des équipements et des utilisateurs, la réservation, l'annulation et la modification, les plannings et la recherche de disponibilités. Pour vérifier tout cela, nous avons écrit des tests automatisés : cent soixante-dix vérifications sur la logique métier, et quatre-vingts en lançant le vrai programme avec des saisies scriptées. Ils couvrent les conflits, les quantités, les dates et heures invalides, les suppressions interdites, les saisies incorrectes et la persistance. Nous avons aussi arrêté brutalement le programme pour prouver que chaque opération est sauvegardée tout de suite. Tous ces tests ont réussi.

**Points techniques importants :**

- Les 170 et 80 vérifications ont toutes réussi lors de la dernière exécution.
- Le menu affiché a été comparé au texte du PDF : il est identique.
- Attention : les scripts de test ne sont pas dans le dossier du projet. Si on te les demande, il faut les ajouter ou les refaire.

**Transition vers la suite :** « Pour terminer, un bilan et quelques pistes d'amélioration. »

---

## Diapositive 12 — Conclusion et perspectives

**Ce qui apparaît sur la diapositive :** La conclusion, les six compétences mobilisées, quatre perspectives présentées comme des améliorations futures, et « Merci pour votre attention – Des questions ? ».

**Ce que je dois expliquer oralement** (environ 0 min 32 s) :

> Pour conclure, ce projet nous a permis de mettre en pratique les notions fondamentales de Java : programmation orientée objet, héritage et encapsulation, collections, fichiers, exceptions, et un peu d'algorithmique avec la détection des conflits. Pour la suite, nous pourrions ajouter une interface graphique, une base de données, une authentification ou des rapports de réservation : ce sont des perspectives, pas des fonctionnalités déjà réalisées. Merci pour votre attention, nous sommes prêts à répondre à vos questions.

**Points techniques importants :**

- Insister sur la différence entre ce qui est réalisé (partie gauche) et ce qui est une piste (cartes en pointillés).
- Questions probables : voir le fichier GUIDE_PRESENTATION.md du projet.

**Fin :** Merci. Des questions ?

---

## Si on vous pose une question

- Les réponses préparées (classes, ArrayList, exceptions, fichiers, conflits…) sont dans `GUIDE_PRESENTATION.md`.
- Si on demande de montrer le code de la règle de conflit : `GestionReservations.java`, méthode `creneauxSeChevauchent`.
- Si on demande de montrer les tests : les scripts de test ne sont pas dans le dossier du projet ; il faut les ajouter ou refaire une démonstration en direct.
