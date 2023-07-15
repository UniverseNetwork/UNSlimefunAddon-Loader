$basedir=(pwd)
$gpgsign=(git config commit.gpgsign)
if (!$gpgsign) {$gpgsign = "false"}

function enableCommitSigningIfNeeded {
    if ($gpgsign -eq "true") {
        echo "Re-enabling GPG Signing"
        # Yes, this has to be global
        git config --global commit.gpgsign true
    }
}

$dir=$args[0]

if ($dir) {
    if ($gpgsign -eq "true") {
        echo "_Temporarily_ disabling GPG signing"
        git config --global commit.gpgsign false
    }

    $name=(Split-Path $dir -Leaf)
    Set-Location "$basedir/$dir"

    echo "Rolling back $name..."
    git reset --soft HEAD~1

    echo "  Creating a patch from $name..."
    git add . >$null 2>&1
    git commit -m "$name" >$null 2>&1
    $commit=(git rev-parse HEAD)
    git format-patch -1 $commit -o "$basedir/build" >$null 2>&1
    if ($LASTEXITCODE -ne 0) {
        echo "  Something did not create cleanly for $name."
        echo "  Please review above details and repeat this process again."
        enableCommitSigningIfNeeded
        exit 1
    } else {
        echo "  Patches created cleanly for $name"
        echo "Go to the \"build\" folder to see the results"

    }} else {
    echo "The directory argument is empty"
    echo "Please repeat in the following way: patchTools rebuild <directory>"
    echo "Example 1: patchTools rebuild InfinityLib-Standalone"
    echo "Example 2: patchTools rebuild Addons/Networks"
}

enableCommitSigningIfNeeded