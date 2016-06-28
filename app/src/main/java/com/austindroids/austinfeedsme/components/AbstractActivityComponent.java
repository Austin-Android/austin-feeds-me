package com.austindroids.austinfeedsme.components;

import android.app.Activity;

import com.austindroids.austinfeedsme.PerActivity;
import com.austindroids.austinfeedsme.modules.ActivityModule;

import dagger.Component;

/**
 * A base component upon which fragment's components may depend.  Activity-level components
 * should extend this component.
 */
@PerActivity // Subtypes of AbstractActivityComponent should be decorated with @PerActivity.
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface AbstractActivityComponent {
    Activity activity(); // Expose the activity to sub-graphs.
}
