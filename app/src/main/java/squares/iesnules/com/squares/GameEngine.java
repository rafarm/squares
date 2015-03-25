package squares.iesnules.com.squares;

import android.graphics.Matrix;

/**
 * Created by rafa on 3/3/15.
 */
public class GameEngine {

    private static int MAX_ROWS = 20;
    private static int MAX_COLS = 20;

    private byte[][] mGameState;


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
        } else {
            throw new RuntimeException("GameEngine: Invalid game state");
        }
    }

    public byte[][] getGameState() {
        return mGameState;
    }

    //This method is executed when a new game is started, showing the initial matrix,the valid and invalid values.
    private byte[][] generateGameState(int rows, int cols) {

        int realRows = 2*rows + 1;
        int realCols = 2*cols + 1;

        byte[][] matrix = new byte[realRows][realCols];

        for (int j = 0; j < realCols; j++) {
            for (int i = 0; i < realRows; i++) {
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
                if(checkSquareSanity( get3x3SubMatrix(state, i, j))==false){
                    return false;
                }

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

    private byte[][] generateSquare(int n, int m){

        byte[][] matrix = new byte[3][3];
        matrix[0][0]= mGameState[n-1][m-1];
        matrix[1][0]= mGameState[n][m-1];
        matrix[2][0]= mGameState[n+1][m-1];
        matrix[0][1]= mGameState[n-1][m];
        matrix[1][1]= mGameState[n][m];
        matrix[2][1]= mGameState[n+1][m];
        matrix[0][2]= mGameState[n-1][m+1];
        matrix[1][2]= mGameState[n][m+1];
        matrix[2][2]= mGameState[n+1][m+1];


        return matrix;

    }


    private boolean squareCaptured(byte[][] square) {
        if (square[0][1] == 1 && square[1][0] == 1 && square[1][2] == 1 && square[2][1] == 1 ){
            return true;
        }
        return false;
    }



    public int markEdge(int i, int j, int id, int rows, int cols) {
        int counter = 0;

        if (!edgeIsChecked(i, j)) {
            mGameState[i][j] = 1;
            if (isEdgeVertical(i, j)) {

                if (i != 0 && i != cols - 1) {              // comprueba las casillas que no estan en los laterales
                    int n = i -1;
                    int m = j;

                    if (squareCaptured(generateSquare(n,m))){
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }

                    n = i +1;
                    m = j;

                    if (squareCaptured(generateSquare(n,m))) {
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }
                }
                else if(i == 0){                             // comprueba la casilla del lateral izquierdo
                    int n = i + 1;
                    int m = j;

                        if (squareCaptured(generateSquare(n,m))) {
                        mGameState[n][m] = (byte) id;
                        counter++;
                        }

                    }
                else if(i == cols - 1){                      // comprueba la casilla del lateral derecho
                    int n = i - 1;
                    int m = j;

                    if (squareCaptured(generateSquare(n,m))) {
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }

                }


            }

            else{
                if (j != 0 && j != rows - 1) {              // comprueba las casillas que no estan en los laterales
                    int n = i ;
                    int m = j-1;

                    if (squareCaptured(generateSquare(n,m))){
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }

                    n = i;
                    m = j+1;

                    if (squareCaptured(generateSquare(n,m))) {
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }
                }
                else if(j == 0){                             // comprueba la casilla del lateral superior
                    int n = i;
                    int m = j + 1;

                    if (squareCaptured(generateSquare(n,m))) {
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }

                }
                else if(j == rows -1 ){                      // comprueba la casilla del lateral inferior
                    int n = i ;
                    int m = j-1;

                    if (squareCaptured(generateSquare(n,m))) {
                        mGameState[n][m] = (byte) id;
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
                if (mGameState[i][j] == playerID){
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
                if (mGameState[i][j] > 0){
                    total++;

                }

            }
        }
        return total;
    }
}
