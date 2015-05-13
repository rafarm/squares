package com.iesnules.squares;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;
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
import java.util.List;


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
    private int[] mShapes = {R.mipmap.p1_shape,
            R.mipmap.p2_shape,
            R.mipmap.p3_shape,
            R.mipmap.p4_shape};
    private int[] mOverPlayers = {R.mipmap.over_p1,
            R.mipmap.over_p2,
            R.mipmap.over_p3,
            R.mipmap.over_p4};

    private LinearLayout mPlayersLayout;
    private FrameLayout mResultsLayout;
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
        mResultsLayout = (FrameLayout)findViewById(R.id.resultsLayout);

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

        if (id == R.id.action_leave_match) { // Player wants to leave match...
            // TODO: Ask player for confirmation...

            if (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                moveToNextPlayer();
                Games.TurnBasedMultiplayer.
                        leaveMatchDuringTurn(mGoogleApiClient, mMatchID, getNextPlayerID());

            }
            else {
                Games.TurnBasedMultiplayer.leaveMatch(mGoogleApiClient, mMatchID);
            }

            finish();
            return true;
        }
        else if (id == R.id.action_dismiss_match) { // Player wants to dismiss match...
            Games.TurnBasedMultiplayer.dismissMatch(mGoogleApiClient, mMatchID);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            return super.onPrepareOptionsMenu(menu);
        }

        return false;
    }

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

        TurnBasedMatch match = loadMatchResult.getMatch();
        mMatchID = match.getMatchId();

        processMatch(match);

        mResultsLayout.setVisibility(View.GONE);
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
        return getResources().getDrawable(mShapes[playerNumber - 1]);
    }

    private String getLeaderBoardId() {
        String leaderBoardID = null;

        switch (mNumberOfPlayers) {
            case 2:
                leaderBoardID = getString(R.string.two_players_score_leaderboard_id);
                break;
            case 3:
                leaderBoardID = getString(R.string.three_players_score_leaderboard_id);
                break;
            case 4:
                leaderBoardID = getString(R.string.four_players_score_leaderboard_id);
                break;
            default:
                break;
        }

        return leaderBoardID;
    }

    // BoardView listener methods
    @Override
    public void edgeClickedWithCoordinates(int row, int col, BoardView boardView) {
        if (mEngine.markEdge(row, col, mTurnPlayerIndex + 1)) { // Square captured. Check for match completion
            if (mOnlineMatch) {
                // Update leaderboard for current player...
                Games.Leaderboards.submitScore(mGoogleApiClient,
                        getLeaderBoardId(),
                        mEngine.numOfCapturedSquaresByPlayer(mTurnPlayerIndex + 1));
            }

            if (mEngine.matchFinished()) {

                PlayerResult[] playerResults = getPlayerResults();

                if (mOnlineMatch) {
                    processAchievements(playerResults);

                    Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient,
                            mMatchID,
                            getMatchData(),
                            getParticipantResults(playerResults)).
                            setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                                @Override
                                public void onResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
                                    processResult(updateMatchResult);
                                }
                            });
                }
                else {
                    updateUI();
                    showResults(playerResults);
                }

                return;
            }
        }
        else {
            moveToNextPlayer();
        }

        if (mOnlineMatch) {
            mBoardView.setEnabled(false);

            String nextPlayerID = getNextPlayerID();

            Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient,
                    mMatchID,
                    getMatchData(),
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

    private void moveToNextPlayer() {
        mTurnPlayerIndex = ++mTurnPlayerIndex % mNumberOfPlayers;
    }

    private String getNextPlayerID() {
        String nextPlayerID = null;

        if (mTurnPlayerIndex < mPlayerIDs.size()) {
            nextPlayerID = mPlayerIDs.get(mTurnPlayerIndex);
        }

        return nextPlayerID;
    }

    private byte[] getMatchData() {
        byte[] state = mEngine.getData();

        byte[] data = new byte[state.length + 1];
        data[0] = (byte)mTurnPlayerIndex;

        for (int i = 0; i < state.length; i++) {
            data[i+1] = state[i];
        }

        return data;
    }

    private PlayerResult[] getPlayerResults() {
        PlayerResult[] playerResults = new PlayerResult[mNumberOfPlayers];

        for (int i =0; i < mNumberOfPlayers; i++) {
            int score = mEngine.numOfCapturedSquaresByPlayer(i + 1);
            String playerID;

            if (mPlayerIDs != null && i < mPlayerIDs.size()) {
                playerID = mPlayerIDs.get(i);
            }
            else {
                playerID = String.valueOf(i);
            }

            PlayerResult playerResult = new PlayerResult(playerID, mPlayerViews[i], score);
            playerResults[i] = playerResult;
        }

        // Sort & invert results to find winner
        Arrays.sort(playerResults);
        for (int i = 0; i < mNumberOfPlayers/2; i++) {
            PlayerResult temp = playerResults[i];
            playerResults[i] = playerResults[mNumberOfPlayers - i - 1];
            playerResults[mNumberOfPlayers - i - 1] = temp;
        }

        return playerResults;
    }

    private List<ParticipantResult> getParticipantResults(PlayerResult[] playerResults) {
        ArrayList<ParticipantResult> participantResults = null;

        if (playerResults != null && playerResults.length > 1) {
            int winnerScore = playerResults[0].getScore();
            boolean tie = winnerScore == playerResults[1].getScore();

            // Build participant results list
            participantResults = new ArrayList<ParticipantResult>();

            int placing = 1;
            for (int i = 0; i < mNumberOfPlayers; i++) {
                PlayerResult playerResult = playerResults[i];
                String playerID = playerResult.getPlayerID();
                int score = playerResult.getScore();
                int result;

                if (score == winnerScore) {
                    result = tie ? ParticipantResult.MATCH_RESULT_TIE : ParticipantResult.MATCH_RESULT_WIN;
                }
                else {
                    result = ParticipantResult.MATCH_RESULT_LOSS;
                    placing++;
                }

                ParticipantResult participantResult = new ParticipantResult(playerID, result, placing);

                participantResults.add(participantResult);
            }
        }

        return participantResults;
    }

    private void processResult(TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {
        // Check if the status code is not success.
        Status status = updateMatchResult.getStatus();
        if (!status.isSuccess()) {
            BaseGameUtils.showAlert(this, status.getStatusMessage());
            return;
        }

        processMatch(updateMatchResult.getMatch());
    }

    private void processMatch(TurnBasedMatch match) {
        setupMatch(match);
        updateUI();

        if (match.getStatus() == TurnBasedMatch.MATCH_STATUS_COMPLETE) {
            PlayerResult[] playerResults = getPlayerResults();

            showResults(playerResults);

            if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                processAchievements(playerResults);

                Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, match.getMatchId());
            }
        }
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
            mPlayerIDs = null;
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
            Uri playerIconURI;

            if (!playerView.getIsParticipant()) {
                if (i < mNumberOfOnlineParticipants) {
                    String playerID = mPlayerIDs.get(i);

                    Participant player = mMatch.getParticipant(playerID);
                    playerName = player.getDisplayName();
                    playerIconURI = player.getIconImageUri();

                    playerView.setIsParticipant(true);
                }
                else {
                    playerName = getString(R.string.player) + " " + (i+1);
                    playerIconURI = null;
                }

                playerView.setPlayerName(playerName);
                mImageManager.loadImage(playerView.getPlayerImage(),playerIconURI,R.mipmap.player_image );
                playerView.setShapeImage(getResources().getDrawable(mShapes[i]));
            }

            playerView.setOverPlayerNoTurnDrawable(getResources().getDrawable(R.mipmap.over_p_looser));
            playerView.setOverPlayerInTurnDrawable(getResources().getDrawable(mOverPlayers[i]));
            playerView.setPlayerScore(String.valueOf(mEngine.numOfCapturedSquaresByPlayer(i + 1)));

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

    private void showResults(PlayerResult[] playerResults) {
        Button accept = (Button)findViewById(R.id.resultsOKButton);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultsLayout.setVisibility(View.GONE);
            }
        });

        if (playerResults != null && playerResults.length > 1) {
            final Button rematch = (Button)findViewById(R.id.resultsRematchButton);
            if (!mOnlineMatch || mMatch.canRematch()) {
                rematch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rematch();

                        if (!mOnlineMatch) {
                            mResultsLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
            else {
                rematch.setVisibility(View.GONE);
            }


            TextView title = (TextView)findViewById(R.id.resultsTitleTextView);
            TextView wins = (TextView)findViewById(R.id.winsTextView);

            //String titleText = null;
            if (playerResults[0].getScore() == playerResults[1].getScore()) { // Tie
                title.setText(getString(R.string.tie));
                wins.setText(null);
            }
            else {
                title.setText(playerResults[0].getPlayeView().getPlayerName());
                wins.setText(getString(R.string.win));
            }

            //title.setText(titleText);

            // Signal winners
            for (int i = 0; i < mNumberOfPlayers; i++) {
                PlayerView playerView = playerResults[i].getPlayeView();

                if (i == 0 ||
                        (playerResults[i].getScore() == playerResults[0].getScore())) {
                    playerView.setPlayerInTurn(true);
                }
                else {
                    playerView.setPlayerInTurn(false);
                }
            }
        }

        mResultsLayout.setVisibility(View.VISIBLE);
    }

    private void rematch() {
        if (mOnlineMatch) {
            Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, mMatchID).
                    setResultCallback(new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
                            processRematch(initiateMatchResult);
                        }
                    });
        }
        else {
            setupMatch(null);
            updateUI();
        }
    }

    private void processRematch(TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
        Status status = initiateMatchResult.getStatus();
        if (status.getStatusCode() == GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED) {
            String rematchID = mMatch.getRematchId();

            Games.TurnBasedMultiplayer.loadMatch(mGoogleApiClient, rematchID).
                    setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.LoadMatchResult loadMatchResult) {
                            processResult(loadMatchResult);
                        }
                    });

            return;
        }

        if (!status.isSuccess()) {
            BaseGameUtils.showAlert(this, status.getStatusMessage());
            return;
        }

        TurnBasedMatch match = initiateMatchResult.getMatch();
        mMatchID = match.getMatchId();

        setupMatch(match);
        updateUI();

        mResultsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        if (turnBasedMatch.getMatchId().equals(mMatchID)) {
            if (turnBasedMatch.getStatus() == TurnBasedMatch.MATCH_STATUS_CANCELED) {
                notifyMatchCancellation();
                finish();
            }

            processMatch(turnBasedMatch);
        }
        else {
            // TODO: Notify match update
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String matchID) {
        if (matchID.equals(mMatchID)) {
            notifyMatchCancellation();
            finish();
        }
    }

    private void notifyMatchCancellation() {
        // TODO: Notify the player that this match has been cancelled...
    }

    @Override
    public void onBackPressed() {
        if(mOnlineMatch){
            super.onBackPressed();
        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MatchActivity.this);

            // set dialog message
            alertDialogBuilder
                    .setTitle(getString(R.string.DialogMessage))
                    .setMessage(getString(R.string.DialogTitle))
                    .setCancelable(false)

                    .setNegativeButton(getString(R.string.NegativeButton), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(getString(R.string.PositiveButton),new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            finish();
                        }
                    }).show();
        }
    }

    private void processAchievements(PlayerResult[] playerResults) {
        // TODO: Process and notify google servers user's achievements

    };
}

class PlayerResult implements Comparable<PlayerResult> {
    private String mPlayerID;
    private PlayerView mPlayerView;
    private int mScore = 0;

    public PlayerResult(String playerID, PlayerView playerView, int score) {
        mPlayerID = playerID;
        mPlayerView = playerView;
        mScore = score;
    }

    public String getPlayerID() {
        return mPlayerID;
    }

    public PlayerView getPlayeView() {
        return mPlayerView;
    }

    public int getScore() {
        return mScore;
    }

    @Override
    public int compareTo(PlayerResult another) {
        if (another == null) {
            throw new NullPointerException("Unable to be compared with a null object.");
        }

        int comparison = 0;

        if (another.getScore() < mScore) {
            comparison = 1;
        }
        else if (another.getScore() > mScore) {
            comparison = -1;
        }

        return comparison;
    }

}
