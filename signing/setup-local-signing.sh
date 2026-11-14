#!/usr/bin/env bash
# Decode the versioned keystore into the working tree so that a LOCAL build is
# signed with exactly the same certificate as the GitHub Actions builds.
#   bash signing/setup-local-signing.sh
set -euo pipefail

cd "$(dirname "$0")/.." || exit 1

STORE_PASS="YtDlHasib2026!Key"
ALIAS="ytdownloader"

if [ ! -f signing/release-keystore.b64 ]; then
  echo "❌ signing/release-keystore.b64 not found" >&2
  exit 1
fi

tr -d '\n' < signing/release-keystore.b64 | base64 -d > signing/release-keystore.jks

cat > keystore.properties <<EOF
storeFile=signing/release-keystore.jks
storePassword=$STORE_PASS
keyAlias=$ALIAS
keyPassword=$STORE_PASS
storeType=JKS
EOF

echo "✅ keystore.properties তৈরি হয়েছে — এখন 'gradle assembleRelease' চালান"
echo "   keystore: signing/release-keystore.jks (git-ignored)"
