package com.fallenritemonk.numbers.splashscreen;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.fallenritemonk.numbers.R;

public class SplashActivity extends Activity {
    private GridView splashGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashGrid = (GridView) findViewById(R.id.splashGrid);
        splashGrid.setAdapter(new SplashAdapter(this));
    }

    public GridView getSplashGridView() {
        return splashGrid;
    }
}
