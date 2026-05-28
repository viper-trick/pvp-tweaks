#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
FINAL_JARS="$PROJECT_DIR/final-jars"
mkdir -p "$FINAL_JARS"

# Format: minecraft_version:yarn_mappings|none:fabric_api_version:modmenu_version:minecraft_range:use_mojmap:java_version
# For 26.x, use use_non_obfuscated + no mappings (yarn_mappings=none, use_mojmap=false)
# non-obfuscated versions (26.x) require loom 1.16+ + net.fabricmc.fabric-loom plugin
VERSIONS=(
  "1.21.4:1.21.4+build.8:0.119.4+1.21.4:11.0.1:~1.21.4:false:21"
  "1.21.5:1.21.5+build.1:0.128.2+1.21.5:11.0.1:~1.21.5:false:21"
  "1.21.6:1.21.6+build.1:0.128.2+1.21.6:11.0.1:~1.21.6:false:21"
  "1.21.7:1.21.7+build.8:0.129.0+1.21.7:11.0.1:~1.21.7:false:21"
  "1.21.8:1.21.8+build.1:0.136.1+1.21.8:11.0.1:~1.21.8:false:21"
  "1.21.9:1.21.9+build.1:0.134.1+1.21.9:11.0.1:~1.21.9:false:21"
  "1.21.10:1.21.10+build.3:0.138.4+1.21.10:11.0.1:~1.21.10:false:21"
  "1.21.11:1.21.11+build.5:0.141.4+1.21.11:11.0.1:~1.21.11:false:21"
#  "26.1:none:0.145.1+26.1::~26.1:false:25"
#  "26.1.1:none:0.145.4+26.1.1::~26.1.1:false:25"
#  "26.1.2:none:0.149.1+26.1.2::~26.1.2:false:25"
)

