@echo off

if (%1=="") (goto noarg)

if ["%~1"]==["create"] (
powershell ./scripts/rebuildPatch.ps1 --new %~2
)

if ["%~1"]==["rebuild"] (
powershell ./scripts/rebuildPatch.ps1 %~2
)

if ["%~1"]==["patch"] (
powershell ./scripts/applyPatch.ps1 %~2
)

if ["%~1"]==["check"] (
powershell ./scripts/checkUpstream.ps1 %~2
)

goto end

:noarg
echo SFALoader patch tool command.
echo View below for details of the available commands.
echo.
echo Commands:
echo   * create  - Create a patch
echo   * rebuild - Rebuild a patch
echo   * patch   - Apply the patch(es) to SFALoader
echo   * check   - Check(s) the addon(s)/library repository upstream

:end