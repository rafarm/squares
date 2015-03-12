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
        } else {
            throw new RuntimeException("GameEngine: Invalid board dimensions");
        }
    }

    public GameEngine(byte[][] newState) {
        if (checkGameStateSanity(newState)) {
            mGameState = newState;
        } else {
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
        if (square[1][1] != 0) {
            if (square[0][1] != 0 && square[1][0] != 0 && square[1][2] != 0 && square[2][1] != 0) {
                return true;
            } else {
                return false;
            }
        } else if (square[1][1] == 0) {
            if (square[0][1] == 0 || square[1][0] == 0 || square[1][2] == 0 || square[2][1] == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkEdge(int i, int j) {
        if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
            if (mGameState[i][j] == 0) {
                return false;
            } else {
                return true;
            }

        }


    }

    private boolean

    public int markEdge(int i, int j, int id) {

        if(checkEdge(i,j) == false){
            mGameState[i][j] = 1;

    }
}






            }