[CmdletBinding()]
param(
    [string]$ResourceRoot
)

$ErrorActionPreference = 'Stop'
if ([string]::IsNullOrWhiteSpace($ResourceRoot)) {
    $ResourceRoot = Join-Path $PSScriptRoot '..\app\src\main\res'
}
$ResourceRoot = [System.IO.Path]::GetFullPath($ResourceRoot)
$localeConfigPath = Join-Path $ResourceRoot 'xml\locales_config.xml'
$issues = [System.Collections.Generic.List[string]]::new()

function Get-FormatSignature {
    param([string]$Text)

    $pattern = '%((?<index>[0-9]+)\$)?[-#+ 0,(<]*[0-9]*(\.[0-9]+)?(?<conversion>[tT][a-zA-Z]|[a-zA-Z])'
    $position = 0
    $signature = foreach ($match in [regex]::Matches($Text, $pattern)) {
        $position++
        $index = $match.Groups['index'].Value
        if ([string]::IsNullOrEmpty($index)) {
            $index = $position.ToString()
        }

        '{0}:{1}' -f $index, $match.Groups['conversion'].Value.ToLowerInvariant()
    }

    return (($signature | Sort-Object) -join ',')
}

function Read-ResourceDirectory {
    param(
        [string]$DirectoryPath,
        [string]$LocaleName
    )

    $resources = @{}
    $duplicates = [System.Collections.Generic.List[string]]::new()

    foreach ($file in (Get-ChildItem -LiteralPath $DirectoryPath -Filter 'strings*.xml' -File | Sort-Object Name)) {
        try {
            [xml]$document = Get-Content -LiteralPath $file.FullName -Raw
        }
        catch {
            $issues.Add("[$LocaleName] Invalid XML in $($file.Name): $($_.Exception.Message)")
            continue
        }

        foreach ($node in $document.SelectNodes('/resources/*[@name]')) {
            $name = $node.GetAttribute('name')
            if ($resources.ContainsKey($name)) {
                $duplicates.Add($name)
                continue
            }

            $arrayItems = @($node.SelectNodes('./item'))
            $pluralQuantities = @(
                $arrayItems |
                    ForEach-Object { $_.GetAttribute('quantity') } |
                    Where-Object { -not [string]::IsNullOrEmpty($_) }
            )
            $itemFormatSignatures = @(
                $arrayItems | ForEach-Object { Get-FormatSignature -Text $_.InnerText }
            )
            $pluralFormatSignatures = @{}
            foreach ($item in $arrayItems) {
                $quantity = $item.GetAttribute('quantity')
                if (-not [string]::IsNullOrEmpty($quantity)) {
                    $pluralFormatSignatures[$quantity] = Get-FormatSignature -Text $item.InnerText
                }
            }

            $resources[$name] = [pscustomobject]@{
                Name                   = $name
                Type                   = $node.LocalName
                File                   = $file.Name
                Translatable           = ($node.GetAttribute('translatable') -ne 'false')
                FormatSignature        = Get-FormatSignature -Text $node.InnerText
                ArrayItemCount         = $arrayItems.Count
                ItemFormatSignatures   = $itemFormatSignatures
                PluralQuantities       = $pluralQuantities
                PluralFormatSignatures = $pluralFormatSignatures
            }
        }
    }

    if ($duplicates.Count -gt 0) {
        $names = ($duplicates | Sort-Object -Unique) -join ', '
        $issues.Add("[$LocaleName] Duplicate resource keys: $names")
    }

    return $resources
}

function Get-LocaleDirectoryCandidates {
    param([string]$LocaleTag)

    if ($LocaleTag -eq 'en') {
        return @('values')
    }

    $parts = @($LocaleTag -split '-')
    if ($parts.Count -eq 1) {
        return @("values-$LocaleTag")
    }

    $candidates = [System.Collections.Generic.List[string]]::new()
    $candidates.Add('values-b+' + ($parts -join '+'))
    if ($parts.Count -eq 2 -and $parts[1] -match '^[A-Za-z]{2}$') {
        $candidates.Add("values-$($parts[0])-r$($parts[1].ToUpperInvariant())")
    }

    return $candidates.ToArray()
}

