package com.fallenritemonk.numbers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fallenritemonk.numbers.db.DatabaseHelper;
import com.fallenritemonk.numbers.db.InitDbAsyncTask;
import com.fallenritemonk.numbers.game.GameActivity;
import com.fallenritemonk.numbers.game.GameModeEnum;

public class MainMenu extends AppCompatActivity {
    private TextView resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SharedPreferences persist = getSharedPreferences(getString(R.string.static_settings_file), 0);
        if (persist.getBoolean(getString(R.string.static_first_launch), true)) {
            showTutorial();
        }

        LudusApplication application = (LudusApplication) getApplication();
        application.getAnalyticsTracker();

        new InitDbAsyncTask().execute(this);

        TextView newClassic = (TextView) findViewById(R.id.new_classic_game);
        TextView newRandom = (TextView) findViewById(R.id.new_random_game);
        resume = (TextView) findViewById(R.id.resume_game);
        TextView tutorial = (TextView) findViewById(R.id.tutorial);
        TextView preview = (TextView) findViewById(R.id.preview_text);

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
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();
            }
        });
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.fallenritemonk.ludus"));
                startActivity(intent);
            }
        });

        appVersion.setText(getString(R.string.app_version) + " " + application.getAppVersion());
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
}
