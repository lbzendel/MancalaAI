public class Main {

    static long deadline; // time limit for search

    static class TimeoutException extends RuntimeException {}

    static class GameState {
        int[] board;
        int turn;
        int playerID;
        int pie;

        GameState(int[] board, int turn, int playerID, int pie) {
            this.board = board;
            this.turn = turn;
            this.playerID = playerID;
            this.pie = pie;
        }
    }

    public static void main(String[] args) {
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length != 17) continue;
                GameState state = parseGameState(parts);
                int move = bestMove(state);
                System.out.println(move);
                System.out.flush();
            }
        } catch (java.io.IOException e) {
            // stdin closed, exit
        }
    }

    public static GameState parseGameState(String[] parts) {
        int[] board = new int[14];
        for (int i = 0; i < 14; i++) {
            board[i] = Integer.parseInt(parts[i]);
        }
        int turn     = Integer.parseInt(parts[14]);
        int playerID = Integer.parseInt(parts[15]);
        int pie      = Integer.parseInt(parts[16]);
        return new GameState(board, turn, playerID, pie);
    }


    public static java.util.List<Integer> legalMoves(GameState s) {
        java.util.List<Integer> moves = new java.util.ArrayList<>();
        if (s.pie == 1) {
            moves.add(0); 
        }
        int start = (s.playerID == 1) ? 0 : 7;
        for (int i = 0; i < 6; i++) {
            if (s.board[start + i] > 0) {
                moves.add(i + 1);
            }
        }
        return moves;
    }    

    public static GameState applyMove(GameState s, int move) {
        int[] newBoard = s.board.clone(); // clone board to modify

        // Pie rule: swap sides
        if (move == 0) {
            for (int i = 0; i < 6; i++) {
                int temp = newBoard[i];
                newBoard[i] = newBoard[7 + i];
                newBoard[7 + i] = temp;
            }
            int temp = newBoard[6];
            newBoard[6] = newBoard[13];
            newBoard[13] = temp;
            int nextPlayer = (s.playerID == 1) ? 2 : 1;
            return new GameState(newBoard, s.turn + 1, nextPlayer, 0);
        }

        int playerOffset = (s.playerID == 1) ? 0 : 7; // check whose turn it is
        int myStore = (s.playerID == 1) ? 6 : 13;
        int oppStore = (s.playerID == 1) ? 13 : 6;

        int stones = newBoard[playerOffset + move - 1]; // get stones from selected pit
        newBoard[playerOffset + move - 1] = 0;
        int index = playerOffset + move - 1;

        while (stones > 0) { // add stones to subsequent pits
            index = (index + 1) % 14; 
            if (index == oppStore) continue; // skip opponent's store
            newBoard[index]++;
            stones--;
        }

        // Check for extra turn
        boolean extraTurn = (index == myStore);

        // Check for capture
        if (!extraTurn && index >= playerOffset && index < playerOffset + 6 && newBoard[index] == 1) {
            int oppositeIndex = 12 - index;
            newBoard[myStore] += newBoard[oppositeIndex] + 1;
            newBoard[index] = 0;
            newBoard[oppositeIndex] = 0;
        }

        int nextPlayer = extraTurn ? s.playerID : (s.playerID == 1 ? 2 : 1); // switch player if no extra turn
        return new GameState(newBoard, s.turn + 1, nextPlayer, s.pie);
    }


    public static boolean isTerminal(GameState s) {
        boolean p1Empty = true, p2Empty = true;
        for (int i = 0; i < 6; i++) {
            if (s.board[i] > 0) p1Empty = false;
            if (s.board[7 + i] > 0) p2Empty = false;
        }
        return p1Empty || p2Empty;
    }

    public static int finalScore(GameState s, int maxPlayer) {
        // Sweep remaining stones into stores
        int p1Total = s.board[6];
        int p2Total = s.board[13];
        for (int i = 0; i < 6; i++) {
            p1Total += s.board[i];
            p2Total += s.board[7 + i];
        }
        if (maxPlayer == 1) return p1Total - p2Total;
        else return p2Total - p1Total;
    }

    public static int evaluate(GameState s, int maxPlayer) {
        if (isTerminal(s)) return finalScore(s, maxPlayer);
        if (maxPlayer == 1) {
            return s.board[6] - s.board[13];
        } else {
            return s.board[13] - s.board[6];
        }
    }

    public static int minimax(GameState s, int depth, int alpha, int beta, int maxPlayer) {
        if (System.currentTimeMillis() >= deadline) throw new TimeoutException();
        if (depth == 0 || isTerminal(s)) { // if the game is over or we've reached max depth, evaluate the board
            return evaluate(s, maxPlayer); 
        }

        java.util.List<Integer> moves = legalMoves(s);
        if (moves.isEmpty()) {
            return evaluate(s, maxPlayer);
        }

        if (s.playerID == maxPlayer) {
            // maximizing
            int best = Integer.MIN_VALUE;
            for (int move : moves) {
                GameState child = applyMove(s, move);
                // Don't decrement depth on extra turns (same player moves again)
                int nextDepth = (child.playerID == s.playerID) ? depth : depth - 1;
                int score = minimax(child, nextDepth, alpha, beta, maxPlayer);
                best = Math.max(best, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            // minimizing
            int best = Integer.MAX_VALUE;
            for (int move : moves) {
                GameState child = applyMove(s, move);
                int nextDepth = (child.playerID == s.playerID) ? depth : depth - 1;
                int score = minimax(child, nextDepth, alpha, beta, maxPlayer);
                best = Math.min(best, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    // iterative deepening: search deeper until time runs out
    public static int bestMove(GameState s) {
        deadline = System.currentTimeMillis() + 450; // 450ms to leave margin
        java.util.List<Integer> moves = legalMoves(s);
        int bestMove = moves.get(0); // fallback

        for (int depth = 1; depth <= 50; depth++) {
            try {
                int bestScore = Integer.MIN_VALUE;
                int candidate = moves.get(0);
                for (int move : moves) {
                    GameState child = applyMove(s, move);
                    int score = minimax(child, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, s.playerID);
                    if (score > bestScore) {
                        bestScore = score;
                        candidate = move;
                    }
                }
                bestMove = candidate;  // if completed search at depth i, update best move - prevents using move from incomplete deeper search
            } catch (TimeoutException e) {
                System.out.println("Time limit reached at depth " + depth);
                break;
            }
            if (System.currentTimeMillis() >= deadline) {
                System.out.println("Time limit reached at depth " + depth);
                break;
            }
        }
        return bestMove;
    }



}

