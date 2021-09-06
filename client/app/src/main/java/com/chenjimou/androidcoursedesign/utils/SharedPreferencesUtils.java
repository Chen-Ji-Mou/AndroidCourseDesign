package com.chenjimou.androidcoursedesign.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils
{
    volatile static SharedPreferencesUtils mInstance;
    final SharedPreferences mPref;

    static final String SHARED_PREFERENCES_NAME = "android-course-design";
    static final String KEY_USERNAME = "username";
    static final String KEY_PASSWORD = "password";
    static final String KEY_TOKEN = "token";
    static final String KEY_USER_ID = "userId";

    public static void init(Application applicationContext)
    {
        if (mInstance == null)
        {
            synchronized (SharedPreferencesUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new SharedPreferencesUtils(applicationContext);
                }
            }
        }
    }

    public static SharedPreferencesUtils getInstance()
    {
        return mInstance;
    }

    private SharedPreferencesUtils(Application applicationContext)
    {
        mPref = applicationContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void saveUsername(String username)
    {
        mPref.edit().putString(KEY_USERNAME, username).apply();
    }

    public String getUsername()
    {
        return mPref.getString(KEY_USERNAME, null);
    }

    public void savePassword(String password)
    {
        mPref.edit().putString(KEY_PASSWORD, password).apply();
    }

    public String getPassword()
    {
        return mPref.getString(KEY_PASSWORD, null);
    }

    public void saveToken(String token)
    {
        mPref.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken()
    {
        return mPref.getString(KEY_TOKEN, null);
    }

    public void saveUserId(String userId)
    {
        mPref.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId()
    {
        return mPref.getString(KEY_USER_ID, null);
    }

    public void reset()
    {
        mPref.edit().remove(KEY_TOKEN).remove(KEY_USERNAME).remove(KEY_PASSWORD).apply();
    }
}
