package com.iesnules.squares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.iesnules.squares.custom_views.BoardView;
import com.iesnules.squares.custom_views.PlayerView;
import com.iesnules.squares.custom_views.interfaces.BoardViewDataProvider;
import com.iesnules.squares.custom_views.interfaces.BoardViewListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;


public class MatchActivity extends BaseGameActivity implements BoardViewListener,
        BoardViewDataProvider,
        ResultCallback<TurnBasedMultiplayer.LoadMatchResult>{

    private static final String TAG = "MatchActivity";

    private boolean mOnlineMatch;
    private int mNumberOfPlayers;
    private int mTurnPlayerIndex;
    private GameEngine mEngine;

    private ArrayList <String> mPlayerIDs;
    private PlayerView[] mPlayerViews;
    private int[] mShapes = {R.mipmap.triangle_player, R.mipmap.square_player, R.mipmap.star_player, R.mipmap.pentagon_player};

    private LinearLayout mPlayersLayout;
    private BoardView mBoardView;

    private String mMatchID;
    private TurnBasedMatch mMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        setContentView(R.layout.activity_match);

        mPlayersLayout = (LinearLayout)findViewById(R.id.playersLayout);

        mBoardView = (BoardView)findViewById(R.id.boardView);
        mBoardView.setDataProvider(this);
        mBoardView.setListener(this);

        // Get match ID
        mMatchID = intent.getStringExtra(MainActivity.MATCH_ID);

        if (mMatchID != null) { // Online mode
            mOnlineMatch = true;
            mBoardView.setEnabled(false);
        }
        else { // Offline mode
            // Get number of players and create engine
            mNumberOfPlayers = intent.getIntExtra(MainActivity.NUMBER_OF_PLAYERS, 2);

            mEngine = new GameEngine(mBoardView.getBoardRows(), mBoardView.getBoardCols());

            // Create players views
            mPlayerViews = new PlayerView[mNumberOfPlayers];

            for (int i=0; i<mNumberOfPlayers; i++) {
                PlayerView player = new PlayerView(this);

                player.setPlayerName("Player "+(i+1));
                player.setPlayerScore("0");
                player.setPlayerImage(getResources().getDrawable(R.mipmap.player_image));
                player.setShapeImage(getResources().getDrawable(mShapes[i]));


                mPlayerViews[i] = player;

                mPlayersLayout.addView(player, i);
            }

            mTurnPlayerIndex = 0;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mOnlineMatch) {
            mInSignInFlow = true;
            mGoogleApiClient.connect();
        }
        else {
            mBoardView.reloadBoard();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mOnlineMatch) {
            ((PlayerView) mPlayerViews[mTurnPlayerIndex]).setPlayerInTurn(true);
        }
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
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        // Get match
        Games.TurnBasedMultiplayer.loadMatch(mGoogleApiClient, mMatchID).setResultCallback(this);
    }

    @Override
    public void onResult(TurnBasedMultiplayer.LoadMatchResult loadMatchResult) {
        // Check if the status code is not success.
        Status status = loadMatchResult.getStatus();
        if (!status.isSuccess()) {
            BaseGameUtils.showAlert(this, status.getStatusMessage());
            return;
        }

        // Get match
        mMatch = loadMatchResult.getMatch();

        // Get match data & initialize engine
        byte[] data = mMatch.getData();
        if (data == null) { // New match
            mTurnPlayerIndex = 0;
            mEngine = new GameEngine(mBoardView.getBoardRows(), mBoardView.getBoardCols());
        }
        else {
            mTurnPlayerIndex = data[0]; // First byte corresponds player in turn index
            byte[] state = Arrays.copyOfRange(data, 1, data.length - 1);
            mEngine = new GameEngine(state);
        }

        // Get player IDs
        mPlayerIDs = mMatch.getParticipantIds();
        int numberOfParticipants = mPlayerIDs.size();
        mNumberOfPlayers = numberOfParticipants + mMatch.getAvailableAutoMatchSlots();

        /*
        // Get this user playerID
        String userPlayerID = Games.Players.getCurrentPlayerId(mGoogleApiClient);

        int userPlayerIndex = 0;
        */

        // Create players views
        mPlayerViews = new PlayerView[mNumberOfPlayers];

        for (int i=0; i<mNumberOfPlayers; i++) {
            PlayerView playerView = new PlayerView(this);

            String playerName;
            String playerScore;
            Uri playerIconURI;

            if (i < numberOfParticipants) {
                String playerID = mPlayerIDs.get(i);

                Participant player = mMatch.getParticipant(playerID);
                playerName = player.getDisplayName();
                playerScore = String.valueOf(mEngine.numOfCapturedSquaresByPlayer(i + 1));
                playerIconURI = player.getIconImageUri();
            }
            else {
                playerName = getResources().getString(R.string.automatched_player_name);
                playerScore = String.valueOf(0);
                playerIconURI = null;
            }

            playerView.setPlayerName(playerName);
            playerView.setPlayerScore(playerScore);
            playerView.setPlayerImage(getResources().getDrawable(R.mipmap.player_image));
            playerView.setShapeImage(getResources().getDrawable(mShapes[i]));

            mPlayerViews[i] = playerView;

            mPlayersLayout.addView(playerView, i);
        }

        // Mark player in turn
        ((PlayerView) mPlayerViews[mTurnPlayerIndex]).setPlayerInTurn(true);

        // If it's this user's turn enable user interaction.
        boolean isThisUserTurn = mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN;
        mBoardView.setEnabled(isThisUserTurn);
    }

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
        return mPlayerViews[playerNumber - 1].getShapeImage();
    }

    // BoardView listener methods
    @Override
    public void edgeClickedWithCoordinates(int row, int col, BoardView boardView) {
        int newCapturedSquares = mEngine.markEdge(row, col, mTurnPlayerIndex + 1);

        if (newCapturedSquares > 0) { // Update score for player and repeat turn
            ((PlayerView)mPlayerViews[mTurnPlayerIndex]).
                    setPlayerScore(String.valueOf(mEngine.numOfCapturedSquaresByPlayer(mTurnPlayerIndex + 1)));

            if (mOnlineMatch) {
                mBoardView.setEnabled(false);
            }

            // TODO: Check if match has finished
        }
        else {
            ((PlayerView)mPlayerViews[mTurnPlayerIndex]).setPlayerInTurn(false);
            mTurnPlayerIndex = ++mTurnPlayerIndex % mNumberOfPlayers;
            ((PlayerView)mPlayerViews[mTurnPlayerIndex]).setPlayerInTurn(true);
        }

        mBoardView.reloadBoard();

        if (mOnlineMatch) {
            String nextPlayerID = null;

            if (mTurnPlayerIndex < mPlayerIDs.size()) {
                nextPlayerID = mPlayerIDs.get(mTurnPlayerIndex);
            }



            //TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mMatchID, )
        }
    }
}
