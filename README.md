# KnightClubbingEngine

# About
KnightClubbingEngine is a Java-based UCI chess engine that uses KnightClubbingLogic for chess logic.

<!-- TOC -->
* [KnightClubbingEngine](#knightclubbingengine)
* [About](#about)
* [Setup](#setup)
  * [Prerequisites](#prerequisites)
  * [OpeningBookMaker](#openingbookmaker)
  * [Running](#running)
* [Testing](#testing)
  * [SPRT usage](#sprt-usage)
    * [Setup](#setup-1)
    * [Running](#running-1)
    * [Script details](#script-details)
* [UCI overview](#uci-overview)
<!-- TOC -->

# Setup
## Prerequisites
- Java 17 or higher installed
- Maven installed
- Github PAT in ```~/.m2/settings.xml``` for dependency access, should look like/contain this:
```xml
<settings>
    <server>
        <id>github</id>
        <username>your-github-username</username>
        <password>your-personal-access-token</password>
    </server>
</settings>
```

## OpeningBookMaker
To make use of this engine, you need to have an opening book running as postgres container.
- Compose the container
    - Compose the container in KnightClubbingLogic's resource folder named `kce-compose.yaml`.
- Run OpeningBookMaker.java
  - (`src/main/java/knight/clubbing/opening/OpeningBookMaker.java`)
  - This will populate the opening book database based on the PGN file given using stockfish.
    - PGN file at `src/main/resources/lichess_db_standard_rated_2016-12.pgn` hardcoded.
    - Stockfish at `stockfish/stockfish` hardcoded.

`Note: the engine and tests need the opening book database running (atm) to work.`

## Running
To build the project, run:
```shell
mvn clean package
```
Then run the created jar in the `target` folder, for example:
```shell
java -jar target/knightclubbingengine-<version>.jar
```

# Testing

## SPRT usage
### Setup
- Java 17 installed
- cutechess-cli installed
  - I downloaded the source code from Github and compiled it myself. Works for now.
- Preferably 2 chess knightclubbingengine's in .jar format to test.
  - Paste them in the ``test-sprt/engines`` directory.

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

# UCI overview

UCI commands supported by this engine:

- uci
    - Engine announces id/author and replies "uciok".
    - Example:
      ```
      uci
      ```
    - Expected output:
      ```
      id name KnightClubbing
      id author Ralf van Aert
      uciok
      ```

- isready
    - Check engine readiness. Engine replies "readyok".
    - Example:
      ```
      isready
      ```
    - Expected output:
      ```
      readyok
      ```

- position
    - Set current board position. Supported forms:
        - Start position:
          ```
          position startpos
          ```
        - Start position + moves:
          ```
          position startpos moves e2e4 e7e5 g1f3
          ```
        - FEN (with optional moves):
          ```
          position fen <FEN>
          position fen <FEN> moves e2e4 ...
          ```
    - Moves must be UCI format (e2e4, g1f3, etc.).

- go
    - Start the search. Supported parameters:
        - wtime <ms>  — white remaining time (ms)
        - btime <ms>  — black remaining time (ms)
        - winc <ms>   — white increment (ms)
        - binc <ms>   — black increment (ms)
        - depth <n>   — search depth
    - The engine derives per-move time from wtime/btime and increments if provided; otherwise a default move time is used.
    - Examples:
      ```
      go depth 8
      go wtime 300000 btime 300000 winc 0 binc 0 depth 10
      ```
    - Note: other common UCI go parameters (movetime, nodes, mate, ponder, searchmoves, infinite) are not (yet) parsed by this engine.

- stop
    - Interrupts a running search. Engine should respond with a bestmove when interrupted.
    - Example:
      ```
      stop
      ```

- quit
    - Interrupts any running search and exits the process.
    - Example:
      ```
      quit
      ```

- bestmove (engine output)
    - After search completes or is interrupted, engine outputs:
      ```
      bestmove <uci>
      ```
      or, if no legal move:
      ```
      bestmove 0000
      ```

- setoption
    - Not (yet) implemented: options are not exposed via UCI setoption at this time.