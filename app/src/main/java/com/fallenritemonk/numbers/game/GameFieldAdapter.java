package com.fallenritemonk.numbers.game;

import android.content.Context;
import android.graphics.Color;
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
public class GameFieldAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<NumberField> fieldArray;

    public GameFieldAdapter(ArrayList<NumberField> fieldArray, Context context) {
        this.fieldArray = fieldArray;
        this.context = context;
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
