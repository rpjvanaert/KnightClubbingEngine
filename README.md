# KnightClubbingEngine

# About
KnightClubbingEngine is a Java-based chess engine that uses KnightClubbingLogic for chess logic.

# Setup


# Testing

## SPRT usage
### Setup
- Java 17 installed
- cutechess-cli installed
  - I downloaded the source code from Github and compiled it myself. Works for now.
- Preferably 2 chess knightclubbingengine's in .jar format to test.
  - Located in the ``test-sprt/engines`` directory.

### Running
To run the SPRT, run the test_sprt.sh in its directory, for example:
```shell
cd test-sprt && ./test_sprt.sh 1.0.0 1.0.1
```
I do recommend creating run configuration in your IDE when testing changes to the engine.
<br/>
The script takes 2 parameters, the base version and the change version. It tests if the change version is stronger than the base version. In this order:
```shell
./test_sprt.sh <base version> <change version>
```

### Script details
Steps:
- Check and report if engines exist in the engines directory.
- Clear previous PGN output file (``games.pgn``)
- Run cutechess-cli with (relevant) params:
  - Time control: 3+0 (3 seconds per side, no increment)
  - Max num of games: 300
  - Concurrency: 5
  - SPRT settings:
    - elo0: 0
    - elo1: 50
    - alpha: 0.05
    - beta: 0.05
  - Openings from 8moves_v3.pgn
  - Output PGN to games.pgn
- When cutechess-cli finished, it prints results to console.
  - SPRT finished when max num of games is reached or when the result is statistically significant.