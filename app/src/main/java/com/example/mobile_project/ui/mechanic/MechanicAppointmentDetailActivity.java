package com.example.mobile_project.ui.mechanic;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MechanicAppointmentDetailActivity extends AppCompatActivity {

    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mechanic_appointment_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        appointmentId = getIntent().getIntExtra("appointmentId", -1);

        MaterialTextView tvStatus = findViewById(R.id.tvStatus);
        MaterialTextView tvCustomerName = findViewById(R.id.tvCustomerName);
        MaterialTextView tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        MaterialTextView tvVehicle = findViewById(R.id.tvVehicle);
        MaterialTextView tvDateTime = findViewById(R.id.tvDateTime);
        MaterialTextView tvCategory = findViewById(R.id.tvCategory);
        MaterialTextView tvDescription = findViewById(R.id.tvDescription);
        MaterialTextView tvNotes = findViewById(R.id.tvNotes);
        MaterialCardView cardNotes = findViewById(R.id.cardNotes);
        TextInputLayout tilNotes = findViewById(R.id.tilNotes);
        TextInputEditText etNotes = findViewById(R.id.etNotes);
        MaterialButton btnStartWork = findViewById(R.id.btnStartWork);
        MaterialButton btnComplete = findViewById(R.id.btnComplete);

        db.appointmentDao().getByIdWithDetails(appointmentId).observe(this, item -> {
            if (item == null) return;

            // Customer info
            tvCustomerName.setText(item.customerName);
            tvCustomerPhone.setText(item.customerPhone);

            // Vehicle info
            tvVehicle.setText(item.vehicleYear + " " + item.vehicleMake + " " + item.vehicleModel);

            // Appointment info
            tvDateTime.setText(item.appointment.getDate() + " at " + item.appointment.getTime());
            tvCategory.setText(item.appointment.getCategory());
            tvDescription.setText(item.appointment.getDescription());

            // Status
            String status = item.appointment.getStatus();
            tvStatus.setText(status);

            int colorRes;
            switch (status) {
                case "APPROVED": colorRes = R.color.status_approved; break;
                case "IN_PROGRESS": colorRes = R.color.status_in_progress; break;
                case "COMPLETED": colorRes = R.color.status_completed; break;
                case "REJECTED": colorRes = R.color.status_rejected; break;
                default: colorRes = R.color.status_pending; break;
            }
            int color = ContextCompat.getColor(this, colorRes);
            tvStatus.setTextColor(color);
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(color & 0x33FFFFFF));

            // Show/hide action buttons based on status
            btnStartWork.setVisibility(View.GONE);
            btnComplete.setVisibility(View.GONE);
            tilNotes.setVisibility(View.GONE);
            cardNotes.setVisibility(View.GONE);

            switch (status) {
                case "APPROVED":
                    btnStartWork.setVisibility(View.VISIBLE);
                    break;
                case "IN_PROGRESS":
                    tilNotes.setVisibility(View.VISIBLE);
                    btnComplete.setVisibility(View.VISIBLE);
                    break;
                case "COMPLETED":
                    if (item.appointment.getNotes() != null && !item.appointment.getNotes().isEmpty()) {
                        cardNotes.setVisibility(View.VISIBLE);
                        tvNotes.setText(item.appointment.getNotes());
                    }
                    break;
            }
        });

        btnStartWork.setOnClickListener(v -> {
            btnStartWork.setEnabled(false);
            executor.execute(() -> {
                db.appointmentDao().updateStatus(appointmentId, "IN_PROGRESS", System.currentTimeMillis());
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.status_updated, Toast.LENGTH_SHORT).show());
            });
        });

        btnComplete.setOnClickListener(v -> {
            btnComplete.setEnabled(false);
            String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";
            executor.execute(() -> {
                db.appointmentDao().updateStatusWithNotes(
                        appointmentId, "COMPLETED", notes, System.currentTimeMillis());
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.appointment_completed, Toast.LENGTH_SHORT).show();
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
