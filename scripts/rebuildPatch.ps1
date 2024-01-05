$basedir=pwd
$gpgsign=git config commit.gpgsign
if (!$gpgsign) {$gpgsign = "false"}

function enableCommitSigningIfNeeded {
    if ($gpgsign -eq "true") {
        Write-Host "Re-enabling GPG Signing" -f blue
        # Yes, this has to be global
        git config --global commit.gpgsign true
    }
}

$dir=$args[0]
$method="rebuild"
if ($dir -eq "--new") {
    $method = "create"
    $dir = $args[1]
}

if ($dir) {
    if (-not (Test-Path "$basedir/$dir")) {
        Write-Host "The directory is invalid or unavailable!" -f red
        exit
    }

    if ($gpgsign -eq "true") {
        Write-Host "_Temporarily_ disabling GPG signing" -f blue
        git config --global commit.gpgsign false
    }

    $name=Split-Path $dir -Leaf
    Set-Location "$basedir/$dir"

    if ($method -eq "rebuild") {
        Write-Host "Rolling back $name..." -f magenta
        git reset --soft HEAD~1
    }

    Write-Host "  Creating a patch from $name..." -f yellow
    git add . >$null 2>&1
    git commit -m "$name" >$null 2>&1
    $commit=git rev-parse HEAD
    git format-patch -1 $commit -o "$basedir/build" >$null 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  Something did not create cleanly for $name." -f yellow
        Write-Host "  Please review above details and repeat this process again." -f red
        enableCommitSigningIfNeeded
        exit 1
    } else {
        Write-Host "  Patches created cleanly for $name" -f cyan
        Write-Host 'Go to the "build" folder to see the results' -f green
        if ($method -eq "create") {
            Write-Host "Before applying the patch, make sure to commit" -f yellow
            Write-Host "the repository first." -yellow
        }
        enableCommitSigningIfNeeded
    }} else {
    Write-Host "The directory argument is empty" -f yellow
    Write-Host "Please repeat in the following way: " -f red
    Write-Host "patchTools $method <directory>" -f yellow
    Write-Host "Example 1: patchTools $method InfinityLib-Standalone" -f green
    Write-Host "Example 2: patchTools $method Addons/Networks" -nonewline -f green
}