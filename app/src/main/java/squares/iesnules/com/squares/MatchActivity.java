package squares.iesnules.com.squares;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import squares.iesnules.com.squares.custom_views.BoardView;
import squares.iesnules.com.squares.custom_views.PlayerView;


public class MatchActivity extends ActionBarActivity {

    private int mNumberOfPlayers;
    private int mBoardRows = 8;
    private int mBoardCols = 6;
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
        mEngine = new GameEngine(mBoardRows, mBoardCols);

        mPlayersLayout = (LinearLayout)findViewById(R.id.playersLayout);
        mBoardView = (BoardView)findViewById(R.id.boardView);

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

}
