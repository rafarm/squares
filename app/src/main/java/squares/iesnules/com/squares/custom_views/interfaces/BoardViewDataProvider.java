package squares.iesnules.com.squares.custom_views.interfaces;

import android.graphics.drawable.Drawable;

import squares.iesnules.com.squares.custom_views.BoardView;

/**
 * Created by rafa on 22/3/15.
 */
public interface BoardViewDataProvider {
    // Asks the provider for board number of rows
    public int rowsOfBoard(BoardView boardView);

    // Asks the provider for board number of cols
    public int colsOfBoard(BoardView boardView);

    // Asks the provider about the status of the edge with coordinates (row,col)
    public byte stateOfEdgeWithCoordinates(int row, int col, BoardView boardView);

    // Asks the provider about the status of the square with coordinates (row,col)
    public byte stateOfSquareWithCoordinates(int row, int col, BoardView boardView);

    // Asks the provider for the shape background of a square captured by player num
    public Drawable shapeForPlayerNumber(int playerNumber, BoardView boardView);
}
