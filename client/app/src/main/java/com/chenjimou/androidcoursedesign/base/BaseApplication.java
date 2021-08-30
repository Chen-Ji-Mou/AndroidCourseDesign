package com.chenjimou.androidcoursedesign.base;

import android.app.Application;

import com.chenjimou.androidcoursedesign.utils.SharedPreferencesUtils;

public class BaseApplication extends Application
{
    public static Application sApplication;
    @Override
    public void onCreate()
    {
        super.onCreate();
        sApplication = this;
        SharedPreferencesUtils.init(this);
    }
}
