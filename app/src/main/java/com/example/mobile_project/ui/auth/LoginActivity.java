package com.example.mobile_project.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private RadioGroup rgRole;
    private MaterialButton btnLogin;
    private MaterialTextView tvRegister;

    private PreferencesManager prefs;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = new PreferencesManager(this);
        db = AppDatabase.getInstance(this);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        rgRole = findViewById(R.id.rgRole);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String selectedRole = rgRole.getCheckedRadioButtonId() == R.id.rbMechanic ? "MECHANIC" : "CUSTOMER";

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.field_required));
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.field_required));
            return;
        }

        btnLogin.setEnabled(false);
        String passwordHash = HashUtils.sha256(password);

        executor.execute(() -> {
            User user = db.userDao().login(email, passwordHash);
            runOnUiThread(() -> {
                btnLogin.setEnabled(true);
                if (user == null) {
                    tilPassword.setError(getString(R.string.invalid_credentials));
                    return;
                }
                if (!user.getRole().equals(selectedRole)) {
                    Toast.makeText(this, R.string.role_mismatch, Toast.LENGTH_SHORT).show();
                    return;
                }
                prefs.saveSession(user.getId(), user.getRole(), user.getEmail());
                navigateToMain(user.getRole());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
