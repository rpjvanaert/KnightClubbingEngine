#!/bin/bash

# Usage: ./test_sprt.sh <base_version> <changed_version>
# Example: ./test_sprt.sh 1.0 1.1
# Searches for KnightClubbingEngine-<version>.jar files in the target/ directory
# and runs a SPRT test between the two versions using cutechess-cli.

INPUT_ARG1="$1"         # Base version
INPUT_ARG2="$2"         # Changed version
PRESET=${3:-"default"}  # (Opt) preset name from sprt_presets.ini

WORK_DIR=$(pwd)
ENGINE1="$WORK_DIR/engines/knightclubbingengine-$INPUT_ARG1.jar"
ENGINE2="$WORK_DIR/engines/knightclubbingengine-$INPUT_ARG2.jar"
PGNOUT="$WORK_DIR/games.pgn"
CONFIG_FILE="$WORK_DIR/sprt_presets.ini"

[[ -f "$ENGINE1" ]] && echo "✓ Engine 1 found" || { echo "✗ Engine 1 missing: $ENGINE1"; exit 1; }
[[ -f "$ENGINE2" ]] && echo "✓ Engine 2 found" || { echo "✗ Engine 2 missing: $ENGINE1"; exit 1; }
[[ -f "$CONFIG_FILE" ]] || { echo "✗ Config file missing: $CONFIG_FILE"; exit 1; }

: > "$PGNOUT"
echo "✓ Cleared $PGNOUT"

# Function to read values from the .ini file
get_config_value() {
  awk -F '=' "/^\[$PRESET\]/ {found=1} found && \$1==\"$1\" {print \$2; exit}" "$CONFIG_FILE"
}

TC=$(get_config_value "tc")
GAMES=$(get_config_value "games")
CONCURRENCY=$(get_config_value "concurrency")
elo0=$(get_config_value "elo0")
elo1=$(get_config_value "elo1")
alpha=$(get_config_value "alpha")
beta=$(get_config_value "beta")

if [[ -z "$TC" || -z "$GAMES" || -z "$CONCURRENCY" || -z "$elo0" || -z "$elo1" || -z "$alpha" || -z "$beta" ]]; then
  echo "✗ Failed to read preset: $PRESET"
  exit 1
fi

echo "Running SPRT with preset: $PRESET"
echo "Time control: $TC, Games: $GAMES, Concurrency: $CONCURRENCY"

cutechess-cli \
  -event "KnightClubbing SPRT $INPUT_ARG1 vs $INPUT_ARG2" \
  -engine cmd=java arg=-jar arg="$ENGINE2" proto=uci name="KC_change_v$INPUT_ARG2" \
  -engine cmd=java arg=-jar arg="$ENGINE1" proto=uci name="KC_base_v$INPUT_ARG1" \
  -each tc=$TC -games $GAMES -concurrency $CONCURRENCY -wait 2000 \
  -sprt elo0="$elo0" elo1="$elo1" alpha="$alpha" beta="$beta" \
  -openings file=8moves_v3.pgn format=pgn order=random \
  -pgnout "$PGNOUT" min fi
