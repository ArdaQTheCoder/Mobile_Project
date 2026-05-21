package com.example.mobile_project.ui.common;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.util.HashUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private PreferencesManager prefs;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextInputEditText etFullName, etPhone, etCurrentPassword, etNewPassword;
    private TextInputLayout tilCurrentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        prefs = new PreferencesManager(this);
        db = AppDatabase.getInstance(this);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        tilCurrentPassword = findViewById(R.id.tilCurrentPassword);
        MaterialButton btnSaveProfile = findViewById(R.id.btnSaveProfile);
        MaterialSwitch switchDarkTheme = findViewById(R.id.switchDarkTheme);
        MaterialSwitch switchNotifications = findViewById(R.id.switchNotifications);

        switchDarkTheme.setChecked(prefs.isDarkTheme());
        switchNotifications.setChecked(prefs.isNotificationsEnabled());

        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setDarkTheme(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.setNotificationsEnabled(isChecked));
    }

    private void loadProfile() {
        executor.execute(() -> {
            var user = db.userDao().getByIdSync(prefs.getUserId());
            if (user != null) {
                runOnUiThread(() -> {
                    etFullName.setText(user.getFullName());
                    etPhone.setText(user.getPhone());
                });
            }
        });
    }

    private void saveProfile() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String currentPw = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString() : "";
        String newPw = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";

        if (fullName.isEmpty()) {
            etFullName.setError(getString(R.string.field_required));
            return;
        }
        if (phone.isEmpty()) {
            etPhone.setError(getString(R.string.field_required));
            return;
        }

        executor.execute(() -> {
            var user = db.userDao().getByIdSync(prefs.getUserId());
            if (user == null) return;

            if (!newPw.isEmpty()) {
                if (currentPw.isEmpty()) {
                    runOnUiThread(() -> etCurrentPassword.setError(getString(R.string.field_required)));
                    return;
                }
                String currentHash = HashUtils.sha256(currentPw);
                if (!currentHash.equals(user.getPasswordHash())) {
                    runOnUiThread(() -> etCurrentPassword.setError(getString(R.string.wrong_password)));
                    return;
                }
                if (newPw.length() < 6) {
                    runOnUiThread(() -> etNewPassword.setError(getString(R.string.password_too_short)));
                    return;
                }
                user.setPasswordHash(HashUtils.sha256(newPw));
            }

            user.setFullName(fullName);
            user.setPhone(phone);
            db.userDao().update(user);

            runOnUiThread(() -> {
                etCurrentPassword.setText("");
                etNewPassword.setText("");
                Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
