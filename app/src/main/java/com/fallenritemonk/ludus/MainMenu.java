package com.fallenritemonk.ludus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fallenritemonk.ludus.db.DatabaseHelper;
import com.fallenritemonk.ludus.db.InitDbAsyncTask;
import com.fallenritemonk.ludus.game.GameActivity;
import com.fallenritemonk.ludus.game.GameModeEnum;
import com.fallenritemonk.ludus.services.GameServicesActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.Games;

import org.codechimp.apprater.AppRater;

public class MainMenu extends GameServicesActivity {
    private TextView resume;
    private TextView signIn;
    private TextView signOut;
    private TextView achievements;
    private TextView leaderboards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        AppRater.app_launched(this, 3, 10, 3, 10);
        //AppRater.setVersionNameCheckEnabled(true);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("58A7B09CCA80B0F0F68D1781BB963472")
                .build();
        mAdView.loadAd(adRequest);

        SharedPreferences persist = getSharedPreferences(getString(R.string.static_settings_file), 0);
        if (persist.getBoolean(getString(R.string.static_first_launch), true)) {
            showTutorial();
        }

        new InitDbAsyncTask().execute(this);

        TextView newClassic = (TextView) findViewById(R.id.new_classic_game);
        TextView newRandom = (TextView) findViewById(R.id.new_random_game);
        resume = (TextView) findViewById(R.id.resume_game);
        signIn = (TextView) findViewById(R.id.game_services_sign_in);
        signOut = (TextView) findViewById(R.id.game_services_sign_out);
        achievements = (TextView) findViewById(R.id.game_services_achievements);
        leaderboards = (TextView) findViewById(R.id.game_services_leaderboards);
        TextView tutorial = (TextView) findViewById(R.id.tutorial);

        TextView appVersion = (TextView) findViewById(R.id.app_version);

        newClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(GameModeEnum.CLASSIC, false);
            }
        });
        newRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(GameModeEnum.RANDOM, false);
            }
        });
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(getString(R.string.static_settings_file), 0);
                String lastGame = settings.getString(getString(R.string.static_last_game), "");

                if (lastGame.equals(GameModeEnum.CLASSIC.toString())) {
                    Log.d("MAIN", "resume classic");
                    launchGame(GameModeEnum.CLASSIC, true);
                } else if (lastGame.equals(GameModeEnum.RANDOM.toString())) {
                    Log.d("MAIN", "resume random");
                    launchGame(GameModeEnum.RANDOM, true);
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExplicitSignOut(false);
                if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExplicitSignOut(true);
                if (mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }

                // show sign-in button, hide the sign-out button
                signIn.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.GONE);
                achievements.setVisibility(View.GONE);
                leaderboards.setVisibility(View.GONE);
            }
        });
        achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1002);
                }
            }
        });
        leaderboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), 1003);
                }
            }
        });
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();
            }
        });

        appVersion.setText(getString(R.string.app_version) + ((LudusApplication) getApplication()).getAppVersion());
    }

    private void showTutorial() {
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        int resumable = dbHelper.getLastStateOrder();

        if (resumable > 0) {
            resume.setVisibility(View.VISIBLE);
        } else {
            resume.setVisibility(View.GONE);
        }
    }

    private void launchGame(GameModeEnum gameMode, Boolean resume) {
        SharedPreferences settings = getSharedPreferences(getString(R.string.static_settings_file), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.static_last_game), gameMode.toString());
        editor.apply();

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(getString(R.string.static_game_mode), gameMode);
        gameIntent.putExtra(getString(R.string.static_game_resume), resume);
        startActivity(gameIntent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        signIn.setVisibility(View.GONE);
        signOut.setVisibility(View.VISIBLE);
        achievements.setVisibility(View.VISIBLE);
        leaderboards.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        super.onConnectionFailed(result);

        signIn.setVisibility(View.VISIBLE);
        signOut.setVisibility(View.GONE);
        achievements.setVisibility(View.GONE);
        leaderboards.setVisibility(View.GONE);
    }
}
