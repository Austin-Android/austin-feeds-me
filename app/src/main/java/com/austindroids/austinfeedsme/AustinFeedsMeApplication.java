package com.austindroids.austinfeedsme;

import com.austindroids.austinfeedsme.di.components.DaggerAppComponent;
import com.austindroids.austinfeedsme.di.modules.DataModule;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.google.firebase.database.FirebaseDatabase;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.fabric.sdk.android.Fabric;

/**
 * Created by darrankelinske on 4/7/16.
 */
public class AustinFeedsMeApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Fabric.with(this, new Crashlytics());
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            AndroidDevMetrics.initWith(this);
        }
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .application(this)
                .dataModule(new DataModule())
                .create(this);
    }
}
