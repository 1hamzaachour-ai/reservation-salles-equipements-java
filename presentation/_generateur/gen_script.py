# -*- coding: utf-8 -*-
"""Génère Script_Presentation.md à partir de content.py (même texte que les notes du PowerPoint)."""
import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from content import SCRIPT, WPM

def mots(t):
    return len(t.replace(" ", " ").split())

def mmss(sec):
    return "%d min %02d s" % (sec // 60, sec % 60)

dur = []
for d in SCRIPT:
    w = mots(d["oral"]) + (mots(d["transition"]) if d["n"] < 12 else 0)
    dur.append((d, w, round(w / WPM * 60)))
total_w = sum(w for _, w, _ in dur)
total_s = sum(s for _, _, s in dur)

L = []
L.append("# Script de présentation orale")
L.append("")
L.append("**Projet :** Application de réservation de salles et d'équipements (Java, console) — présentation PowerPoint de 12 diapositives.")
L.append("")
L.append("**Durée prévue :** environ **%s** (%d mots à %d mots par minute), soit dans la fourchette de 7 à 10 minutes. "
         "Avec les pauses entre les diapositives, comptez environ 8 à 9 minutes." % (mmss(total_s), total_w, WPM))
L.append("")
L.append("> Ce texte est aussi dans les **notes** de chaque diapositive du fichier `Presentation_Projet_Java.pptx` "
         "(mode Présentateur de PowerPoint). Il est écrit à la première personne du pluriel, pour un binôme.")
L.append("")
L.append("## Avant de commencer")
L.append("")
L.append("- Compléter la diapositive 1 : noms du binôme, nom du professeur, établissement (les trois informations entre crochets).")
L.append("- Ouvrir le projet (NetBeans ou autre) pour pouvoir lancer une démonstration si on la demande ; voir `GUIDE_PRESENTATION.md` à la racine du projet.")
L.append("- Ne pas lire les diapositives : elles donnent les mots-clés, le texte ci-dessous donne les explications.")
L.append("")
L.append("## Répartition du temps")
L.append("")
L.append("| Diapositive | Titre | Durée approximative |")
L.append("|---|---|---|")
for d, w, s in dur:
    L.append("| %d | %s | %s |" % (d["n"], d["titre"], mmss(s)))
L.append("| | **Total** | **%s** |" % mmss(total_s))
L.append("")
for d, w, s in dur:
    L.append("---")
    L.append("")
    L.append("## Diapositive %d — %s" % (d["n"], d["titre"]))
    L.append("")
    L.append("**Ce qui apparaît sur la diapositive :** " + d["apparait"])
    L.append("")
    L.append("**Ce que je dois expliquer oralement** (environ %s) :" % mmss(s))
    L.append("")
    L.append("> " + d["oral"])
    L.append("")
    L.append("**Points techniques importants :**")
    L.append("")
    for t in d["tech"]:
        L.append("- " + t)
    L.append("")
    L.append("**Transition vers la suite :** « " + d["transition"].strip("()") + " »" if d["n"] < 12 else "**Fin :** Merci. Des questions ?")
    L.append("")
L.append("---")
L.append("")
L.append("## Si on vous pose une question")
L.append("")
L.append("- Les réponses préparées (classes, ArrayList, exceptions, fichiers, conflits…) sont dans `GUIDE_PRESENTATION.md`.")
L.append("- Si on demande de montrer le code de la règle de conflit : `GestionReservations.java`, méthode `creneauxSeChevauchent`.")
L.append("- Si on demande de montrer les tests : les scripts de test ne sont pas dans le dossier du projet ; il faut les ajouter ou refaire une démonstration en direct.")
L.append("")
out = sys.argv[1] if len(sys.argv) > 1 else "Script_Presentation.md"
open(out, "w", encoding="utf-8").write("\n".join(L))
print(out, total_w, "mots", mmss(total_s))
for d, w, s in dur:
    print("  diapo %2d : %3d mots  %s" % (d["n"], w, mmss(s)))
