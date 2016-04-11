package com.example.utfeedsme;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import io.fabric.sdk.android.Fabric;

/**
 * Created by darrankelinske on 4/7/16.
 */
public class AustinFeedsMeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://austin-feeds-me.firebaseio.com/");
        myFirebaseRef.child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Prints the events we have in firebase
                System.out.println(snapshot.getValue());
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }
}
