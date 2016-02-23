package com.fallenritemonk.ludus.services;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fallenritemonk.ludus.BuildConfig;
import com.fallenritemonk.ludus.R;
import com.fallenritemonk.ludus.game.GameModeEnum;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;

/**
 * Created by FallenRiteMonk on 9/25/15.
 */
public abstract class GameServicesActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    protected GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private boolean explicitSignOut;
    private SharedPreferences persist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        persist = getSharedPreferences(getString(R.string.static_settings_file), 0);
        explicitSignOut = persist.getBoolean(getString(R.string.static_explicit_logout), false);

        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError && !explicitSignOut) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        int tempClassic = persist.getInt(getString(R.string.achievements_persist_classic), 0);
        if (tempClassic > 0) {
            unlockClassicGame(tempClassic);
        }

        int tempRandom= persist.getInt(getString(R.string.achievements_persist_classic), 0);
        if (tempRandom > 0) {
            unlockRandomGame(tempRandom);
        }

        int tempMin= persist.getInt(getString(R.string.leaderboard_persist_minimal), -1);
        if (tempMin > -1) {
            submitScore(tempMin);
        }

        int tempMax= persist.getInt(getString(R.string.leaderboard_persist_maximal), -1);
        if (tempMax > -1) {
            submitScore(tempMax);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (BuildConfig.DEBUG) {
            Log.d("GAMES_SERVICES", "onConnectionSuspended");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mResolvingError) {
            if (result.hasResolution()) {
                try {
                    mResolvingError = true;
                    result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            } else {
                // Show dialog using GoogleApiAvailability.getErrorDialog()
                showErrorDialog(result.getErrorCode());
                mResolvingError = true;
            }
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    private void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((GameServicesActivity) getActivity()).onDialogDismissed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if ((resultCode == RESULT_CANCELED)) {
                setExplicitSignOut(true);
            }
        }
    }

    protected void setExplicitSignOut(boolean explicitSignOut) {
        this.explicitSignOut = explicitSignOut;
        SharedPreferences.Editor editor = persist.edit();
        editor.putBoolean(getString(R.string.static_explicit_logout), this.explicitSignOut);
        editor.apply();
    }

    public void gameWon(GameModeEnum gameMode, int score) {
        if (gameMode == GameModeEnum.CLASSIC) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                unlockClassicGame(1);
            } else {
                persistClassicGame();
            }
        } else if (gameMode == GameModeEnum.RANDOM) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                unlockRandomGame(1);
            } else {
                persistRandomGame();
            }
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            submitScore(score);
        } else {
            persistScore(score);
        }
    }

    private void unlockClassicGame(int victories) {
        Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_first_victory));
        Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_amateur_player), victories);
        Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_pro), victories);
        Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_game_master), victories);

        SharedPreferences.Editor editor = persist.edit();
        editor.putInt(getString(R.string.achievements_persist_classic), 0);
        editor.apply();
    }

    private void persistClassicGame() {
        int tempVictories = persist.getInt(getString(R.string.achievements_persist_classic), 0);
        tempVictories += 1;

        SharedPreferences.Editor editor = persist.edit();
        editor.putInt(getString(R.string.achievements_persist_classic), tempVictories);
        editor.apply();
    }

    private void unlockRandomGame(int victories) {
        Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_first_random_victory));
        Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_amateur_random_player), victories);
        Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_random_pro), victories);
        Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_random_master), victories);

        SharedPreferences.Editor editor = persist.edit();
        editor.putInt(getString(R.string.achievements_persist_random), 0);
        editor.apply();
    }

    private void persistRandomGame() {
        int tempVictories = persist.getInt(getString(R.string.achievements_persist_random), 0);
        tempVictories += 1;

        SharedPreferences.Editor editor = persist.edit();
        editor.putInt(getString(R.string.achievements_persist_random), tempVictories);
        editor.apply();
    }

    private void submitScore(int score) {
        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_combinations_to_victory), score);
        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_minimalist), score);
    }

    private void persistScore(int score) {
        int min = persist.getInt(getString(R.string.leaderboard_persist_minimal), -1);
        int max = persist.getInt(getString(R.string.leaderboard_persist_maximal), -1);

        if (min == -1) {
            min = score;
        } else if (score < min) {
            min = score;
        }
        if (max == -1) {
            max = score;
        } else if (score > max) {
            max = score;
        }

        SharedPreferences.Editor editor = persist.edit();
        editor.putInt(getString(R.string.leaderboard_persist_minimal), min);
        editor.putInt(getString(R.string.leaderboard_persist_maximal), max);
        editor.apply();
    }
}
