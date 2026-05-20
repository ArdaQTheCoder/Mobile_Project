package com.example.mobile_project.ui.customer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.entity.Appointment;
import com.example.mobile_project.data.entity.Vehicle;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookAppointmentActivity extends AppCompatActivity {

    private TextInputLayout tilVehicle, tilCategory, tilDate, tilTime, tilDescription;
    private AutoCompleteTextView actvVehicle, actvCategory;
    private TextInputEditText etDate, etTime, etDescription;
    private MaterialButton btnBook;

    private AppDatabase db;
    private PreferencesManager prefs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private int mechanicUserId;
    private List<Vehicle> vehicleList = new ArrayList<>();

    private static final String[] CATEGORIES = {
            "General", "Engine", "Electrical", "Transmission", "Brakes", "Oil Change", "Body Work"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        prefs = new PreferencesManager(this);
        mechanicUserId = getIntent().getIntExtra("mechanicUserId", -1);

        MaterialTextView tvMechanicName = findViewById(R.id.tvMechanicName);
        tilVehicle = findViewById(R.id.tilVehicle);
        tilCategory = findViewById(R.id.tilCategory);
        tilDate = findViewById(R.id.tilDate);
        tilTime = findViewById(R.id.tilTime);
        tilDescription = findViewById(R.id.tilDescription);
        actvVehicle = findViewById(R.id.actvVehicle);
        actvCategory = findViewById(R.id.actvCategory);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        btnBook = findViewById(R.id.btnBook);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        actvCategory.setAdapter(categoryAdapter);

        executor.execute(() -> {
            var mechanic = db.userDao().getByIdSync(mechanicUserId);
            if (mechanic != null) {
                runOnUiThread(() -> tvMechanicName.setText(
                        getString(R.string.mechanic_label, mechanic.getFullName())));
            }
        });

        loadVehicles();

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnBook.setOnClickListener(v -> bookAppointment());
    }

    private void loadVehicles() {
        db.vehicleDao().getByUserId(prefs.getUserId()).observe(this, vehicles -> {
            vehicleList = vehicles != null ? vehicles : new ArrayList<>();
            List<String> names = new ArrayList<>();
            for (Vehicle v : vehicleList) {
                names.add(v.getYear() + " " + v.getMake() + " " + v.getModel());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, names);
            actvVehicle.setAdapter(adapter);

            if (names.size() == 1) {
                actvVehicle.setText(names.get(0), false);
            }
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            etDate.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            etTime.setText(time);
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
    }

    private void bookAppointment() {
        tilVehicle.setError(null);
        tilCategory.setError(null);
        tilDate.setError(null);
        tilTime.setError(null);
        tilDescription.setError(null);

        String vehicleText = actvVehicle.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String time = etTime.getText() != null ? etTime.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (vehicleText.isEmpty()) { tilVehicle.setError(getString(R.string.field_required)); return; }
        if (category.isEmpty()) { tilCategory.setError(getString(R.string.field_required)); return; }
        if (date.isEmpty()) { tilDate.setError(getString(R.string.field_required)); return; }
        if (time.isEmpty()) { tilTime.setError(getString(R.string.field_required)); return; }
        if (description.isEmpty()) { tilDescription.setError(getString(R.string.field_required)); return; }

        int vehicleIndex = -1;
        for (int i = 0; i < vehicleList.size(); i++) {
            Vehicle v = vehicleList.get(i);
            String name = v.getYear() + " " + v.getMake() + " " + v.getModel();
            if (name.equals(vehicleText)) {
                vehicleIndex = i;
                break;
            }
        }
        if (vehicleIndex < 0) {
            tilVehicle.setError(getString(R.string.field_required));
            return;
        }

        btnBook.setEnabled(false);
        Vehicle selectedVehicle = vehicleList.get(vehicleIndex);

        Appointment appointment = new Appointment();
        appointment.setCustomerId(prefs.getUserId());
        appointment.setMechanicId(mechanicUserId);
        appointment.setVehicleId(selectedVehicle.getId());
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setCategory(category);
        appointment.setDescription(description);
        appointment.setStatus("PENDING");
        appointment.setCreatedAt(System.currentTimeMillis());
        appointment.setUpdatedAt(System.currentTimeMillis());

        executor.execute(() -> {
            db.appointmentDao().insert(appointment);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.appointment_created, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
