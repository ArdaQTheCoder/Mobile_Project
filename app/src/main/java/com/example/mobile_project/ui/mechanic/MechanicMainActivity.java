package com.example.mobile_project.ui.mechanic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.example.mobile_project.ui.auth.LoginActivity;
import com.example.mobile_project.ui.mechanic.adapter.MechanicTodayAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MechanicMainActivity extends AppCompatActivity implements MechanicTodayAdapter.OnItemClickListener {

    private PreferencesManager prefs;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private MechanicTodayAdapter todayAdapter;
    private MaterialTextView tvNoAppointments;
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mechanic_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = new PreferencesManager(this);
        db = AppDatabase.getInstance(this);

        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        MaterialTextView tvGreeting = findViewById(R.id.tvGreeting);
        MaterialTextView tvDate = findViewById(R.id.tvDate);
        MaterialTextView tvPendingCount = findViewById(R.id.tvPendingCount);
        MaterialTextView tvTodayCount = findViewById(R.id.tvTodayCount);
        tvNoAppointments = findViewById(R.id.tvNoAppointments);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnViewPending = findViewById(R.id.btnViewPending);
        MaterialButton btnViewAll = findViewById(R.id.btnViewAll);
        MaterialCardView cardPendingCount = findViewById(R.id.cardPendingCount);

        RecyclerView rvToday = findViewById(R.id.rvTodayAppointments);
        todayAdapter = new MechanicTodayAdapter(this);
        rvToday.setLayoutManager(new LinearLayoutManager(this));
        rvToday.setAdapter(todayAdapter);

        tvDate.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(new Date()));

        executor.execute(() -> {
            var user = db.userDao().getByIdSync(prefs.getUserId());
            if (user != null) {
                runOnUiThread(() -> tvGreeting.setText(
                        getString(R.string.welcome_mechanic, user.getFullName())));
            }
        });

        // Stats
        db.appointmentDao().getPendingCountForMechanic(prefs.getUserId()).observe(this, count -> {
            tvPendingCount.setText(String.valueOf(count != null ? count : 0));
        });

        db.appointmentDao().getTodayCountForMechanic(prefs.getUserId(), todayDate).observe(this, count -> {
            tvTodayCount.setText(String.valueOf(count != null ? count : 0));
        });

        // Today's appointments
        db.appointmentDao().getMechanicDailyWithDetails(prefs.getUserId(), todayDate).observe(this, appointments -> {
            if (appointments == null || appointments.isEmpty()) {
                tvNoAppointments.setVisibility(View.VISIBLE);
            } else {
                tvNoAppointments.setVisibility(View.GONE);
            }
            todayAdapter.setAppointments(appointments != null ? appointments : java.util.List.of());
        });

        btnLogout.setOnClickListener(v -> {
            prefs.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        cardPendingCount.setOnClickListener(v ->
                startActivity(new Intent(this, MechanicPendingActivity.class)));
        btnViewPending.setOnClickListener(v ->
                startActivity(new Intent(this, MechanicPendingActivity.class)));
        btnViewAll.setOnClickListener(v ->
                startActivity(new Intent(this, MechanicAllAppointmentsActivity.class)));
    }

    @Override
    public void onItemClick(AppointmentWithDetails item) {
        Intent intent = new Intent(this, MechanicAppointmentDetailActivity.class);
        intent.putExtra("appointmentId", item.appointment.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
