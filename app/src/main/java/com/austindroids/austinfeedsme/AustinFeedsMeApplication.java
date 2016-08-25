package com.austindroids.austinfeedsme;

import android.app.Application;

import com.austindroids.austinfeedsme.components.ApplicationComponent;
import com.austindroids.austinfeedsme.components.DaggerApplicationComponent;
import com.austindroids.austinfeedsme.modules.AustinFeedsMeApplicationModule;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

/**
 * Created by darrankelinske on 4/7/16.
 */
public class AustinFeedsMeApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Fabric.with(this, new Crashlytics());

        applicationComponent = DaggerApplicationComponent.builder()
                .austinFeedsMeApplicationModule(new AustinFeedsMeApplicationModule(this))
                .build();
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }
}
