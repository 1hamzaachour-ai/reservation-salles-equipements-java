# -*- coding: utf-8 -*-
"""Contenu oral : sert aux notes du PowerPoint ET au fichier Script_Presentation.md (même texte)."""

# minutes approximatives par diapositive : calculées plus tard à 145 mots/minute
SCRIPT = [
    dict(
        n=1,
        titre="Page de garde",
        apparait="Le titre « Application de réservation de salles et d'équipements », le sous-titre « Projet universitaire – "
                 "Programmation Java », les informations du binôme (à compléter) et un schéma de réservations sur une frise horaire.",
        oral="Bonjour. Nous allons vous présenter notre projet de fin de semestre : une application Java en mode console qui "
             "permet de réserver des salles et des équipements, sans jamais créer de conflit. Nous commencerons par le contexte, "
             "puis nous expliquerons comment l'application est construite, comment elle évite les doubles réservations, "
             "et ce que nous avons testé.",
        tech=["Avant de commencer, compléter les trois informations entre crochets sur la diapositive (noms du binôme, professeur, établissement)."],
        transition="Commençons par le contexte.",
    ),
    dict(
        n=2,
        titre="Contexte et problématique",
        apparait="Quatre risques sous forme de cartes (double réservation, disponibilités floues, gestion manuelle, conflits) "
                 "et la question posée dans un bloc noir.",
        oral="Imaginez un établissement avec plusieurs salles et du matériel : vidéoprojecteurs, PC portables, caméras. Tout le monde "
             "veut les utiliser, parfois au même moment. Sans outil, on risque la double réservation, on ne sait pas ce qui est libre, "
             "et la gestion à la main devient vite source de conflits. Notre problématique est donc la suivante : comment concevoir une "
             "application simple qui gère les ressources et les réservations, tout en empêchant les conflits de créneaux ?",
        tech=["Le sujet impose une application console, des fichiers texte et une interface sans graphisme."],
        transition="Pour répondre à cette question, nous avons défini sept objectifs.",
    ),
    dict(
        n=3,
        titre="Objectifs du projet",
        apparait="Sept blocs numérotés, un par objectif, et un bloc noir qui rappelle le principe central.",
        oral="Nous avons fixé sept objectifs, qui correspondent aux options du menu : gérer les salles, les équipements et les "
             "utilisateurs ; créer, modifier et annuler des réservations ; consulter les plannings ; rechercher ce qui est disponible "
             "sur un créneau ; et conserver les données dans des fichiers pour qu'elles survivent à la fermeture du programme. "
             "Le fil conducteur, c'est le bloc noir : une ressource n'est jamais réservée deux fois sur le même créneau.",
        tech=["Les sept objectifs sont tous réalisés dans le code : on peut les montrer un par un dans le menu principal."],
        transition="Voyons maintenant avec quels outils Java nous avons travaillé.",
    ),
    dict(
        n=4,
        titre="Technologies utilisées",
        apparait="Un tableau en trois colonnes : la technologie, son utilité, et l'endroit où elle sert dans le projet.",
        oral="Techniquement, nous n'utilisons que la bibliothèque standard de Java, sans aucune dépendance externe. La programmation "
             "orientée objet sert à modéliser le problème. ArrayList garde les données en mémoire. Scanner lit le clavier. "
             "LocalDate et LocalTime permettent de manipuler proprement les dates et les heures. Les fichiers texte conservent les "
             "données, et les exceptions signalent les erreurs sans faire planter le programme.",
        tech=["Le code est compatible Java 8 (vérifié en compilant avec l'option --release 8).",
              "La troisième colonne du tableau indique la classe où chaque technologie est utilisée : on peut ouvrir le fichier correspondant."],
        transition="Regardons maintenant comment tout cela est organisé.",
    ),
    dict(
        n=5,
        titre="Architecture du projet",
        apparait="Un schéma avec cinq groupes : Application (Main), Utilitaires (Saisie), Gestion (GestionReservations et GestionFichiers), "
                 "Modèle (cinq classes) et Exceptions (deux classes), reliés par des flèches. Une légende explique les trois types de flèches.",
        oral="Le projet compte onze classes, rangées en cinq groupes. Main affiche les menus : il lit les saisies grâce à la classe "
             "utilitaire Saisie, puis il appelle GestionReservations, qui contient toute la logique métier. GestionReservations "
             "manipule les objets du modèle, c'est-à-dire Ressource, Salle, Equipement, Utilisateur et Reservation, et passe par "
             "GestionFichiers pour charger et sauvegarder. Enfin, les deux exceptions personnalisées sont lancées par GestionReservations "
             "et attrapées dans les menus. Chaque classe a un rôle unique : Main n'a aucune règle métier, et GestionReservations n'affiche rien.",
        tech=["Packages : model, gestion, util, exceptions ; Main est à la racine de src.",
              "GestionReservations contient les quatre ArrayList (salles, equipements, utilisateurs, reservations).",
              "GestionFichiers ne connaît que les fichiers ; il crée les objets du modèle à partir des lignes lues."],
        transition="Zoomons sur le modèle, et en particulier sur l'héritage.",
    ),
    dict(
        n=6,
        titre="Programmation orientée objet et héritage",
        apparait="Un diagramme de classes (Ressource abstraite, Salle et Equipement), quatre notions clés et un extrait de code réel.",
        oral="Une salle et un équipement ont des points communs : un identifiant et un nom. Nous les avons mis dans une classe abstraite, "
             "Ressource. On ne peut pas créer une ressource générique, seulement une salle ou un équipement. Salle et Equipement "
             "héritent de Ressource avec le mot extends, et appellent super(id, nom) dans leur constructeur. Les attributs sont privés : "
             "c'est l'encapsulation, on y accède par des getters et des setters. Et la méthode abstraite getCategorie oblige chaque "
             "sous-classe à dire si elle est une salle ou un équipement.",
        tech=["getCategorie() renvoie « SALLE » ou « EQUIPEMENT » : c'est la valeur écrite dans reservations.txt.",
              "L'extrait vient de Ressource.java et Salle.java ; les trois points remplacent des lignes omises."],
        transition="Voyons maintenant ce qui se passe quand on réserve.",
    ),
    dict(
        n=7,
        titre="Gestion des réservations",
        apparait="Un processus en sept étapes sur une ligne (avec une branche « refus ») et la vraie sortie du programme après une réservation réussie.",
        oral="Voici ce qui se passe quand on réserve. L'utilisateur choisit la ressource, donne son identifiant, la date, puis l'heure de "
             "début et de fin : ces saisies sont contrôlées par Saisie, qui redemande tant que la valeur est invalide. Ensuite "
             "GestionReservations vérifie tout : la ressource et l'utilisateur existent, les heures sont cohérentes, la date n'est "
             "pas passée et la ressource est libre. Si une seule condition échoue, un message s'affiche et rien n'est enregistré. "
             "Sinon la réservation reçoit un identifiant et les fichiers sont sauvegardés immédiatement. À droite, la vraie sortie du programme.",
        tech=["Méthode GestionReservations.reserver : les contrôles sont faits avant la création de l'objet Reservation.",
              "Trois familles d'erreurs : élément introuvable, données invalides (heures, date passée), conflit de créneau.",
              "Le bloc de droite est un copier-coller d'une exécution réelle (réservation R6, salle S2)."],
        transition="Le contrôle de disponibilité repose sur une règle précise : la détection des conflits.",
    ),
    dict(
        n=8,
        titre="Détection des conflits",
        apparait="La règle en grand, puis trois cas sur une frise horaire de 08:00 à 12:00 : conflit, accepté, accepté.",
        oral="C'est le cœur du projet. Deux créneaux se chevauchent s'ils sont le même jour et si chacun commence avant la fin de l'autre : "
             "debut1 inférieur à fin2, et debut2 inférieur à fin1. Dans le code, c'est une seule ligne avec isBefore. Prenons une salle "
             "déjà réservée de 9 h à 11 h. Une demande de 10 h à 12 h : 9 est avant 12 et 10 est avant 11, donc conflit. Une demande "
             "de 11 h à 12 h : 11 n'est pas avant 11, donc elle est acceptée. De même, 8 h à 9 h est acceptée. La comparaison est "
             "volontairement stricte : une réservation qui se termine à 11 h libère la salle à 11 h, la suivante peut commencer à cette "
             "minute. Avec un « inférieur ou égal », on refuserait des réservations légitimes.",
        tech=["GestionReservations.creneauxSeChevauchent : date1.equals(date2) && debut1.isBefore(fin2) && debut2.isBefore(fin1).",
              "La comparaison est stricte (isBefore, jamais <=) : c'est ce qui autorise 9 h–11 h puis 11 h–12 h.",
              "La condition « même jour » est dans la même ligne ; « même ressource » est testé dans la boucle qui appelle cette méthode."],
        transition="Pour un équipement, la règle est différente, car il existe en plusieurs exemplaires.",
    ),
    dict(
        n=9,
        titre="Gestion des équipements",
        apparait="Quatre réservations d'un même vidéoprojecteur (stock de 3) : trois acceptées, la quatrième refusée, "
                 "puis la comparaison entre une salle et un équipement.",
        oral="Pour un équipement, la logique est différente. Prenons trois vidéoprojecteurs. Chaque réservation utilise une unité : les "
             "trois premières sont acceptées en même temps, la quatrième est refusée car il ne reste plus d'unité. Une salle, au contraire, "
             "ne peut avoir qu'une réservation à la fois. Un détail important : nous ne comptons pas simplement les réservations qui "
             "touchent le créneau, mais le nombre d'unités utilisées au même instant. Une réservation de 8 h à 10 h et une autre de 10 h à "
             "12 h n'utilisent jamais deux unités ensemble.",
        tech=["Méthode unitesUtilisees : on teste le début du créneau et le début de chaque réservation voisine, et on compte les réservations en cours.",
              "Si le nombre d'unités utilisées est supérieur ou égal au stock, on lance ConflitReservationException (« quantité disponible insuffisante »).",
              "Diminuer la quantité d'un équipement est refusé si des réservations simultanées dépasseraient le nouveau stock."],
        transition="Ces réservations doivent survivre à la fermeture du programme : voyons le stockage.",
    ),
    dict(
        n=10,
        titre="Stockage des données",
        apparait="Le cycle Fichiers texte → Chargement → ArrayList → Application → Sauvegarde → Fichiers texte, "
                 "et les quatre fichiers avec leur format et une vraie ligne d'exemple.",
        oral="Les données sont dans quatre fichiers texte, dans le dossier data : salles, équipements, utilisateurs et réservations. "
             "Une ligne par enregistrement, champs séparés par un point-virgule, comme cette ligne de réservation. Au démarrage, "
             "GestionFichiers lit les fichiers et remplit les ArrayList. Pendant l'utilisation, tout se passe en mémoire, puis les "
             "fichiers sont réécrits après chaque modification et à la fermeture. Ainsi les données persistent. Nous avons ajouté un "
             "petit fichier technique, compteurs point txt, pour ne jamais réutiliser un identifiant supprimé.",
        tech=["Encodage UTF-8 (InputStreamReader et OutputStreamWriter), pour garder les accents.",
              "Une ligne incorrecte dans un fichier est ignorée et signalée au démarrage : le programme ne plante pas.",
              "Si une écriture échoue, le programme le dit clairement ; il n'affiche « Données sauvegardées » que si l'écriture a réussi.",
              "compteurs.txt est facultatif : s'il manque, il est recréé à partir des identifiants existants."],
        transition="Que fait concrètement l'application, et qu'avons-nous vérifié ?",
    ),
    dict(
        n=11,
        titre="Fonctionnalités et tests",
        apparait="Trois chiffres sur les tests, la liste des sept fonctionnalités et la liste des six familles de tests réussis.",
        oral="Côté fonctionnalités : la gestion complète des salles, des équipements et des utilisateurs, la réservation, l'annulation "
             "et la modification, les plannings et la recherche de disponibilités. Pour vérifier tout cela, nous avons écrit des tests "
             "automatisés : cent soixante-dix vérifications sur la logique métier, et quatre-vingts en lançant le vrai programme avec "
             "des saisies scriptées. Ils couvrent les conflits, les quantités, les dates et heures invalides, les suppressions "
             "interdites, les saisies incorrectes et la persistance. Nous avons aussi arrêté brutalement le programme pour prouver que "
             "chaque opération est sauvegardée tout de suite. Tous ces tests ont réussi.",
        tech=["Les 170 et 80 vérifications ont toutes réussi lors de la dernière exécution.",
              "Le menu affiché a été comparé au texte du PDF : il est identique.",
              "Attention : les scripts de test ne sont pas dans le dossier du projet. Si on te les demande, il faut les ajouter ou les refaire."],
        transition="Pour terminer, un bilan et quelques pistes d'amélioration.",
    ),
    dict(
        n=12,
        titre="Conclusion et perspectives",
        apparait="La conclusion, les six compétences mobilisées, quatre perspectives présentées comme des améliorations futures, "
                 "et « Merci pour votre attention – Des questions ? ».",
        oral="Pour conclure, ce projet nous a permis de mettre en pratique les notions fondamentales de Java : programmation orientée objet, "
             "héritage et encapsulation, collections, fichiers, exceptions, et un peu d'algorithmique avec la détection des conflits. "
             "Pour la suite, nous pourrions ajouter une interface graphique, une base de données, une authentification ou des rapports de "
             "réservation : ce sont des perspectives, pas des fonctionnalités déjà réalisées. Merci pour votre attention, nous sommes "
             "prêts à répondre à vos questions.",
        tech=["Insister sur la différence entre ce qui est réalisé (partie gauche) et ce qui est une piste (cartes en pointillés).",
              "Questions probables : voir le fichier GUIDE_PRESENTATION.md du projet."],
        transition="(Fin) Merci. Des questions ?",
    ),
]

WPM = 145  # débit de parole moyen utilisé pour estimer la durée
