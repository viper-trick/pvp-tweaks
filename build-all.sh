#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

# Build all versions — set -e catches any failure
./gradlew build

# Move built JARs to final-jars/
mkdir -p final-jars
for jar in src/*/build/libs/pvp-tweaks-*.jar; do
    [[ "$jar" != *-sources.jar ]] || continue
    [ -f "$jar" ] || continue
    mv "$jar" final-jars/
done

echo "All JARs moved to final-jars/"
