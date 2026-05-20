package com.example.mobile_project.ui.customer;

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
import com.example.mobile_project.ui.customer.adapter.ServiceRecordAdapter;
import com.google.android.material.textview.MaterialTextView;

public class ServiceRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int vehicleId = getIntent().getIntExtra("vehicleId", -1);
        String vehicleInfo = getIntent().getStringExtra("vehicleInfo");

        MaterialTextView tvVehicleInfo = findViewById(R.id.tvVehicleInfo);
        MaterialTextView tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rvRecords = findViewById(R.id.rvRecords);

        tvVehicleInfo.setText(vehicleInfo);

        ServiceRecordAdapter adapter = new ServiceRecordAdapter();
        rvRecords.setLayoutManager(new LinearLayoutManager(this));
        rvRecords.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(this);
        db.appointmentDao().getServiceRecordsByVehicle(vehicleId).observe(this, records -> {
            if (records == null || records.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvRecords.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvRecords.setVisibility(View.VISIBLE);
                adapter.setRecords(records);
            }
        });
    }
}
