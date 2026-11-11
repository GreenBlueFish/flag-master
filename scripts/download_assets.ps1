<#
    download_assets.ps1
    Fetches every flag PNG (countries, US states/DC/territories, Canadian
    provinces/territories) plus the offline world map into
    app/src/main/assets. Run once at dev time; the resulting PNGs are committed.

    Sources:
      Country + US  : https://flagcdn.com/w320/<code>.png
      Canada        : https://commons.wikimedia.org/wiki/Special:FilePath/<Flag_of_Name>.svg?width=320
      World map     : https://commons.wikimedia.org/wiki/Special:FilePath/BlankMap-World.svg?width=2048

    Every file is verified non-empty and starts with the PNG magic bytes
    0x89 0x50 0x4E 0x47. Any failure is deleted and reported. Windows PowerShell.
#>

$ProgressPreference = 'SilentlyContinue'
$ErrorActionPreference = 'Stop'

$root     = Split-Path -Parent $PSScriptRoot
$flagsDir = Join-Path $root 'app\src\main\assets\flags'
$assetsDir = Join-Path $root 'app\src\main\assets'
New-Item -ItemType Directory -Force -Path $flagsDir | Out-Null

# --- Country alpha-2 keys (199, incl. tw xk va ps) ---
$countries = @(
  'af','al','dz','ad','ao','ag','ar','am','au','at','az','bs','bh','bd','bb','by','be','bz','bj','bt',
  'bo','ba','bw','br','bn','bg','bf','bi','cv','kh','cm','ca','cf','td','cl','cn','co','km','cg','cd',
  'cr','ci','hr','cu','cy','cz','dk','dj','dm','do','ec','eg','sv','gq','er','ee','sz','et','fj','fi',
  'fr','ga','gm','ge','de','gh','gr','gd','gt','gn','gw','gy','ht','hn','hu','is','in','id','ir','iq',
  'ie','il','it','jm','jp','jo','kz','ke','ki','kp','kr','kw','kg','la','lv','lb','ls','lr','ly','li',
  'lt','lu','mg','mw','my','mv','ml','mt','mh','mr','mu','mx','fm','md','mc','mn','me','ma','mz','mm',
  'na','nr','np','nl','nz','ni','ne','ng','mk','no','om','pk','pw','pa','pg','py','pe','ph','pl','pt',
  'qa','ro','ru','rw','kn','lc','vc','ws','sm','st','sa','sn','rs','sc','sl','sg','sk','si','sb','so',
  'za','ss','es','lk','sd','sr','se','ch','sy','tj','tz','th','tl','tg','to','tt','tn','tr','tm','tv',
  'ug','ua','ae','gb','us','uy','uz','vu','ve','vn','ye','zm','zw','tw','xk','va','ps'
)

# --- US keys (56): 50 states + us-dc + territories pr gu vi as mp ---
$usStates = @(
  'us-al','us-ak','us-az','us-ar','us-ca','us-co','us-ct','us-de','us-fl','us-ga','us-hi','us-id',
  'us-il','us-in','us-ia','us-ks','us-ky','us-la','us-me','us-md','us-ma','us-mi','us-mn','us-ms',
  'us-mo','us-mt','us-ne','us-nv','us-nh','us-nj','us-nm','us-ny','us-nc','us-nd','us-oh','us-ok',
  'us-or','us-pa','us-ri','us-sc','us-sd','us-tn','us-tx','us-ut','us-vt','us-va','us-wa','us-wv',
  'us-wi','us-wy','us-dc'
)
# US territories live under their own country-code keys on flagcdn
$usTerritories = @('pr','gu','vi','as','mp')

# --- Canada keys -> Wikimedia filename ---
$canada = [ordered]@{
  'ca-ab' = 'Flag_of_Alberta'
  'ca-bc' = 'Flag_of_British_Columbia'
  'ca-mb' = 'Flag_of_Manitoba'
  'ca-nb' = 'Flag_of_New_Brunswick'
  'ca-nl' = 'Flag_of_Newfoundland_and_Labrador'
  'ca-ns' = 'Flag_of_Nova_Scotia'
  'ca-on' = 'Flag_of_Ontario'
  'ca-pe' = 'Flag_of_Prince_Edward_Island'
  'ca-qc' = 'Flag_of_Quebec'
  'ca-sk' = 'Flag_of_Saskatchewan'
  'ca-yt' = 'Flag_of_Yukon'
  'ca-nt' = 'Flag_of_the_Northwest_Territories'
  'ca-nu' = 'Flag_of_Nunavut'
}

