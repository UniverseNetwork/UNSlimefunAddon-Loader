$basedir=(pwd)
$gpgsign=(git config commit.gpgsign)
if (!$gpgsign) {$gpgsign = "false"}
echo "Rebuilding Forked projects..."

function enableCommitSigningIfNeeded {
    if ($gpgsign -eq "true") {
        echo "Re-enabling GPG Signing"
        # Yes, this has to be global
        git config --global commit.gpgsign true
    }
}

function applyPatch($dir) {
    $name=(Split-Path $dir -Leaf)
    Set-Location "$basedir/$dir"

    echo "Resetting $name..."
    git reset --hard HEAD

    echo "  Applying a patch to $name..."
    git am --abort >$null 2>&1
    git am --3way --ignore-whitespace "$basedir/patches/$name.patch" >$null 2>&1
    if ($LASTEXITCODE -ne 0) {
        echo "  Something did not apply cleanly to $name."
        echo "  Please review above details and finish the apply then"
        echo "  save the changes with rebuildPatches.sh"
        enableCommitSigningIfNeeded
        exit 1
    } else {
        echo "  Patches applied cleanly to $name"
    }
}

# Disable GPG signing before AM, slows things down and doesn't play nicely.
# There is also zero rational or logical reason to do so for these sub-repo AMs.
# Calm down kids, it's re-enabled (if needed) immediately after, pass or fail.
if ($gpgsign -eq "true") {
    echo "_Temporarily_ disabling GPG signing"
    git config --global commit.gpgsign false
}

applyPatch InfinityLib-Standalone

applyPatch Addons/DynaTech
applyPatch Addons/ExtraTools
applyPatch Addons/InfinityExpansion
applyPatch Addons/SFCalc
applyPatch Addons/SMG

enableCommitSigningIfNeeded