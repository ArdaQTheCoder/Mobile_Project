package com.example.mobile_project.ui.common;

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
import com.example.mobile_project.data.entity.Message;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.ui.common.adapter.MessageAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private AppDatabase db;
    private PreferencesManager prefs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MessageAdapter adapter;
    private RecyclerView rvMessages;
    private int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);
        prefs = new PreferencesManager(this);

        appointmentId = getIntent().getIntExtra("appointmentId", -1);
        if (appointmentId == -1) {
            finish();
            return;
        }
        String otherName = getIntent().getStringExtra("otherName");
        String category = getIntent().getStringExtra("category");
        String vehicleInfo = getIntent().getStringExtra("vehicleInfo");

        MaterialTextView tvChatTitle = findViewById(R.id.tvChatTitle);
        MaterialTextView tvChatSubtitle = findViewById(R.id.tvChatSubtitle);
        MaterialTextView tvEmpty = findViewById(R.id.tvEmpty);
        rvMessages = findViewById(R.id.rvMessages);
        TextInputEditText etMessage = findViewById(R.id.etMessage);
        MaterialButton btnSend = findViewById(R.id.btnSend);

        tvChatTitle.setText(getString(R.string.chat_with, otherName));
        tvChatSubtitle.setText(category + " - " + vehicleInfo);

        adapter = new MessageAdapter(prefs.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);

        db.messageDao().getByAppointmentId(appointmentId).observe(this, messages -> {
            if (messages == null || messages.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvMessages.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvMessages.setVisibility(View.VISIBLE);
                adapter.setMessages(messages);
                rvMessages.scrollToPosition(messages.size() - 1);
            }
        });

        executor.execute(() ->
                db.messageDao().markAsRead(appointmentId, prefs.getUserId()));

        btnSend.setOnClickListener(v -> {
            String content = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
            if (content.isEmpty()) return;

            etMessage.setText("");

            Message message = new Message();
            message.setAppointmentId(appointmentId);
            message.setSenderId(prefs.getUserId());
            message.setContent(content);
            message.setTimestamp(System.currentTimeMillis());
            message.setRead(false);

            executor.execute(() -> db.messageDao().insert(message));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        executor.execute(() ->
                db.messageDao().markAsRead(appointmentId, prefs.getUserId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
