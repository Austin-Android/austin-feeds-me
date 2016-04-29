package com.example.utfeedsme.modules;

import android.app.Activity;

import com.example.utfeedsme.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides @PerActivity Activity activity() {
        return activity;
    }
}
