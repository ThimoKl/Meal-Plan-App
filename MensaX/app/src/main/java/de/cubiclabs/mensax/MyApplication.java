package de.cubiclabs.mensax;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by thimokluser on 5/6/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

}
