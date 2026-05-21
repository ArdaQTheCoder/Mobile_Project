package com.example.mobile_project.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.entity.MechanicProfile;
import com.example.mobile_project.data.entity.User;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.ui.customer.CustomerMainActivity;
import com.example.mobile_project.ui.mechanic.MechanicMainActivity;
import com.example.mobile_project.util.HashUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilEmail, tilPhone, tilPassword, tilConfirmPassword, tilSpecialization;
    private TextInputEditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private AutoCompleteTextView actvSpecialization;
    private RadioGroup rgRole;
    private MaterialButton btnRegister;
    private MaterialTextView tvLogin;

    private PreferencesManager prefs;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        prefs = new PreferencesManager(this);
        db = AppDatabase.getInstance(this);

        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilSpecialization = findViewById(R.id.tilSpecialization);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        actvSpecialization = findViewById(R.id.actvSpecialization);

        rgRole = findViewById(R.id.rgRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        String[] specializations = getResources().getStringArray(R.array.specializations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, specializations);
        actvSpecialization.setAdapter(adapter);
        actvSpecialization.setText(specializations[0], false);

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            tilSpecialization.setVisibility(
                    checkedId == R.id.rbMechanic ? View.VISIBLE : View.GONE);
        });

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        clearErrors();

        String fullName = getText(etFullName);
        String email = getText(etEmail);
        String phone = getText(etPhone);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);
        String selectedRole = rgRole.getCheckedRadioButtonId() == R.id.rbMechanic ? "MECHANIC" : "CUSTOMER";
        String specialization = actvSpecialization.getText().toString().trim();

        if (fullName.isEmpty()) { tilFullName.setError(getString(R.string.field_required)); return; }
        if (email.isEmpty()) { tilEmail.setError(getString(R.string.field_required)); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.invalid_email)); return;
        }
        if (phone.isEmpty()) { tilPhone.setError(getString(R.string.field_required)); return; }
        if (password.isEmpty()) { tilPassword.setError(getString(R.string.field_required)); return; }
        if (password.length() < 6) { tilPassword.setError(getString(R.string.password_too_short)); return; }
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.passwords_dont_match)); return;
        }
        if ("MECHANIC".equals(selectedRole) && specialization.isEmpty()) {
            tilSpecialization.setError(getString(R.string.field_required)); return;
        }

        btnRegister.setEnabled(false);

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(HashUtils.sha256(password));
        user.setRole(selectedRole);
        user.setCreatedAt(System.currentTimeMillis());

        executor.execute(() -> {
            User existing = db.userDao().getByEmail(email);
            if (existing != null) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    tilEmail.setError(getString(R.string.email_exists));
                });
                return;
            }

            long userId = db.userDao().insert(user);

            if ("MECHANIC".equals(selectedRole)) {
                MechanicProfile profile = new MechanicProfile();
                profile.setUserId((int) userId);
                profile.setSpecialization(specialization);
                profile.setExperienceYears(0);
                profile.setRating(0f);
                profile.setReviewCount(0);
                db.mechanicProfileDao().insert(profile);
            }

            runOnUiThread(() -> {
                prefs.saveSession((int) userId, selectedRole, email);
                Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show();
                navigateToMain(selectedRole);
            });
        });
    }

    private void navigateToMain(String role) {
        Intent intent;
        if ("MECHANIC".equals(role)) {
            intent = new Intent(this, MechanicMainActivity.class);
        } else {
            intent = new Intent(this, CustomerMainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getText(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    private void clearErrors() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilSpecialization.setError(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
