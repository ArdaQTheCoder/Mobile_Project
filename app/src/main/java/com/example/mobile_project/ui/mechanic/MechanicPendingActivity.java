package com.example.mobile_project.ui.mechanic;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.model.AppointmentWithDetails;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.ui.mechanic.adapter.PendingAppointmentAdapter;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MechanicPendingActivity extends AppCompatActivity implements PendingAppointmentAdapter.OnActionListener {

    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mechanic_pending);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);
        PreferencesManager prefs = new PreferencesManager(this);

        MaterialTextView tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rvPending = findViewById(R.id.rvPending);

        PendingAppointmentAdapter adapter = new PendingAppointmentAdapter(this);
        rvPending.setLayoutManager(new LinearLayoutManager(this));
        rvPending.setAdapter(adapter);

        db.appointmentDao().getPendingForMechanicWithDetails(prefs.getUserId()).observe(this, appointments -> {
            if (appointments == null || appointments.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvPending.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvPending.setVisibility(View.VISIBLE);
                adapter.setAppointments(appointments);
            }
        });
    }

    @Override
    public void onApprove(AppointmentWithDetails item) {
        executor.execute(() -> {
            db.appointmentDao().updateStatus(
                    item.appointment.getId(), "APPROVED", System.currentTimeMillis());
            runOnUiThread(() ->
                    Toast.makeText(this, R.string.appointment_approved, Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public void onReject(AppointmentWithDetails item) {
        executor.execute(() -> {
            db.appointmentDao().updateStatus(
                    item.appointment.getId(), "REJECTED", System.currentTimeMillis());
            runOnUiThread(() ->
                    Toast.makeText(this, R.string.appointment_rejected, Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
