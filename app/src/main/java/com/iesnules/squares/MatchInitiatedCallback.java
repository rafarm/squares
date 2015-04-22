package com.iesnules.squares;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.nio.charset.Charset;

/**
 * Created by alumne on 22/04/15.
 */
public class MatchInitiatedCallback extends GameEngine  implements ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>{
    private GoogleApiClient mGoogleApiClient;
    private byte[][] state = getGameState();

    public MatchInitiatedCallback(byte[][] newState) {
        super(newState);
    }

    @Override
    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        // Check if the status code is not success.
        Status status = result.getStatus();
        if (status.isSuccess()) {
            showError(status.getStatusCode());
            return;
        }

        TurnBasedMatch match = result.getMatch();

        // If this player is not the first player in this match, continue.
        if (match.getData() != null) {
            showTurnUI(match);
            return;
        }

        // Otherwise, this is the first player. Initialize the game state.
        initGame(match);

        // Let the player take the first turn
        showTurnUI(match);
    }
    public void initGame(TurnBasedMatch match) {
        String initialise = "initialised";
        Games.TurnBasedMultiplayer.takeTurn(
                mGoogleApiClient,
                match.getMatchId(),
                state,
                null
        ).setResultCallback(this);
    }

}
