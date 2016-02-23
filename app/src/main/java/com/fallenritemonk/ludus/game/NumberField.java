package com.fallenritemonk.ludus.game;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
public class NumberField {
    public enum STATE {UNUSED, USED, SELECTED, HINT}

    private final int number;
    private STATE state = STATE.UNUSED;

    public NumberField(int number) {
        this.number = number;
    }

    public NumberField(String fieldState) {
        String[] field = fieldState.split("/");
        number = Integer.parseInt(field[0]);
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
    }

    public String stringify() {
        return number + "/" + state;
    }
}
