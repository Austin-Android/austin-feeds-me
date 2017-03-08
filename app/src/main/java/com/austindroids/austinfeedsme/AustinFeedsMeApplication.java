package com.austindroids.austinfeedsme;

import android.support.multidex.MultiDexApplication;

import com.austindroids.austinfeedsme.components.ApplicationComponent;
import com.austindroids.austinfeedsme.components.DaggerApplicationComponent;
import com.austindroids.austinfeedsme.modules.ApplicationModule;
import com.austindroids.austinfeedsme.modules.DataModule;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

/**
 * Created by darrankelinske on 4/7/16.
 */
public class AustinFeedsMeApplication extends MultiDexApplication {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Fabric.with(this, new Crashlytics());

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataModule(new DataModule(this))
                .build();
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }
}
