# -*- coding: utf-8 -*-
"""Construit Presentation_Projet_Java.pptx (12 diapositives, formes natives modifiables)."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from lib import *          # noqa: F401,F403
from content import SCRIPT

TOTAL = 12


def note_text(i):
    d = SCRIPT[i]
    return d["oral"] + "\n\nTransition : " + d["transition"]


FOOT = "Réservation de salles et d'équipements · Projet Java"


# ======================================================================
#  Éléments communs
# ======================================================================
def furniture(prs, n, kicker, title, subtitle, dark=False):
    layout = [l for l in prs.slide_layouts if l.name == "Title Only"][0]
    s = prs.slides.add_slide(layout)
    set_bg(s, BLACK if dark else WHITE)
    fg = WHITE if dark else INK
    sub = G400 if dark else G500
    acc = ACCENT_LINE if dark else ACCENT
    rect(s, LM, 0.5, 0.42, 0.05, fill=acc, name="Barre d'accent")
    textbox(s, LM, 0.64, 8, 0.26, [P(R(kicker, MONO, 11, acc, spc=120))], name="Rubrique")
    t = s.shapes.title
    t.left, t.top, t.width, t.height = Inches(LM), Inches(0.95), Inches(CW), Inches(0.6)
    fill_tf(t.text_frame, [P(R(title, HEAD, 30, fg, bold=True))], anchor="t")
    t.name = "Titre"
    textbox(s, LM, 1.6, CW, 0.36, [P(R(subtitle, BODY, 17, sub))], name="Sous-titre")
    textbox(s, LM, 7.04, 7, 0.24, [P(R(FOOT, BODY, 10, G500 if dark else G400))], name="Pied de page")
    textbox(s, RM - 1.5, 7.04, 1.5, 0.24,
            [P(R("%02d / %d" % (n, TOTAL), MONO, 10, G500 if dark else G400), align="r")], name="Numéro")
    return s


def class_box(s, x, y, w, h, name, sub=None, fill=WHITE, fg=INK, subc=G500, line_c=G800, italic=False, lw=1.0, size=13):
    runs = [P(R(name, MONO, size, fg, bold=True, italic=italic), align="c")]
    if sub:
        runs.append(P(R(sub, BODY, 11, subc), align="c", before=2))
    return rect(s, x, y, w, h, fill=fill, line=line_c, lw=lw, paras=runs, anchor="m",
                margins=(0.05, 0.03, 0.05, 0.03), name="Classe " + name)


def tag(s, x, y, text, color=G500, w=3.0):
    return textbox(s, x, y, w, 0.22, [P(R(text, MONO, 10, color, spc=100))])


# ======================================================================
#  1. Couverture
# ======================================================================
def slide1(prs):
    layout = [l for l in prs.slide_layouts if l.name == "Title Only"][0]
    s = prs.slides.add_slide(layout)
    set_bg(s, BLACK)
    rect(s, 0.9, 1.15, 0.5, 0.055, fill=ACCENT_LINE, name="Barre d'accent")
    textbox(s, 0.9, 1.3, 7, 0.26, [P(R("PROJET UNIVERSITAIRE · PROGRAMMATION JAVA", MONO, 11, ACCENT_LINE, spc=120))], name="Rubrique")
    t = s.shapes.title
    t.left, t.top, t.width, t.height = Inches(0.9), Inches(1.75), Inches(7.2), Inches(2.5)
    fill_tf(t.text_frame, [P(R(w, HEAD, 35, WHITE, bold=True), line=1.0)
                           for w in ("APPLICATION", "DE RÉSERVATION", "DE SALLES ET", "D'ÉQUIPEMENTS")], anchor="t")
    t.name = "Titre"
    textbox(s, 0.9, 4.4, 7, 0.34, [P(R("Projet universitaire – Programmation Java", MONO, 14, G300))], name="Sous-titre")

    infos = [("Réalisé par", "[Nom et prénom des membres du binôme]"),
             ("Encadré par", "[Nom du professeur]"),
             ("Établissement", "[Nom de l'établissement]"),
             ("Année universitaire", "2026-2027")]
    y = 5.15
    for lab, val in infos:
        textbox(s, 0.9, y + 0.03, 2.1, 0.26, [P(R(lab.upper(), MONO, 10, G500, spc=80))])
        textbox(s, 3.0, y, 5.2, 0.3, [P(R(val, BODY, 15, WHITE))], name="Info " + lab)
        y += 0.4

    # --- frise de réservations (formes natives, schéma purement illustratif) ---
    gx, gy, gw = 9.0, 2.0, 3.55        # origine de la grille
    hours = list(range(8, 15))          # 08 .. 14
    hw = gw / (len(hours) - 1)
    rows = [("A101", 0), ("A102", 1), ("B201", 2), ("VIDÉOPROJ.", 3)]
    rh = 0.95
    for i, hh in enumerate(hours):
        x = gx + i * hw
        line(s, x, gy - 0.05, x, gy + 4 * rh - 0.1, color=G800, lw=0.75)
        textbox(s, x - 0.3, gy - 0.4, 0.6, 0.22, [P(R("%02d" % hh, MONO, 9, G500), align="c")])
    for lab, i in rows:
        textbox(s, 8.0, gy + i * rh + 0.2, 0.95, 0.22, [P(R(lab, MONO, 8.5, G400))])

    def bar(row, h1, h2, fill, sub=0, hh=0.42, line_c=None):
        x1 = gx + (h1 - 8) * hw
        x2 = gx + (h2 - 8) * hw
        rect(s, x1, gy + row * rh + 0.08 + sub, x2 - x1, hh, fill=fill, line=line_c, lw=1.0)

    bar(0, 9, 11, WHITE)
    bar(0, 11, 12, G500)
    bar(1, 10, 12, ACCENT, line_c=ACCENT_LINE)
    bar(2, 8, 10, G700)
    bar(2, 12, 14, G500)
    for k in range(3):                    # trois unités d'un même équipement
        bar(3, 9, 11, G300, sub=k * 0.15, hh=0.1)
    textbox(s, 8.0, gy + 4 * rh + 0.0, 4.6, 0.3,
            [P(R("Réservations d'une journée : salles et équipements", BODY, 11, G500))])
    notes(s, note_text(0))
    return s


# ======================================================================
#  2. Contexte et problématique
# ======================================================================
def slide2(prs):
    s = furniture(prs, 2, "02 — CONTEXTE", "Contexte et problématique",
                  "Un établissement, des salles et du matériel partagés par plusieurs utilisateurs.")
    probs = [("01", "Double réservation", "Deux personnes obtiennent la même salle au même moment."),
             ("02", "Disponibilités floues", "Difficile de savoir ce qui est libre sur un créneau."),
             ("03", "Gestion manuelle", "Listes et tableaux à tenir à jour à la main."),
             ("04", "Conflits entre utilisateurs", "Salles et matériel réclamés en même temps.")]
    cw, ch, gap = 3.5, 2.05, 0.25
    for i, (num, h, t) in enumerate(probs):
        cx = LM + (i % 2) * (cw + gap)
        cy = 2.35 + (i // 2) * (ch + gap)
        rect(s, cx, cy, cw, ch, fill=G50, line=G200, lw=0.75, name="Carte " + h)
        textbox(s, cx + 0.28, cy + 0.25, 1, 0.25, [P(R(num, MONO, 12, ACCENT, bold=True))])
        textbox(s, cx + 0.28, cy + 0.62, cw - 0.4, 0.35, [P(R(h, HEAD, 16, INK, bold=True))])
        textbox(s, cx + 0.28, cy + 1.1, cw - 0.45, 0.85, [P(R(t, BODY, 15, G500), line=1.05)])
    px, py, pw, ph = 8.4, 2.35, 4.18, 4.35
    rect(s, px, py, pw, ph, fill=BLACK, name="Bloc problématique")
    rect(s, px + 0.4, py + 0.42, 0.3, 0.04, fill=ACCENT_LINE)
    textbox(s, px + 0.4, py + 0.58, 3.3, 0.25, [P(R("PROBLÉMATIQUE", MONO, 11, ACCENT_LINE, spc=120))])
    textbox(s, px + 0.4, py + 1.05, pw - 0.8, 3.1,
            [P(R("Comment développer une application simple permettant de gérer les ressources et les réservations "
                 "tout en évitant les conflits de créneaux ?", HEAD, 19, WHITE, bold=True), line=1.12)],
            name="Question")
    notes(s, note_text(1))


# ======================================================================
#  3. Objectifs
# ======================================================================
def slide3(prs):
    s = furniture(prs, 3, "03 — OBJECTIFS", "Objectifs du projet",
                  "Sept objectifs, un seul principe : ne jamais réserver deux fois la même ressource.")
    items = [("01", "Gérer les salles", "Ajouter, modifier, supprimer et afficher."),
             ("02", "Gérer les équipements", "Type et quantité disponible."),
             ("03", "Gérer les utilisateurs", "Nom, prénom et rôle."),
             ("04", "Réserver et annuler", "Créer, modifier ou annuler une réservation."),
             ("05", "Consulter les plannings", "Par salle, équipement, date ou utilisateur."),
             ("06", "Trouver les disponibilités", "Salles libres et unités restantes sur un créneau."),
             ("07", "Conserver les données", "Sauvegarde permanente dans des fichiers texte.")]
    gap = 0.25
    cw = (CW - 3 * gap) / 4
    ch = 2.05
    for i, (num, h, t) in enumerate(items):
        cx = LM + (i % 4) * (cw + gap)
        cy = 2.35 + (i // 4) * (ch + gap)
        rect(s, cx, cy, cw, ch, fill=G50, line=G200, lw=0.75, name="Objectif " + num)
        oval(s, cx + 0.25, cy + 0.25, 0.42, 0.42, fill=None, line=ACCENT, lw=1.25,
             paras=[P(R(num, MONO, 11, ACCENT, bold=True), align="c")], margins=(0, 0, 0, 0))
        textbox(s, cx + 0.25, cy + 0.82, cw - 0.45, 0.6, [P(R(h, HEAD, 15, INK, bold=True), line=1.0)])
        textbox(s, cx + 0.25, cy + 1.5, cw - 0.45, 0.5, [P(R(t, BODY, 14, G500), line=1.0)])
    cx = LM + 3 * (cw + gap)
    cy = 2.35 + (ch + gap)
    rect(s, cx, cy, cw, ch, fill=BLACK, name="Principe")
    textbox(s, cx + 0.25, cy + 0.3, cw - 0.5, 0.25, [P(R("PRINCIPE", MONO, 10.5, ACCENT_LINE, spc=120))])
    textbox(s, cx + 0.25, cy + 0.7, cw - 0.5, 1.2,
            [P(R("Une ressource n'est jamais réservée deux fois sur un même créneau.", HEAD, 14.5, WHITE, bold=True), line=1.1)])
    notes(s, note_text(2))


# ======================================================================
#  4. Technologies
# ======================================================================
def slide4(prs):
    s = furniture(prs, 4, "04 — TECHNOLOGIES", "Technologies utilisées",
                  "Uniquement la bibliothèque standard de Java : aucune dépendance externe.")
    c1, c2, c3 = LM, 3.95, 8.95
    y0 = 2.3
    for x, lab in ((c1, "TECHNOLOGIE"), (c2, "À QUOI ELLE SERT"), (c3, "OÙ DANS LE PROJET")):
        textbox(s, x, y0, 3.5, 0.22, [P(R(lab, MONO, 10, G500, spc=100))])
    rows = [("Java", "Langage du projet, compatible Java 8 et plus", "bibliothèque standard seulement"),
            ("Programmation orientée objet", "Modéliser salles, équipements, utilisateurs et réservations", "package model"),
            ("ArrayList", "Garder les listes d'objets en mémoire", "GestionReservations"),
            ("Scanner", "Lire les saisies au clavier", "Saisie"),
            ("LocalDate", "Représenter et comparer les dates", "Reservation, GestionReservations"),
            ("LocalTime", "Représenter et comparer les heures", "Reservation, GestionReservations"),
            ("Fichiers texte", "Conserver les données entre deux exécutions", "GestionFichiers (UTF-8)"),
            ("Exceptions", "Signaler les erreurs sans faire planter le programme", "package exceptions, Main")]
    rh = 0.51
    y = y0 + 0.33
    line(s, LM, y, RM, y, color=INK, lw=1.25)
    for name_, role, where in rows:
        textbox(s, c1, y, 3.0, rh, [P(R(name_, HEAD, 13, INK, bold=True), line=0.95)], anchor="m")
        textbox(s, c2, y, 4.8, rh, [P(R(role, BODY, 14, INK))], anchor="m")
        textbox(s, c3, y, 3.63, rh, [P(R(where, MONO, 11, ACCENT))], anchor="m")
        y += rh
        line(s, LM, y, RM, y, color=G200, lw=0.75)
    notes(s, note_text(3))


# ======================================================================
#  5. Architecture
# ======================================================================
def slide5(prs):
    s = furniture(prs, 5, "05 — ARCHITECTURE", "Architecture du projet",
                  "Onze classes réparties en cinq groupes, chacune avec un rôle précis.")
    ax, bx, cx = LM, 4.85, 9.45
    # --- conteneurs (groupes) ---
    def group(x, y, w, h, label):
        rect(s, x, y, w, h, fill=G100, name="Groupe " + label)
        tag(s, x + 0.18, y + 0.12, label, G500)

    group(ax, 2.2, 3.2, 1.3, "APPLICATION")
    group(ax, 3.9, 3.2, 1.3, "UTILITAIRES")
    group(bx, 2.2, 3.4, 2.8, "GESTION")
    group(bx, 5.35, 3.4, 1.5, "EXCEPTIONS")
    group(cx, 2.2, 3.13, 4.65, "MODÈLE")

    main = class_box(s, ax + 0.2, 2.6, 2.8, 0.75, "Main", "menus et sous-menus", fill=BLACK, fg=WHITE, subc=G300, line_c=BLACK)
    sais = class_box(s, ax + 0.2, 4.3, 2.8, 0.75, "Saisie", "saisies sécurisées")
    gr = class_box(s, bx + 0.2, 2.6, 3.0, 0.75, "GestionReservations", "logique métier · 4 ArrayList",
                   fill=ACCENT, fg=WHITE, subc="C9D6EE", line_c=ACCENT)
    gf = class_box(s, bx + 0.2, 3.95, 3.0, 0.8, "GestionFichiers", "lit et écrit data/*.txt")
    class_box(s, bx + 0.2, 5.8, 3.0, 0.42, "ConflitReservationException", None, size=9.5)
    class_box(s, bx + 0.2, 6.3, 3.0, 0.42, "RessourceIntrouvableException", None, size=9.5)

    # --- modèle ---
    mx = cx + 0.2
    mw = 2.73
    class_box(s, mx, 2.65, mw, 0.72, "Ressource", "classe abstraite", italic=True)
    sal = class_box(s, mx, 3.9, 1.28, 0.55, "Salle")
    equ = class_box(s, mx + 1.45, 3.9, 1.28, 0.55, "Equipement", size=11.5)
    class_box(s, mx, 4.8, mw, 0.55, "Utilisateur")
    class_box(s, mx, 5.6, mw, 0.55, "Reservation")
    textbox(s, mx, 6.3, mw, 0.45, [P(R("relie une ressource à un utilisateur", BODY, 11, G500), align="c")])
    # flèche d'héritage (triangle creux + bus)
    pcx = mx + mw / 2
    shape(s, MSO_SHAPE.ISOSCELES_TRIANGLE, pcx - 0.13, 3.37, 0.26, 0.2, fill=WHITE, line=G800, lw=1.0, name="Héritage")
    line(s, pcx, 3.57, pcx, 3.72, color=G800, lw=1.0)
    line(s, mx + 0.64, 3.72, mx + 1.45 + 0.64, 3.72, color=G800, lw=1.0)
    line(s, mx + 0.64, 3.72, mx + 0.64, 3.9, color=G800, lw=1.0)
    line(s, mx + 1.45 + 0.64, 3.72, mx + 1.45 + 0.64, 3.9, color=G800, lw=1.0)

    # --- flèches entre groupes ---
    line(s, ax + 3.0, 2.975, bx + 0.2, 2.975, color=G800, lw=1.5, tail="triangle", name="Main appelle GestionReservations")
    textbox(s, ax + 3.0, 2.68, 1.2, 0.25, [P(R("appelle", BODY, 11, G500), align="c")])
    line(s, ax + 1.6, 3.35, ax + 1.6, 4.3, color=G800, lw=1.5, tail="triangle", name="Main utilise Saisie")
    textbox(s, ax + 1.75, 3.6, 1.4, 0.25, [P(R("lit le clavier", BODY, 11, G500))])
    line(s, bx + 1.7, 3.35, bx + 1.7, 3.95, color=G800, lw=1.5, tail="triangle", name="GestionReservations utilise GestionFichiers")
    textbox(s, bx + 1.85, 3.5, 1.5, 0.25, [P(R("charge · sauvegarde", BODY, 11, G500))])
    line(s, bx + 3.2, 2.975, cx, 2.975, color=G800, lw=1.5, tail="triangle", name="GestionReservations manipule le modèle")
    textbox(s, bx + 3.4, 2.68, 1.2, 0.25, [P(R("manipule", BODY, 11, G500), align="c")])
    line(s, bx + 3.2, 4.35, cx, 4.35, color=G800, lw=1.5, tail="triangle", name="GestionFichiers crée les objets")
    textbox(s, bx + 3.4, 4.05, 1.2, 0.25, [P(R("crée · écrit", BODY, 11, G500), align="c")])
    line(s, bx + 1.7, 5.0, bx + 1.7, 5.35, color=G800, lw=1.25, dash=MSO_LINE.DASH, tail="triangle", name="GestionReservations lance les exceptions")
    textbox(s, bx + 1.85, 5.03, 1.0, 0.25, [P(R("lance", BODY, 11, G500))])
    textbox(s, bx + 1.4, 5.47, 1.85, 0.22, [P(R("attrapées dans Main", BODY, 10, G500), align="r")])

    # --- légende ---
    ly = 5.55
    tag(s, ax, ly, "LÉGENDE", G500)
    line(s, ax, ly + 0.5, ax + 0.7, ly + 0.5, color=G800, lw=1.5, tail="triangle")
    textbox(s, ax + 0.9, ly + 0.38, 2.3, 0.25, [P(R("utilise · appelle", BODY, 12, G500))])
    line(s, ax, ly + 0.85, ax + 0.7, ly + 0.85, color=G800, lw=1.25, dash=MSO_LINE.DASH, tail="triangle")
    textbox(s, ax + 0.9, ly + 0.73, 2.3, 0.25, [P(R("lance une exception", BODY, 12, G500))])
    shape(s, MSO_SHAPE.ISOSCELES_TRIANGLE, ax + 0.25, ly + 1.04, 0.2, 0.15, fill=WHITE, line=G800, lw=1.0)
    textbox(s, ax + 0.9, ly + 1.08, 2.3, 0.25, [P(R("hérite de", BODY, 12, G500))])
    notes(s, note_text(4))


# ======================================================================
#  6. POO et héritage
# ======================================================================
def uml_box(s, x, y, w, head_h, body_lines, title, sub=None, abstract=False):
    hh = head_h
    head_paras = []
    if sub:
        head_paras.append(P(R(sub, MONO, 9, G300), align="c"))
    head_paras.append(P(R(title, HEAD, 15, WHITE, bold=True), align="c"))
    rect(s, x, y, w, hh, fill=BLACK, paras=head_paras, anchor="m", margins=(0.05, 0.03, 0.05, 0.03), name="Entête " + title)
    bh = 0.32 + 0.3 * len(body_lines)
    rect(s, x, y + hh, w, bh, fill=WHITE, line=G800, lw=1.0, name="Attributs " + title,
         paras=[P(R(t, MONO, 12, INK, italic=it), before=0) for t, it in body_lines], anchor="m", margins=(0.15, 0.04, 0.05, 0.04))
    return y + hh + bh


def slide6(prs):
    s = furniture(prs, 6, "06 — PROGRAMMATION ORIENTÉE OBJET", "Programmation orientée objet et héritage",
                  "Une classe abstraite regroupe ce que les salles et les équipements ont en commun.")
    # diagramme (gauche)
    pw = 3.1
    px = 3.725 - pw / 2
    bottom = uml_box(s, px, 2.3, pw, 0.62,
                     [("- id : String", False), ("- nom : String", False), ("+ getCategorie() : String", True)],
                     "Ressource", sub="classe abstraite")
    sx, ex, cwid = 0.75, 3.95, 2.85
    top2 = 5.0
    uml_box(s, sx, top2, cwid, 0.5, [("- capacite : int", False), ("- batiment : String", False), ("+ getCategorie()", False)], "Salle")
    uml_box(s, ex, top2, cwid, 0.5, [("- type : String", False), ("- quantiteDisponible : int", False), ("+ getCategorie()", False)], "Equipement")
    pcx = 3.725
    shape(s, MSO_SHAPE.ISOSCELES_TRIANGLE, pcx - 0.15, bottom, 0.3, 0.22, fill=WHITE, line=G800, lw=1.0, name="Héritage")
    yb = bottom + 0.22
    ybus = yb + 0.3
    line(s, pcx, yb, pcx, ybus, color=G800, lw=1.0)
    line(s, sx + cwid / 2, ybus, ex + cwid / 2, ybus, color=G800, lw=1.0)
    line(s, sx + cwid / 2, ybus, sx + cwid / 2, top2, color=G800, lw=1.0)
    line(s, ex + cwid / 2, ybus, ex + cwid / 2, top2, color=G800, lw=1.0)
    textbox(s, pcx + 0.15, ybus + 0.03, 1.2, 0.22, [P(R("extends", MONO, 10, G500))])

    # notions clés (droite, grille 2×2)
    concepts = [("Héritage", "Salle et Equipement reprennent id et nom de Ressource."),
                ("Encapsulation", "Attributs private, accessibles par getters et setters."),
                ("Réutilisation", "Le code commun est écrit une seule fois, dans Ressource."),
                ("Classe abstraite", "Ressource n'est jamais créée seule : c'est une salle ou un équipement.")]
    gx0, gy0, gw, gh = 7.35, 2.3, 2.5, 1.12
    for i, (h, t) in enumerate(concepts):
        x = gx0 + (i % 2) * (gw + 0.23)
        y = gy0 + (i // 2) * (gh + 0.12)
        line(s, x, y, x + gw, y, color=INK, lw=1.25)
        textbox(s, x, y + 0.1, gw, 0.28, [P(R(h, HEAD, 13, INK, bold=True))])
        textbox(s, x, y + 0.42, gw, 0.7, [P(R(t, BODY, 13, G500), line=1.0)])
    # extrait de code réel
    cxp, cyp, cwp, chp = 7.35, 4.8, 5.23, 2.1
    rect(s, cxp, cyp, cwp, chp, fill=PANEL, name="Extrait de code")
    textbox(s, cxp + 0.22, cyp + 0.12, 4.5, 0.2, [P(R("EXTRAITS DE Ressource.java ET Salle.java", MONO, 9, G500, spc=60))])
    code = ["public abstract class Ressource {",
            "    private String id;",
            "    private String nom;",
            "    public abstract String getCategorie();",
            "    ...",
            "public class Salle extends Ressource {",
            "    ...",
            "        super(id, nom);"]
    textbox(s, cxp + 0.22, cyp + 0.42, cwp - 0.4, 1.62,
            [P(R(c, MONO, 11.5, G200 if c.strip() != "..." else G500)) for c in code], name="Code")
    notes(s, note_text(5))


# ======================================================================
#  7. Gestion des réservations
# ======================================================================
def slide7(prs):
    s = furniture(prs, 7, "07 — RÉSERVATION", "Gestion des réservations",
                  "Sept étapes, de la saisie à la sauvegarde. Rien n'est enregistré si une vérification échoue.")
    steps = ["Choisir la ressource", "Identifier l'utilisateur", "Saisir la date", "Saisir l'heure de début et de fin",
             "Vérifier les disponibilités", "Valider ou refuser", "Sauvegarder les données"]
    x0, dx, cy = 1.45, 1.72, 3.4
    xs = [x0 + i * dx for i in range(7)]
    line(s, xs[0], cy, xs[-1], cy, color=G300, lw=1.5, name="Fil du processus")
    for i, (x, t) in enumerate(zip(xs, steps)):
        if i < 4:
            f, lc, fc = WHITE, INK, INK
        elif i < 6:
            f, lc, fc = BLACK, BLACK, WHITE
        else:
            f, lc, fc = ACCENT, ACCENT, WHITE
        oval(s, x - 0.3, cy - 0.3, 0.6, 0.6, fill=f, line=lc, lw=1.5, name="Étape %d" % (i + 1),
             paras=[P(R(str(i + 1), MONO, 15, fc, bold=True), align="c")], margins=(0, 0, 0, 0))
        textbox(s, x - 0.82, cy + 0.42, 1.64, 0.7, [P(R(t, BODY, 13, INK), align="c", line=1.0)])
    # branche « refus »
    bxc = xs[5]
    rrect(s, bxc - 1.2, 2.15, 2.4, 0.62, radius=0.06, fill=None, line=G500, lw=1.0, dash=MSO_LINE.DASH, name="Refus",
          paras=[P(R("Refus : message d'erreur", BODY, 12.5, INK, bold=True), align="c"),
                 P(R("rien n'est enregistré", BODY, 11.5, G500), align="c")], margins=(0.05, 0.02, 0.05, 0.02))
    line(s, bxc, cy - 0.3, bxc, 2.77, color=G500, lw=1.25, dash=MSO_LINE.DASH, tail="triangle", name="Branche non")
    textbox(s, bxc - 0.62, 2.9, 0.5, 0.22, [P(R("non", MONO, 10, G500), align="r")])
    textbox(s, (xs[5] + xs[6]) / 2 - 0.25, cy - 0.28, 0.5, 0.22, [P(R("oui", MONO, 10, ACCENT, bold=True), align="c")])
    # accolades de classes
    def brace(xa, xb, label):
        yb = 4.6
        line(s, xa, yb, xb, yb, color=G400, lw=1.0)
        line(s, xa, yb - 0.08, xa, yb, color=G400, lw=1.0)
        line(s, xb, yb - 0.08, xb, yb, color=G400, lw=1.0)
        textbox(s, xa - 0.5, yb + 0.07, xb - xa + 1.0, 0.22, [P(R(label, MONO, 10.5, G500), align="c")])
    brace(xs[0] - 0.55, xs[3] + 0.55, "Main · Saisie")
    brace(xs[4] - 0.55, xs[5] + 0.55, "GestionReservations")
    brace(xs[6] - 0.55, xs[6] + 0.55, "GestionFichiers")

    # texte (gauche) + sortie réelle (droite)
    pts = ["Les contrôles passent avant la création : une réservation invalide n'est jamais enregistrée.",
           "Trois familles d'erreurs : élément introuvable, données invalides, conflit de créneau.",
           "Après un succès, Main affiche le récapitulatif puis demande la sauvegarde."]
    y = 5.1
    for p in pts:
        rect(s, LM, y + 0.1, 0.09, 0.09, fill=ACCENT)
        textbox(s, LM + 0.25, y, 5.2, 0.55, [P(R(p, BODY, 13.5, INK), line=1.0)])
        y += 0.62
    px, py, pw, ph = 6.75, 4.98, 5.83, 1.9
    rect(s, px, py, pw, ph, fill=PANEL, name="Sortie réelle du programme")
    textbox(s, px + 0.22, py + 0.1, 5, 0.2, [P(R("SORTIE RÉELLE DU PROGRAMME", MONO, 9, G500, spc=60))])
    out = ["=== RESERVATION CONFIRMEE ===", "Identifiant : R6", "Ressource : S2 (A102)", "Utilisateur : U1 (Sara Benali)",
           "Date : 07/12/2026", "Début : 09:00", "Fin : 11:00", "Données sauvegardées."]
    textbox(s, px + 0.22, py + 0.38, pw - 0.4, 1.5,
            [P(R(l, MONO, 10.5, WHITE if i == 0 else G200, bold=(i == 0))) for i, l in enumerate(out)], name="Console")
    notes(s, note_text(6))


# ======================================================================
#  8. Détection des conflits (fond noir)
# ======================================================================
def slide8(prs):
    s = furniture(prs, 8, "08 — CONFLITS", "Détection des conflits",
                  "Deux créneaux se chevauchent s'ils ont lieu le même jour et si chacun commence avant la fin de l'autre.",
                  dark=True)
    textbox(s, LM, 2.05, 8.5, 0.5, [P(R("debut1 < fin2   &&   debut2 < fin1", MONO, 24, WHITE, bold=True))], name="Règle")
    textbox(s, LM, 2.6, 11.8, 0.25,
            [P(R("date1.equals(date2) && debut1.isBefore(fin2) && debut2.isBefore(fin1)", MONO, 11, G400))], name="Règle dans le code")
    textbox(s, LM, 2.9, 11.8, 0.25,
            [P(R("barre claire = créneau 1 (réservation existante) · barre bleue = créneau 2 (demande) · "
                 "1re ligne : debut1 < fin2 · 2e ligne : debut2 < fin1", BODY, 11.5, G500))])
    ax0, hw = 3.7, 1.55
    def X(h):
        return ax0 + (h - 8) * hw
    for h in range(8, 13):
        line(s, X(h), 3.5, X(h), 6.3, color=G700, lw=0.75)
        textbox(s, X(h) - 0.35, 3.22, 0.7, 0.22, [P(R("%02d:00" % h, MONO, 10.5, G400), align="c")])
    cases = [
        ("CAS 1", (9, 11), (10, 12), ["09 < 12  →  vrai", "10 < 11  →  vrai"], True, 10),
        ("CAS 2", (9, 11), (11, 12), ["09 < 12  →  vrai", "11 < 11  →  faux"], False, 11),
        ("CAS 3", (9, 11), (8, 9), ["09 < 09  →  faux", "08 < 11  →  vrai"], False, 9),
    ]
    y = 3.62
    for lab, ex, nw, ev, conflict, contact in cases:
        textbox(s, LM, y, 1.0, 0.25, [P(R(lab, MONO, 12, WHITE, bold=True))])
        textbox(s, LM, y + 0.3, 2.6, 0.5,
                [P([R(e.split("  →  ")[0], MONO, 11.5, G300), R("  →  ", BODY, 12, G500), R(e.split("  →  ")[1], MONO, 11.5, WHITE)]) for e in ev])
        # barre existante / demandée
        rect(s, X(ex[0]), y + 0.04, X(ex[1]) - X(ex[0]), 0.34, fill=G200, name="Réservation existante",
             paras=[P(R("09:00 – 11:00", BODY, 11.5, BLACK, bold=True), align="l")], margins=(0.1, 0, 0.05, 0))
        rect(s, X(nw[0]), y + 0.42, X(nw[1]) - X(nw[0]), 0.34, fill=ACCENT, line=ACCENT_LINE, lw=1.0, name="Demande",
             paras=[P(R("%02d:00 – %02d:00" % nw, BODY, 11.5, WHITE, bold=True), align="l")], margins=(0.1, 0, 0.05, 0))
        if conflict:
            rect(s, X(10), y - 0.02, X(11) - X(10), 0.82, fill=None, line=WHITE, lw=1.5, dash=MSO_LINE.DASH, name="Zone de chevauchement")
        else:
            line(s, X(contact), y - 0.06, X(contact), y + 0.86, color=WHITE, lw=1.5, name="Les créneaux se touchent")
        # verdict
        if conflict:
            rect(s, 10.35, y + 0.14, 2.23, 0.5, fill=WHITE, name="Verdict conflit",
                 paras=[P(R("CONFLIT", HEAD, 14, BLACK, bold=True), align="c")])
        else:
            rect(s, 10.35, y + 0.14, 2.23, 0.5, fill=None, line=WHITE, lw=1.5, name="Verdict accepté",
                 paras=[P(R("ACCEPTÉ", HEAD, 14, WHITE, bold=True), align="c")])
        y += 0.9
    textbox(s, LM, 6.4, CW, 0.5,
            [P([R("Pourquoi 11:00 est accepté ? ", BODY, 15, WHITE, bold=True),
                R("Une réservation qui se termine à 11:00 libère la salle à 11:00 : la suivante peut commencer à cette minute. "
                  "D'où la comparaison stricte (isBefore), jamais <=.", BODY, 15, G300)], line=1.0)])
    notes(s, note_text(7))


# ======================================================================
#  9. Équipements
# ======================================================================
def projector(s, x, y, used, ghost=False):
    if ghost:
        rrect(s, x, y, 0.52, 0.36, radius=0.05, fill=None, line=G400, lw=1.0, dash=MSO_LINE.DASH, name="Unité demandée en trop")
        textbox(s, x, y + 0.02, 0.52, 0.32, [P(R("✕", SYMBOL, 12, G500, bold=True), align="c")], anchor="m")
        return
    rrect(s, x, y, 0.52, 0.36, radius=0.05, fill=INK if used else WHITE, line=INK, lw=1.0, name="Unité %s" % ("utilisée" if used else "libre"))
    oval(s, x + 0.31, y + 0.09, 0.18, 0.18, fill=WHITE if used else None, line=WHITE if used else INK, lw=1.0)


def slide9(prs):
    s = furniture(prs, 9, "09 — ÉQUIPEMENTS", "Gestion des équipements",
                  "Plusieurs réservations simultanées sont possibles, dans la limite de la quantité disponible.")
    cw, gap = 2.75, 0.277
    texts = [("1", "ACCEPTÉE", "1 / 3 unités utilisées"), ("2", "ACCEPTÉE", "2 / 3 unités utilisées"),
             ("3", "ACCEPTÉE", "3 / 3 unités utilisées"), ("4", "REFUSÉE", "3 / 3 déjà utilisées")]
    textbox(s, LM, 2.12, 8, 0.22, [P(R("3 VIDÉOPROJECTEURS DISPONIBLES · MÊME CRÉNEAU 09:00 – 11:00", MONO, 10, G500, spc=80))])
    for i, (num, status, counter) in enumerate(texts):
        x = LM + i * (cw + gap)
        y = 2.45
        refused = (i == 3)
        rect(s, x, y, cw, 2.45, fill=G50 if refused else WHITE, line=G400 if refused else G300, lw=1.0,
             dash=MSO_LINE.DASH if refused else None, name="Réservation " + num)
        textbox(s, x + 0.22, y + 0.2, cw - 0.4, 0.3, [P(R("Réservation " + num, HEAD, 14, G500 if refused else INK, bold=True))])
        textbox(s, x + 0.22, y + 0.55, cw - 0.4, 0.22, [P(R("09:00 – 11:00", MONO, 11, G500))])
        for k in range(3):
            projector(s, x + 0.22 + k * 0.62, y + 1.0, used=(k < i + 1 if not refused else True))
        if refused:
            projector(s, x + 0.22 + 3 * 0.62 - 0.0, y + 1.0, used=False, ghost=True)
        textbox(s, x + 0.22, y + 1.5, cw - 0.4, 0.25, [P(R(counter, BODY, 12, G500))])
        if refused:
            rect(s, x + 0.22, y + 1.88, cw - 0.44, 0.38, fill=None, line=G500, lw=1.25, name="Statut refusée",
                 paras=[P(R(status, HEAD, 11, G500, bold=True), align="c")])
        else:
            rect(s, x + 0.22, y + 1.88, cw - 0.44, 0.38, fill=INK, name="Statut acceptée",
                 paras=[P(R(status, HEAD, 11, WHITE, bold=True), align="c")])
    # comparaison salle / équipement
    ty = 5.3
    line(s, LM, ty, RM, ty, color=G200, lw=0.75)
    line(s, LM + CW / 2, ty + 0.15, LM + CW / 2, 6.85, color=G200, lw=0.75)
    for k, (lab, head, txt) in enumerate([
        ("SALLE", "Une seule réservation à la fois",
         "Dès que deux créneaux se chevauchent, la seconde demande est refusée."),
        ("ÉQUIPEMENT", "Plusieurs en même temps, jusqu'à la quantité",
         "On compte les unités utilisées au même instant : 8h–10h puis 10h–12h ne comptent que pour 1 unité.")]):
        x = LM + k * (CW / 2) + (0.3 if k else 0)
        w = CW / 2 - 0.4
        textbox(s, x, ty + 0.2, w, 0.22, [P(R(lab, MONO, 10.5, ACCENT, bold=True, spc=100))])
        textbox(s, x, ty + 0.5, w, 0.35, [P(R(head, HEAD, 15, INK, bold=True))])
        textbox(s, x, ty + 0.92, w, 0.6, [P(R(txt, BODY, 13.5, G500), line=1.0)])
    notes(s, note_text(8))


# ======================================================================
#  10. Stockage des données
# ======================================================================
def slide10(prs):
    s = furniture(prs, 10, "10 — DONNÉES", "Stockage des données",
                  "Des fichiers texte UTF-8 : une ligne par enregistrement, champs séparés par un point-virgule.")
    nodes = [("Fichiers texte", "data/*.txt", True), ("Chargement", "au démarrage", False),
             ("ArrayList", "données en mémoire", False), ("Application", "menus et règles", False),
             ("Sauvegarde", "après chaque modification et à la fermeture", False), ("Fichiers texte", "data/*.txt", True)]
    nw, ng = 1.65, 0.386
    y = 2.2
    for i, (t, cap, dark) in enumerate(nodes):
        x = LM + i * (nw + ng)
        rrect(s, x, y, nw, 0.62, radius=0.07, fill=BLACK if dark else WHITE, line=BLACK if dark else G800, lw=1.25,
              name="Flux " + t, paras=[P(R(t, HEAD, 12, WHITE if dark else INK, bold=True), align="c")], margins=(0.04, 0.02, 0.04, 0.02))
        textbox(s, x - 0.05, y + 0.68, nw + 0.1, 0.5, [P(R(cap, BODY, 11, G500), align="c", line=1.0)])
        if i < len(nodes) - 1:
            line(s, x + nw + 0.03, y + 0.31, x + nw + ng - 0.03, y + 0.31, color=G800, lw=1.5, tail="triangle")
    files = [("salles.txt", "id;nom;capacité;bâtiment", "S1;A101;30;A"),
             ("equipements.txt", "id;nom;type;quantité", "E1;Videoprojecteur Epson;VIDEOPROJECTEUR;3"),
             ("utilisateurs.txt", "id;nom;prénom;rôle", "U1;Benali;Sara;ENSEIGNANT"),
             ("reservations.txt", "id;idRessource;catégorie;idUtilisateur;date;début;fin",
              "R1;S1;SALLE;U1;2026-12-07;09:00;11:00")]
    cw, ch, gx, gy = 5.78, 1.4, 0.27, 0.15
    for i, (fn, fmt, ex) in enumerate(files):
        x = LM + (i % 2) * (cw + gx)
        yy = 3.58 + (i // 2) * (ch + gy)
        rect(s, x, yy, cw, ch, fill=G50, line=G200, lw=0.75, name="Fichier " + fn)
        textbox(s, x + 0.22, yy + 0.12, 2.6, 0.26, [P(R(fn, MONO, 13, INK, bold=True))])
        textbox(s, x + 0.22, yy + 0.43, cw - 0.4, 0.22, [P(R(fmt, MONO, 10.5, G500))])
        rect(s, x + 0.22, yy + 0.8, cw - 0.44, 0.45, fill=PANEL, name="Exemple " + fn,
             paras=[P(R(ex, MONO, 11.5, WHITE))], anchor="m", margins=(0.15, 0, 0.1, 0))
    textbox(s, LM, 6.66, CW, 0.26,
            [P([R("Fichier technique facultatif : ", BODY, 12, INK, bold=True),
                R("compteurs.txt mémorise les derniers numéros attribués, pour ne jamais réutiliser un identifiant supprimé.", BODY, 12, G500)])])
    notes(s, note_text(9))


# ======================================================================
#  11. Fonctionnalités et tests
# ======================================================================
def check(s, x, y):
    oval(s, x, y, 0.27, 0.27, fill=ACCENT, name="Test réussi",
         paras=[P(R("✓", SYMBOL, 10, WHITE, bold=True), align="c")], margins=(0, 0, 0, 0))


def slide11(prs):
    s = furniture(prs, 11, "11 — RÉSULTATS", "Fonctionnalités et tests",
                  "Ce qui fonctionne dans l'application, et ce qui a été réellement vérifié.")
    kpis = [("170", "vérifications automatisées de la logique métier"),
            ("80", "vérifications en console, sur le vrai programme"),
            ("1", "arrêt brutal : sauvegarde immédiate prouvée")]
    kx = LM
    for num, lab in kpis:
        textbox(s, kx, 2.12, 1.2, 0.6, [P(R(num, HEAD, 36, INK, bold=True))], anchor="m")
        textbox(s, kx + 1.15 if len(num) > 2 else kx + 0.95 if len(num) == 2 else kx + 0.55, 2.12, 2.75, 0.6,
                [P(R(lab, BODY, 12.5, G500), line=1.0)], anchor="m")
        kx += 4.0
    line(s, LM, 3.0, RM, 3.0, color=G200, lw=0.75)
    # fonctionnalités
    textbox(s, LM, 3.2, 5, 0.22, [P(R("FONCTIONNALITÉS IMPLÉMENTÉES", MONO, 10.5, ACCENT, bold=True, spc=100))])
    feats = ["Salles : ajouter, modifier, supprimer, afficher", "Équipements : ajouter, modifier, supprimer, afficher",
             "Utilisateurs : ajouter, modifier, supprimer, afficher", "Réserver une salle ou un équipement",
             "Annuler ou modifier une réservation", "Consulter le planning (4 modes, trié par date)",
             "Rechercher les disponibilités sur un créneau"]
    y = 3.6
    for f in feats:
        rect(s, LM, y + 0.09, 0.09, 0.09, fill=INK)
        textbox(s, LM + 0.28, y, 5.5, 0.3, [P(R(f, BODY, 14, INK))])
        y += 0.45
    # tests
    tx = 6.95
    textbox(s, tx, 3.2, 5.6, 0.22, [P(R("TESTS EXÉCUTÉS · TOUS RÉUSSIS", MONO, 10.5, ACCENT, bold=True, spc=100))])
    tests = [("Détection des conflits", "10h–12h refusé ; 11h–12h et 8h–9h acceptés"),
             ("Gestion des quantités", "3 acceptées, la 4e refusée ; baisse de stock refusée si incompatible"),
             ("Dates et heures", "début ≥ fin, date passée, 31/02, 25:00"),
             ("Suppressions interdites", "salle, équipement ou utilisateur ayant des réservations"),
             ("Persistance", "relance du programme, identifiants jamais réutilisés"),
             ("Saisies incorrectes", "lettres, option ou identifiant inconnus, clavier fermé")]
    y = 3.58
    for h, d in tests:
        check(s, tx, y + 0.04)
        textbox(s, tx + 0.42, y, 5.2, 0.28, [P(R(h, HEAD, 12.5, INK, bold=True))])
        textbox(s, tx + 0.42, y + 0.27, 5.2, 0.24, [P(R(d, BODY, 11.5, G500))])
        y += 0.54
    notes(s, note_text(10))


# ======================================================================
#  12. Conclusion (fond noir)
# ======================================================================
def slide12(prs):
    s = furniture(prs, 12, "12 — CONCLUSION", "Conclusion et perspectives",
                  "Ce que le projet nous a appris, et ce qui pourrait venir ensuite.", dark=True)
    # colonne gauche : conclusion + compétences
    textbox(s, LM, 2.15, 5.6, 1.3,
            [P(R("Ce projet permet de mettre en pratique les notions fondamentales de Java à travers une application réelle "
                 "de gestion de ressources et de réservations.", BODY, 17, WHITE), line=1.08)], name="Conclusion")
    textbox(s, LM, 3.6, 5, 0.22, [P(R("COMPÉTENCES MOBILISÉES", MONO, 10.5, ACCENT_LINE, spc=100))])
    chips = ["Programmation orientée objet", "Héritage et encapsulation", "Collections", "Manipulation des fichiers",
             "Gestion des exceptions", "Algorithmique"]
    widths = [2.75, 2.5, 1.45, 2.45, 2.45, 1.55]
    x, y = LM, 3.95
    for t, w in zip(chips, widths):
        if x + w > LM + 5.75:
            x, y = LM, y + 0.5
        rrect(s, x, y, w, 0.38, radius=0.19, fill=None, line=G400, lw=1.0, name="Compétence " + t,
              paras=[P(R(t, BODY, 12, WHITE), align="c")], margins=(0.05, 0, 0.05, 0))
        x += w + 0.12
    # colonne droite : perspectives (non réalisées)
    px = 7.15
    textbox(s, px, 2.15, 5.4, 0.22, [P(R("PERSPECTIVES · AMÉLIORATIONS FUTURES, NON RÉALISÉES", MONO, 10, G400, spc=60))])
    persp = ["Ajouter une interface graphique", "Ajouter une base de données", "Ajouter une authentification",
             "Générer des rapports de réservation"]
    yy = 2.55
    for t in persp:
        rrect(s, px, yy, 5.43, 0.52, radius=0.05, fill=None, line=G500, lw=1.0, dash=MSO_LINE.DASH, name="Perspective " + t,
              paras=[P(R(t, BODY, 14, G300))], anchor="m", margins=(0.2, 0, 0.1, 0))
        yy += 0.62
    # remerciements
    line(s, LM, 5.45, RM, 5.45, color=G700, lw=0.75)
    textbox(s, LM, 5.7, 8.5, 0.6, [P(R("MERCI POUR VOTRE ATTENTION", HEAD, 30, WHITE, bold=True))], name="Merci")
    textbox(s, LM, 6.4, 6, 0.4, [P(R("Des questions ?", MONO, 18, ACCENT_LINE))], name="Questions")
    notes(s, note_text(11))


def build(out_path):
    prs = Presentation()
    old_w = prs.slide_width
    prs.slide_width, prs.slide_height = Inches(SW), Inches(SH)
    factor = prs.slide_width / old_w
    for part in [prs.slide_master] + list(prs.slide_layouts):     # layouts 16:9 propres pour les futurs ajouts
        for sh in part.shapes:
            sh.left, sh.width = int(sh.left * factor), int(sh.width * factor)
    for fn in (slide1, slide2, slide3, slide4, slide5, slide6, slide7, slide8, slide9, slide10, slide11, slide12):
        fn(prs)
    cp = prs.core_properties
    cp.title = "Application de réservation de salles et d'équipements – Projet Java"
    cp.subject = "Présentation du projet universitaire"
    cp.author = ""
    cp.last_modified_by = ""
    cp.keywords = "Java; réservation; POO"
    cp.comments = ""
    prs.save(out_path)
    return out_path


if __name__ == "__main__":
    out = sys.argv[1] if len(sys.argv) > 1 else "deck_raw.pptx"
    print(build(out))
