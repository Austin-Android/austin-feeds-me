package com.example.utfeedsme;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;

import io.fabric.sdk.android.Fabric;

/**
 * Created by darrankelinske on 4/7/16.
 */
public class AustinFeedsMeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Firebase.setAndroidContext(this);
    }
}
