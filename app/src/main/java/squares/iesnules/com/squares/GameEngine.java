package squares.iesnules.com.squares;

import android.graphics.Matrix;

/**
 * Created by rafa on 3/3/15.
 */
public class GameEngine {

    private static int MAX_ROWS = 20;
    private static int MAX_COLS = 20;

    private byte[][] mGameState;
    private int mRealRows;
    private int mRealCols;

    public GameEngine(int rows, int cols) throws RuntimeException {
        if (rows > 0 && cols > 0 && rows <= MAX_ROWS && cols <= MAX_COLS) {
            mGameState = generateGameState(rows, cols);
        } else {
            throw new RuntimeException("GameEngine: Invalid board dimensions");
        }
    }

    public GameEngine(byte[][] newState) {
        if (checkGameStateSanity(newState)) {
            mGameState = newState;
            mRealRows = mGameState.length;
            mRealCols = mGameState[0].length;
        } else {
            throw new RuntimeException("GameEngine: Invalid game state");
        }
    }

    public byte[][] getGameState() {
        return mGameState;
    }

    //This method is executed when a new game is started, showing the initial matrix,the valid and invalid values.
    private byte[][] generateGameState(int rows, int cols) {

        mRealRows = 2 * rows + 1;
        mRealCols = 2 * cols + 1;

        byte[][] matrix = new byte[mRealRows][mRealCols];

        for (int j = 0; j < mRealCols; j++) {
            for (int i = 0; i < mRealRows; i++) {
                if (j % 2 != 0) {
                    matrix[i][j] = 0;
                    //if the result is 0, the player will can press in this
                } else {
                    if (i % 2 != 0) {
                        matrix[i][j] = 0;
                        //if the result is 0, the player will can press in this
                    } else {
                        matrix[i][j] = -1;
                        //if the result is -1,the player won't can press in this.The result -1 is an invalid value
                    }
                }
            }
        }

        return matrix;
    }

    private boolean checkGameStateSanity(byte[][] state) {
        for (int i = 1; i < state.length; i += 2) {
            for (int j = 1; j < state[i].length; j += 2) {
                if (!checkSquareSanity(get3x3SubMatrix(i, j, state))) {
                    return false;
                }

            }

        }


        return true;
    }


    private boolean checkSquareSanity(byte[][] square) {
// TODO: Check if a square state represented by 3x3 matrix 'square' is valid
        if (square[1][1] != 0) {
            if (square[0][1] == 1 && square[1][0] == 1 && square[1][2] == 1 && square[2][1] == 1) {
                return true;
            }
        } else if (square[1][1] == 0) {
            if (square[0][1] == 0 || square[1][0] == 0 || square[1][2] == 0 || square[2][1] == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean edgeIsChecked(int i, int j) {
        if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
            if (mGameState[i][j] == 0) {
                return false;
            }
        }
        return true;
    }


    //Comprueba si es horizontal o vertical
    private boolean isEdgeVertical(int i, int j) {
        if (i % 2 == 0 && j % 2 != 0) {
            return true; //Vertical
        }
        return false;//Horizontal
    }

    private byte[][] get3x3SubMatrix(int n, int m, byte[][] state) {

        byte[][] subMatrix = new byte[3][3];
        subMatrix[0][0] = state[n - 1][m - 1];
        subMatrix[1][0] = state[n - 1][m];
        subMatrix[2][0] = state[n - 1][m + 1];
        subMatrix[0][1] = state[n][m - 1];
        subMatrix[1][1] = state[n][m];
        subMatrix[2][1] = state[n][m + 1];
        subMatrix[0][2] = state[n + 1][m - 1];
        subMatrix[1][2] = state[n + 1][m];
        subMatrix[2][2] = state[n + 1][m + 1];


        return subMatrix;

    }


    private boolean squareCaptured(byte[][] square) {
        if (square[0][1] == 1 && square[1][0] == 1 && square[1][2] == 1 && square[2][1] == 1) {
            return true;
        }
        return false;
    }


    public int markEdge(int edgeRow, int edgeCol, int playerID) {
        int counter = 0;

        if (!edgeIsChecked(edgeRow, edgeCol)) {
            mGameState[edgeRow][edgeCol] = 1;
            if (isEdgeVertical(edgeRow, edgeCol)) {

                if (edgeRow != 0 && edgeRow != -1) {              // comprueba las casillas que no estan en los laterales
                    int n = edgeRow - 1;
                    int m = edgeCol;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }

                    n = edgeRow + 1;
                    m = edgeCol;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }
                } else if (edgeRow == 0) {                             // comprueba la casilla del lateral izquierdo
                    int n = edgeRow + 1;
                    int m = edgeCol;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }

                } else if (edgeRow == mRealCols - 1) {                      // comprueba la casilla del lateral derecho
                    int n = edgeRow - 1;
                    int m = edgeCol;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }

                }


            } else {
                if (edgeCol != 0 && edgeCol != mRealRows - 1) {              // comprueba las casillas que no estan en los laterales
                    int n = edgeRow;
                    int m = edgeCol - 1;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }

                    n = edgeRow;
                    m = edgeCol + 1;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }
                } else if (edgeCol == 0) {                             // comprueba la casilla del lateral superior
                    int n = edgeRow;
                    int m = edgeCol + 1;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }

                } else if (edgeCol == mRealRows - 1) {                      // comprueba la casilla del lateral inferior
                    int n = edgeRow;
                    int m = edgeCol - 1;

                    if (squareCaptured(get3x3SubMatrix(n, m, mGameState))) {
                        mGameState[n][m] = (byte) playerID;
                        counter++;
                    }

                }


            }


        }

        return counter;
    }
    /*    if (square[0][1] == 1 && square[1][0] == 1 && square[1][2] == 1 && square[2][1] == 1) {
            //The four sides of the 3x3 matrix are occupied by both the matrix will be captured
            return true;

        }

        //If instead the four sides are not busy the matrix will not be captured
        return false;

    }*/

    /*
    numOfCapturedSquaresByPlayer(int playerID)
    playerID: Player identifier

    Returns the number of squares captured by player with playerID
     */
    public int numOfCapturedSquaresByPlayer(int playerID) {
        int total = 0;

        for (int i = 1; i < mGameState.length; i += 2) {
            for (int j = 1; j < mGameState[i].length; j += 2) {
                if (mGameState[i][j] == playerID) {
                    total++;

                }

            }
        }
        return total;
    }

    /*
    numOfCapturedSquares()

    Returns the total number of squares captured
     */
    public int numOfCapturedSquares() {
        int total = 0;

        for (int i = 1; i < mGameState.length; i += 2) {
            for (int j = 1; j < mGameState[i].length; j += 2) {
                if (mGameState[i][j] > 0) {
                    total++;

                }

            }
        }
        return total;
    }

    public boolean gameFinished(){
        int numSquares = ((mRealRows-1)/2)*((mRealCols-1)/2);
        if(!(numOfCapturedSquares() == (numSquares))){
            return false;
        }
        return true;
    }

}
