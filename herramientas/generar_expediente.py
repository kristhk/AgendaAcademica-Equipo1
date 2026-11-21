"""Genera el expediente final en DOCX y PDF a partir de los archivos Markdown.

Flujo (sin instalar software adicional):
  1. Diagramas Mermaid -> PNG con Microsoft Edge en modo headless (mermaid.js desde jsDelivr).
  2. Pantallas del prototipo HTML -> PNG con Edge headless (?solo=pX).
  3. Markdown -> HTML con un convertidor propio (solo biblioteca estándar de Python).
  4. HTML -> DOCX y PDF con Microsoft Word (automatización COM, script exportar_word.ps1).

Uso (desde la carpeta raíz del proyecto):
    python herramientas/generar_expediente.py
"""
import hashlib
import html
import json
import re
import struct
import subprocess
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
BUILD = RAIZ / "herramientas" / "_build"
EDGE = Path(r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe")
MERMAID_CDN = "https://cdn.jsdelivr.net/npm/mermaid@10.9.1/dist/mermaid.min.js"
ANCHO_MAX_IMG = 640  # px dentro de la página

# (archivo markdown, título para el índice). El orden define el expediente consolidado.
DOCUMENTOS = [
    ("01_Analisis_Requerimientos/Analisis_Requerimientos.md", "Entregable 1 — Análisis de requerimientos"),
    ("02_Mapa_Navegacion/Mapa_Navegacion.md", "Entregable 2 — Mapa de navegación"),
    ("03_Maquetado/Maquetado.md", "Entregable 3 — Maquetado"),
    ("04_Modelo_Datos/Modelo_Datos.md", "Entregable 4 — Modelo de datos"),
    ("05_Instrumentos_Evaluacion/Instrumentos_Evaluacion.md", "Entregable 5 — Instrumentos de evaluación"),
    ("05_Instrumentos_Evaluacion/Registro_Resultados.md", "Entregable 5 — Registro de resultados"),
    ("05_Instrumentos_Evaluacion/Cuestionario_SUS.md", "Entregable 5 — Cuestionario SUS"),
    ("06_Desarrollo_Agentico/Bitacora_Desarrollo_Agentico.md", "Entregable 6 — Desarrollo agéntico"),
    ("06_Desarrollo_Agentico/Prompts.md", "Entregable 6 — Prompts"),
    ("06_Desarrollo_Agentico/Problemas_y_Soluciones.md", "Entregable 6 — Problemas y soluciones"),
    ("06_Desarrollo_Agentico/Capturas_App.md", "Entregable 6 — Capturas de la aplicación"),
    ("06_Desarrollo_Agentico/Historial_Git.md", "Entregable 6 — Historial Git"),
]
EXTRA = ["ESTADO_PROYECTO.md", "PLAN_PROYECTO.md", "LEEME.md"]  # se exporta aparte (no forma parte del expediente consolidado)

CSS = """
body{font-family:Calibri,'Segoe UI',sans-serif;font-size:10.5pt;color:#1A1B21;line-height:1.3}
h1{font-size:18pt;color:#283593;border-bottom:2px solid #3949AB;padding-bottom:4px;margin-top:6pt}
h2{font-size:14pt;color:#3949AB;margin-top:14pt}
h3{font-size:12pt;color:#00695C;margin-top:10pt}
h4{font-size:11pt;color:#333}
table{border-collapse:collapse;margin:6pt 0;font-size:9pt}
th{background:#DDE1FF;color:#00105C;font-weight:bold;border:1px solid #9FA8DA;padding:3pt 4pt;text-align:left;vertical-align:top}
td{border:1px solid #C5CAE9;padding:3pt 4pt;vertical-align:top}
code{font-family:Consolas,monospace;font-size:9pt;background:#F1F2F8}
pre{font-family:Consolas,monospace;font-size:8pt;background:#F4F5FA;border:1px solid #DDE1FF;padding:6pt;white-space:pre-wrap}
blockquote{border-left:4px solid #00897B;background:#EEF7F6;margin:6pt 0;padding:4pt 8pt;color:#204040}
img{border:0}
.portada{text-align:center;margin-top:120pt}
.salto{page-break-before:always}
"""


# ----------------------------------------------------------------- utilidades Edge
def edge(*args):
    perfil = BUILD / "edge-perfil"  # perfil aislado: no toca el perfil del usuario
    cmd = [str(EDGE), "--headless=new", "--disable-gpu", "--no-first-run", "--hide-scrollbars",
           "--allow-file-access-from-files", f"--user-data-dir={perfil}", *args]
    return subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8", errors="replace", timeout=180)


def captura(url, png, ancho, alto, escala=2):
    png.unlink(missing_ok=True)
    edge(f"--screenshot={png}", f"--window-size={ancho},{alto}", f"--force-device-scale-factor={escala}",
         "--virtual-time-budget=15000", url)
    if not png.exists():
        raise RuntimeError(f"Edge no generó la captura {png}")


def dimensiones(png, ancho, alto_max=800):
    """Ancho y alto (px) que conservan la proporción del PNG; limita la altura a una página."""
    with open(png, "rb") as f:
        w, h = struct.unpack(">II", f.read(24)[16:24])  # cabecera IHDR del PNG
    alto = round(h * ancho / w)
    if alto > alto_max:
        ancho, alto = round(ancho * alto_max / alto), alto_max
    return ancho, alto


# ----------------------------------------------------------------- Mermaid -> PNG
def renderizar_mermaid(codigo, nombre):
    """Devuelve (ruta_png, ancho_css). Usa caché por contenido."""
    destino = BUILD / "diagramas"
    destino.mkdir(parents=True, exist_ok=True)
    clave = f"{nombre}_{hashlib.md5(codigo.encode('utf-8')).hexdigest()[:8]}"
    png, meta = destino / f"{clave}.png", destino / f"{clave}.json"
    if png.exists() and meta.exists():
        return png, json.loads(meta.read_text())["ancho"]
    pagina = destino / f"{clave}.html"
    pagina.write_text(
        "<!doctype html><html><head><meta charset='utf-8'></head>"
        "<body style='margin:0;padding:8px;background:#fff'>"
        f"<pre class='mermaid'>{html.escape(codigo)}</pre>"
        f"<script src='{MERMAID_CDN}'></script>"
        "<script>mermaid.initialize({startOnLoad:true,theme:'default',"
        "fontFamily:'Segoe UI, Calibri, sans-serif'});</script></body></html>",
        encoding="utf-8")
    url = pagina.as_uri()
    dom = edge("--dump-dom", "--virtual-time-budget=15000", url).stdout
    m = re.search(r'viewBox="[\d.\-]+ [\d.\-]+ ([\d.]+) ([\d.]+)"', dom)
    if not m:
        raise RuntimeError(f"Mermaid no se renderizó para {nombre} (¿sin conexión a jsDelivr?)")
    w, h = float(m.group(1)), float(m.group(2))
    captura(url, png, int(w) + 20, int(h) + 20)
    meta.write_text(json.dumps({"ancho": min(int(w), ANCHO_MAX_IMG)}))
    return png, min(int(w), ANCHO_MAX_IMG)


# ----------------------------------------------------------------- Prototipo -> PNG
def capturar_prototipo():
    destino = RAIZ / "03_Maquetado" / "capturas_prototipo"
    destino.mkdir(exist_ok=True)
    proto = (RAIZ / "03_Maquetado" / "Prototipo_Navegable.html").as_uri()
    for p in ["p0", "p1", "p2", "p3", "p4", "p5", "p6", "p6e", "d1", "d3"]:
        captura(f"{proto}?solo={p}", destino / f"{p}.png", 390, 700)
    captura(f"{proto}?solo=guia", destino / "guia_estilo.png", 1020, 1250)
    print(f"  prototipo: {len(list(destino.glob('*.png')))} capturas en {destino.relative_to(RAIZ)}")


# ----------------------------------------------------------------- Markdown -> HTML
def inline(texto, base):
    partes = re.split(r"(`[^`]+`)", texto)  # protege el código en línea
    salida = []
    for p in partes:
        if p.startswith("`") and p.endswith("`") and len(p) > 1:
            salida.append(f"<code>{html.escape(p[1:-1])}</code>")
            continue
        p = html.escape(p, quote=False)
        p = re.sub(r"&lt;(/?)(br|u|b|i)\s*/?&gt;", r"<\1\2>", p)  # etiquetas simples permitidas
        def img(m):
            ruta = (base / m.group(2)).resolve()
            w, h = dimensiones(ruta, int(m.group(3) or 150))
            return f'<img src="{ruta.as_uri()}" alt="{m.group(1)}" width="{w}" height="{h}">'
        p = re.sub(r"!\[([^\]]*)\]\(([^)\s]+)\)(?:\{width=(\d+)\})?", img, p)
        p = re.sub(r"\[([^\]]+)\]\((https?://[^)]+)\)", r'<a href="\2">\1</a>', p)
        p = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", p)
        p = re.sub(r"(?<![\*\w])\*(?!\s)(.+?)(?<!\s)\*(?![\*\w])", r"<i>\1</i>", p)
        p = re.sub(r"(?<![\w/])_(?!\s)([^_]+?)(?<!\s)_(?![\w/])", r"<i>\1</i>", p)
        salida.append(p)
    return "".join(salida)


def tabla(lineas, base):
    filas = [[c.strip() for c in l.strip().strip("|").split("|")] for l in lineas]
    cab, cuerpo = filas[0], filas[2:]
    ncol = len(cab)
    # Ancho de cada columna proporcional a su contenido (Word no autoajusta bien el HTML)
    def largo(c):
        if "![" in c:
            return 30  # celdas con imagen: columnas iguales
        texto = re.sub(r"[`*_]|\[([^\]]*)\]\([^)]*\)", r"", c)
        return min(max(len(texto), 9), 70)
    pesos = [max((largo(f[i]) for f in [cab] + cuerpo if i < len(f)), default=4) for i in range(ncol)]
    pct = [max(8, round(100 * p / sum(pesos))) for p in pesos]
    h = ['<table border="1" cellspacing="0" cellpadding="3" width="100%">']
    primera = True
    if any(cab):  # las tablas de imágenes usan un encabezado vacío: no se imprime
        h += ["<tr>"] + [f'<th width="{pct[i]}%">{inline(c, base)}</th>' for i, c in enumerate(cab)] + ["</tr>"]
        primera = False
    for f in cuerpo:
        celdas = [f'<td{f" width={chr(34)}{pct[i]}%{chr(34)}" if primera and i < ncol else ""}>{inline(c, base)}</td>'
                  for i, c in enumerate(f)]
        h.append("<tr>" + "".join(celdas) + "</tr>")
        primera = False
    h.append("</table>")
    return "\n".join(h)


def md_a_html(ruta_md, nombre):
    base = ruta_md.parent
    lineas = ruta_md.read_text(encoding="utf-8").splitlines()
    out, i, parrafo = [], 0, []
    pila_listas = []  # [(tipo, sangría)]

    def cerrar_parrafo():
        if parrafo:
            out.append(f"<p>{inline(' '.join(parrafo), base)}</p>")
            parrafo.clear()

    def cerrar_listas(hasta=-1):
        while pila_listas and pila_listas[-1][1] > hasta:
            out.append(f"</{pila_listas.pop()[0]}>")

    n_diag = 0
    while i < len(lineas):
        l = lineas[i]
        if l.strip().startswith("```"):
            cerrar_parrafo(); cerrar_listas()
            lang = l.strip()[3:].strip()
            bloque = []
            i += 1
            while i < len(lineas) and not lineas[i].strip().startswith("```"):
                bloque.append(lineas[i]); i += 1
            codigo = "\n".join(bloque)
            if lang == "mermaid":
                n_diag += 1
                png, ancho = renderizar_mermaid(codigo, f"{nombre}_{n_diag}")
                w, h = dimensiones(png, ancho)
                out.append(f'<p align="center"><img src="{png.as_uri()}" width="{w}" height="{h}"></p>')
            else:
                out.append(f"<pre>{html.escape(codigo)}</pre>")
        elif re.match(r"^\s*\|.*\|\s*$", l) and i + 1 < len(lineas) and re.match(r"^\s*\|[\s:\-|]+\|\s*$", lineas[i + 1]):
            cerrar_parrafo(); cerrar_listas()
            bloque = []
            while i < len(lineas) and re.match(r"^\s*\|.*\|\s*$", lineas[i]):
                bloque.append(lineas[i]); i += 1
            out.append(tabla(bloque, base))
            continue
        elif m := re.match(r"^(#{1,6})\s+(.*)", l):
            cerrar_parrafo(); cerrar_listas()
            n = len(m.group(1))
            out.append(f"<h{n}>{inline(m.group(2), base)}</h{n}>")
        elif re.match(r"^\s*(---|\*\*\*)\s*$", l):
            cerrar_parrafo(); cerrar_listas()
            out.append("<hr>")
        elif l.startswith(">"):
            cerrar_parrafo(); cerrar_listas()
            bloque = []
            while i < len(lineas) and lineas[i].startswith(">"):
                bloque.append(lineas[i].lstrip(">").strip()); i += 1
            out.append(f"<blockquote>{inline(' '.join(bloque), base)}</blockquote>")
            continue
        elif m := re.match(r"^(\s*)([-*]|\d+\.)\s+(.*)", l):
            cerrar_parrafo()
            sangria = len(m.group(1))
            tipo = "ol" if m.group(2)[0].isdigit() else "ul"
            cerrar_listas(sangria)
            if not pila_listas or pila_listas[-1][1] < sangria:
                out.append(f"<{tipo}>"); pila_listas.append((tipo, sangria))
            texto = m.group(3)
            # líneas de continuación con sangría mayor que no son nuevos ítems
            while i + 1 < len(lineas) and lineas[i + 1].startswith(" " * (sangria + 2)) \
                    and not re.match(r"^\s*([-*]|\d+\.)\s+", lineas[i + 1]) and not lineas[i + 1].strip().startswith("```"):
                i += 1; texto += " " + lineas[i].strip()
            out.append(f"<li>{inline(texto, base)}</li>")
        elif not l.strip():
            cerrar_parrafo()
            # una línea vacía no cierra la lista si sigue otro ítem
            sig = next((x for x in lineas[i + 1:] if x.strip()), "")
            if not re.match(r"^\s*([-*]|\d+\.)\s+", sig):
                cerrar_listas()
        else:
            if pila_listas and l.startswith(" "):
                out[-1] = out[-1].replace("</li>", " " + inline(l.strip(), base) + "</li>")
            else:
                cerrar_listas(); parrafo.append(l.strip())
        i += 1
    cerrar_parrafo(); cerrar_listas()
    return "\n".join(out)


def pagina(cuerpo, titulo):
    return (f"<!doctype html><html lang='es'><head><meta charset='utf-8'><title>{html.escape(titulo)}</title>"
            f"<style>{CSS}</style></head><body>{cuerpo}</body></html>")


def escribir_historial_git():
    """Genera 06_Desarrollo_Agentico/Historial_Git.md a partir del historial real del repositorio."""
    r = subprocess.run(["git", "-C", str(RAIZ), "log", "--reverse", "--date=format:%Y-%m-%d %H:%M",
                        "--pretty=format:%h\x1f%ad\x1f%s\x1f%b\x1e"],
                       capture_output=True, text=True, encoding="utf-8")
    filas = []
    for reg in r.stdout.split("\x1e"):
        if not reg.strip():
            continue
        h, fecha, asunto, cuerpo = (reg.strip("\n").split("\x1f") + ["", "", "", ""])[:4]
        coautor = "Sí" if "Co-Authored-By: Claude" in cuerpo else "No"
        filas.append(f"| `{h}` | {fecha} | {asunto.replace('|', '/')} | {coautor} |")
    texto = (
        "# Anexo — Historial Git del desarrollo\n\n"
        "Repositorio Git **local** en `ProyectoExamenMoviles/` (no publicado en ningún servicio externo). "
        "Se inicializó el 2026-11-20, cuando el equipo lo autorizó. Los commits agrupan por etapas el trabajo "
        "de la sesión con Claude Code y llevan su **fecha real** (no se alteraron fechas). La columna *Claude* "
        "indica si el commit lleva el trailer `Co-Authored-By: Claude`, que identifica el trabajo generado con el agente.\n\n"
        "Para verlo completo: `git log --stat` dentro de la carpeta del proyecto.\n\n"
        "| Commit | Fecha | Descripción | Claude |\n|---|---|---|---|\n" + "\n".join(filas) + "\n\n"
        f"Total de commits al generar este documento: **{len(filas)}** (el commit que incluye este archivo puede no aparecer en la lista).\n")
    (RAIZ / "06_Desarrollo_Agentico" / "Historial_Git.md").write_text(texto, encoding="utf-8")


# ----------------------------------------------------------------- principal
def main():
    BUILD.mkdir(parents=True, exist_ok=True)
    escribir_historial_git()
    print("1) Capturando pantallas del prototipo HTML...")
    capturar_prototipo()

    print("2) Convirtiendo Markdown a HTML (con diagramas)...")
    trabajos, secciones = [], []
    for rel in [d for d, _ in DOCUMENTOS] + EXTRA:
        md = RAIZ / rel
        cuerpo = md_a_html(md, md.stem)
        h = BUILD / f"{md.stem}.html"
        h.write_text(pagina(cuerpo, md.stem), encoding="utf-8")
        trabajos.append({"html": str(h), "docx": str(md.with_suffix(".docx")), "pdf": str(md.with_suffix(".pdf")), "indice": False})
        if rel not in EXTRA:
            secciones.append(cuerpo)
        print(f"   {rel}")

    portada = (
        "<div class='portada'><p style='font-size:12pt;color:#555'>EXAMEN POR EQUIPOS — Programación de Móviles</p>"
        "<p style='font-size:26pt;color:#283593;font-weight:bold;margin:12pt 0'>Expediente técnico</p>"
        "<p style='font-size:20pt;color:#3949AB'>Agenda Académica</p>"
        "<p style='font-size:13pt'>Equipo 1 — Gestor de tareas y actividades académicas</p>"
        "<p style='font-size:11pt;margin-top:40pt'>Análisis, documentación y desarrollo agéntico de una aplicación móvil Android</p>"
        "<table border='1' cellpadding='4' style='width:70%;margin:30pt auto 0 auto;font-size:11pt'>"
        "<tr><td><b>Asignatura</b></td><td>Programación de Móviles</td></tr>"
        "<tr><td><b>Grupo</b></td><td>(completar)</td></tr>"
        "<tr><td><b>Integrantes</b></td><td>(completar)</td></tr>"
        "<tr><td><b>Herramienta agéntica</b></td><td>Claude Code (modelo Claude Opus 5.5), autorizada por el profesor</td></tr>"
        "<tr><td><b>Fecha</b></td><td>Noviembre de 2026</td></tr></table></div>"
        "<p class='salto' style='font-size:16pt;color:#283593;font-weight:bold'>Contenido</p><p>[[INDICE]]</p>")
    consolidado = portada + "".join(f"<div class='salto'></div>{s}" for s in secciones)
    h = BUILD / "Expediente_Tecnico_Equipo1.html"
    h.write_text(pagina(consolidado, "Expediente técnico — Equipo 1"), encoding="utf-8")
    final = RAIZ / "00_Expediente_Final"
    final.mkdir(exist_ok=True)
    trabajos.append({"html": str(h), "docx": str(final / "Expediente_Tecnico_Equipo1.docx"),
                     "pdf": str(final / "Expediente_Tecnico_Equipo1.pdf"), "indice": True})

    lista = BUILD / "trabajos.json"
    lista.write_text(json.dumps(trabajos, ensure_ascii=False, indent=1), encoding="utf-8")
    print("3) Exportando a DOCX y PDF con Microsoft Word...")
    r = subprocess.run(["powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File",
                        str(RAIZ / "herramientas" / "exportar_word.ps1"), str(lista)])
    sys.exit(r.returncode)


if __name__ == "__main__":
    main()
