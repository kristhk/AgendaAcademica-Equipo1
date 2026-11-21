# Convierte los HTML generados a DOCX y PDF usando Microsoft Word (COM).
# Uso: powershell -File exportar_word.ps1 <trabajos.json>
param([Parameter(Mandatory = $true)][string]$ListaTrabajos)

$ErrorActionPreference = 'Stop'
$trabajos = Get-Content -Raw -Encoding UTF8 $ListaTrabajos | ConvertFrom-Json

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$word.DisplayAlerts = 0
$errores = 0
try {
    foreach ($t in $trabajos) {
        try {
            $doc = $word.Documents.Open($t.html, $false, $true)   # sin confirmar conversión, solo lectura
            $doc.ActiveWindow.View.Type = 3                      # vista Diseño de impresión

            # Incrustar las imágenes (vienen como vínculos desde el HTML)
            foreach ($s in @($doc.InlineShapes)) {
                if ($null -ne $s.LinkFormat) {
                    $s.LinkFormat.SavePictureWithDocument = $true
                    $s.LinkFormat.BreakLink()
                }
            }

            # Página tamaño Carta con márgenes de 2 cm
            $ps = $doc.PageSetup
            $ps.PaperSize = 2
            $ps.TopMargin = 56.7; $ps.BottomMargin = 56.7; $ps.LeftMargin = 56.7; $ps.RightMargin = 56.7

            # Tablas ajustadas al ancho de la página
            foreach ($tb in @($doc.Tables)) {
                $tb.Range.ParagraphFormat.SpaceBefore = 0
                $tb.Range.ParagraphFormat.SpaceAfter = 0
                $tb.AllowAutoFit = $false               # respeta los anchos proporcionales del HTML
                $tb.PreferredWidthType = 2              # porcentaje
                $tb.PreferredWidth = 100
            }

            # Número de página centrado en el pie
            $null = $doc.Sections.Item(1).Footers.Item(1).PageNumbers.Add(1, $true)

            # Índice automático en el expediente consolidado
            if ($t.indice) {
                $rango = $doc.Content
                $f = $rango.Find
                if ($f.Execute('[[INDICE]]')) {
                    $rango.Text = ''
                    $toc = $doc.TablesOfContents.Add($rango, $true, 1, 2)
                    $toc.Update()
                }
            }

            $doc.SaveAs2([string]$t.docx, 16)                    # wdFormatDocumentDefault (.docx)
            if ($t.indice) { $doc.TablesOfContents.Item(1).Update() }
            $doc.ExportAsFixedFormat([string]$t.pdf, 17)          # wdExportFormatPDF
            $paginas = $doc.ComputeStatistics(2)
            $doc.Close(0)
            Write-Output ("   OK  {0}  ({1} paginas)" -f (Split-Path -Leaf $t.docx), $paginas)
        }
        catch {
            $errores++
            Write-Output ("   ERROR en {0}: {1}" -f $t.html, $_.Exception.Message)
            if ($doc) { try { $doc.Close(0) } catch {} }
        }
    }
}
finally {
    $word.Quit()
    [void][System.Runtime.InteropServices.Marshal]::ReleaseComObject($word)
}
exit $errores
