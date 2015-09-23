package com.fallenritemonk.numbers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fallenritemonk.numbers.db.InitDbAsyncTask;
import com.fallenritemonk.numbers.game.GameActivity;
import com.fallenritemonk.numbers.game.GameModeEnum;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        new InitDbAsyncTask().execute(this);

        Button classicButton = (Button) findViewById(R.id.menu_button_classic_game);
        Button randomButton = (Button) findViewById(R.id.menu_button_random_game);

        classicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(GameModeEnum.CLASSIC);
            }
        });
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame(GameModeEnum.RANDOM);
            }
        });
    }

    private void launchGame(GameModeEnum gameMode) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(getString(R.string.static_game_mode), gameMode);
        startActivity(gameIntent);
    }
}
