package squares.iesnules.com.squares.custom_views.interfaces;

import squares.iesnules.com.squares.custom_views.BoardView;

/**
 * Created by rafa on 22/3/15.
 */
public interface BoardViewListener {

    // Informs listener that player touched the edge with coords (row, col)
    public void edgeClickedWithCoordinates(int row, int col, BoardView boardView);
}
