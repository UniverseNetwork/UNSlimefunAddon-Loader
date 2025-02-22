$basedir=pwd
$gpgsign=git config commit.gpgsign
if (!$gpgsign) {$gpgsign = "false"}

$setup=git config sfaloader.setup
if (!$setup) {$setup = "false"}

if ($setup -eq "false") {
    Write-Host "Rebuilding Forked projects..."
    git submodule update --recursive --init | Write-Host -f darkgray
    git config sfaloader.setup true
}

function enableCommitSigningIfNeeded {
    if ($gpgsign -eq "true") {
        Write-Host "Re-enabling GPG Signing" -nonewline -f blue
        # Yes, this has to be global
        git config --global commit.gpgsign true
    }
}

$dir=$args[0]
$method="recreate"
if ($dir -and $dir.StartsWith("--")) {
    $method = $dir.Replace("--","")
    $dir = $args[1]
}

if ($dir) {
    if (-not (Test-Path "$basedir/$dir")) {
        Write-Host "The directory is invalid or unavailable!" -f red
        exit 1
    }

    $name=Split-Path $dir -Leaf
    Set-Location "$basedir/$dir"

    if (-not ($method -eq "recreate")) {
        $branch = (git remote show origin | Select-String -Pattern "HEAD branch:").ToString().Split(":")[1].Trim(" ")
    }

    if ($method -eq "fetch") {
        Write-Host "Fetching & Rolling back $name..." -f yellow
        git fetch >$null 2>&1
        git reset --soft origin/$branch
        Write-Host ""
        Write-Host "All patched files will be unstaged and you" -f green
        Write-Host "will have to merge it by yourself manually" -f green
        Write-Host "After that, you must run " -nonewline -f cyan
        Write-Host "`"patchTools merge $dir`"" -f yellow
        Write-Host "command to complete the merge process." -nonewline -f cyan
        exit
    }

    if ($gpgsign -eq "true") {
        Write-Host "_Temporarily_ disabling GPG signing" -f blue
        git config --global commit.gpgsign false
    }

    if ($method -eq "recreate") {
        Write-Host "Rolling back $name..." -f yellow
        git reset --soft HEAD~1 | Write-Host -f magenta
    }

    Write-Host "  Creating a patch from $name..." -f yellow
    git add . >$null 2>&1
    git commit -m "$name" >$null 2>&1
    $commit=git rev-parse HEAD
    git format-patch -1 $commit -o "$basedir/build" >$null 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  Something did not create cleanly for $name." -f yellow
        Write-Host "  Please review above details and repeat this process again." -nonewline -f red
        enableCommitSigningIfNeeded
        exit 1
    } else {
        Write-Host "  Patches created cleanly for $name" -f cyan
        Write-Host "Go to the " -nonewline -f green
        Write-Host '"build"' -nonewline -f yellow
        Write-Host " folder to see the results." -f green
        Write-Host "The patch file name must be " -nonewline -f green
        Write-Host "0001-$name.patch" -f magenta
        Write-Host "Move it to the " -nonewline -f cyan
        Write-Host '"patches"' -nonewline -f yellow
        Write-Host " folder and rename it " -f cyan
        Write-Host "to " -nonewline -f cyan
        Write-Host "$name.patch" -f magenta
        if ($method -eq "create") {
            Write-Host ""
            Write-Host "Before applying the patch, make sure to commit" -f yellow
            Write-Host "the repository folder first." -f yellow
            git reset --soft origin/$branch
        }
        if ($method -eq "merge") {
            Write-Host ""
            Write-Host "DO NOT apply the patch (using patchTools) before" -f red
            Write-Host "committing it to the GitHub repository!!!" -f red
            Write-Host "Whether you forked this repository or not!" -f red
            git reset --soft origin/$branch
        }
        enableCommitSigningIfNeeded
    }} else {
    Write-Host "The directory argument is empty" -f yellow
    Write-Host "Please repeat in the following way: " -f red
    Write-Host "patchTools $method <directory>" -f yellow
    Write-Host "Example 1: patchTools $method InfinityLib-Standalone" -f green
    Write-Host "Example 2: patchTools $method Addons/Networks" -nonewline -f green
}