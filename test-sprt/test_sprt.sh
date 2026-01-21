#!/bin/bash

# Usage: ./test_sprt.sh <base_version> <changed_version>
# Example: ./test_sprt.sh 1.0 1.1
# Searches for KnightClubbingEngine-<version>.jar files in the target/ directory
# and runs a SPRT test between the two versions using cutechess-cli.

INPUT_ARG1="$1" # Base version
INPUT_ARG2="$2" # Changed version

WORK_DIR=$(pwd)
ENGINE1="$WORK_DIR/engines/knightclubbingengine-$INPUT_ARG1.jar"
ENGINE2="$WORK_DIR/engines/knightclubbingengine-$INPUT_ARG2.jar"
PGNOUT="$WORK_DIR/games.pgn"

[[ -f "$ENGINE1" ]] && echo "✓ Engine 1 found" || { echo "✗ Engine 1 missing: $ENGINE1"; exit 1; }
[[ -f "$ENGINE2" ]] && echo "✓ Engine 2 found" || { echo "✗ Engine 2 missing: $ENGINE1"; exit 1; }

: > "$PGNOUT"
echo "✓ Cleared $PGNOUT"

cutechess-cli \
  -event "KnightClubbing SPRT $INPUT_ARG1 vs $INPUT_ARG2" \
  -engine cmd=java arg=-jar arg="$ENGINE2" proto=uci name="KC_change_v$INPUT_ARG2" \
  -engine cmd=java arg=-jar arg="$ENGINE1" proto=uci name="KC_base_v$INPUT_ARG1" \
  -each tc=3+0 -games 300 -concurrency 5 -wait 2000 \
  -sprt elo0=0 elo1=50 alpha=0.05 beta=0.05 \
  -openings file=8moves_v3.pgn format=pgn order=random \
  -pgnout "$PGNOUT" min fi
