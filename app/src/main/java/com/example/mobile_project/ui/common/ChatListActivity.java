package com.example.mobile_project.ui.common;

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
import com.example.mobile_project.data.entity.Message;
import com.example.mobile_project.data.model.AppointmentWithDetails;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.ui.common.adapter.ChatListAdapter;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatListActivity extends AppCompatActivity implements ChatListAdapter.OnChatClickListener {

    private PreferencesManager prefs;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ChatListAdapter adapter;
    private MaterialTextView tvEmpty;
    private RecyclerView rvChats;
    private List<AppointmentWithDetails> currentAppointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        prefs = new PreferencesManager(this);
        db = AppDatabase.getInstance(this);

        tvEmpty = findViewById(R.id.tvEmpty);
        rvChats = findViewById(R.id.rvChats);

        boolean isCustomer = "CUSTOMER".equals(prefs.getUserRole());
        adapter = new ChatListAdapter(this, isCustomer);
        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.setAdapter(adapter);

        if (isCustomer) {
            db.appointmentDao().getActiveForCustomerWithDetails(prefs.getUserId())
                    .observe(this, this::onAppointmentsLoaded);
        } else {
            db.appointmentDao().getActiveForMechanicWithDetails(prefs.getUserId())
                    .observe(this, this::onAppointmentsLoaded);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!currentAppointments.isEmpty()) {
            loadChatMeta();
        }
    }

    private void onAppointmentsLoaded(List<AppointmentWithDetails> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            currentAppointments = new ArrayList<>();
            tvEmpty.setVisibility(View.VISIBLE);
            rvChats.setVisibility(View.GONE);
        } else {
            currentAppointments = appointments;
            tvEmpty.setVisibility(View.GONE);
            rvChats.setVisibility(View.VISIBLE);
            adapter.setAppointments(appointments);
            loadChatMeta();
        }
    }

    private void loadChatMeta() {
        executor.execute(() -> {
            Map<Integer, String> lastMessages = new HashMap<>();
            Map<Integer, Long> lastTimestamps = new HashMap<>();
            Map<Integer, Integer> unreadCounts = new HashMap<>();

            int userId = prefs.getUserId();
            for (AppointmentWithDetails appt : currentAppointments) {
                int apptId = appt.appointment.getId();

                Message lastMsg = db.messageDao().getLastMessageSync(apptId);
                if (lastMsg != null) {
                    lastMessages.put(apptId, lastMsg.getContent());
                    lastTimestamps.put(apptId, lastMsg.getTimestamp());
                }

                int unread = db.messageDao().getUnreadCountSync(apptId, userId);
                unreadCounts.put(apptId, unread);
            }

            runOnUiThread(() -> adapter.setChatMeta(lastMessages, lastTimestamps, unreadCounts));
        });
    }

    @Override
    public void onChatClick(AppointmentWithDetails appointment) {
        boolean isCustomer = "CUSTOMER".equals(prefs.getUserRole());
        String otherName = isCustomer ? appointment.mechanicName : appointment.customerName;

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("appointmentId", appointment.appointment.getId());
        intent.putExtra("otherName", otherName);
        intent.putExtra("category", appointment.appointment.getCategory());
        intent.putExtra("vehicleInfo", appointment.vehicleMake + " " + appointment.vehicleModel);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
