param([string]$Pptx, [string]$OutDir, [switch]$NoRegister)
# Rend le .pptx en PNG avec PowerPoint (COM) et signale les textes qui débordent.
$ErrorActionPreference = "Stop"
$sp = $PSScriptRoot
Add-Type -TypeDefinition @"
using System; using System.Runtime.InteropServices;
public class FontReg {
  [DllImport("gdi32.dll", CharSet=CharSet.Unicode)] public static extern int AddFontResource(string f);
  [DllImport("user32.dll")] public static extern bool PostMessage(IntPtr h, uint m, IntPtr w, IntPtr l);
}
"@
$fonts = Get-ChildItem "$sp\fonts" -Filter "*-*.ttf" | Where-Object { $_.Name -notmatch "var" }
if (-not $NoRegister) {
  foreach ($f in $fonts) { $n = [FontReg]::AddFontResource($f.FullName); "police enregistrée : " + $f.Name + " -> " + $n }
  [void][FontReg]::PostMessage([IntPtr]0xFFFF, 0x1D, [IntPtr]::Zero, [IntPtr]::Zero)
  Start-Sleep -Seconds 2
} else { "polices NON enregistrées (test de l'incorporation)" }

$before = @(Get-Process POWERPNT -ErrorAction SilentlyContinue).Count
"instances PowerPoint avant : $before"
if (Test-Path $OutDir) { Remove-Item "$OutDir\*" -Recurse -Force }
New-Item -ItemType Directory -Force $OutDir | Out-Null
$ppt = New-Object -ComObject PowerPoint.Application
try {
  $pres = $ppt.Presentations.Open($Pptx, -1, 0, 0)
  "ouvert : " + $pres.Slides.Count + " diapositives, " + [math]::Round($pres.PageSetup.SlideWidth,1) + " x " + [math]::Round($pres.PageSetup.SlideHeight,1) + " pt"
  $pres.Export($OutDir, "PNG", 1600, 900)
  "--- contrôle des débordements (texte plus haut que sa forme, ou forme hors de la page) ---"
  $problemes = 0
  foreach ($sl in $pres.Slides) {
    foreach ($sh in $sl.Shapes) {
      if ($sh.HasTextFrame -and $sh.TextFrame2.HasText) {
        $bh = $sh.TextFrame2.TextRange.BoundHeight
        $bw = $sh.TextFrame2.TextRange.BoundWidth
        if ($bh -gt $sh.Height + 1.5) {
          $problemes++
          "  DEBORDE diapo {0} [{1}] texte {2:N1} pt > forme {3:N1} pt : {4}" -f $sl.SlideIndex, $sh.Name, $bh, $sh.Height, $sh.TextFrame2.TextRange.Text.Substring(0, [math]::Min(50, $sh.TextFrame2.TextRange.Text.Length))
        }
        if ($bw -gt $sh.Width + 1.5) {
          $problemes++
          "  TROP LARGE diapo {0} [{1}] texte {2:N1} pt > forme {3:N1} pt" -f $sl.SlideIndex, $sh.Name, $bw, $sh.Width
        }
      }
      if (($sh.Left -lt -1) -or ($sh.Top -lt -1) -or ($sh.Left + $sh.Width -gt $pres.PageSetup.SlideWidth + 1) -or ($sh.Top + $sh.Height -gt $pres.PageSetup.SlideHeight + 1)) {
        $problemes++
        "  HORS PAGE diapo {0} [{1}]" -f $sl.SlideIndex, $sh.Name
      }
    }
  }
  "problèmes détectés : $problemes"
  $pres.Close()
} finally {
  if ($before -eq 0) { $ppt.Quit(); [void][System.Runtime.InteropServices.Marshal]::ReleaseComObject($ppt) }
}
"PNG : " + @(Get-ChildItem $OutDir -Filter *.PNG).Count
