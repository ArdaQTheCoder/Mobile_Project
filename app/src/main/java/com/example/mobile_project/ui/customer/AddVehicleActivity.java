package com.example.mobile_project.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_project.R;
import com.example.mobile_project.data.api.ApiClient;
import com.example.mobile_project.data.api.NhtsaApiService;
import com.example.mobile_project.data.api.model.NhtsaMakeResponse;
import com.example.mobile_project.data.api.model.NhtsaModelResponse;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.entity.Vehicle;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {

    private TextInputLayout tilMake, tilModel, tilYear, tilMileage;
    private AutoCompleteTextView actvMake, actvModel;
    private TextInputEditText etYear, etMileage, etPlate;
    private MaterialButton btnSave;
    private ProgressBar progressBar;

    private AppDatabase db;
    private PreferencesManager prefs;
    private NhtsaApiService apiService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private List<NhtsaMakeResponse.Make> makeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_vehicle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        prefs = new PreferencesManager(this);
        apiService = ApiClient.getInstance().create(NhtsaApiService.class);

        tilMake = findViewById(R.id.tilMake);
        tilModel = findViewById(R.id.tilModel);
        tilYear = findViewById(R.id.tilYear);
        tilMileage = findViewById(R.id.tilMileage);
        actvMake = findViewById(R.id.actvMake);
        actvModel = findViewById(R.id.actvModel);
        etYear = findViewById(R.id.etYear);
        etMileage = findViewById(R.id.etMileage);
        etPlate = findViewById(R.id.etPlate);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> saveVehicle());

        actvMake.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMake = (String) parent.getItemAtPosition(position);
            for (NhtsaMakeResponse.Make make : makeList) {
                if (make.makeName.equalsIgnoreCase(selectedMake)) {
                    loadModels(make.makeId);
                    break;
                }
            }
        });

        loadMakes();
    }

    private void loadMakes() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getCarMakes().enqueue(new Callback<NhtsaMakeResponse>() {
            @Override
            public void onResponse(Call<NhtsaMakeResponse> call, Response<NhtsaMakeResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    makeList = response.body().results;
                    List<String> makeNames = new ArrayList<>();
                    for (NhtsaMakeResponse.Make make : makeList) {
                        makeNames.add(make.makeName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddVehicleActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            makeNames);
                    actvMake.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<NhtsaMakeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddVehicleActivity.this,
                        R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadModels(int makeId) {
        actvModel.setText("", false);
        progressBar.setVisibility(View.VISIBLE);
        apiService.getModelsForMake(makeId).enqueue(new Callback<NhtsaModelResponse>() {
            @Override
            public void onResponse(Call<NhtsaModelResponse> call, Response<NhtsaModelResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<String> modelNames = new ArrayList<>();
                    for (NhtsaModelResponse.Model model : response.body().results) {
                        modelNames.add(model.modelName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddVehicleActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            modelNames);
                    actvModel.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<NhtsaModelResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddVehicleActivity.this,
                        R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveVehicle() {
        tilMake.setError(null);
        tilModel.setError(null);
        tilYear.setError(null);
        tilMileage.setError(null);

        String make = actvMake.getText().toString().trim();
        String model = actvModel.getText().toString().trim();
        String yearStr = etYear.getText() != null ? etYear.getText().toString().trim() : "";
        String mileageStr = etMileage.getText() != null ? etMileage.getText().toString().trim() : "";
        String plate = etPlate.getText() != null ? etPlate.getText().toString().trim() : "";

        if (make.isEmpty()) { tilMake.setError(getString(R.string.field_required)); return; }
        if (model.isEmpty()) { tilModel.setError(getString(R.string.field_required)); return; }
        if (yearStr.isEmpty()) { tilYear.setError(getString(R.string.field_required)); return; }
        if (mileageStr.isEmpty()) { tilMileage.setError(getString(R.string.field_required)); return; }

        int year = Integer.parseInt(yearStr);
        int mileage = Integer.parseInt(mileageStr);

        if (year < 1900 || year > 2030) {
            tilYear.setError(getString(R.string.invalid_year));
            return;
        }

        btnSave.setEnabled(false);

        Vehicle vehicle = new Vehicle();
        vehicle.setUserId(prefs.getUserId());
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setMileage(mileage);
        vehicle.setPlateNumber(plate.isEmpty() ? null : plate);
        vehicle.setCreatedAt(System.currentTimeMillis());

        executor.execute(() -> {
            db.vehicleDao().insert(vehicle);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.vehicle_added, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
