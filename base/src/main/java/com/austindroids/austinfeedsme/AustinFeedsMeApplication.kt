package com.austindroids.austinfeedsme

import android.os.StrictMode
import com.austindroids.austinfeedsme.common.logging.CrashlyticsTree
import com.austindroids.austinfeedsme.di.components.DaggerAppComponent
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import timber.log.Timber


/**
 * Created by darrankelinske on 4/7/16.
 */
class AustinFeedsMeApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
        Timber.plant(CrashlyticsTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .application(this)
                .create(this)
    }
}
