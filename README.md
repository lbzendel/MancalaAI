# 🏺 Mancala AI

An AI agent for **Kalah (6-pit Mancala)** using **minimax search with alpha-beta pruning** and **iterative deepening**, built for CSC 242 at the University of Rochester.

## How It Works

### Game Representation

The board is stored as a 14-element array:

```
Index:   0  1  2  3  4  5   [6]   7  8  9  10 11 12  [13]
         ── P1 Pits ──    P1 Store   ── P2 Pits ──   P2 Store
```

Input is read via stdin as 17 space-separated integers per line:

```
<p1_pit1> ... <p1_pit6> <p1_store> <p2_pit1> ... <p2_pit6> <p2_store> <turn> <playerID> <pie>
```

### Search Algorithm

| Component | Details |
|---|---|
| **Search** | Minimax with alpha-beta pruning |
| **Deepening** | Iterative deepening under a 450ms time budget |
| **Heuristic** | Store difference (my store − opponent's store) |
| **Extra turns** | Don't consume search depth — chains are explored fully |
| **Terminal states** | Remaining stones swept into stores before scoring |
| **Pie rule** | Supported — swaps board sides when invoked |

### Game Loop

The agent runs in a loop, reading a board state from stdin, computing the best move, and printing it to stdout. It handles multiple consecutive games cleanly.

## Usage

**Compile:**
```sh
javac Main.java
```

**Run with a single state:**
```sh
echo "4 4 4 4 4 4 0 4 4 4 4 4 4 0 0 1 1" | java Main
```

**Run interactively (pipe states line by line):**
```sh
java Main
```

### Output

A single integer per turn:
- `1`–`6` — pit to move from
- `0` — invoke the pie rule

## Project Structure

```
Main.java       # Everything: parsing, game rules, search, and game loop
```

