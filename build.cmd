@echo off
git submodule update --recursive --init
powershell ./scripts/applyPatch.ps1