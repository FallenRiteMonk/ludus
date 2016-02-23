package com.fallenritemonk.ludus.game;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.fallenritemonk.ludus.R;
import com.fallenritemonk.ludus.db.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by FallenRiteMonk on 23/12/15.
 */
abstract class AbstractGame extends BaseAdapter {
    private final int INITIAL_FIELD_ARRAY_CAPACITY = 200;
    private final int INITIAL_POSSIBILITIES_ARRAY_CAPACITY = 200;

    final GameActivity activity;
    private final FloatingActionButton addFieldsButton;
    private final TextView headerCombos;

    private final DatabaseHelper dbHelper;

    ArrayList<NumberField> fieldArray;
    private ArrayList<CombinePos> possibilities;
    private int selectedField;
    private int hintIndex;
    int stateOrder;

    AbstractGame(final GameActivity activity, FloatingActionButton addFieldsButton, TextView headerCombos, Boolean resume) {
        this.activity = activity;
        this.addFieldsButton = addFieldsButton;
        this.headerCombos = headerCombos;

        dbHelper = DatabaseHelper.getInstance(activity);

        if (resume) {
            resumeGame();
        } else {
            newGame();
        }
    }

    public void newGame() {
        initFields();

        possibilities = new ArrayList<>(INITIAL_POSSIBILITIES_ARRAY_CAPACITY);

        selectedField = -1;
        hintIndex = -1;
        stateOrder = -1;

        findPossibilities();

        dbHelper.clearDB();
        saveState();
    }

    void initFields() {
        fieldArray = new ArrayList<>(INITIAL_FIELD_ARRAY_CAPACITY);
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

    private void resumeGame() {
        fieldsFromString(dbHelper.getLastState());

        possibilities = new ArrayList<>(INITIAL_POSSIBILITIES_ARRAY_CAPACITY);

        selectedField = -1;
        hintIndex = -1;
        stateOrder = dbHelper.getLastStateOrder();
        headerCombos.setText(String.valueOf(stateOrder));

        findPossibilities();
    }

    private void findPossibilities() {
        hideHint();

        possibilities.clear();
        for (int i = 0; i < fieldArray.size() - 1; i++) {
            if (fieldArray.get(i).getState() == NumberField.STATE.USED) {
                continue;
            }

            for (int ii = i + 1; ii < fieldArray.size(); ii++) {
                if (fieldArray.get(ii).getState() != NumberField.STATE.USED) {
                    if (canBeCombined(i, ii)) {
                        possibilities.add(new CombinePos(i, ii));
                    }
                    break;
                }
            }

            for (int ii = i + 9; ii < fieldArray.size(); ii += 9) {
                if (fieldArray.get(ii).getState() != NumberField.STATE.USED) {
                    if (canBeCombined(i, ii)) {
                        possibilities.add(new CombinePos(i, ii));
                    }
                    break;
                }
            }
        }

        if (possibilities.size() == 0) {
            addFieldsButton.show();
        } else {
            addFieldsButton.hide();
        }
    }

    private boolean canBeCombined(int id1, int id2) {
        return id1 != id2 && !(fieldArray.get(id1).getNumber() + fieldArray.get(id2).getNumber() != 10 && fieldArray.get(id1).getNumber() != fieldArray.get(id2).getNumber());
    }

    public void hint(GridView gridView) {
        if (possibilities.size() == 0) {
            return;
        }
        if (hintIndex >= 0) {
            fieldArray.get(possibilities.get(hintIndex).getId1()).setState(NumberField.STATE.UNUSED);
            fieldArray.get(possibilities.get(hintIndex).getId2()).setState(NumberField.STATE.UNUSED);
        }
        hintIndex = ++hintIndex % possibilities.size();

        fieldArray.get(possibilities.get(hintIndex).getId1()).setState(NumberField.STATE.HINT);
        fieldArray.get(possibilities.get(hintIndex).getId2()).setState(NumberField.STATE.HINT);

        gridView.smoothScrollToPosition(possibilities.get(hintIndex).getId1());

        notifyDataSetChanged();
    }

    private void hideHint() {
        if (hintIndex != -1) {
            CombinePos shownHint = possibilities.get(hintIndex);
            if (shownHint.getId1() < fieldArray.size() && fieldArray.get(shownHint.getId1()).getState() == NumberField.STATE.HINT) {
                fieldArray.get(shownHint.getId1()).setState(NumberField.STATE.UNUSED);
            }
            if (shownHint.getId2() < fieldArray.size() && fieldArray.get(shownHint.getId2()).getState() == NumberField.STATE.HINT) {
                fieldArray.get(shownHint.getId2()).setState(NumberField.STATE.UNUSED);
            }
            hintIndex = -1;
        }
    }

    public void undo() {
        if (dbHelper.undo()) {
            resumeGame();
        }
    }

    public void clicked(int id) {
        if (fieldArray.get(id).getState() == NumberField.STATE.USED) {
            return;
        }

        if (selectedField == -1) {
            selectedField = id;
            fieldArray.get(id).setState(NumberField.STATE.SELECTED);
            notifyDataSetChanged();
        } else if (id == selectedField) {
            fieldArray.get(id).setState(NumberField.STATE.UNUSED);
            selectedField = -1;
            notifyDataSetChanged();
        } else {
            combine(id);
        }
    }

    private void combine(int id) {
        boolean combined = false;
        for (CombinePos pos : possibilities) {
            if (pos.equals(new CombinePos(id, selectedField))) {
                fieldArray.get(id).setState(NumberField.STATE.USED);
                fieldArray.get(selectedField).setState(NumberField.STATE.USED);

                if (reduceFields()) {
                    won();
                } else {
                    saveState();

                    findPossibilities();
                }
                selectedField = -1;
                combined = true;
                break;
            }
        }
        if (!combined) {
            fieldArray.get(selectedField).setState(NumberField.STATE.UNUSED);
            selectedField = -1;
        }
        notifyDataSetChanged();
    }

    private boolean reduceFields() {
        ArrayList<NumberField> deleteList = new ArrayList<>(fieldArray.size());
        int usedCound = 0;
        for (int i = 0; i < fieldArray.size(); i++) {
            if (fieldArray.get(i).getState() == NumberField.STATE.USED) {
                if (++usedCound == 9){
                    deleteNine(i, deleteList);
                    usedCound = 0;
                }
            } else {
                usedCound = 0;
            }
        }

        fieldArray.removeAll(deleteList);

        if (usedCound == fieldArray.size()) {
            fieldArray.clear();
        }

        return fieldArray.isEmpty();
    }

    private void deleteNine(int index, ArrayList<NumberField> deleteList) {
        ArrayList<NumberField> tempList = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            if (deleteList.contains(fieldArray.get(index - i))) {
                return;
            } else {
                tempList.add(fieldArray.get(index - i));
            }
        }
        deleteList.addAll(tempList);
    }

