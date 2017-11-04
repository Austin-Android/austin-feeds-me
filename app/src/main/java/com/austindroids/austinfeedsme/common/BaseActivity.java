package com.austindroids.austinfeedsme.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by darrankelinske on 3/1/17.
 */

public class BaseActivity extends DaggerAppCompatActivity {

    View contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = findViewById(android.R.id.content);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void showSnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(contentView, message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }
}
