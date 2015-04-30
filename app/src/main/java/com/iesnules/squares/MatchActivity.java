package com.iesnules.squares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
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
import java.util.Iterator;


public class MatchActivity extends BaseGameActivity implements BoardViewListener,
        BoardViewDataProvider, OnTurnBasedMatchUpdateReceivedListener {

    private static final String TAG = "MatchActivity";

    // How long to show toasts.
    final static int TOAST_DELAY = Toast.LENGTH_SHORT;

    private boolean mOnlineMatch;
    private int mNumberOfPlayers;
    private int mNumberOfOnlineParticipants;
    private int mTurnPlayerIndex;
    private GameEngine mEngine;

    private ArrayList <String> mPlayerIDs;
    private PlayerView[] mPlayerViews;
    private int[] mShapes = {R.mipmap.triangle_player, R.mipmap.square_player, R.mipmap.star_player, R.mipmap.pentagon_player};

    private LinearLayout mPlayersLayout;
    private BoardView mBoardView;

    private String mMatchID;
    private TurnBasedMatch mMatch;

    private ImageManager mImageManager;

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

        mImageManager = ImageManager.create(this);

        if (mMatchID != null) { // Online mode
            mOnlineMatch = true;
            mBoardView.setEnabled(false);
        }
        else { // Offline mode
            // Get number of players and create engine
            mNumberOfPlayers = intent.getIntExtra(MainActivity.NUMBER_OF_PLAYERS, 2);

            // Setup an empty match
            setupMatch(null);

            updateUI();
            /*
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
            */
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

        updatePlayerInTurn();
    }

    @Override
    protected void onStop() {
        if (mOnlineMatch) {
            Games.TurnBasedMultiplayer.unregisterMatchUpdateListener(mGoogleApiClient);
        }

        super.onStop();
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

        // Register for match updates
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);

        // Load match
        Games.TurnBasedMultiplayer.loadMatch(mGoogleApiClient, mMatchID).
                setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.LoadMatchResult loadMatchResult) {
                        processResult(loadMatchResult);
                    }
                });
    }

    private void processResult(TurnBasedMultiplayer.LoadMatchResult loadMatchResult) {
        // Check if the status code is not success.
        Status status = loadMatchResult.getStatus();
        if (!status.isSuccess()) {
            BaseGameUtils.showAlert(this, status.getStatusMessage());
            return;
        }

        // Get match
        setupMatch(loadMatchResult.getMatch());

        updateUI();
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
        if (mEngine.markEdge(row, col, mTurnPlayerIndex + 1)) { // Update score for player and repeat turn
            // TODO: Check if match has finished
        }
        else {
            mTurnPlayerIndex = ++mTurnPlayerIndex % mNumberOfPlayers;
        }

        if (mOnlineMatch) {
            mBoardView.setEnabled(false);

            String nextPlayerID = null;

            if (mTurnPlayerIndex < mPlayerIDs.size()) {
                nextPlayerID = mPlayerIDs.get(mTurnPlayerIndex);
            }

            byte[] state = mEngine.getData();
            byte[] data = new byte[state.length + 1];
            data[0] = (byte)mTurnPlayerIndex;
            for (int i = 0; i < state.length; i++) {
                data[i+1] = state[i];
            }

            Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient,
                    mMatchID,
                    data,
                    nextPlayerID).
                    setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                            processResult(updateMatchResult);
                        }
                    });
        }
        else {
            //mBoardView.reloadBoard();
            updateUI();
        }
    }

    private void processResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
        // Check if the status code is not success.
        Status status = updateMatchResult.getStatus();
        if (!status.isSuccess()) {
            BaseGameUtils.showAlert(this, status.getStatusMessage());
            return;
        }

        setupMatch(updateMatchResult.getMatch());

        updateUI();
    }

    private void setupMatch(TurnBasedMatch match) {
        if (match != null) {
            mMatch = match;

            // Get player IDs
            mPlayerIDs = mMatch.getParticipantIds();
            mNumberOfOnlineParticipants = mPlayerIDs.size();
            mNumberOfPlayers = mNumberOfOnlineParticipants + mMatch.getAvailableAutoMatchSlots();

            // Get match data & initialize engine
            byte[] data = mMatch.getData();
            if (data == null) { // New match
                newMatch();
            }
            else {
                mTurnPlayerIndex = data[0]; // First byte corresponds player in turn index
                byte[] state = Arrays.copyOfRange(data, 1, data.length);
                mEngine = new GameEngine(state);
            }
        }
        else {
            newMatch();
            mNumberOfOnlineParticipants = 0;
        }

    }

    private void newMatch() {
        mTurnPlayerIndex = 0;
        mEngine = new GameEngine(mBoardView.getBoardRows(), mBoardView.getBoardCols());
    }

    private void updateUI() {
        // Create players views reusing old ones
        PlayerView[] oldPlayerViews = mPlayerViews;
        int numberOfOldViews = 0;
        if (oldPlayerViews != null) {
            numberOfOldViews = oldPlayerViews.length;
        }
        mPlayerViews = new PlayerView[mNumberOfPlayers];

        for (int i=0; i<mNumberOfPlayers; i++) {
            PlayerView playerView;

            if (i < numberOfOldViews) {
                playerView = oldPlayerViews[i];
            }
            else {
                playerView = new PlayerView(this);
                mPlayersLayout.addView(playerView, i);
            }

            String playerName;
            String playerScore;
            Uri playerIconURI;

            if (i < mNumberOfOnlineParticipants) {
                String playerID = mPlayerIDs.get(i);

                Participant player = mMatch.getParticipant(playerID);
                playerName = player.getDisplayName();
                playerIconURI = player.getIconImageUri();
            }
            else {
                playerName = "Player "+(i+1);
                playerIconURI = null;
            }
            playerScore = String.valueOf(mEngine.numOfCapturedSquaresByPlayer(i + 1));

            playerView.setPlayerName(playerName);
            playerView.setPlayerScore(playerScore);
            //playerView.setPlayerImage(getResources().getDrawable(R.mipmap.player_image));
            mImageManager.loadImage(playerView.getPlayerImage(),playerIconURI,R.mipmap.player_image );
            //playerView.setShapeImage(getResources().getDrawable(mShapes[i]));



            mPlayerViews[i] = playerView;
        }

        updatePlayerInTurn();

        mBoardView.reloadBoard();
    }

    private void updatePlayerInTurn() {
        if (mPlayerViews != null) {
            for(int i = 0; i < mPlayerViews.length; i++ ) {
                mPlayerViews[i].setPlayerInTurn(false);
            }

            // Mark player in turn
            ((PlayerView) mPlayerViews[mTurnPlayerIndex]).setPlayerInTurn(true);

            // If it's this user's turn enable user interaction.
            if (mOnlineMatch) {
                boolean isThisUserTurn = mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN;
                mBoardView.setEnabled(isThisUserTurn);
            }
        }
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        if (turnBasedMatch.getMatchId().equals(mMatchID)) {
            setupMatch(turnBasedMatch);
            updateUI();
        }
        else {
            // TODO: Notify match update
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
        // TODO: To be completed...
    }
}
