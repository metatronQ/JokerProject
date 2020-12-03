package com.example.jokerproject;

import android.app.Application;

import com.example.jokerproject.util.ScreenUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ScreenUtil.initAttr(this);
    }
}
