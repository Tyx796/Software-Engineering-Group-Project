param(
    [string]$TomcatDir = "",
    [string]$ProjectDir = $PSScriptRoot,
    [string]$ContextName = "ta-recruit",
    [string]$DataDir = "",
    [switch]$SkipBuild,
    [switch]$SkipStop,
    [switch]$OpenBrowser,
    [int]$StartTimeoutSeconds = 60
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Resolve-ExistingPath {
    param(
        [Parameter(Mandatory = $true)]
        [string]$PathValue,
        [Parameter(Mandatory = $true)]
        [string]$Label
    )

    if ([string]::IsNullOrWhiteSpace($PathValue)) {
        throw "$Label is required."
    }

    $resolved = Resolve-Path -LiteralPath $PathValue -ErrorAction SilentlyContinue
    if ($null -eq $resolved) {
        throw "$Label does not exist: $PathValue"
    }

    return $resolved.Path
}

function Test-PathSafe {
    param(
        [Parameter(Mandatory = $true)]
        [string]$LiteralPath
    )

    try {
        return Test-Path -LiteralPath $LiteralPath -ErrorAction Stop
    } catch [System.UnauthorizedAccessException] {
        return $false
    } catch [System.Management.Automation.ItemNotFoundException] {
        return $false
    } catch {
        throw
    }
}

function Resolve-PathSafe {
    param(
        [Parameter(Mandatory = $true)]
        [string]$LiteralPath
    )

    try {
        $resolved = Resolve-Path -LiteralPath $LiteralPath -ErrorAction Stop
        return $resolved.Path
    } catch [System.UnauthorizedAccessException] {
        return $null
    } catch [System.Management.Automation.ItemNotFoundException] {
        return $null
    } catch {
        throw
    }
}

function Get-TomcatServiceName {
    param(
        [Parameter(Mandatory = $true)]
        [string]$TomcatRoot
    )

    $binDir = Join-Path $TomcatRoot "bin"
    $serviceExecutables = Get-ChildItem -LiteralPath $binDir -Filter "Tomcat*.exe" -ErrorAction SilentlyContinue |
        Where-Object { $_.BaseName -notlike "*w" } |
        Sort-Object Name

    foreach ($serviceExecutable in $serviceExecutables) {
        $serviceName = $serviceExecutable.BaseName
        $service = Get-Service -Name $serviceName -ErrorAction SilentlyContinue
        if ($null -ne $service) {
            return $serviceName
        }
    }

    return $null
}

function Stop-TomcatInstance {
    param(
        [Parameter(Mandatory = $true)]
        [string]$TomcatRoot,
        [Parameter(Mandatory = $false)]
        [string]$TomcatServiceName
    )

    if (-not [string]::IsNullOrWhiteSpace($TomcatServiceName)) {
        $service = Get-Service -Name $TomcatServiceName -ErrorAction SilentlyContinue
        if ($null -ne $service -and $service.Status -ne [System.ServiceProcess.ServiceControllerStatus]::Stopped) {
            try {
                Stop-Service -Name $TomcatServiceName -Force -ErrorAction Stop
                $service.WaitForStatus([System.ServiceProcess.ServiceControllerStatus]::Stopped, [TimeSpan]::FromSeconds(30))
            } catch {
                throw "Tomcat service '$TomcatServiceName' exists but could not be stopped. Run this PowerShell session as Administrator, or stop the service manually before rerunning run-local.ps1."
            }
        }
        return
    }

    $shutdownScript = Join-Path $TomcatRoot "bin\shutdown.bat"
    & $shutdownScript | Out-Null
    Start-Sleep -Seconds 4
}

function Start-TomcatInstance {
    param(
        [Parameter(Mandatory = $true)]
        [string]$TomcatRoot,
        [Parameter(Mandatory = $false)]
        [string]$TomcatServiceName
    )

    if (-not [string]::IsNullOrWhiteSpace($TomcatServiceName)) {
        try {
            Start-Service -Name $TomcatServiceName -ErrorAction Stop
        } catch {
            throw "Tomcat service '$TomcatServiceName' exists but could not be started. Run this PowerShell session as Administrator, or start the service manually after deployment."
        }
        return
    }

    $startupScript = Join-Path $TomcatRoot "bin\startup.bat"
    & $startupScript | Out-Null
}

function Find-TomcatDirectory {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot
    )

    $candidates = @()

    if (-not [string]::IsNullOrWhiteSpace($env:TOMCAT_HOME)) {
        $candidates += $env:TOMCAT_HOME
    }
    if (-not [string]::IsNullOrWhiteSpace($env:CATALINA_HOME)) {
        $candidates += $env:CATALINA_HOME
    }

    $commonDirectories = @(
        (Join-Path $ProjectRoot "tomcat"),
        "C:\apache-tomcat-10.1",
        "C:\apache-tomcat-10.1.39",
        "C:\Program Files\Apache Software Foundation\Tomcat 10.1",
        "C:\Program Files\Apache Software Foundation\Tomcat 11.0",
        "C:\Tools\apache-tomcat-10.1"
    )

    foreach ($pattern in @("C:\apache-tomcat-10.1*", "C:\Tools\apache-tomcat-*")) {
        $candidates += Get-ChildItem -Path $pattern -Directory -ErrorAction SilentlyContinue |
            Sort-Object Name -Descending |
            Select-Object -ExpandProperty FullName
    }

    $candidates += $commonDirectories

    foreach ($candidate in $candidates | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -Unique) {
        $binStartup = Join-Path $candidate "bin\startup.bat"
        $webappsDir = Join-Path $candidate "webapps"
        if ((Test-PathSafe -LiteralPath $binStartup) -and (Test-PathSafe -LiteralPath $webappsDir)) {
            $resolvedCandidate = Resolve-PathSafe -LiteralPath $candidate
            if ($null -ne $resolvedCandidate) {
                return $resolvedCandidate
            }
        }
    }

    throw "Could not locate a Tomcat installation automatically. Pass -TomcatDir or set TOMCAT_HOME."
}

