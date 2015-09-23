package com.fallenritemonk.numbers.game;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fallenritemonk.numbers.R;
import com.fallenritemonk.numbers.db.InitDbAsyncTask;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
public class GameActivity extends AppCompatActivity {
    private static GameField gameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        new InitDbAsyncTask().execute(this);

        GridView gameFieldView = (GridView) findViewById(R.id.fieldGrid);
        FloatingActionButton addFieldsButton = (FloatingActionButton) findViewById(R.id.addFields);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.game_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.game_menu_drawer);

        gameField = new GameField(this, addFieldsButton);
        gameFieldView.setAdapter(gameField);

        gameFieldView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {
                gameField.clicked(id);
            }
        });

        addFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameField.addFields();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.action_hint) {
                    gameField.hint();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.action_restart) {
                    restartDialog();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }

                return false;
            }
        });
    }

    private void restartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_restart_title);
        builder.setMessage(R.string.confirm_restart_message);

        builder.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gameField.newGame();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
