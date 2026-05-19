$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

$deploy = Join-Path $root 'deploy.bat'
$src = Join-Path $root 'src'
$pom = Join-Path $root 'pom.xml'

if (-not (Test-Path -LiteralPath $deploy)) {
    throw "File deploy.bat tidak ditemukan: $deploy"
}

if (-not (Test-Path -LiteralPath $src)) {
    throw "Folder src tidak ditemukan: $src"
}

if (-not (Test-Path -LiteralPath $pom)) {
    throw "File pom.xml tidak ditemukan: $pom"
}

$script:pending = $false
$script:running = $false
$script:lastChange = $null

function Invoke-Deploy {
    if ($script:running) {
        return
    }

    $script:running = $true

    try {
        Write-Host ''
        Write-Host '[INFO] Perubahan terdeteksi. Menjalankan deploy.bat...'

        cmd.exe /c "`"$deploy`""

        if ($LASTEXITCODE -ne 0) {
            Write-Host '[ERROR] deploy.bat gagal.' -ForegroundColor Red
        } else {
            Write-Host '[OK] deploy.bat selesai.' -ForegroundColor Green
        }
    } catch {
        Write-Host "[ERROR] Gagal menjalankan deploy.bat: $($_.Exception.Message)" -ForegroundColor Red
    } finally {
        $script:running = $false
    }
}

$srcWatcher = New-Object IO.FileSystemWatcher $src, '*.*'
$srcWatcher.IncludeSubdirectories = $true
$srcWatcher.EnableRaisingEvents = $true

$pomWatcher = New-Object IO.FileSystemWatcher $root, 'pom.xml'
$pomWatcher.IncludeSubdirectories = $false
$pomWatcher.EnableRaisingEvents = $true

$registrations = @()

try {
    $registrations += Register-ObjectEvent $srcWatcher Changed -SourceIdentifier 'src.changed'
    $registrations += Register-ObjectEvent $srcWatcher Created -SourceIdentifier 'src.created'
    $registrations += Register-ObjectEvent $srcWatcher Deleted -SourceIdentifier 'src.deleted'
    $registrations += Register-ObjectEvent $srcWatcher Renamed -SourceIdentifier 'src.renamed'
    $registrations += Register-ObjectEvent $pomWatcher Changed -SourceIdentifier 'pom.changed'
    $registrations += Register-ObjectEvent $pomWatcher Created -SourceIdentifier 'pom.created'
    $registrations += Register-ObjectEvent $pomWatcher Deleted -SourceIdentifier 'pom.deleted'
    $registrations += Register-ObjectEvent $pomWatcher Renamed -SourceIdentifier 'pom.renamed'

    Write-Host '[INFO] Watch aktif: src dan pom.xml'
    Write-Host '[INFO] Tekan CTRL+C untuk berhenti.'

    while ($true) {
        $event = Wait-Event -Timeout 1

        if ($null -ne $event) {
            $script:pending = $true
            $script:lastChange = Get-Date

            Get-Event | Remove-Event
        }

        if ($script:pending -and -not $script:running) {
            $elapsed = ((Get-Date) - $script:lastChange).TotalSeconds

            if ($elapsed -ge 3) {
                $script:pending = $false
                Invoke-Deploy
            }
        }
    }
} finally {
    foreach ($registration in $registrations) {
        Unregister-Event -SubscriptionId $registration.Id -ErrorAction SilentlyContinue
    }

    $srcWatcher.Dispose()
    $pomWatcher.Dispose()
}
