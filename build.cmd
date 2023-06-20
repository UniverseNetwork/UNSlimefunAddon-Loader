@echo off
git submodule update --recursive --init
powershell ./scripts/applyPatches.ps1