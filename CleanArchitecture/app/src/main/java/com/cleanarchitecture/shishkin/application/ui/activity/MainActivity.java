package com.cleanarchitecture.shishkin.application.ui.activity;

import android.os.Bundle;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;

public class MainActivity extends AbstractActivity {

    public static String NAME = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
