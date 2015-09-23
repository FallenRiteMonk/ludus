package com.fallenritemonk.numbers.game;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenritemonk.numbers.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
class GameField extends BaseAdapter {
    private final Context context;
    private final FloatingActionButton addFieldsButton;
    private final TextView headerCombos;
    private final GameModeEnum gameMode;
    private final DatabaseHelper dbHelper;

    private ArrayList<NumberField> fieldArray;
    private ArrayList<CombinePos> possibilities = new ArrayList<>();
    private int selectedField;
    private int hint;
    private int stateOrder;

    public GameField(Context context, FloatingActionButton addFieldsButton, TextView headerCombos, GameModeEnum gameMode, Boolean resume) {
        this.context = context;
        this.addFieldsButton = addFieldsButton;
        this.headerCombos = headerCombos;
        this.gameMode = gameMode;

        dbHelper = DatabaseHelper.getInstance(context);

        if (resume) {
            resumeGame();
        } else {
            newGame();
        }
    }

    public void newGame() {
        fieldArray = new ArrayList<>();
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(2));
        fieldArray.add(new NumberField(3));
        fieldArray.add(new NumberField(4));
        fieldArray.add(new NumberField(5));
        fieldArray.add(new NumberField(6));
        fieldArray.add(new NumberField(7));
        fieldArray.add(new NumberField(8));
        fieldArray.add(new NumberField(9));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(2));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(3));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(4));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(5));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(6));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(7));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(8));
        fieldArray.add(new NumberField(1));
        fieldArray.add(new NumberField(9));

        if (gameMode == GameModeEnum.RANDOM) {
            Collections.shuffle(fieldArray, new Random(System.nanoTime()));
        }

        notifyDataSetChanged();

        selectedField = -1;
        hint = -1;
        stateOrder = -1;

        findPossibilities();

        dbHelper.clearDB();
        saveState();
    }

    private void resumeGame() {
        fieldsFromString(dbHelper.getLastState());
        selectedField = -1;
        hint = -1;
        stateOrder = dbHelper.getLastStateOrder();

        findPossibilities();
    }

    private void findPossibilities() {
        hint = -1;
        possibilities = new ArrayList<>();
        for (int i = 0; i < fieldArray.size() - 1; i++) {
            if (fieldArray.get(i).getState() == NumberField.STATE.USED) continue;

            for (int ii = i + 1; ii < fieldArray.size(); ii++) {
                if (fieldArray.get(ii).getState() == NumberField.STATE.USED) continue;
                if (canBeCombined(i, ii)) {
                    possibilities.add(new CombinePos(i, ii));
                }
                break;
            }

            for (int ii = i + 9; ii < fieldArray.size(); ii += 9) {
                if (fieldArray.get(ii).getState() == NumberField.STATE.USED) continue;
                if (canBeCombined(i, ii)) {
                    possibilities.add(new CombinePos(i, ii));
                }
                break;
            }
        }

        if (possibilities.size() == 0)
            addFieldsButton.setVisibility(View.VISIBLE);
        else addFieldsButton.setVisibility(View.GONE);
    }

    private boolean canBeCombined(int id1, int id2) {
        return id1 != id2 && !(fieldArray.get(id1).getNumber() + fieldArray.get(id2).getNumber() != 10 && fieldArray.get(id1).getNumber() != fieldArray.get(id2).getNumber());
    }

    public void hint() {
        if (possibilities.size() == 0) return;
        if (hint >= 0) {
            fieldArray.get(possibilities.get(hint).getId1()).setState(NumberField.STATE.UNUSED);
            fieldArray.get(possibilities.get(hint).getId2()).setState(NumberField.STATE.UNUSED);
        }
        hint = ++hint % possibilities.size();

        fieldArray.get(possibilities.get(hint).getId1()).setState(NumberField.STATE.HINT);
        fieldArray.get(possibilities.get(hint).getId2()).setState(NumberField.STATE.HINT);

        notifyDataSetChanged();
    }

    public void clicked(int id) {
        if (fieldArray.get(id).getState() == NumberField.STATE.USED) return;

        if (selectedField == -1) {
            selectedField = id;
            fieldArray.get(id).setState(NumberField.STATE.SELECTED);
        } else if (id == selectedField) {
            fieldArray.get(id).setState(NumberField.STATE.UNUSED);
            selectedField = -1;
        } else {
            combine(id);
        }

        notifyDataSetChanged();
    }

    private void combine(int id) {
        boolean combined = false;
        for (CombinePos pos : possibilities) {
            if (pos.equals(new CombinePos(id, selectedField))) {
                fieldArray.get(id).setState(NumberField.STATE.USED);
                fieldArray.get(selectedField).setState(NumberField.STATE.USED);
                reduceFields();
                saveState();

                selectedField = -1;
                combined = true;
                findPossibilities();
                break;
            }
        }
        if (!combined) {
            fieldArray.get(selectedField).setState(NumberField.STATE.UNUSED);
            selectedField = -1;
        }
    }

    private void reduceFields() {
        int fieldSize = fieldArray.size();
        int preLeft = -1;
        for (int i = 0; i < fieldArray.size(); i += 9) {
            preLeft = reduceRow(i, preLeft);
            if (fieldSize > fieldArray.size()) {
                i--;
                fieldSize = fieldArray.size();
            }
        }
        if (fieldSize > fieldArray.size()) findPossibilities();
    }

    private int reduceRow(int index, int preLeft) {
        boolean empty = true;
        int left = -1;
        int right = -1;
        for (int ii = 0; ii < 9 && index + ii < fieldArray.size(); ii++) {
            if (fieldArray.get(index + ii).getState() != NumberField.STATE.USED) {
                empty = false;
                left = ii;
                if (right == -1) right = ii;
            }
        }
        if (empty && index + 8 < fieldArray.size()) {
            deleteNine(index);
        } else if (preLeft != -1 && right > preLeft) {
            deleteNine(index - 8 + preLeft);
        }
        return left;
    }

    private void deleteNine(int index) {
        for (int i = 0; i < 9; i++) {
            fieldArray.remove(index);
        }
    }

    public void addFields() {
        int fieldLength = fieldArray.size();
        for (int i = 0; i < fieldLength; i++) {
            if (fieldArray.get(i).getState() != NumberField.STATE.USED) {
                fieldArray.add(new NumberField(fieldArray.get(i).getNumber()));
            }
        }
        notifyDataSetChanged();
        findPossibilities();
    }

    private String fieldsToString() {
        String stringified = "";
        for (NumberField field : fieldArray) {
            stringified += field.stringify() + ",";
        }
        stringified = stringified.substring(0, stringified.length() - 1);
        return stringified;
    }

    private void fieldsFromString(String string) {
        fieldArray = new ArrayList<>();
        String[] tempFieldArray = string.split(",");
        for (String tempField : tempFieldArray) {
            fieldArray.add(new NumberField(tempField));
        }
        notifyDataSetChanged();
    }

    private void saveState() {
        dbHelper.saveState(++stateOrder, fieldsToString());
        headerCombos.setText(String.valueOf(stateOrder));
    }

    @Override
    public int getCount() {
        return fieldArray.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(params);
        } else {
            textView = (TextView) view;
        }

        textView.setText("" + fieldArray.get(i).getNumber());

        switch (fieldArray.get(i).getState()) {
            case UNUSED: textView.setBackgroundColor(Color.BLACK); break;
            case USED: textView.setBackgroundColor(Color.LTGRAY); textView.setText(""); break;
            case SELECTED: textView.setBackgroundColor(Color.BLUE); break;
            case HINT: textView.setBackgroundColor(Color.GREEN); break;
        }

        return textView;
    }
}