for VERSION_SPEC in "${VERSIONS[@]}"; do
  IFS=':' read -r MC_VER MAPPINGS FABRIC_VER MODMENU_VER MC_RANGE USE_MOJMAP JAVA_VER <<< "$VERSION_SPEC"

  echo ""
  echo "================================================"
  echo "Building for Minecraft $MC_VER (Java $JAVA_VER)..."
  echo "================================================"

  # Set JAVA_HOME for Java 25 builds
  export JAVA_HOME=""
  if [ "$JAVA_VER" = "25" ]; then
    export JAVA_HOME="/usr/lib/jvm/jdk-25.0.1-oracle-x64"
  elif [ "$JAVA_VER" = "21" ]; then
    export JAVA_HOME="/usr/lib/jvm/jdk-21.0.9-oracle-x64"
  fi
  if [ -n "$JAVA_HOME" ]; then
    echo "  Using JAVA_HOME=$JAVA_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
  fi

  # Set version-specific gradle properties
  sed -i "s/^minecraft_version=.*/minecraft_version=$MC_VER/" "$PROJECT_DIR/gradle.properties"
  if [ "$USE_MOJMAP" == "true" ]; then
    sed -i "s/^use_mojmap=.*/use_mojmap=true/" "$PROJECT_DIR/gradle.properties"
    sed -i "s/^yarn_mappings=.*/yarn_mappings=unused-with-mojmap/" "$PROJECT_DIR/gradle.properties"
  else
    sed -i "s/^use_mojmap=.*/use_mojmap=false/" "$PROJECT_DIR/gradle.properties"
    sed -i "s/^yarn_mappings=.*/yarn_mappings=$MAPPINGS/" "$PROJECT_DIR/gradle.properties"
  fi
  sed -i "s/^fabric_version=.*/fabric_version=$FABRIC_VER/" "$PROJECT_DIR/gradle.properties"
  sed -i "s/^modmenu_version=.*/modmenu_version=$MODMENU_VER/" "$PROJECT_DIR/gradle.properties"
  sed -i "s/^minecraft_version_range=.*/minecraft_version_range=$MC_RANGE/" "$PROJECT_DIR/gradle.properties"
  sed -i "s/^mod_version=.*/mod_version=1.8.9-mc$MC_VER/" "$PROJECT_DIR/gradle.properties"
  sed -i "s/^java_version=.*/java_version=$JAVA_VER/" "$PROJECT_DIR/gradle.properties"
  if [[ "$MC_VER" == 26.* ]]; then
    sed -i "s/^loader_version=.*/loader_version=0.17.0/" "$PROJECT_DIR/gradle.properties"
    sed -i "s/^loom_version=.*/loom_version=1.16.2/" "$PROJECT_DIR/gradle.properties"
    sed -i "s/^use_non_obfuscated=.*/use_non_obfuscated=true/" "$PROJECT_DIR/gradle.properties"
  else
    sed -i "s/^loader_version=.*/loader_version=0.18.4/" "$PROJECT_DIR/gradle.properties"
    sed -i "s/^loom_version=.*/loom_version=1.15.3/" "$PROJECT_DIR/gradle.properties"
    sed -i "s/^use_non_obfuscated=.*/use_non_obfuscated=false/" "$PROJECT_DIR/gradle.properties"
  fi

  # Track override files so we can restore between builds
  OVERRIDE_FILES=()

  # Remove any previous version-specific source to start clean
  rm -rf "$PROJECT_DIR/src/main/java/com"
  OVERRIDE_FILES+=("$PROJECT_DIR/src/main/java/com")

  # Apply stub overrides FIRST (base non-Cloth-Config replacements)
  mkdir -p "$PROJECT_DIR/src/main/java/com"
  if [ -d "$PROJECT_DIR/src/main/com/pvptweaks" ]; then
    echo "  Applying non-Cloth Config stubs from src/main/com/..."
    while IFS= read -r -d '' stub_file; do
      rel_path="${stub_file#$PROJECT_DIR/src/main/com/}"
      target="$PROJECT_DIR/src/main/java/com/$rel_path"
      mkdir -p "$(dirname "$target")"
      cp "$stub_file" "$target"
      OVERRIDE_FILES+=("$target")
    done < <(find "$PROJECT_DIR/src/main/com/pvptweaks" -type f -print0)
  fi

  # Copy full version-specific source tree OVER the stubs (version wins)
  mkdir -p "$PROJECT_DIR/src/main/java/com/pvptweaks"
  VERSION_DIR="$PROJECT_DIR/versions/$MC_VER"
  if [ -d "$VERSION_DIR" ] && [ -d "$VERSION_DIR/com/pvptweaks" ]; then
    echo "  Applying version-specific source from $VERSION_DIR..."
    cp -r "$VERSION_DIR/com/pvptweaks"/* "$PROJECT_DIR/src/main/java/com/pvptweaks/"
  else
    # Fall back to 1.21.11 source
    echo "  ! No version source for $MC_VER, using default 1.21.11 source"
    cp -r "$PROJECT_DIR/versions/1.21.11/com/pvptweaks"/* "$PROJECT_DIR/src/main/java/com/pvptweaks/"
  fi

  # Apply version-specific resources (e.g. modified mixins.json)
  if [ -f "$VERSION_DIR/pvptweaks.mixins.json" ]; then
    echo "  Applying version-specific mixins.json from $VERSION_DIR"
    cp "$VERSION_DIR/pvptweaks.mixins.json" "$PROJECT_DIR/src/main/resources/pvptweaks.mixins.json"
    OVERRIDE_FILES+=("$PROJECT_DIR/src/main/resources/pvptweaks.mixins.json")
  fi

  # Apply version-specific overrides from version-src/ (backward compat)
  VERSION_SRC="$PROJECT_DIR/version-src/$MC_VER"
  if [ -d "$VERSION_SRC" ] && [ "$(find "$VERSION_SRC" -type f 2>/dev/null | head -1)" ]; then
    echo "  Applying additional overrides from $VERSION_SRC..."
    while IFS= read -r -d '' override_file; do
      rel_path="${override_file#$VERSION_SRC/}"
      target="$PROJECT_DIR/src/main/java/$rel_path"
      mkdir -p "$(dirname "$target")"
      cp "$override_file" "$target"
      OVERRIDE_FILES+=("$target")
    done < <(find "$VERSION_SRC" -type f -print0)
  fi

  # Build
  cd "$PROJECT_DIR"
  if ./gradlew clean build --no-daemon -q 2>&1; then
    # Copy JAR
    JAR_FILE=$(ls -t "$PROJECT_DIR/build/libs/pvp-tweaks-"*.jar 2>/dev/null | grep -v sources | head -1)
    if [ -n "$JAR_FILE" ]; then
      FINAL_NAME="pvp-tweaks-1.8.9-mc$MC_VER.jar"
      cp "$JAR_FILE" "$FINAL_JARS/$FINAL_NAME"
      echo "  ✓ Created $FINAL_NAME"
    else
      echo "  !! No JAR found for $MC_VER"
      ls -la "$PROJECT_DIR/build/libs/" 2>/dev/null || true
    fi
  else
    echo "  ✗ Build failed for $MC_VER"
  fi

  # Restore: nothing needed; next iteration clears and recreates from scratch
  echo "  Cleanup done."
done

# Restore default gradle.properties to 1.21.11
cat > "$PROJECT_DIR/gradle.properties" << 'EOF'
org.gradle.jvmargs=-Xmx4G -Xms512m
loom_version=1.15.3
minecraft_version=1.21.11
yarn_mappings=1.21.11+build.5
loader_version=0.18.4
fabric_version=0.141.4+1.21.11
modmenu_version=11.0.1
mod_version=1.8.9-mc1.21.11
maven_group=com.pvptweaks
archives_base_name=pvp-tweaks
minecraft_version_range=~1.21.11
use_mojmap=false
use_non_obfuscated=false
java_version=21
EOF

echo ""
echo "================================================"
echo "Multi-version build complete!"
echo "JARs in: $FINAL_JARS"
echo "================================================"
ls -la "$FINAL_JARS"
