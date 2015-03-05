package squares.iesnules.com.squares;

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
        // TODO: Check if game state represented by 'state' is valid
        return false;
    }

    private boolean checkSquareSanity(byte[][] square) {
        // TODO: Check if a square state represented by 3x3 matrix 'square' is valid
        // get[1] [1]
        return false;
    }


}
