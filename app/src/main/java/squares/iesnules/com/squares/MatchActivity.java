package squares.iesnules.com.squares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.logging.Logger;

import squares.iesnules.com.squares.custom_views.BoardView;
import squares.iesnules.com.squares.custom_views.PlayerView;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewDataProvider;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewListener;


public class MatchActivity extends Activity implements BoardViewListener, BoardViewDataProvider {
    private static final String TAG = "MatchActivity";

    private int mNumberOfPlayers;
    //private int mBoardRows = 10;
    //private int mBoardCols = 8;
    private GameEngine mEngine;


    private PlayerView[] mPlayerViews;

    private LinearLayout mPlayersLayout;
    private BoardView mBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_match);

        // Get number of players and create engine
        mNumberOfPlayers = intent.getIntExtra(MainActivity.NUMBER_OF_PLAYERS, 2);

        mPlayersLayout = (LinearLayout)findViewById(R.id.playersLayout);
        mBoardView = (BoardView)findViewById(R.id.boardView);

        mEngine = new GameEngine(mBoardView.getBoardRows(), mBoardView.getBoardCols());

        // Create players views
        mPlayerViews = new PlayerView[mNumberOfPlayers];

        for (int i=0; i<mNumberOfPlayers; i++) {
            PlayerView player = new PlayerView(this);

            player.setPlayerName("Player "+(i+1));
            player.setPlayerScore("0");
            player.setPlayerImage(getResources().getDrawable(R.mipmap.ic_launcher));
            player.setShapeImage(getResources().getDrawable(R.mipmap.ic_launcher));


            mPlayerViews[i] = player;

            mPlayersLayout.addView(player, i);
        }

        mBoardView.setDataProvider(this);
        mBoardView.setListener(this);
        mBoardView.reloadBoard();
    }

    /*
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
    */

    @Override
    public byte stateOfEdgeWithCoordinates(int row, int col, BoardView boardView) {
        return mEngine.getGameState()[row][col];
    }

    @Override
    public byte stateOfSquareWithCoordinates(int row, int col, BoardView boardView) {
       return mEngine.getGameState()[row][col];
    }

    @Override
    public Drawable shapeForPlayerNumber(int playerNumber, BoardView boardView) {
        return mPlayerViews[playerNumber].getShapeImage();
    }

    // BoardView listener methods
    @Override
    public void edgeClickedWithCoordinates(int row, int col, BoardView boardView) {
        // TODO: Process player turn

        Log.d("Edge touched at ("+row+","+col+")", TAG);
    }
}
