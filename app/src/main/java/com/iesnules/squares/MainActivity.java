package com.iesnules.squares;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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

    public static String NUMBER_OF_PLAYERS = "NumberOfPlayers";
    public static String MATCH_ID = "MatchID";
    //public static String EXPLICIT_SIGN_OUT = "ExplicitSignOut";



    //private boolean mSignInClicked = false;
    //boolean mExplicitSignOut = false;

    private LinearLayout mOptionsLayout;
    private FrameLayout mOverlayLayout;
    private Button mOfflineButton;
    private Button mNewMatchButton;

    private int mPlayServicesAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mOptionsLayout = (LinearLayout)findViewById(R.id.optionsLayout);
        mOverlayLayout = (FrameLayout)findViewById(R.id.overlayLayout);
        mOfflineButton = (Button)findViewById(R.id.offlineButton);
        mNewMatchButton = (Button)findViewById(R.id.newMatchButton);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mInSignInFlow) {
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
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        mNewMatchButton.setVisibility(View.VISIBLE);
        mOverlayLayout.setVisibility(View.GONE);
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
            //mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    /*
    public void signOutClicked(View view) {
        mSignInClicked = false;

        mExplicitSignOut = true;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        // show sign-in button, hide the sign-out button
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED){
                //mExplicitSignOut = true;
                mGoogleApiClient.disconnect();

                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                findViewById(R.id.newMatchButton).setVisibility(View.GONE);
            }
            else if (resultCode == Activity.RESULT_OK) {
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
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
        // Check if the status code is not success.
        Status status = initiateMatchResult.getStatus();
        if (!status.isSuccess()) {
            BaseGameUtils.showAlert(this, status.getStatusMessage());
            return;
        }

        // Launch match activity
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(MATCH_ID, initiateMatchResult.getMatch().getMatchId());

        startActivity(intent);

        mOverlayLayout.setVisibility(View.GONE);
    }
}
