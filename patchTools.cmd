@echo off

if ["%~1"]==[""] (goto noarg)

if ["%~1"]==["create"] (
powershell ./scripts/editPatch.ps1 --create %~2
)

if ["%~1"]==["recreate"] (
powershell ./scripts/editPatch.ps1 %~2
)

if ["%~1"]==["fetch"] (
powershell ./scripts/editPatch.ps1 --fetch %~2
)

if ["%~1"]==["merge"] (
powershell ./scripts/editPatch.ps1 --merge %~2
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
echo   * fetch   - Fetch a repository and perform a soft-reset on
echo               the repository before the merge is performed
echo   * merge   - Merge the repository with a previously created
echo               patch after the fetch and soft-reset are performed
echo   * patch   - Apply the patch(es) to SFALoader
echo   * check   - Check(s) the addon(s)/library repository upstream

:end