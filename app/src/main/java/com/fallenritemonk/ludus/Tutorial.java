package com.fallenritemonk.ludus;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by FallenRiteMonk on 10/21/15.
 */
public class Tutorial extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_title), R.drawable.tut_initial, Color.RED));
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_combine_1) + "\n" + getString(R.string.guide_combine_2), R.drawable.tut_combine, Color.parseColor("#FF8000")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_empty_fields) + "\n" + getString(R.string.guide_new_line), R.drawable.tut_empty, Color.parseColor("#D7DF01")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_add_fields) + "\n" + getString(R.string.guide_won), R.drawable.tut_add, Color.parseColor("#04B404")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_drawer_1) + "\n" + getString(R.string.guide_drawer_2), R.drawable.tut_drawer, Color.GRAY));

        setZoomAnimation();
    }

    @Override
    public void onDonePressed() {
        SharedPreferences persist = getSharedPreferences(getString(R.string.static_settings_file), 0);
        SharedPreferences.Editor editor = persist.edit();
        editor.putBoolean(getString(R.string.static_first_launch), false);
        editor.apply();
        finish();
    }
}
