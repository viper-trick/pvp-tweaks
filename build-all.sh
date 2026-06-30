#!/usr/bin/env bash
set -uo pipefail
cd "$(dirname "$0")"

# Group builds — each JAR covers a range of compatible MC versions
VERSION_GROUPS=(
    "1.21.4-5"
    "1.21.6-8"
    "1.21.9-10"
    "1.21.11"
    "26.1.x"
    "26.2"
)

FAILED=()

echo "Building all version groups..."
echo

for group in "${VERSION_GROUPS[@]}"; do
    safe="${group//./_}"
    safe="${safe//-/_}"
    echo "===== Building $group ($safe) ====="
    if ./gradlew ":$safe:build" 2>&1; then
        echo "===== $group OK ====="
    else
        echo "===== $group FAILED ====="
        FAILED+=("$group")
    fi
    echo
done

# Move built JARs to final-jars/
mkdir -p final-jars
for jar in src/build-*/build/libs/pvp-tweaks-*.jar; do
    [[ "$jar" != *-sources.jar ]] || continue
    [ -f "$jar" ] || continue
    cp "$jar" final-jars/
done

if [ ${#FAILED[@]} -eq 0 ]; then
    echo "All groups built successfully. JARs copied to final-jars/"
else
    echo "The following groups FAILED: ${FAILED[*]}"
    echo "Successful JARs copied to final-jars/"
fi
