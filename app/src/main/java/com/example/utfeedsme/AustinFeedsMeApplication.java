package com.example.utfeedsme;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.example.utfeedsme.components.ApplicationComponent;
import com.example.utfeedsme.components.DaggerApplicationComponent;
import com.example.utfeedsme.modules.AustinFeedsMeApplicationModule;
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
        Fabric.with(this, new Crashlytics());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        applicationComponent = DaggerApplicationComponent.builder()
                .austinFeedsMeApplicationModule(new AustinFeedsMeApplicationModule(this))
                .build();
    }

    public ApplicationComponent componbuildent() {
        return applicationComponent;
    }
}