    public void addFields(GridView gridView) {
        if (possibilities.size() > 0) {
            return;
        }

        int initialSize = fieldArray.size();
        ArrayList<NumberField> tempField = new ArrayList<>(fieldArray.size());
        for (NumberField field : fieldArray) {
            if (field.getState() != NumberField.STATE.USED) {
                tempField.add(new NumberField(field.getNumber()));
            }
        }
        fieldArray.addAll(tempField);

        gridView.smoothScrollToPosition(initialSize - 1);

        notifyDataSetChanged();
        findPossibilities();
    }

    void won() {
        stateOrder += 1;

        dbHelper.clearDB();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.game_won);

        builder.setPositiveButton(R.string.confirm_restart_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                newGame();
            }
        });
        builder.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void saveState() {
        dbHelper.saveState(++stateOrder, fieldsToString());
        headerCombos.setText(String.valueOf(stateOrder));
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
        fieldArray = new ArrayList<>(INITIAL_FIELD_ARRAY_CAPACITY);
        String[] tempFieldArray = string.split(",");
        for (String tempField : tempFieldArray) {
            fieldArray.add(new NumberField(tempField));
        }
        notifyDataSetChanged();
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
    public View getView(int i, final View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = new TextView(activity);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(params);
        } else {
            textView = (TextView) view;
        }

        textView.setText(String.valueOf(fieldArray.get(i).getNumber()));
        textView.setAlpha(1);

        switch (fieldArray.get(i).getState()) {
            case USED: textView.setAlpha(0.2f); textView.setText("");
            case UNUSED: textView.setBackgroundColor(Color.BLACK); break;
            case SELECTED: textView.setBackgroundColor(Color.BLUE); break;
            case HINT: textView.setBackgroundColor(Color.GREEN); break;
        }

        return textView;
    }
}
