package com.iesnules.squares;

import android.graphics.Matrix;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by rafa on 3/3/15.
 */
public class GameEngine {
    private static String TAG = "com.iesnules.squares.GameEngine";

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

    public GameEngine(byte[] data) {
        mRealCols = data[0];
        mRealRows = (data.length - 1) / mRealCols;

        mGameState = new byte[mRealRows][mRealCols];
        for (int i = 0; i < mRealRows; i++) {
            int start = i * mRealCols + 1;
            int end = (i + 1) * mRealCols + 1;
            mGameState[i] = Arrays.copyOfRange(data, start, end);
        }

        if (!checkGameStateSanity()) {
            throw new RuntimeException("GameEngine: Invalid game state");
        }
    }

    public int getRows() {
        return (mRealRows - 1) / 2;
    }

    public int getCols() {
        return (mRealCols - 1) / 2;
    }

    public int getTotalSquares() {
        return getRows() * getCols();
    }

    public boolean matchFinished() {
        return getTotalSquares() == numOfCapturedSquares();
    }

    public byte[] getData() {
        byte[] data = new byte[mRealRows * mRealCols + 1];

        // Store row size
        data[0] = (byte)mRealCols;

        // Store matrix
        for (int i = 0; i < mRealRows; i++) {
            for (int j = 0; j < mRealCols; j++) {
                int pos = i * mRealCols + j + 1;
                data[pos] = mGameState[i][j];
            }
        }

        return data;
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

    public boolean isGameStateValid() {
        return checkGameStateSanity();
    }

    private boolean checkGameStateSanity() {
        // Check matrix dimensions uniformity
        for (int i = 0; i < mRealRows; i++) {
            if (mGameState[i].length != mRealCols) {
                return false;
            }
        }

        for (int i = 1; i < mRealRows; i += 2) {
            for (int j = 1; j < mRealCols; j += 2) {
                if (!checkSquareSanity(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }


    private boolean checkSquareSanity(int squareRow, int squareCol) {

        // Get a reference to submatrix elements
        byte s01 = mGameState[squareRow - 1][squareCol];
        byte s10 = mGameState[squareRow][squareCol - 1];
        byte s11 = mGameState[squareRow][squareCol];
        byte s12 = mGameState[squareRow][squareCol + 1];
        byte s21 = mGameState[squareRow + 1][squareCol];

        return (s11 != 0 && (s01 == 1 && s10 == 1 && s12 == 1 && s21 == 1)) ||
                (s11 == 0 && (s01 == 0 || s10 == 0 || s12 == 0 || s21 == 0));
    }

    private boolean edgeIsMarked(int i, int j) {
        if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
            return mGameState[i][j] != 0;
        }

        /*
        Log.w(TAG, "Coordinates passed don't correspond to an edge.");
        return true;
        */
        throw new RuntimeException("GameEngine: Invalid coordinates for an edge.");
    }


    //Comprueba si es horizontal o vertical
    private boolean isEdgeVertical(int i, int j) {
        if (i % 2 == 0 && j % 2 != 0) {
            return false; //Horizontal
        }
        else if (i % 2 != 0 && j % 2 == 0) {
            return true; //Vertical
        }

        throw new RuntimeException("GameEngine: Invalid coordinates for an edge.");
    }

    private boolean shouldSquareBeCaptured(int squareRow, int squareCol) {

        // Get a reference to submatrix elements
        byte s01 = mGameState[squareRow - 1][squareCol];
        byte s10 = mGameState[squareRow][squareCol - 1];
        byte s12 = mGameState[squareRow][squareCol + 1];
        byte s21 = mGameState[squareRow + 1][squareCol];

        return s01 == 1 && s10 == 1 && s12 == 1 && s21 == 1;
    }


    public boolean markEdge(int edgeRow, int edgeCol, int playerID) {
        boolean squareCaptured = false;

        if (!edgeIsMarked(edgeRow, edgeCol)) {
            mGameState[edgeRow][edgeCol] = 1; // Mark edge

            if (isEdgeVertical(edgeRow, edgeCol)) {

                if (edgeCol > 0) { // Check left square
                    if (shouldSquareBeCaptured(edgeRow, edgeCol - 1)) {
                        mGameState[edgeRow][edgeCol - 1] = (byte) playerID;
                        squareCaptured = true;
                    }
                }

                if (edgeCol < (mRealCols - 1)) { // Check right square
                    if (shouldSquareBeCaptured(edgeRow, edgeCol + 1)) {
                        mGameState[edgeRow][edgeCol + 1] = (byte) playerID;
                        squareCaptured = true;
                    }
                }
            } else { // Horizontal edge

                if (edgeRow > 0) { // Check upper square
                    if (shouldSquareBeCaptured(edgeRow - 1, edgeCol)) {
                        mGameState[edgeRow - 1][edgeCol] = (byte) playerID;
                        squareCaptured = true;
                    }
                }

                if (edgeRow < (mRealRows - 1)) { // Check lower square
                    if (shouldSquareBeCaptured(edgeRow + 1, edgeCol)) {
                        mGameState[edgeRow + 1][edgeCol] = (byte) playerID;
                        squareCaptured = true;
                    }
                }
            }
        }

        return squareCaptured;
    }

    /*
    numOfCapturedSquaresByPlayer(int playerID)
    playerID: Player identifier

    Returns the number of squares captured by player with playerID
     */
    public int numOfCapturedSquaresByPlayer(int playerID) {
        int total = 0;

        for (int i = 1; i < mRealRows; i += 2) {
            for (int j = 1; j < mRealCols; j += 2) {
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

        for (int i = 1; i < mRealRows; i += 2) {
            for (int j = 1; j < mRealCols; j += 2) {
                if (mGameState[i][j] != 0) {
                    total++;
                }
            }
        }
        return total;
    }


    public boolean  gameFinished(){
        return numOfCapturedSquares() == ((mRealRows - 1) / 2) * ((mRealCols - 1) / 2);
    }







}
