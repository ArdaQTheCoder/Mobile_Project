package com.example.mobile_project;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.mobile_project.data.preferences.PreferencesManager;

public class AutoServiceApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager prefs = new PreferencesManager(this);
        AppCompatDelegate.setDefaultNightMode(
                prefs.isDarkTheme()
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
