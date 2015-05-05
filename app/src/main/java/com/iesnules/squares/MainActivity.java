package com.iesnules.squares;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;


public class MainActivity extends BaseGameActivity implements
        View.OnClickListener,
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {

    private static int RC_SELECT_PLAYERS = 9002;
    private static int RC_LIST_MATCHES = 9003;
    private static int RC_REQUEST_LEADERBOARDS = 9004;

    public static String NUMBER_OF_PLAYERS = "NumberOfPlayers";
    public static String MATCH_ID = "MatchID";



    private boolean mCreatingMatch = false;
    boolean mExplicitSignOut = false;

    private LinearLayout mOptionsLayout;
    private FrameLayout mOverlayLayout;
    private Button mOfflineButton;
    private Button mNewMatchButton;
    private Button mListMatchesButton;
    private Button mQuickMatchButton;
    private Button mLeaderboardsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mOptionsLayout = (LinearLayout)findViewById(R.id.optionsLayout);
        mOverlayLayout = (FrameLayout)findViewById(R.id.overlayLayout);
        mOfflineButton = (Button)findViewById(R.id.offlineButton);
        mNewMatchButton = (Button)findViewById(R.id.newMatchButton);
        mListMatchesButton = (Button)findViewById(R.id.listMatchesButton);
        mQuickMatchButton = (Button)findViewById(R.id.quickMatchButton);
        mLeaderboardsButton = (Button)findViewById(R.id.leaderboardsButton);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            mInSignInFlow = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        mOptionsLayout.setVisibility(View.GONE);
        mOfflineButton.setEnabled(true);

        if (!mInSignInFlow && !mCreatingMatch) {
            mOverlayLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);

        mNewMatchButton.setVisibility(View.VISIBLE);
        mQuickMatchButton.setVisibility(View.VISIBLE);
        mListMatchesButton.setVisibility(View.VISIBLE);
        mLeaderboardsButton.setVisibility(View.VISIBLE);
        mOverlayLayout.setVisibility(View.GONE);

        if (bundle != null) {
            TurnBasedMatch match = bundle.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);
            if (match != null) {
                launchActivityForMatch(match);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        super.onConnectionFailed(connectionResult);

        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        mOverlayLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            mExplicitSignOut = false;
            mGoogleApiClient.connect();
        }
    }

    public void onSignOut() {
        mExplicitSignOut = true;

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        // show sign-in button && hide other buttons
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        mNewMatchButton.setVisibility(View.GONE);
        mQuickMatchButton.setVisibility(View.GONE);
        mListMatchesButton.setVisibility(View.GONE);
        mLeaderboardsButton.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED){
            mExplicitSignOut = true;
            mGoogleApiClient.disconnect();

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            mNewMatchButton.setVisibility(View.GONE);
            mQuickMatchButton.setVisibility(View.GONE);
            mListMatchesButton.setVisibility(View.GONE);
            mLeaderboardsButton.setVisibility(View.GONE);

            return;
        }

        if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode == Activity.RESULT_OK) {
                mCreatingMatch = true;

                // Show overlay...
                mOverlayLayout.setVisibility(View.VISIBLE);

                // Get the invitee list.
                final ArrayList<String> invitees =
                        data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                // Get auto-match criteria.
                Bundle autoMatchCriteria = null;
                int minAutoMatchPlayers = data.getIntExtra(
                        Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers = data.getIntExtra(
                        Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
                if (minAutoMatchPlayers > 0) {
                    autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                            minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                } else {
                    autoMatchCriteria = null;
                }

                TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                        .addInvitedPlayers(invitees)
                        .setAutoMatchCriteria(autoMatchCriteria)
                        .build();

                // Create and start the match.
                Games.TurnBasedMultiplayer
                        .createMatch(mGoogleApiClient, tbmc)
                        .setResultCallback(this);
            }
        }
        else if (requestCode == RC_LIST_MATCHES) {
            if (resultCode == Activity.RESULT_OK) {
                TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

                if (match != null) {
                    launchActivityForMatch(match);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            return super.onPrepareOptionsMenu(menu);
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            onSignOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onShowOfflineOptions(View view) {
        mOfflineButton.setEnabled(false);
        mOptionsLayout.setVisibility(View.VISIBLE);
    }

    public void onLaunchMatchActivity(View view) {
        Intent intent = new Intent(this, MatchActivity.class);

        int viewId = view.getId();

        if (viewId == R.id.twoPlayersButton) {
            intent.putExtra(NUMBER_OF_PLAYERS, 2);
        }
        else if (viewId == R.id.threePlayersButton) {
            intent.putExtra(NUMBER_OF_PLAYERS, 3);
        }
        else if (viewId == R.id.fourPlayersButton) {
            intent.putExtra(NUMBER_OF_PLAYERS, 4);
        }

        startActivity(intent);
    }

    public void onStartNewMatch(View view) {
        mOfflineButton.setEnabled(true);
        mOptionsLayout.setVisibility(View.GONE);

        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    public void onQuickMatch(View view) {
        mOfflineButton.setEnabled(true);
        mOptionsLayout.setVisibility(View.GONE);

        // Show overlay...
        mOverlayLayout.setVisibility(View.VISIBLE);

        // Get auto-match criteria.
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);


        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .setAutoMatchCriteria(autoMatchCriteria)
                .build();

        // Create and start the match.
        Games.TurnBasedMultiplayer
                .createMatch(mGoogleApiClient, tbmc)
                .setResultCallback(this);
    }

    public void onListAllMatches(View view) {
        mOfflineButton.setEnabled(true);
        mOptionsLayout.setVisibility(View.GONE);

        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_LIST_MATCHES);
    }

    public void onDisplayLeaderboards(View view) {
        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),
                RC_REQUEST_LEADERBOARDS);
    }

    @Override
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
        mCreatingMatch = false;

        // Check if the status code is not success.
        Status status = initiateMatchResult.getStatus();
        if (!status.isSuccess()) {
            BaseGameUtils.showAlert(this, status.getStatusMessage());
            return;
        }

        launchActivityForMatch(initiateMatchResult.getMatch());
    }

    private void launchActivityForMatch(TurnBasedMatch match) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(MATCH_ID, match.getMatchId());

        startActivity(intent);

        mOverlayLayout.setVisibility(View.GONE);
    }
}
