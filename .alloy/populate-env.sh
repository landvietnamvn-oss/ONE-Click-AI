#!/usr/bin/env bash
# Idempotent: creates .env (read by the secrets gradle plugin) without clobbering user values.
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f .env ] || touch .env
if [ -n "${GEMINI_API_KEY:-}" ]; then
  if grep -qE '^GEMINI_API_KEY=(|MY_GEMINI_API_KEY)$' .env || ! grep -q '^GEMINI_API_KEY=' .env; then
    sed -i '/^GEMINI_API_KEY=/d' .env; echo "GEMINI_API_KEY=${GEMINI_API_KEY}" >> .env
  fi
elif ! grep -q '^GEMINI_API_KEY=' .env; then
  echo "GEMINI_API_KEY=MY_GEMINI_API_KEY" >> .env
fi
grep -q '^IS_ALLOY=' .env || echo "IS_ALLOY=${IS_ALLOY:-false}" >> .env