function Get-TomcatPort {
    param(
        [Parameter(Mandatory = $true)]
        [string]$TomcatRoot
    )

    $serverXmlPath = Join-Path $TomcatRoot "conf\server.xml"
    if (-not (Test-PathSafe -LiteralPath $serverXmlPath)) {
        return 8080
    }

    [xml]$serverXml = Get-Content -Raw $serverXmlPath
    foreach ($connector in $serverXml.Server.Service.Connector) {
        $port = 0
        if ([int]::TryParse([string]$connector.port, [ref]$port) -and $port -gt 0) {
            return $port
        }
    }

    return 8080
}

function Set-TarecruitDataDir {
    param(
        [Parameter(Mandatory = $true)]
        [string]$TomcatRoot,
        [Parameter(Mandatory = $true)]
        [string]$RuntimeDataDir
    )

    $setenvPath = Join-Path $TomcatRoot "bin\setenv.bat"
    $managedLine = "set `"TARECRUIT_DATA_DIR=$RuntimeDataDir`""
    $marker = "REM Managed by run-local.ps1"
    $linePattern = '^\s*set\s+"?TARECRUIT_DATA_DIR='

    $lines = @()
    if (Test-PathSafe -LiteralPath $setenvPath) {
        $lines = Get-Content -LiteralPath $setenvPath
    }

    $updated = $false
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match $linePattern) {
            $lines[$i] = $managedLine
            $updated = $true
        }
    }

    if (-not $updated) {
        if ($lines.Count -gt 0 -and $lines[-1] -ne "") {
            $lines += ""
        }
        $lines += $marker
        $lines += $managedLine
    }

    Set-Content -LiteralPath $setenvPath -Value $lines -Encoding ASCII
}

function Wait-ForUrl {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Url,
        [Parameter(Mandatory = $true)]
        [int]$TimeoutSeconds
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5 | Out-Null
            return $true
        } catch {
            Start-Sleep -Seconds 2
        }
    }

    return $false
}

$ProjectDir = Resolve-ExistingPath -PathValue $ProjectDir -Label "ProjectDir"

if ([string]::IsNullOrWhiteSpace($TomcatDir)) {
    $TomcatDir = Find-TomcatDirectory -ProjectRoot $ProjectDir
} else {
    $TomcatDir = Resolve-ExistingPath -PathValue $TomcatDir -Label "TomcatDir"
}

if ([string]::IsNullOrWhiteSpace($DataDir)) {
    $DataDir = Join-Path $ProjectDir "data"
}
if (-not (Test-PathSafe -LiteralPath $DataDir)) {
    New-Item -ItemType Directory -Path $DataDir | Out-Null
}
$DataDir = Resolve-ExistingPath -PathValue $DataDir -Label "DataDir"

$binDir = Join-Path $TomcatDir "bin"
$webappsDir = Join-Path $TomcatDir "webapps"
$startupScript = Join-Path $binDir "startup.bat"
$shutdownScript = Join-Path $binDir "shutdown.bat"
$targetWar = Join-Path $ProjectDir "target\$ContextName.war"
$deployWar = Join-Path $webappsDir "$ContextName.war"
$deployDir = Join-Path $webappsDir $ContextName
$port = Get-TomcatPort -TomcatRoot $TomcatDir
$tomcatServiceName = Get-TomcatServiceName -TomcatRoot $TomcatDir
$contextPath = if ($ContextName -eq "ROOT") { "" } else { "/$ContextName" }
$appUrl = "http://localhost:$port$contextPath/login"

if (-not (Test-PathSafe -LiteralPath $startupScript)) {
    throw "Tomcat startup script not found: $startupScript"
}
if (-not (Test-PathSafe -LiteralPath $shutdownScript)) {
    throw "Tomcat shutdown script not found: $shutdownScript"
}
if (-not (Test-PathSafe -LiteralPath $webappsDir)) {
    throw "Tomcat webapps directory not found: $webappsDir"
}

$maven = Get-Command mvn -ErrorAction SilentlyContinue
if ($null -eq $maven) {
    throw "Maven is not installed or mvn is not on PATH."
}

if (-not $SkipBuild) {
    Write-Host "Building WAR with Maven..."
    Push-Location $ProjectDir
    try {
        & mvn clean package
    } finally {
        Pop-Location
    }
}

if (-not (Test-PathSafe -LiteralPath $targetWar)) {
    throw "Built WAR not found: $targetWar"
}

if (-not $SkipStop) {
    Write-Host "Stopping Tomcat..."
    Stop-TomcatInstance -TomcatRoot $TomcatDir -TomcatServiceName $tomcatServiceName
}

Write-Host "Updating Tomcat runtime data directory..."
Set-TarecruitDataDir -TomcatRoot $TomcatDir -RuntimeDataDir $DataDir

if (Test-PathSafe -LiteralPath $deployWar) {
    Write-Host "Removing old WAR..."
    Remove-Item -LiteralPath $deployWar -Force
}
if (Test-PathSafe -LiteralPath $deployDir) {
    Write-Host "Removing old exploded app..."
    Remove-Item -LiteralPath $deployDir -Recurse -Force
}

Write-Host "Copying WAR to Tomcat webapps..."
Copy-Item -LiteralPath $targetWar -Destination $deployWar -Force

Write-Host "Starting Tomcat..."
Start-TomcatInstance -TomcatRoot $TomcatDir -TomcatServiceName $tomcatServiceName

Write-Host "Waiting for application to become available at $appUrl ..."
$started = Wait-ForUrl -Url $appUrl -TimeoutSeconds $StartTimeoutSeconds
if (-not $started) {
    throw "Tomcat started but the application did not become reachable within $StartTimeoutSeconds seconds. Check Tomcat logs in $TomcatDir\logs."
}

if ($OpenBrowser) {
    Start-Process $appUrl
}

Write-Host ""
Write-Host "Local deployment is ready."
Write-Host "Application URL: $appUrl"
Write-Host "Runtime data: $DataDir"
Write-Host "Tomcat: $TomcatDir"
Write-Host ""
Write-Host "Demo login:"
Write-Host "  Applicant: applicant1@example.com"
Write-Host "  Organiser: organiser1@example.com"
Write-Host "  Admin: 3423432@qq.com"
Write-Host "  Password: password123"
