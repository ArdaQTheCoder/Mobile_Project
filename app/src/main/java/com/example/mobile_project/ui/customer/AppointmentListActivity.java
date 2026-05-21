package com.example.mobile_project.ui.customer;

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
import com.example.mobile_project.ui.common.ChatActivity;
import com.example.mobile_project.ui.customer.adapter.AppointmentAdapter;
import com.google.android.material.textview.MaterialTextView;

public class AppointmentListActivity extends AppCompatActivity
        implements AppointmentAdapter.OnRateClickListener, AppointmentAdapter.OnChatClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        MaterialTextView tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rvAppointments = findViewById(R.id.rvAppointments);

        AppointmentAdapter adapter = new AppointmentAdapter(this, this);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvAppointments.setAdapter(adapter);

        PreferencesManager prefs = new PreferencesManager(this);
        AppDatabase db = AppDatabase.getInstance(this);

        db.appointmentDao().getByCustomerWithDetails(prefs.getUserId()).observe(this, appointments -> {
            if (appointments == null || appointments.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvAppointments.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvAppointments.setVisibility(View.VISIBLE);
                adapter.setAppointments(appointments);
            }
        });
    }

    @Override
    public void onRateClick(AppointmentWithDetails appointment) {
        Intent intent = new Intent(this, RateAppointmentActivity.class);
        intent.putExtra("appointmentId", appointment.appointment.getId());
        intent.putExtra("mechanicName", appointment.mechanicName);
        intent.putExtra("mechanicId", appointment.appointment.getMechanicId());
        startActivity(intent);
    }

    @Override
    public void onChatClick(AppointmentWithDetails appointment) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("appointmentId", appointment.appointment.getId());
        intent.putExtra("otherName", appointment.mechanicName);
        intent.putExtra("category", appointment.appointment.getCategory());
        intent.putExtra("vehicleInfo", appointment.vehicleMake + " " + appointment.vehicleModel);
        startActivity(intent);
    }
}
