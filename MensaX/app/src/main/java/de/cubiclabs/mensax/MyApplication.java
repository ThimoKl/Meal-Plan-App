package de.cubiclabs.mensax;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import org.androidannotations.annotations.EApplication;

/**
 * Created by thimokluser on 5/6/15.
 */
public class MyApplication extends Application {

    public static GoogleAnalytics mAnalytics;
    public static Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        mAnalytics = GoogleAnalytics.getInstance(this);
        //mAnalytics.setDryRun(true);
        mTracker = mAnalytics.newTracker("UA-27694799-17");
        mTracker.enableExceptionReporting(true);
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.enableAutoActivityTracking(false);

        Fabric.with(this, new Crashlytics());
    }

}
