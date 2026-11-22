# Retire Sora et Spline Sans Mono de la session (annule AddFontResource) et vérifie qu'elles ne sont plus installées.
$sp = $PSScriptRoot
Add-Type -TypeDefinition @"
using System; using System.Runtime.InteropServices;
public class FontUnreg {
  [DllImport("gdi32.dll", CharSet=CharSet.Unicode)] public static extern bool RemoveFontResource(string f);
  [DllImport("user32.dll")] public static extern bool PostMessage(IntPtr h, uint m, IntPtr w, IntPtr l);
}
"@
$fonts = Get-ChildItem "$sp\fonts" -Filter "*-*.ttf" | Where-Object { $_.Name -notmatch "var" }
foreach ($f in $fonts) {
  $n = 0
  while ([FontUnreg]::RemoveFontResource($f.FullName) -and $n -lt 10) { $n++ }
  "retiré : " + $f.Name + " (x$n)"
}
[void][FontUnreg]::PostMessage([IntPtr]0xFFFF, 0x1D, [IntPtr]::Zero, [IntPtr]::Zero)
Start-Sleep -Seconds 2
Add-Type -AssemblyName System.Drawing
$fam = (New-Object System.Drawing.Text.InstalledFontCollection).Families | ForEach-Object { $_.Name }
"Sora installée : " + ($fam -contains "Sora") + " ; Spline Sans Mono installée : " + ($fam -contains "Spline Sans Mono")
