import java.math.*;
public class Main {
    // board layout: [p1_1..p1_6, p1Store, p2_1..p2_6, p2Store] = 14 slots
    // input: p1_1..p1_6 p1Store p2_1..p2_6 p2Store turn playerID pie  (17 ints)

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
        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (sc.hasNextLine()) {
            if (!sc.hasNextInt()) {
                sc.nextLine();
                continue;
            }
            GameState state = parseGameState(sc);
            int move = bestMove(state, 3);
            System.out.println(move);
    
        }
    }

    public static GameState parseGameState(java.util.Scanner sc) {
        int[] board = new int[14];
        for (int i = 0; i < 14; i++) {
            board[i] = sc.nextInt();
        }
        int turn     = sc.nextInt();
        int playerID = sc.nextInt();
        int pie      = sc.nextInt();
        return new GameState(board, turn, playerID, pie);
    }


    public static java.util.List<Integer> legalMoves(GameState s) {
        java.util.List<Integer> moves = new java.util.ArrayList<>();
        if (s.pie == 1) {
            moves.add(0); // pie rule
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


    public static boolean isTerminal(GameState s) { // checks if the game has ended
        boolean p1Empty = true, p2Empty = true;
        for (int i = 0; i < 6; i++) {
            if (s.board[i] > 0) p1Empty = false;
            if (s.board[7 + i] > 0) p2Empty = false;
        }
        return p1Empty || p2Empty;
    }

    public static int evaluate(GameState s, int maxPlayer) {
        if (maxPlayer == 1) {
            return s.board[6] - s.board[13];
        } else {
            return s.board[13] - s.board[6];
        }
    }

    public static int minimax(GameState s, int depth, int alpha, int beta, int maxPlayer) {
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
                int score = minimax(child, depth - 1, alpha, beta, maxPlayer);
                best = Math.max(best, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break; // prune branches where alpha is greater than or equal to beta (opponent has a better option)
            }
            return best;
        } else {
            // minimizing
            int best = Integer.MAX_VALUE;
            for (int move : moves) {
                GameState child = applyMove(s, move);
                int score = minimax(child, depth - 1, alpha, beta, maxPlayer);
                best = Math.min(best, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    // wrapper class that runs minimax and returns the best move
    public static int bestMove(GameState s, int depth) {
        java.util.List<Integer> moves = legalMoves(s);
        int bestScore = Integer.MIN_VALUE;
        int bestMove = moves.get(0);

        for (int move : moves) {
            GameState child = applyMove(s, move);
            int score = minimax(child, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, s.playerID);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }



}

