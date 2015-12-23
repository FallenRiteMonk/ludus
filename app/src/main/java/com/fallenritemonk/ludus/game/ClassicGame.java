package com.fallenritemonk.ludus.game;

import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;

/**
 * Created by FallenRiteMonk on 23/12/15.
 */
public class ClassicGame extends AbstractGame {
    public ClassicGame(GameActivity activity, FloatingActionButton addFieldsButton, TextView headerCombos, Boolean resume) {
        super(activity, addFieldsButton, headerCombos, resume);
    }

    @Override
    protected void won() {
        super.won();

        activity.gameWon(GameModeEnum.CLASSIC, stateOrder);
    }
}
