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
            if(state.playerID == 1) {
                for (int i = 0; i < 6; i++) {
                    if(state.board[i] > 0) {
                        System.out.println(i+1);
                    }
                }
            } else {
                for (int i = 7; i < 13; i++) {
                    if(state.board[i] > 0) {
                        System.out.println(i-6);
                    }
                }
            }
    
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
        int playerOffset = (s.playerID == 1) ? 0 : 7; // check whose turn it is
        int myStore = (s.playerID == 1) ? 6 : 13;
        int oppStore = (s.playerID == 1) ? 13 : 6;

        int stones = newBoard[playerOffset + move - 1];
        newBoard[playerOffset + move - 1] = 0;
        int index = playerOffset + move - 1;

        while (stones > 0) {
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

        int nextPlayer = extraTurn ? s.playerID : (s.playerID == 1 ? 2 : 1);
        return new GameState(newBoard, s.turn + 1, nextPlayer, s.pie);
    }




}

