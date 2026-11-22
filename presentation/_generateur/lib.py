# -*- coding: utf-8 -*-
"""Petite bibliothèque de dessin pour python-pptx : formes natives, textes, connecteurs."""
import re
from pptx import Presentation
from pptx.util import Inches, Pt, Emu
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_SHAPE, MSO_CONNECTOR
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR, MSO_AUTO_SIZE
from pptx.enum.dml import MSO_LINE
from pptx.oxml.ns import qn
from lxml import etree

# ---------- Palette (noir, blanc, gris + un bleu foncé discret) ----------
BLACK = "0B0B0D"
INK = "17181B"
WHITE = "FFFFFF"
G50 = "F6F6F5"
G100 = "EFEFEE"
G200 = "DEDEDC"
G300 = "BEBEBC"
G400 = "9A9A98"
G500 = "6F6F6D"
G700 = "3B3B3D"
G800 = "26272A"
PANEL = "17181B"       # fond des blocs de code
ACCENT = "2A4E8F"      # bleu foncé discret
ACCENT_LINE = "6F92D1"  # liseré clair pour détacher le bleu sur fond noir

HEAD = "Sora"
MONO = "Spline Sans Mono"
BODY = "Calibri"
SYMBOL = "Segoe UI Symbol"

SW, SH = 13.333, 7.5
LM = 0.75
CW = 11.83
RM = LM + CW


def rgb(h):
    return RGBColor.from_string(h)


# ---------- Textes ----------
def fr(t):
    """Typographie française : espace insécable avant ? ! : ; et à l'intérieur des guillemets."""
    t = re.sub(r" ([?!:;»])", " \\1", t)
    return t.replace("« ", "« ")


def R(text, font=BODY, size=14, color=INK, bold=False, italic=False, spc=None):
    if font != MONO:  # le code et les lignes de fichiers restent tels quels
        text = fr(text)
    if font == HEAD and size < 16 and " " in text.strip().replace(" ", " "):
        # Sora a un espace très étroit (0,21 em) : illisible en petit corps -> petits intitulés en gras dans la police de texte
        font, size, bold = BODY, size + 1.5, True
    elif font == HEAD and spc is None:
        spc = -int(round(size * 1.0))   # grand corps : on resserre un peu les lettres
    return dict(text=text, font=font, size=size, color=color, bold=bold, italic=italic, spc=spc)


def P(runs, align="l", before=0, after=0, line=None):
    if isinstance(runs, dict):
        runs = [runs]
    return dict(runs=runs, align=align, before=before, after=after, line=line)


_ALIGN = {"l": PP_ALIGN.LEFT, "c": PP_ALIGN.CENTER, "r": PP_ALIGN.RIGHT}
_ANCHOR = {"t": MSO_ANCHOR.TOP, "m": MSO_ANCHOR.MIDDLE, "b": MSO_ANCHOR.BOTTOM}


def fill_tf(tf, paras, anchor="t", margins=(0, 0, 0, 0)):
    """Remplit un cadre de texte. margins = (gauche, haut, droite, bas) en pouces."""
    tf.word_wrap = True
    tf.auto_size = MSO_AUTO_SIZE.NONE
    tf.margin_left, tf.margin_top, tf.margin_right, tf.margin_bottom = [Inches(m) for m in margins]
    tf.vertical_anchor = _ANCHOR[anchor]
    first = True
    for p in paras:
        para = tf.paragraphs[0] if first else tf.add_paragraph()
        first = False
        para.alignment = _ALIGN[p["align"]]
        para.space_before = Pt(p["before"])
        para.space_after = Pt(p["after"])
        if p["line"]:
            para.line_spacing = p["line"]
        for r in p["runs"]:
            run = para.add_run()
            run.text = r["text"]
            f = run.font
            f.name = r["font"]
            f.size = Pt(r["size"])
            f.bold = r["bold"]
            f.italic = r["italic"]
            f.color.rgb = rgb(r["color"])
            rpr = run._r.get_or_add_rPr()
            if r["spc"] is not None:
                rpr.set("spc", str(r["spc"]))
            rpr.set("lang", "fr-FR")


def textbox(slide, x, y, w, h, paras, anchor="t", margins=(0, 0, 0, 0), name=None):
    tb = slide.shapes.add_textbox(Inches(x), Inches(y), Inches(w), Inches(h))
    fill_tf(tb.text_frame, paras, anchor, margins)
    if name:
        tb.name = name
    return tb


# ---------- Formes ----------
def _strip_style(shape):
    st = shape._element.find(qn("p:style"))
    if st is not None:
        shape._element.remove(st)


def shape(slide, kind, x, y, w, h, fill=None, line=None, lw=0.75, dash=None, radius=None,
          paras=None, anchor="m", margins=(0.08, 0.04, 0.08, 0.04), name=None, alt=None):
    sp = slide.shapes.add_shape(kind, Inches(x), Inches(y), Inches(w), Inches(h))
    _strip_style(sp)
    if fill is None:
        sp.fill.background()
    else:
        sp.fill.solid()
        sp.fill.fore_color.rgb = rgb(fill)
    if line is None:
        sp.line.fill.background()
    else:
        sp.line.color.rgb = rgb(line)
        sp.line.width = Pt(lw)
        if dash:
            sp.line.dash_style = dash
    if radius is not None and kind == MSO_SHAPE.ROUNDED_RECTANGLE:
        sp.adjustments[0] = min(0.5, radius / min(w, h))
    if paras:
        fill_tf(sp.text_frame, paras, anchor, margins)
    if name:
        sp.name = name
    if alt:
        sp._element.nvSpPr.cNvPr.set("descr", alt)
    return sp


def rect(slide, x, y, w, h, **kw):
    return shape(slide, MSO_SHAPE.RECTANGLE, x, y, w, h, **kw)


def rrect(slide, x, y, w, h, radius=0.08, **kw):
    return shape(slide, MSO_SHAPE.ROUNDED_RECTANGLE, x, y, w, h, radius=radius, **kw)


def oval(slide, x, y, w, h, **kw):
    return shape(slide, MSO_SHAPE.OVAL, x, y, w, h, **kw)


def line(slide, x1, y1, x2, y2, color=G300, lw=1.0, dash=None, tail=None, head=None, name=None):
    c = slide.shapes.add_connector(MSO_CONNECTOR.STRAIGHT, Inches(x1), Inches(y1), Inches(x2), Inches(y2))
    _strip_style(c)
    c.line.color.rgb = rgb(color)
    c.line.width = Pt(lw)
    if dash:
        c.line.dash_style = dash
    ln = c.line._get_or_add_ln()
    for tag, kind in (("a:headEnd", head), ("a:tailEnd", tail)):
        if kind:
            el = etree.SubElement(ln, qn(tag))
            el.set("type", kind)
            el.set("w", "med")
            el.set("len", "med")
    if name:
        c.name = name
    return c


def set_bg(slide, color):
    slide.background.fill.solid()
    slide.background.fill.fore_color.rgb = rgb(color)


def notes(slide, text):
    slide.notes_slide.notes_text_frame.text = text
