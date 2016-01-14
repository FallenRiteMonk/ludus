package com.fallenritemonk.ludus.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.fallenritemonk.ludus.R;
import com.fallenritemonk.ludus.services.GameServicesActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
public class GameActivity extends GameServicesActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static AbstractGame gameField;
    private GridView gameFieldView;

    private InterstitialAd hintInterstitialAd;
    private InterstitialAd undoInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hintInterstitialAd = new InterstitialAd(this);
        undoInterstitialAd = new InterstitialAd(this);
        hintInterstitialAd.setAdUnitId(getString(R.string.game_hint_ad_unit_id));
        undoInterstitialAd.setAdUnitId(getString(R.string.game_undo_ad_unit_id));

        hintInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial(hintInterstitialAd);
                gameField.hint(gameFieldView);
            }
        });
        undoInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial(undoInterstitialAd);
                gameField.undo();
            }
        });
        requestNewInterstitial(hintInterstitialAd);
        requestNewInterstitial(undoInterstitialAd);

        Intent intent = getIntent();
        GameModeEnum gameMode = (GameModeEnum) intent.getSerializableExtra(getString(R.string.static_game_mode));
        Boolean resume = intent.getBooleanExtra(getString(R.string.static_game_resume), false);

        gameFieldView = (GridView) findViewById(R.id.fieldGrid);
        FloatingActionButton addFieldsButton = (FloatingActionButton) findViewById(R.id.add_fields_fab);

        View header = ((NavigationView) findViewById(R.id.game_menu_drawer)).getHeaderView(0);
        TextView headerGameMode = (TextView) header.findViewById(R.id.header_game_mode);
        TextView headerCombos = (TextView) header.findViewById(R.id.header_combos);

        switch (gameMode) {
            case CLASSIC: gameField = new ClassicGame(this, addFieldsButton, headerCombos, resume);
                break;
            case RANDOM: gameField = new RandomGame(this, addFieldsButton, headerCombos, resume);
                break;
        }

        gameFieldView.setAdapter(gameField);

        gameFieldView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {
                gameField.clicked(id);
            }
        });
        addFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameField.addFields(gameFieldView);
            }
        });

        if (gameMode == GameModeEnum.CLASSIC) {
            headerGameMode.setText(R.string.classic_game);
        } else if (gameMode == GameModeEnum.RANDOM) {
            headerGameMode.setText(R.string.random_game);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.game_menu_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_hint) {
            if (hintInterstitialAd.isLoaded()) {
                hintInterstitialAd.show();
            } else {
                gameField.hint(gameFieldView);
            }
        } else if (id == R.id.action_undo) {
            if (undoInterstitialAd.isLoaded()) {
                undoInterstitialAd.show();
            } else {
                gameField.undo();
            }
        } else if (id == R.id.action_restart) {
            restartDialog();
        } else if (id == R.id.action_menu) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void requestNewInterstitial(InterstitialAd interstitialAd) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("58A7B09CCA80B0F0F68D1781BB963472")
                .build();

        interstitialAd.loadAd(adRequest);
    }
}
