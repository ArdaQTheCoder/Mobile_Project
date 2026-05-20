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
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.example.mobile_project.ui.customer.adapter.VehicleAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

public class VehicleListActivity extends AppCompatActivity {

    private VehicleAdapter adapter;
    private MaterialTextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vehicle_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rvVehicles = findViewById(R.id.rvVehicles);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        adapter = new VehicleAdapter();
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddVehicleActivity.class)));

        PreferencesManager prefs = new PreferencesManager(this);
        AppDatabase db = AppDatabase.getInstance(this);

        db.vehicleDao().getByUserId(prefs.getUserId()).observe(this, vehicles -> {
            if (vehicles == null || vehicles.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvVehicles.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvVehicles.setVisibility(View.VISIBLE);
                adapter.setVehicles(vehicles);
            }
        });
    }
}
