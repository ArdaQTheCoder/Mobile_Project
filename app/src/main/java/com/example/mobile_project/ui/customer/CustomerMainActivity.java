package com.example.mobile_project.ui.customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.entity.Appointment;
import com.example.mobile_project.data.entity.Vehicle;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.ui.auth.LoginActivity;
import com.example.mobile_project.ui.common.ChatListActivity;
import com.example.mobile_project.ui.common.SettingsActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerMainActivity extends AppCompatActivity {

    private PreferencesManager prefs;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private View layoutEmptyState, layoutDashboard;
    private Spinner spinnerVehicle;
    private MaterialTextView tvGreeting, tvLastOilChange, tvTotalServices;
    private PieChart pieChart;
    private MaterialCardView cardChart;

    private List<Vehicle> vehicleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = new PreferencesManager(this);
        db = AppDatabase.getInstance(this);

        tvGreeting = findViewById(R.id.tvGreeting);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        layoutDashboard = findViewById(R.id.layoutDashboard);
        spinnerVehicle = findViewById(R.id.spinnerVehicle);
        tvLastOilChange = findViewById(R.id.tvLastOilChange);
        tvTotalServices = findViewById(R.id.tvTotalServices);
        pieChart = findViewById(R.id.pieChart);
        cardChart = findViewById(R.id.cardChart);

        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnAddFirstVehicle = findViewById(R.id.btnAddFirstVehicle);
        MaterialCardView cardVehicles = findViewById(R.id.cardVehicles);
        MaterialCardView cardBookAppointment = findViewById(R.id.cardBookAppointment);
        MaterialCardView cardAppointments = findViewById(R.id.cardAppointments);
        MaterialCardView cardServiceHistory = findViewById(R.id.cardServiceHistory);
        MaterialCardView cardMessages = findViewById(R.id.cardMessages);
        MaterialCardView cardSettings = findViewById(R.id.cardSettings);

        btnLogout.setOnClickListener(v -> logout());
        btnAddFirstVehicle.setOnClickListener(v ->
                startActivity(new Intent(this, AddVehicleActivity.class)));

        cardVehicles.setOnClickListener(v ->
                startActivity(new Intent(this, VehicleListActivity.class)));
        cardBookAppointment.setOnClickListener(v ->
                startActivity(new Intent(this, MechanicListActivity.class)));
        cardAppointments.setOnClickListener(v ->
                startActivity(new Intent(this, AppointmentListActivity.class)));
        cardServiceHistory.setOnClickListener(v -> openServiceHistory());
        cardMessages.setOnClickListener(v ->
                startActivity(new Intent(this, ChatListActivity.class)));
        cardSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        setupPieChart();
        loadUserGreeting();

        spinnerVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < vehicleList.size()) {
                    loadVehicleStats(vehicleList.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVehicles();
    }

    private void loadUserGreeting() {
        executor.execute(() -> {
            var user = db.userDao().getByIdSync(prefs.getUserId());
            if (user != null) {
                runOnUiThread(() -> tvGreeting.setText(
                        getString(R.string.welcome_customer, user.getFullName())));
            }
        });
    }

    private void loadVehicles() {
        db.vehicleDao().getByUserId(prefs.getUserId()).observe(this, vehicles -> {
            vehicleList = vehicles != null ? vehicles : new ArrayList<>();
            if (vehicleList.isEmpty()) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                layoutDashboard.setVisibility(View.GONE);
            } else {
                layoutEmptyState.setVisibility(View.GONE);
                layoutDashboard.setVisibility(View.VISIBLE);

                List<String> vehicleNames = new ArrayList<>();
                for (Vehicle v : vehicleList) {
                    vehicleNames.add(v.getYear() + " " + v.getMake() + " " + v.getModel());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, vehicleNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVehicle.setAdapter(adapter);
            }
        });
    }

    private void loadVehicleStats(Vehicle vehicle) {
        executor.execute(() -> {
            Appointment lastOil = db.appointmentDao().getLastOilChange(vehicle.getId());
            int totalServices = db.appointmentDao().getCompletedCountByVehicle(vehicle.getId());

            runOnUiThread(() -> {
                tvTotalServices.setText(String.valueOf(totalServices));
                if (lastOil != null) {
                    tvLastOilChange.setText(lastOil.getDate());
                } else {
                    tvLastOilChange.setText(R.string.no_data);
                }
            });
        });

        db.appointmentDao().getCategoryCountsByVehicle(vehicle.getId()).observe(this, counts -> {
            if (counts != null && !counts.isEmpty()) {
                cardChart.setVisibility(View.VISIBLE);
                updatePieChart(counts);
            } else {
                cardChart.setVisibility(View.GONE);
            }
        });
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setEntryLabelTextSize(11f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false);
    }

    private void updatePieChart(List<com.example.mobile_project.data.dao.AppointmentDao.CategoryCount> counts) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (var count : counts) {
            if (count.category == null) continue;
            entries.add(new PieEntry(count.count, count.category));
            colors.add(getCategoryColor(count.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        pieChart.setData(new PieData(dataSet));
        pieChart.invalidate();
    }

    private int getCategoryColor(String category) {
        switch (category) {
            case "Engine": return ContextCompat.getColor(this, R.color.chart_engine);
            case "Electrical": return ContextCompat.getColor(this, R.color.chart_electrical);
            case "Transmission": return ContextCompat.getColor(this, R.color.chart_transmission);
            case "Brakes": return ContextCompat.getColor(this, R.color.chart_brakes);
            case "Oil Change": return ContextCompat.getColor(this, R.color.chart_oil_change);
            case "Body Work": return ContextCompat.getColor(this, R.color.chart_body_work);
            default: return ContextCompat.getColor(this, R.color.chart_general);
        }
    }

    private void openServiceHistory() {
        if (!vehicleList.isEmpty()) {
            int selectedPos = spinnerVehicle.getSelectedItemPosition();
            if (selectedPos >= 0 && selectedPos < vehicleList.size()) {
                Vehicle vehicle = vehicleList.get(selectedPos);
                Intent intent = new Intent(this, ServiceRecordsActivity.class);
                intent.putExtra("vehicleId", vehicle.getId());
                intent.putExtra("vehicleInfo", vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());
                startActivity(intent);
            }
        }
    }

    private void logout() {
        prefs.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
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
