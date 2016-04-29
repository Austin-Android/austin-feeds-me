package com.example.utfeedsme.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AustinFeedsMeApplicationModule {
    private final Application application;

    public AustinFeedsMeApplicationModule(Application application) {
        this.application = application;
    }

    @Provides @Singleton Application application() {
        return application;
    }
}
