package com.fallenritemonk.numbers.game;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fallenritemonk.numbers.R;

import java.util.ArrayList;

/**
 * Created by FallenRiteMonk on 9/19/15.
 */
public class GameActivity extends AppCompatActivity {
    private ArrayList<NumberField> fieldArray = new ArrayList<>();
    private ArrayList<CombinePos> possibilities = new ArrayList<>();
    private GameFieldAdapter fieldAdapter;
    private int selectedField = -1;
    private int hint = -1;
    private int stateOrder = 0;
    private FloatingActionButton addFieldsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GridView fieldView = (GridView) findViewById(R.id.fieldGrid);
        addFieldsButton = (FloatingActionButton) findViewById(R.id.addFields);

        fieldAdapter = new GameFieldAdapter(fieldArray, this);
        fieldView.setAdapter(fieldAdapter);

        initFields();

        findPossibilities();

        fieldView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {
                clicked(id);
                //Toast.makeText(GameActivity.this, "" + id, Toast.LENGTH_SHORT).show();
            }
        });

        addFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFields();
            }
        });
    }

    private void initFields() {
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(2, fieldAdapter));
        fieldArray.add(new NumberField(3, fieldAdapter));
        fieldArray.add(new NumberField(4, fieldAdapter));
        fieldArray.add(new NumberField(5, fieldAdapter));
        fieldArray.add(new NumberField(6, fieldAdapter));
        fieldArray.add(new NumberField(7, fieldAdapter));
        fieldArray.add(new NumberField(8, fieldAdapter));
        fieldArray.add(new NumberField(9, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(2, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(3, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(4, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(5, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(6, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(7, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(8, fieldAdapter));
        fieldArray.add(new NumberField(1, fieldAdapter));
        fieldArray.add(new NumberField(9, fieldAdapter));
        fieldAdapter.notifyDataSetChanged();

        /*Log.d("TEST", fieldsToString());
        fieldsFromString("9/UNUSED,2/UNUSED,3/UNUSED,4/USED,5/UNUSED,6/UNUSED,7/UNUSED,8/UNUSED,9/UNUSED,1/HINT,1/SELECTED,1/UNUSED,2/UNUSED,1/UNUSED,3/UNUSED,1/UNUSED,4/UNUSED,1/UNUSED,5/UNUSED,1/UNUSED,6/UNUSED,1/UNUSED,7/UNUSED,1/UNUSED,8/UNUSED,1/UNUSED,9/UNUSED");
        fieldAdapter.notifyDataSetChanged();*/
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

    public void hint(View view) {
        if (possibilities.size() == 0) return;
        if (hint >= 0) {
            fieldArray.get(possibilities.get(hint).getId1()).setState(NumberField.STATE.UNUSED);
            fieldArray.get(possibilities.get(hint).getId2()).setState(NumberField.STATE.UNUSED);
        }
        hint = ++hint % possibilities.size();

        fieldArray.get(possibilities.get(hint).getId1()).setState(NumberField.STATE.HINT);
        fieldArray.get(possibilities.get(hint).getId2()).setState(NumberField.STATE.HINT);
    }

    private void clicked(int id) {
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

    private void addFields() {
        int fieldLength = fieldArray.size();
        for (int i = 0; i < fieldLength; i++) {
            if (fieldArray.get(i).getState() != NumberField.STATE.USED) {
                fieldArray.add(new NumberField(fieldArray.get(i).getNumber(), fieldAdapter));
            }
        }
        fieldAdapter.notifyDataSetChanged();
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
        fieldArray.clear();
        String[] tempFieldArray = string.split(",");
        for (String tempField : tempFieldArray) {
            fieldArray.add(new NumberField(tempField, fieldAdapter));
        }
    }

    private void createDbEntry() {
        ContentValues values = new ContentValues();
        /*values.put(StateEntry.COLUMN_NAME_ORDER, id);
        values.put(StateEntry.COLUMN_NAME_ACTION, title);
        values.put(StateEntry.COLUMN_NAME_STATE, content);*/
    }
}