if (-not (Test-Path -LiteralPath $localeConfigPath -PathType Leaf)) {
    throw "Locale config not found: $localeConfigPath"
}

try {
    [xml]$localeConfig = Get-Content -LiteralPath $localeConfigPath -Raw
}
catch {
    throw "Invalid locale config XML: $($_.Exception.Message)"
}

$androidNamespace = 'http://schemas.android.com/apk/res/android'
$localeTags = @(
    $localeConfig.SelectNodes('//*[local-name()="locale"]') |
        ForEach-Object { $_.GetAttribute('name', $androidNamespace) } |
        Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
)

if ($localeTags -notcontains 'en') {
    $issues.Add('[config] locales_config.xml must include the source locale en')
}
$duplicateLocaleTags = @($localeTags | Group-Object | Where-Object Count -gt 1 | ForEach-Object Name)
if ($duplicateLocaleTags.Count -gt 0) {
    $issues.Add("[config] Duplicate locale tags: $($duplicateLocaleTags -join ', ')")
}

$sourcePath = Join-Path $ResourceRoot 'values'
$source = Read-ResourceDirectory -DirectoryPath $sourcePath -LocaleName 'en'
$sourceTranslatableCount = @($source.Values | Where-Object Translatable).Count
$knownLocaleDirectories = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$rows = [System.Collections.Generic.List[object]]::new()

