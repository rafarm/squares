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

    //This method is executed when a new game is started, showing the initial matrix,the valid and invalid values.
    private byte[][] generateGameState(int rows, int cols) {

        byte[][] matrix = new byte[rows][cols];

        for(int j=0;j<100;j++){
            for(int i=0;i<100;i++){
                if(j%2!= 0){
                    matrix[i][j] = 0;
                    //if the result is 0, the player will can press in this
                }
                else{
                    if (i%2!=0){
                        matrix[i][j]=0;
                        //if the result is 0, the player will can press in this
                    }
                    else{
                        matrix[i][j]=-1;
                        //if the result is -1,the player won't can press in this.The result -1 is an invalid value
                    }
                }
            }
        }

        return matrix;
    }

    private boolean checkGameStateSanity(byte[][] state) {
        // TODO: Check if game state represented by 'state' is valid
        return false;
    }

    private boolean checkSquareSanity(byte[][] square) {
        // TODO: Check if a square state represented by 3x3 matrix 'square' is valid
        return false;
    }

    private boolean squareCaptured(byte[][] square) {
        // TODO: Check if a square represented by 3x3 matrix 'square' has been captured

        if (square[0][1]==1 && square[1][0]==1 && square[1][2]==1 && square[2][1]==1){
        //The four sides of the 3x3 matrix are occupied by both the matrix will be captured
            return true;

        }

        //If instead the four sides are not busy the matrix will not be occupied
       return false;

    }
}
