param([string]$Raw, [string]$Final, [string]$Pdf)
# Enregistre le .pptx via PowerPoint en incorporant Sora et Spline Sans Mono, puis exporte un PDF.
$ErrorActionPreference = "Stop"
$sp = $PSScriptRoot
Add-Type -TypeDefinition @"
using System; using System.Runtime.InteropServices;
public class FontReg2 {
  [DllImport("gdi32.dll", CharSet=CharSet.Unicode)] public static extern int AddFontResource(string f);
  [DllImport("user32.dll")] public static extern bool PostMessage(IntPtr h, uint m, IntPtr w, IntPtr l);
}
"@
$fonts = Get-ChildItem "$sp\fonts" -Filter "*-*.ttf" | Where-Object { $_.Name -notmatch "var" }
foreach ($f in $fonts) { [void][FontReg2]::AddFontResource($f.FullName) }
[void][FontReg2]::PostMessage([IntPtr]0xFFFF, 0x1D, [IntPtr]::Zero, [IntPtr]::Zero)
Start-Sleep -Seconds 2

$before = @(Get-Process POWERPNT -ErrorAction SilentlyContinue).Count
"instances PowerPoint avant : $before"
foreach ($p in @($Final, $Pdf)) { if (Test-Path $p) { Remove-Item $p -Force } }
$ppt = New-Object -ComObject PowerPoint.Application
try {
  $pres = $ppt.Presentations.Open($Raw, 0, 0, 0)      # lecture/écriture, sans fenêtre
  $pres.SaveAs($Final, 24, -1)                         # .pptx + incorporation des polices
  "enregistré : $Final  (" + [math]::Round((Get-Item $Final).Length / 1KB) + " Ko)"
  try { $pres.SaveCopyAs($Pdf, 32); "PDF exporté : $Pdf  (" + [math]::Round((Get-Item $Pdf).Length / 1KB) + " Ko)" }
  catch { "SaveCopyAs PDF a échoué : " + $_.Exception.Message }
  $pres.Close()
} finally {
  if ($before -eq 0) { $ppt.Quit(); [void][System.Runtime.InteropServices.Marshal]::ReleaseComObject($ppt) }
}
