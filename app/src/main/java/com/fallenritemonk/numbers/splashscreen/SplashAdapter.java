package com.fallenritemonk.numbers.splashscreen;

import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.fallenritemonk.numbers.MainMenu;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by FallenRiteMonk on 10/7/15.
 */
public class SplashAdapter extends BaseAdapter {

    private SplashActivity activity;
    private GridView splashGrid;
    private ArrayList<String> splashList;
    private int runningAnimations;

    public SplashAdapter(final SplashActivity activity) {
        this.activity = activity;
        splashGrid = activity.getSplashGridView();

        Animation inAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        inAnim.setDuration(400);
        inAnim.setInterpolator(new LinearInterpolator());
        inAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                runningAnimations++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (--runningAnimations == 0) {
                    Intent intent = new Intent(activity, MainMenu.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        LayoutAnimationController controller = new LayoutAnimationController(inAnim, 0.1f);
        activity.getSplashGridView().setLayoutAnimation(controller);

        init();
    }

    @Override
    public int getCount() {
        return splashList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        textView = new TextView(activity);
        textView.setBackgroundColor(Color.BLACK);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);

        if (splashList.get(position).length() > 0) {
            try {
                Integer.valueOf(splashList.get(position));
            } catch (Exception e) {
                textView.setBackgroundColor(Color.BLUE);
            }
            textView.setText(splashList.get(position));
        } else {
            textView.setAlpha(0.2f);
        }

        return textView;
    }

    public void init() {
        splashList = new ArrayList<>();

        for (int i = 0; i < 18; i++) {
            addField();
        }
        for (int i = 0; i < 9; i++) {
            splashList.add("");
        }
        splashList.add("");
        splashList.add("#");
        splashList.add("-");
        splashList.add("l");
        splashList.add("u");
        splashList.add("d");
        splashList.add("u");
        splashList.add("s");
        splashList.add("");
        for (int i = 0; i < 9; i++) {
            splashList.add("");
        }
        for (int i = 0; i < 90; i++) {
            addField();
        }

        runningAnimations = 0;
    }

    private void addField() {
        Random random = new Random();
        if (random.nextBoolean()) {
            splashList.add(String.valueOf(random.nextInt(9) + 1));
        } else {
            splashList.add("");
        }
    }
}
