@echo off

if ["%~1"]==[""] (
echo SFALoader patch tool command.
echo View below for details of the available commands.
echo.
echo Commands:
echo   * create  - Creates the patches
echo   * rebuild - Rebuilds the patches
echo   * patch   - Applies all the patches to SFALoader
)

if ["%~1"]==["patch"] (
git submodule update --recursive --init
powershell ./scripts/applyPatches.ps1
)

if ["%~1"]==["rebuild"] (
powershell ./scripts/rebuildPatches.ps1 %~2
)

if ["%~1"]==["create"] (
powershell ./scripts/rebuildPatches.ps1 --new %~2
)