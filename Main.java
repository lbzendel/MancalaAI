
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
            System.out.println("1");
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
}

