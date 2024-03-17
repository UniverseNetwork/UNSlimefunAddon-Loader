$basedir=pwd

$setup=git config sfaloader.setup
if (!$setup) {$setup = "false"}

if ($setup -eq "false") {
    Write-Host "Rebuilding Forked projects..."
    git submodule update --recursive --init | Write-Host -f darkgray
    git config sfaloader.setup true
}

function checkUpstream($dir) {
    $name=Split-Path $dir -Leaf
    if (-not (Test-Path "$basedir/$dir")) {
        Write-Host "The directory is invalid or unavailable!" -f red
        exit
    }
    Set-Location "$basedir/$dir"

    Write-Host "Checking upstream for $name..." -f yellow

    $url=git ls-remote --get-url
    $branch=(git remote show origin | Select-String -Pattern "HEAD branch:").ToString().Split(":")[1].Trim(" ")
    $commit=(git rev-parse origin/$branch).substring(0, 7)
    $newcommit=(git ls-remote $url HEAD).substring(0, 7)

    Write-Host "Upstream branch: " -nonewline
    Write-Host $branch -f blue
    Write-Host "Upstream commit: " -nonewline
    Write-Host $newcommit -f cyan
    Write-Host "Current commit: " -nonewline
    Write-Host $commit -f magenta
    Write-Host "Current up-to-date status: " -nonewline
    if ($commit -eq $newcommit) {
        Write-Host "Yes" -f green
    } else {
        Write-Host "No" -f red
    }
}

if ($args[0]) {
    if ($args[0] -eq "--all") {
        checkUpstream InfinityGuizhanLib-Standalone

        checkUpstream Addons/ColoredEnderChests
        checkUpstream Addons/DyeBench
        checkUpstream Addons/DyedBackpacks
        checkUpstream Addons/DynaTech
        checkUpstream Addons/EcoPower
        checkUpstream Addons/ExoticGarden
        checkUpstream Addons/ExtraGear
        checkUpstream Addons/ExtraHeads
        checkUpstream Addons/ExtraTools
        checkUpstream Addons/FluffyMachines
        checkUpstream Addons/HeadLimiter
        checkUpstream Addons/InfinityExpansion
        checkUpstream Addons/Networks
        checkUpstream Addons/PrivateStorage
        checkUpstream Addons/SFCalc
        checkUpstream Addons/SfChunkInfo
        checkUpstream Addons/SlimeCustomizer
        checkUpstream Addons/SlimefunTranslation
        checkUpstream Addons/SMG
        checkUpstream Addons/SoundMuffler
    } else {checkUpstream $args[0]}
} else {
    Write-Host "The argument is empty" -f yellow
    Write-Host "Please repeat in the following way: " -nonewline -f red
    Write-Host "patchTools check <arg>" -f yellow
    Write-Host "Example 1: patchTools check InfinityLib-Standalone" -f yellow
    Write-Host "Example 2: patchTools check Addons/Networks" -f green
    Write-Host "Example 3: patchTools check --all" -nonewline -f green
}