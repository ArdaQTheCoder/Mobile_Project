package com.example.mobile_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.ui.auth.LoginActivity;
import com.example.mobile_project.ui.customer.CustomerMainActivity;
import com.example.mobile_project.ui.mechanic.MechanicMainActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager prefs = new PreferencesManager(this);

        Intent intent;
        if (prefs.isLoggedIn()) {
            if ("MECHANIC".equals(prefs.getUserRole())) {
                intent = new Intent(this, MechanicMainActivity.class);
            } else {
                intent = new Intent(this, CustomerMainActivity.class);
            }
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
