package squares.iesnules.com.squares;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import squares.iesnules.com.squares.custom_views.BoardView;
import squares.iesnules.com.squares.custom_views.PlayerView;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewDataProvider;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewListener;


public class MatchActivity extends ActionBarActivity implements BoardViewListener, BoardViewDataProvider {

    private int mNumberOfPlayers;
    private int mBoardRows = 8;
    private int mBoardCols = 6;
    private GameEngine mEngine;

    private PlayerView[] mPlayerViews;

    private LinearLayout mPlayersLayout;
    private FrameLayout mBoardLayout;
    private BoardView mBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_match);

        // Get number of players and create engine
        mNumberOfPlayers = intent.getIntExtra(MainActivity.NUMBER_OF_PLAYERS, 2);
        mEngine = new GameEngine(mBoardRows, mBoardCols);

        mPlayersLayout = (LinearLayout)findViewById(R.id.playersLayout);
        mBoardLayout = (FrameLayout)findViewById(R.id.boardLayout);

        // Create players views
        mPlayerViews = new PlayerView[mNumberOfPlayers];

        for (int i=0; i<mNumberOfPlayers; i++) {
            PlayerView player = new PlayerView(this);

            player.setPlayerName("Player "+(i+1));
            player.setPlayerScore("0");
            player.setPlayerImage(getResources().getDrawable(R.mipmap.ic_launcher));
            // TODO: Set player image & shape

            mPlayerViews[i] = player;

            mPlayersLayout.addView(player, i);
        }

        // Create board view
        mBoardView = new BoardView(this);
        mBoardView.setDataProvider(this);
        mBoardView.setListener(this);

        mBoardLayout.addView(mBoardView);
        mBoardView.reloadBoard();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // BoardView data provider methods
    @Override
    public int rowsOfBoard(BoardView boardView) {
        return mBoardRows;
    }

    @Override
    public int colsOfBoard(BoardView boardView) {
        return mBoardCols;
    }

    @Override
    public byte stateOfEdgeWithCoordinates(int row, int col, BoardView boardView) {
        return mEngine.getGameState()[row][col];
    }

    @Override
    public byte stateOfSquareWithCoordinates(int row, int col, BoardView boardView) {
        // TODO: Return the state of board's square with coordinates (row, col) as represented in the engine's state
        return 0;
       return mEngine.
    }

    @Override
    public Drawable shapeForPlayerNumber(int playerNumber, BoardView boardView) {
        // TODO: Return the shape assigned to the player with number 'playerNumber'
        return null;
    }

    // BoardView listener methods
    @Override
    public void edgeClickedWithCoordinates(int row, int col, BoardView boardView) {
        // TODO: Process player turn
    }
}
