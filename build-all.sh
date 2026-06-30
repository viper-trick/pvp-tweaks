#!/usr/bin/env bash
set -uo pipefail
cd "$(dirname "$0")"

# Versions to build (same order as settings.gradle)
VERSIONS=(
    1.21.4 1.21.5 1.21.6 1.21.7 1.21.8
    1.21.9 1.21.10 1.21.11
    26.1 26.1.1 26.1.2 26.2
)

FAILED=()

echo "Building all versions..."
echo

for ver in "${VERSIONS[@]}"; do
    safe="${ver//./_}"
    echo "===== Building $ver ($safe) ====="
    if ./gradlew ":$safe:build" 2>&1; then
        echo "===== $ver OK ====="
    else
        echo "===== $ver FAILED ====="
        FAILED+=("$ver")
    fi
    echo
done

# Move built JARs to final-jars/
mkdir -p final-jars
for jar in src/*/build/libs/pvp-tweaks-*.jar; do
    [[ "$jar" != *-sources.jar ]] || continue
    [ -f "$jar" ] || continue
    cp "$jar" final-jars/
done

if [ ${#FAILED[@]} -eq 0 ]; then
    echo "All versions built successfully. JARs copied to final-jars/"
else
    echo "The following versions FAILED: ${FAILED[*]}"
    echo "Successful JARs copied to final-jars/"
fi
