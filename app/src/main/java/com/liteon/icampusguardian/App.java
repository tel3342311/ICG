package com.liteon.icampusguardian;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

public class App extends Application{

    private static Context mContext;
    public static Boolean isOffline = true;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Context getContext(){
        return mContext;
    }
}