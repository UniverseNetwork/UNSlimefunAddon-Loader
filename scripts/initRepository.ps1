$basedir=pwd
$link=$args[0]
$dir=$args[1]
$name=$args[2]

if ($link -and ($link.StartsWith("https://") -or $link.StartsWith("http://") -or $link.StartsWith("git@"))) {
    if ($link.EndsWith(".git")) {$link=$link.Replace(".git","")}
    Write-Host "Initializing a new repository to this project..." -f yellow
    if ($dir) {
        if (-not $name) {$name=Split-Path $dir -Leaf}
        git submodule add --name $name $link $dir | Write-Host -f darkgray
    } else {git submodule add $link | Write-Host -f darkgray}
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to clone repository, does the folder already exists?" -nonewline -f red
        exit 1
    }
    Write-Host "Successfully initialized $name repository with this project" -nonewline -f green
} else {
    Write-Host "The link argument is empty" -f yellow
    Write-Host "Please repeat in the following way: " -f red
    Write-Host "patchTools init <link> [directory] [name]" -f yellow
    Write-Host "Example 1: patchTools init https://github.com/ARVIN3108/InfinityLib-Standalone" -f green
    Write-Host "Example 2: patchTools init https://github.com/Sefiraat/Networks Addons/Networks" -f green
    Write-Host "Example 3: patchTools init https://github.com/Sefiraat/SMG Addons/SMG SimpleMaterialGenerators" -nonewline -f green
}