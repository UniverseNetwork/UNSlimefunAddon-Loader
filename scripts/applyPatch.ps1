$basedir=pwd
$gpgsign=git config commit.gpgsign
if (!$gpgsign) {$gpgsign = "false"}

function disableCommitSigning {
    # Disable GPG signing before AM, slows things down and doesn't play nicely.
    # There is also zero rational or logical reason to do so for these sub-repo AMs.
    # Calm down kids, it's re-enabled (if needed) immediately after, pass or fail.
    if ($gpgsign -eq "true") {
        Write-Host "_Temporarily_ disabling GPG signing" -f blue
        git config --global commit.gpgsign false
    }
}

function enableCommitSigningIfNeeded {
    if ($gpgsign -eq "true") {
        Write-Host "Re-enabling GPG Signing" -nonewline -f blue
        # Yes, this has to be global
        git config --global commit.gpgsign true
    }
}

function applyPatch($dir) {
    $name=Split-Path $dir -Leaf
    if (-not (Test-Path "$basedir/$dir")) {
        Write-Host "The directory is invalid or unavailable!" -f red
        exit
    }
    Set-Location "$basedir/$dir"

    Write-Host "Resetting $name..." -f yellow
    git reset --hard HEAD | Write-Host -f magenta

    Write-Host "  Applying a patch to $name..." -f green
    git am --abort >$null 2>&1
    git am --3way --ignore-whitespace "$basedir/patches/$name.patch" >$null 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  Something did not apply cleanly to $name." -f yellow
        Write-Host "  Please review above details and finish the apply then" -f red
        Write-Host "  save the changes with patchTools rebuild" -f red
        enableCommitSigningIfNeeded
        exit 1
    } else {
        Write-Host "  Patches applied cleanly to $name" -f cyan
    }
}

if ($args[0]) {
    if ($args[0] -eq "--all") {
        Write-Host "Rebuilding Forked projects..."
        git submodule update --recursive --init | Write-Host -f darkgray

        disableCommitSigning

        applyPatch InfinityLib-Standalone

        applyPatch Addons/ColoredEnderChests
        applyPatch Addons/DynaTech
        applyPatch Addons/ExtraTools
        applyPatch Addons/FluffyMachines
        applyPatch Addons/HeadLimiter
        applyPatch Addons/InfinityExpansion
        applyPatch Addons/Networks
        applyPatch Addons/SFCalc
        applyPatch Addons/SMG
        enableCommitSigningIfNeeded
    } else {
        disableCommitSigning

        applyPatch $args[0]

        enableCommitSigningIfNeeded
    }
} else {
    Write-Host "The argument is empty" -f yellow
    Write-Host "Please repeat in the following way: " -nonewline -f red
    Write-Host "patchTools patch <arg>" -f yellow
    Write-Host "Example 1: patchTools patch InfinityLib-Standalone" -f yellow
    Write-Host "Example 2: patchTools patch Addons/Networks" -f green
    Write-Host "Example 3: patchTools patch --all" -nonewline -f green
}