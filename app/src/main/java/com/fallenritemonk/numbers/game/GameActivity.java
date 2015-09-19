package com.fallenritemonk.numbers.game;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fallenritemonk.numbers.R;

import java.util.ArrayList;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
public class GameActivity extends AppCompatActivity {
    private GameField gameField;
    private FloatingActionButton addFieldsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GridView gameFieldView = (GridView) findViewById(R.id.fieldGrid);
        addFieldsButton = (FloatingActionButton) findViewById(R.id.addFields);

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
    }
}
