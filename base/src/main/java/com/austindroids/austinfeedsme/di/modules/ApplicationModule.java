package com.austindroids.austinfeedsme.di.modules;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ApplicationModule {
    @Binds
    abstract Context bindContext(Application application);
}
