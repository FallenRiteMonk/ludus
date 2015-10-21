package com.fallenritemonk.numbers;

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
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_title), R.mipmap.icon, Color.RED));
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_combine_1) + "\n" + getString(R.string.guide_combine_2), R.drawable.common_signin_btn_icon_light, Color.parseColor("#FF8000")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_empty_fields) + "\n" + getString(R.string.guide_new_line), R.drawable.common_signin_btn_icon_light, Color.parseColor("#D7DF01")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.guide), getString(R.string.guide_add_fields) + "\n" + getString(R.string.guide_won), R.drawable.common_signin_btn_icon_light, Color.parseColor("#04B404")));

        setZoomAnimation();
    }

    @Override
    public void onDonePressed() {
        finish();
    }
}
