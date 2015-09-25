package com.fallenritemonk.numbers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fallenritemonk.numbers.db.DatabaseHelper;
import com.fallenritemonk.numbers.db.InitDbAsyncTask;
import com.fallenritemonk.numbers.game.GameActivity;
import com.fallenritemonk.numbers.game.GameModeEnum;
import com.fallenritemonk.numbers.services.GameServicesActivity;

public class MainMenu extends GameServicesActivity {
    private Button resumeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        new InitDbAsyncTask().execute(this);

        Button classicButton = (Button) findViewById(R.id.menu_button_classic_game);
        Button randomButton = (Button) findViewById(R.id.menu_button_random_game);
        resumeButton = (Button) findViewById(R.id.menu_button_resume);
        TextView appVersion = (TextView) findViewById(R.id.app_version);

        classicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(GameModeEnum.CLASSIC, false);
            }
        });
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(GameModeEnum.RANDOM, false);
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
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

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion.setText(getString(R.string.app_version) + " " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        int resumable = dbHelper.getLastStateOrder();

        if (resumable > 0) {
            resumeButton.setVisibility(View.VISIBLE);
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
