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


    public int markEdge(int i, int j, int id, int rows, int cols) {
        int counter = 0;

        if (!edgeIsChecked(i, j)) {
            mGameState[i][j] = 1;
            if (isEdgeVertical(i, j)) {

                if (i != 0 && i != cols - 1) {
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
                else if
                }
            }

            else{
                if (j != rows - 1 && j != 0){
                    int n = i;
                    int m = j - 1;
                    if(squareCaptured(generateSquare(n,m))){
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }

                    n = i;
                    m = j + 1;

                    if(squareCaptured(generateSquare(n,m))){
                        mGameState[n][m] = (byte) id;
                        counter++;
                    }





             }




            }
        }
        return counter;
    }
}

