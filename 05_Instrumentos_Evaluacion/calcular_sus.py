"""Calcula el puntaje SUS a partir de las respuestas REALES de los participantes.

Uso:
    python calcular_sus.py plantilla_respuestas_sus.csv

Formato del CSV (con encabezado): participante,p1,p2,...,p10
Cada respuesta debe ser un entero de 1 a 5 (escala Likert de la guía 5-3).
Las fórmulas y la tabla de interpretación son las de "5-3 guia-evaluacion-sus.docx".
"""
import csv
import statistics
import sys

GRADOS = [  # (mínimo, grado, adjetivo, aceptabilidad)
    (84.1, "A+", "Lo mejor imaginable", "Aceptable"),
    (80.8, "A", "Excelente", "Aceptable"),
    (78.9, "A-", "Muy bueno", "Aceptable"),
    (74.1, "B / B+", "Muy bueno", "Aceptable"),
    (65.0, "C / B-", "Bueno", "Marginal / Aceptable"),
    (51.7, "D / C-", "Bueno / Pobre", "Marginal"),
    (0.0, "F", "Pobre / Insatisfactorio", "No aceptable"),
]


def contribuciones(respuestas):
    """Convierte las 10 respuestas (1-5) en contribuciones de 0 a 4."""
    if len(respuestas) != 10 or any(r not in (1, 2, 3, 4, 5) for r in respuestas):
        raise ValueError("Se requieren exactamente 10 respuestas enteras entre 1 y 5")
    # Ítems impares (posición 0, 2, 4...) positivos: r - 1; pares negativos: 5 - r
    return [(r - 1) if i % 2 == 0 else (5 - r) for i, r in enumerate(respuestas)]


def puntajes(respuestas):
    c = contribuciones(respuestas)
    sus = sum(c) * 2.5
    aprendizaje = (c[3] + c[9]) * 12.5               # ítems 4 y 10
    usabilidad = (sum(c) - c[3] - c[9]) * 3.125      # ítems 1,2,3,5,6,7,8,9
    return sus, usabilidad, aprendizaje


def interpretar(valor):
    for minimo, grado, adjetivo, acept in GRADOS:
        if valor >= minimo:
            return grado, adjetivo, acept
    return GRADOS[-1][1:]


def main(ruta):
    filas = []
    with open(ruta, newline="", encoding="utf-8") as f:
        for fila in csv.DictReader(f):
            if not fila.get("participante", "").strip():
                continue
            resp = [int(fila[f"p{i}"]) for i in range(1, 11)]
            filas.append((fila["participante"].strip(), *puntajes(resp)))

    if not filas:
        print("El archivo no contiene respuestas. Captura primero las respuestas reales de los participantes.")
        return

    print(f"{'Participante':<15}{'SUS':>8}{'Usabilidad':>12}{'Aprendizaje':>13}")
    for p, s, u, a in filas:
        print(f"{p:<15}{s:>8.1f}{u:>12.1f}{a:>13.1f}")

    sus = [f[1] for f in filas]
    media = statistics.mean(sus)
    grado, adjetivo, acept = interpretar(media)
    print("-" * 48)
    print(f"Participantes: {len(sus)}")
    print(f"SUS promedio: {media:.1f}  (desv. estándar: {statistics.stdev(sus):.1f})" if len(sus) > 1
          else f"SUS promedio: {media:.1f}")
    print(f"Usabilidad promedio: {statistics.mean(f[2] for f in filas):.1f}")
    print(f"Aprendizaje promedio: {statistics.mean(f[3] for f in filas):.1f}")
    print(f"Grado: {grado} · Adjetivo: {adjetivo} · Aceptabilidad: {acept}")
    print("Referencia: promedio histórico = 68 (un puntaje SUS no es un porcentaje).")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    main(sys.argv[1])
