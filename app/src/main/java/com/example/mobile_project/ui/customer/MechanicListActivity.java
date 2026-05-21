package com.example.mobile_project.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.model.MechanicWithUser;
import com.example.mobile_project.ui.customer.adapter.MechanicAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class MechanicListActivity extends AppCompatActivity implements MechanicAdapter.OnMechanicClickListener {

    private AppDatabase db;
    private MechanicAdapter adapter;
    private MaterialTextView tvEmpty;
    private LiveData<List<MechanicWithUser>> currentLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mechanic_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rvMechanics = findViewById(R.id.rvMechanics);
        adapter = new MechanicAdapter(this);
        rvMechanics.setLayoutManager(new LinearLayoutManager(this));
        rvMechanics.setAdapter(adapter);

        ChipGroup chipGroup = findViewById(R.id.chipGroupFilter);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                loadMechanics(null);
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipEngine) loadMechanics("Engine");
            else if (checkedId == R.id.chipElectrical) loadMechanics("Electrical");
            else if (checkedId == R.id.chipTransmission) loadMechanics("Transmission");
            else if (checkedId == R.id.chipBrakes) loadMechanics("Brakes");
            else if (checkedId == R.id.chipBodyWork) loadMechanics("Body Work");
            else loadMechanics(null);
        });

        loadMechanics(null);
    }

    private void loadMechanics(String specialization) {
        if (currentLiveData != null) {
            currentLiveData.removeObservers(this);
        }

        if (specialization == null) {
            currentLiveData = db.mechanicProfileDao().getAllWithUser();
        } else {
            currentLiveData = db.mechanicProfileDao().getBySpecializationWithUser(specialization);
        }

        currentLiveData.observe(this, mechanics -> {
            if (mechanics == null || mechanics.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
            adapter.setMechanics(mechanics != null ? mechanics : List.of());
        });
    }

    @Override
    public void onMechanicClick(MechanicWithUser mechanic) {
        Intent intent = new Intent(this, MechanicDetailActivity.class);
        intent.putExtra("mechanicUserId", mechanic.profile.getUserId());
        startActivity(intent);
    }
}
