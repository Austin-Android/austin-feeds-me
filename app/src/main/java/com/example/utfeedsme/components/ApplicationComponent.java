package com.example.utfeedsme.components;

import android.app.Application;

import com.example.utfeedsme.modules.AustinFeedsMeApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AustinFeedsMeApplicationModule.class)
public interface ApplicationComponent {
    // Exported for child-components.
    Application application();
}
