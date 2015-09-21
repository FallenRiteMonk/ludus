package com.fallenritemonk.numbers.game;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
class GameField extends BaseAdapter {
    private final Context context;
    private final FloatingActionButton addFieldsButton;

    private final ArrayList<NumberField> fieldArray;
    private ArrayList<CombinePos> possibilities = new ArrayList<>();
    private int selectedField = -1;
    private int hint = -1;

    public GameField(Context context, FloatingActionButton addFieldsButton) {
        this.context = context;
        this.addFieldsButton = addFieldsButton;

        fieldArray = new ArrayList<>();
        initFields();

        findPossibilities();
    }

    private void initFields() {
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
        notifyDataSetChanged();
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
                selectedField = -1;
                combined = true;
                reduceFields();
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
