package com.example.utfeedsme;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.example.utfeedsme.components.ApplicationComponent;
import com.example.utfeedsme.components.DaggerApplicationComponent;
import com.example.utfeedsme.modules.AustinFeedsMeApplicationModule;
import com.firebase.client.Firebase;

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
        Firebase.setAndroidContext(this);

        applicationComponent = DaggerApplicationComponent.builder()
                .austinFeedsMeApplicationModule(new AustinFeedsMeApplicationModule(this))
                .build();
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }
}
