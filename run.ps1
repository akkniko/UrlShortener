$ErrorActionPreference = 'Stop'

if (Test-Path '.env') {
    Get-Content '.env' | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
            $parts = $line -split '=', 2
            [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), 'Process')
        }
    }
}

docker compose up -d --wait
if ($LASTEXITCODE -ne 0) { throw 'Could not start PostgreSQL with Docker Compose.' }

.\mvnw.cmd clean package
if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }

java -jar target/url-shortener.jar