foreach ($localeTag in $localeTags) {
    $candidates = @(Get-LocaleDirectoryCandidates -LocaleTag $localeTag)
    $directoryName = $candidates | Where-Object {
        Test-Path -LiteralPath (Join-Path $ResourceRoot $_) -PathType Container
    } | Select-Object -First 1

    if ([string]::IsNullOrEmpty($directoryName)) {
        $issues.Add("[$localeTag] Locale directory not found; expected one of: $($candidates -join ', ')")
        continue
    }

    [void]$knownLocaleDirectories.Add($directoryName)
    if ($localeTag -eq 'en') {
        continue
    }

    $localized = Read-ResourceDirectory -DirectoryPath (Join-Path $ResourceRoot $directoryName) -LocaleName $localeTag
    $missing = @($source.Keys | Where-Object {
        $source[$_].Translatable -and -not $localized.ContainsKey($_)
    } | Sort-Object)
    $extra = @($localized.Keys | Where-Object { -not $source.ContainsKey($_) } | Sort-Object)
    $wrongType = [System.Collections.Generic.List[string]]::new()
    $wrongFormat = [System.Collections.Generic.List[string]]::new()
    $wrongArraySize = [System.Collections.Generic.List[string]]::new()
    $invalidPlurals = [System.Collections.Generic.List[string]]::new()
    $wrongFile = [System.Collections.Generic.List[string]]::new()

    foreach ($name in $localized.Keys) {
        if (-not $source.ContainsKey($name)) {
            continue
        }

        $expected = $source[$name]
        $actual = $localized[$name]
        if ($expected.Type -ne $actual.Type) {
            $wrongType.Add("$name ($($actual.Type) -> $($expected.Type))")
        }
        if ($expected.Type -eq 'string' -and $actual.Type -eq 'string' -and
            $expected.FormatSignature -ne $actual.FormatSignature) {
            $wrongFormat.Add("$name ($($actual.FormatSignature) -> $($expected.FormatSignature))")
        }
        if ($expected.Type -eq 'string-array' -and $actual.Type -eq 'string-array' -and
            $expected.ArrayItemCount -ne $actual.ArrayItemCount) {
            $wrongArraySize.Add("$name ($($actual.ArrayItemCount) -> $($expected.ArrayItemCount))")
        }
        if ($expected.Type -eq 'string-array' -and $actual.Type -eq 'string-array' -and
            $expected.ArrayItemCount -eq $actual.ArrayItemCount) {
            for ($index = 0; $index -lt $expected.ArrayItemCount; $index++) {
                if ($expected.ItemFormatSignatures[$index] -ne $actual.ItemFormatSignatures[$index]) {
                    $wrongFormat.Add("$name item $index ($($actual.ItemFormatSignatures[$index]) -> $($expected.ItemFormatSignatures[$index]))")
                }
            }
        }
        if ($expected.Type -eq 'plurals' -and $actual.Type -eq 'plurals') {
            $quantities = @($actual.PluralQuantities)
            $duplicateQuantities = @($quantities | Group-Object | Where-Object Count -gt 1)
            if ($quantities -notcontains 'other' -or $duplicateQuantities.Count -gt 0) {
                $invalidPlurals.Add($name)
            }
            foreach ($quantity in ($quantities | Sort-Object -Unique)) {
                $expectedQuantity = if ($expected.PluralFormatSignatures.ContainsKey($quantity)) {
                    $quantity
                }
                else {
                    'other'
                }
                if ($expected.PluralFormatSignatures.ContainsKey($expectedQuantity) -and
                    $expected.PluralFormatSignatures[$expectedQuantity] -ne $actual.PluralFormatSignatures[$quantity]) {
                    $wrongFormat.Add("$name quantity $quantity ($($actual.PluralFormatSignatures[$quantity]) -> $($expected.PluralFormatSignatures[$expectedQuantity]))")
                }
            }
        }
        if ($expected.File -ne $actual.File) {
            $wrongFile.Add("$name ($($actual.File) -> $($expected.File))")
        }
    }

    $checks = @(
        @{ Label = 'Missing translatable keys'; Values = $missing },
        @{ Label = 'Extra keys'; Values = $extra },
        @{ Label = 'Resource type mismatches'; Values = $wrongType },
        @{ Label = 'Format argument mismatches'; Values = $wrongFormat },
        @{ Label = 'Array size mismatches'; Values = $wrongArraySize },
        @{ Label = 'Invalid plurals'; Values = $invalidPlurals },
        @{ Label = 'Resources in wrong thematic file'; Values = $wrongFile }
    )

    foreach ($check in $checks) {
        if ($check.Values.Count -gt 0) {
            $sample = @($check.Values | Select-Object -First 10) -join ', '
            if ($check.Values.Count -gt 10) {
                $sample += ", ... (+$($check.Values.Count - 10))"
            }
            $issues.Add("[$localeTag] $($check.Label): $sample")
        }
    }

    $rows.Add([pscustomobject]@{
        Locale          = $localeTag
        Directory       = $directoryName
        Resources       = $localized.Count
        Missing         = $missing.Count
        Extra           = $extra.Count
        LayoutMismatch  = $wrongFile.Count
        FormatMismatch  = $wrongFormat.Count
    })
}

foreach ($directory in (Get-ChildItem -LiteralPath $ResourceRoot -Directory -Filter 'values-*')) {
    $hasStringResources = @(Get-ChildItem -LiteralPath $directory.FullName -Filter 'strings*.xml' -File).Count -gt 0
    if ($hasStringResources -and -not $knownLocaleDirectories.Contains($directory.Name)) {
        $issues.Add("[config] Locale directory is not declared in locales_config.xml: $($directory.Name)")
    }
}

Write-Host "Source: en ($($source.Count) resources, $sourceTranslatableCount translatable)"
if ($rows.Count -gt 0) {
    $rows | Format-Table -AutoSize
}

if ($issues.Count -gt 0) {
    Write-Host "`nLocalization check FAILED ($($issues.Count) issue groups):" -ForegroundColor Red
    foreach ($issue in $issues) {
        Write-Host "- $issue"
    }
    exit 1
}

Write-Host "`nLocalization check PASSED." -ForegroundColor Green
exit 0
