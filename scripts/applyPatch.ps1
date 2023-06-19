$basedir=(pwd)
$gpgsign=(git config commit.gpgsign) -or "false"
Write-Host "Rebuilding Forked projects.... "

function enableCommitSigningIfNeeded {
    if ($gpgsign -eq "true") {
        Write-Host "Re-enabling GPG Signing"
        # Yes, this has to be global
        git config --global commit.gpgsign true
    }
}

function applyPatch {
    param($dir)
    $name=(Split-Path $dir -Leaf)
    Set-Location "$basedir/$dir"

    Write-Host "Resetting $name..."
    git reset --hard HEAD

    Write-Host "  Applying patches to $name..."
    git am --abort >$null 2>&1
    git am --3way --ignore-whitespace "$basedir/patches/$name.patch"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  Something did not apply cleanly to $name."
        Write-Host "  Please review above details and finish the apply then"
        Write-Host "  save the changes with rebuildPatches.sh"
        enableCommitSigningIfNeeded
        exit 1
    } else {
        Write-Host "  Patches applied cleanly to $name"
    }
}

# Disable GPG signing before AM, slows things down and doesn't play nicely.
# There is also zero rational or logical reason to do so for these sub-repo AMs.
# Calm down kids, it's re-enabled (if needed) immediately after, pass or fail.
if ($gpgsign -eq "true") {
    Write-Host "_Temporarily_ disabling GPG signing"
    git config --global commit.gpgsign false
}

applyPatch InfinityLib-Standalone

enableCommitSigningIfNeeded