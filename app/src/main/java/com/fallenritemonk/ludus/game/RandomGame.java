package com.fallenritemonk.ludus.game;

import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.Random;

/**
 * Created by FallenRiteMonk on 23/12/15.
 */
public class RandomGame extends AbstractGame {

    public RandomGame(GameActivity activity, FloatingActionButton addFieldsButton, TextView headerCombos, Boolean resume) {
        super(activity, addFieldsButton, headerCombos, resume);
    }

    @Override
    protected void initFields() {
        super.initFields();

        Collections.shuffle(fieldArray, new Random(System.nanoTime()));
        notifyDataSetChanged();
    }

    @Override
    protected void won() {
        super.won();

        activity.gameWon(GameModeEnum.RANDOM, stateOrder);
    }
}