$succeeded = 0
$failed    = @()

function Test-PngMagic {
    param([string]$Path)
    if (-not (Test-Path $Path)) { return $false }
    $fi = Get-Item $Path
    if ($fi.Length -lt 4) { return $false }
    $bytes = [System.IO.File]::ReadAllBytes($Path)
    return ($bytes[0] -eq 0x89 -and $bytes[1] -eq 0x50 -and $bytes[2] -eq 0x4E -and $bytes[3] -eq 0x47)
}

function Save-Flag {
    param([string]$Key, [string]$Url, [string]$FallbackUrl)
    $dest = Join-Path $flagsDir "$Key.png"
    $urls = @($Url)
    if ($FallbackUrl) { $urls += $FallbackUrl }
    foreach ($u in $urls) {
        try {
            Invoke-WebRequest -Uri $u -OutFile $dest -UseBasicParsing -TimeoutSec 60
            if (Test-PngMagic -Path $dest) {
                $script:succeeded++
                Write-Host "ok   $Key  <- $u"
                return
            } else {
                Write-Host "bad  $Key  (not PNG) <- $u"
                Remove-Item $dest -Force -ErrorAction SilentlyContinue
            }
        } catch {
            Write-Host "fail $Key  <- $u  ($($_.Exception.Message))"
            Remove-Item $dest -Force -ErrorAction SilentlyContinue
        }
    }
    $script:failed += $Key
}

# Country name map for Wikimedia fallback on flagcdn 404s (filled lazily only if needed)
$countryFallbackNames = @{
  'tw' = 'Flag_of_the_Republic_of_China'
  'xk' = 'Flag_of_Kosovo'
  'va' = 'Flag_of_Vatican_City'
  'ps' = 'Flag_of_Palestine'
}

# Country flags (flagcdn), with Wikimedia Special:FilePath fallback
foreach ($c in $countries) {
    $primary  = "https://flagcdn.com/w320/$c.png"
    $fallback = $null
    if ($countryFallbackNames.ContainsKey($c)) {
        $fallback = "https://commons.wikimedia.org/wiki/Special:FilePath/$($countryFallbackNames[$c]).svg?width=320"
    }
    Save-Flag -Key $c -Url $primary -FallbackUrl $fallback
}

# US states + DC (flagcdn us-<postal>); us-dc 404s on flagcdn -> Wikimedia fallback
$usFallbackNames = @{
  'us-dc' = 'Flag_of_Washington,_D.C.'
}
foreach ($s in $usStates) {
    $fallback = $null
    if ($usFallbackNames.ContainsKey($s)) {
        $fallback = "https://commons.wikimedia.org/wiki/Special:FilePath/$($usFallbackNames[$s]).svg?width=320"
    }
    Save-Flag -Key $s -Url "https://flagcdn.com/w320/$s.png" -FallbackUrl $fallback
}

# US territories (flagcdn country-code form) stored under own keys
foreach ($t in $usTerritories) {
    Save-Flag -Key $t -Url "https://flagcdn.com/w320/$t.png"
}

# Canada (Wikimedia Special:FilePath -> rasterized PNG)
foreach ($key in $canada.Keys) {
    $name = $canada[$key]
    Save-Flag -Key $key -Url "https://commons.wikimedia.org/wiki/Special:FilePath/$name.svg?width=320"
}

# World map
$mapDest = Join-Path $assetsDir 'world_map.png'
try {
    Invoke-WebRequest -Uri 'https://commons.wikimedia.org/wiki/Special:FilePath/BlankMap-World.svg?width=2048' -OutFile $mapDest -UseBasicParsing -TimeoutSec 120
    if (Test-PngMagic -Path $mapDest) {
        Write-Host "ok   world_map.png ($((Get-Item $mapDest).Length) bytes)"
    } else {
        Write-Host "bad  world_map.png (not PNG)"
        Remove-Item $mapDest -Force -ErrorAction SilentlyContinue
        $failed += 'world_map'
    }
} catch {
    Write-Host "fail world_map.png ($($_.Exception.Message))"
    $failed += 'world_map'
}

Write-Host ""
Write-Host "==================================="
Write-Host "Succeeded (flags): $succeeded"
Write-Host "Failed:            $($failed.Count)"
if ($failed.Count -gt 0) {
    Write-Host "Failed list: $($failed -join ', ')"
}
Write-Host "==================================="
