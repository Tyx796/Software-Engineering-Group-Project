param(
    [string]$TomcatDir = "",
    [string]$ProjectDir = $PSScriptRoot,
    [string]$ContextName = "ta-recruit",
    [string]$DataDir = "",
    [switch]$StopTomcat,
    [switch]$StartTomcat,
    [switch]$SkipBuild
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Resolve-RequiredPath {
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

if ([string]::IsNullOrWhiteSpace($TomcatDir)) {
    if (-not [string]::IsNullOrWhiteSpace($env:TOMCAT_HOME)) {
        $TomcatDir = $env:TOMCAT_HOME
    } elseif (-not [string]::IsNullOrWhiteSpace($env:CATALINA_HOME)) {
        $TomcatDir = $env:CATALINA_HOME
    }
}

if ([string]::IsNullOrWhiteSpace($DataDir)) {
    $DataDir = Join-Path $ProjectDir "data"
}

$ProjectDir = Resolve-RequiredPath -PathValue $ProjectDir -Label "ProjectDir"
$TomcatDir = Resolve-RequiredPath -PathValue $TomcatDir -Label "TomcatDir"

if (-not (Test-Path -LiteralPath $DataDir)) {
    New-Item -ItemType Directory -Path $DataDir | Out-Null
}
$DataDir = Resolve-RequiredPath -PathValue $DataDir -Label "DataDir"

$webappsDir = Join-Path $TomcatDir "webapps"
$binDir = Join-Path $TomcatDir "bin"
$startupScript = Join-Path $binDir "startup.bat"
$shutdownScript = Join-Path $binDir "shutdown.bat"
$targetWar = Join-Path $ProjectDir ("target\{0}.war" -f $ContextName)
$deployWar = Join-Path $webappsDir ("{0}.war" -f $ContextName)
$deployDir = Join-Path $webappsDir $ContextName

if (-not (Test-Path -LiteralPath $webappsDir)) {
    throw "Tomcat webapps directory does not exist: $webappsDir"
}

if ($StopTomcat) {
    if (-not (Test-Path -LiteralPath $shutdownScript)) {
        throw "Tomcat shutdown script not found: $shutdownScript"
    }

    Write-Host "Stopping Tomcat..."
    & $shutdownScript
    Start-Sleep -Seconds 3
}

if (-not $SkipBuild) {
    $maven = Get-Command mvn -ErrorAction SilentlyContinue
    if ($null -eq $maven) {
        throw "Maven is not installed or mvn is not on PATH."
    }

    Write-Host "Building WAR with Maven..."
    Push-Location $ProjectDir
    try {
        & mvn clean package
    } finally {
        Pop-Location
    }
}

if (-not (Test-Path -LiteralPath $targetWar)) {
    throw "Built WAR not found: $targetWar"
}

if (Test-Path -LiteralPath $deployWar) {
    Write-Host "Removing old WAR: $deployWar"
    Remove-Item -LiteralPath $deployWar -Force
}

if (Test-Path -LiteralPath $deployDir) {
    Write-Host "Removing old exploded app directory: $deployDir"
    Remove-Item -LiteralPath $deployDir -Recurse -Force
}

Write-Host "Copying new WAR to Tomcat webapps..."
Copy-Item -LiteralPath $targetWar -Destination $deployWar -Force

if ($StartTomcat) {
    if (-not (Test-Path -LiteralPath $startupScript)) {
        throw "Tomcat startup script not found: $startupScript"
    }

    $env:TARECRUIT_DATA_DIR = $DataDir
    Write-Host "Starting Tomcat with TARECRUIT_DATA_DIR=$DataDir"
    & $startupScript
} else {
    Write-Host ""
    Write-Host "Deployment complete."
    Write-Host "Before starting Tomcat, make sure TARECRUIT_DATA_DIR is set to:"
    Write-Host "  $DataDir"
}

Write-Host "Application path: /$ContextName"
