public class Search {
    /*
    public static int miniMax(GameState state, int depth) {
        if (depth == 0) {
            return evaluate(state);
        }
        int bestValue = Integer.MIN_VALUE;
        for (GameState child : getChildren(state)) {
            int value = -miniMax(child, depth - 1);
            bestValue = Math.max(bestValue, value);
        }
        return bestValue;
    }

    public static int evaluate(GameState state) {
        if (state.playerID == 1) {
            return state.board[6] - state.board[13];
        } else {
            return state.board[13] - state.board[6];
        }
    }
        */
}
