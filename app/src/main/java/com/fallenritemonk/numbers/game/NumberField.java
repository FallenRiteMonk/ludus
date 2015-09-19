package com.fallenritemonk.numbers.game;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
public class NumberField {
    public enum STATE {UNUSED, USED, SELECTED, HINT}

    private final int number;
    private STATE state = STATE.UNUSED;
    private final GameFieldAdapter adapter;

    public NumberField(int number, GameFieldAdapter adapter) {
        this.number = number;
        this.adapter = adapter;
    }

    public NumberField(String fieldState, GameFieldAdapter adapter) {
        this.adapter = adapter;
        String[] field = fieldState.split("/");
        number = Integer.valueOf(field[0]);
        switch (field[1]) {
            case "UNUSED": state = STATE.UNUSED;
                break;
            case "USED": state = STATE.USED;
                break;
            case "SELECTED": state = STATE.SELECTED;
                break;
            case "HINT": state = STATE.HINT;
                break;
        }
    }

    public int getNumber() {
        return number;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
        adapter.notifyDataSetChanged();
    }

    public String stringify() {
        return number + "/" + state;
    }
}
