package com.fusedlocationproviderclient_srs.app;

import android.app.Application;
import android.arch.lifecycle.ProcessLifecycleOwner;

/*
 * Created by shayan.raees on 10/23/2018.
 */
public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener());
    }

    public static synchronized App getInstance() {
        return mInstance;
    }
}
