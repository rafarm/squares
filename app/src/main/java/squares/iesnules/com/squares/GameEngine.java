package squares.iesnules.com.squares;

import android.graphics.Matrix;

/**
 * Created by rafa on 3/3/15.
 */
public class GameEngine {

    private static int MAX_ROWS = 100;
    private static int MAX_COLS = 100;

    private byte[][] mGameState;


    public GameEngine(int rows, int cols) throws RuntimeException {
        if (rows > 0 && cols > 0) {
            mGameState = generateGameState(rows, cols);
        }
        else {
            throw new RuntimeException("GameEngine: Invalid board dimensions");
        }
    }

    public GameEngine(byte[][] newState ) {
        if (checkGameStateSanity(newState)) {
            mGameState = newState;
        }
        else {
            throw new RuntimeException("GameEngine: Invalid game state");
        }
    }

    private byte[][] generateGameState(int rows, int cols) {
        // TODO: Generate an empty board state array
        return new byte[1][1];
    }

    private boolean checkGameStateSanity(byte[][] state) {
        for (int i = 1; i < state.length; i += 2){
            for (int j = 1; j < state[i].length; j += 2){
                byte[][] submatrix = get3x3SubMatrix(state, i, j);

            }
        }
        return true;
    }
    public byte[][] get3x3SubMatrix(byte[][] state, int i, int j) {
        byte[][] subMatrix = new byte[3][3];

        for (int n = 0; n < 3; n++) {
            for (int m = 0; m < 3; m++) {
                subMatrix[n][m] = state[i-1][j-1];
            }
        }

        return subMatrix;
    }

    private boolean checkSquareSanity(byte[][] square) {
        // TODO: Check if a square state represented by 3x3 matrix 'square' is valid
        return false;
    }


}
