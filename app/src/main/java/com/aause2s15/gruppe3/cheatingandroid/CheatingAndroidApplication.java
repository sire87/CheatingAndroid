package com.aause2s15.gruppe3.cheatingandroid;

import android.app.Application;

/**
 * Created by Simon on 04.06.2015.
 */
public class CheatingAndroidApplication extends Application {

    // ACCESS: ((CheatingAndroidApplication)this.getApplicationContext()).caService.SomeMethod();
    public CheatingAndroidService caService;

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        caService = CheatingAndroidService.getInstance();
    }
}