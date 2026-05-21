package com.example.mobile_project.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
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
import com.example.mobile_project.ui.customer.adapter.ReviewAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MechanicDetailActivity extends AppCompatActivity {

    private AppDatabase db;
    private ReviewAdapter reviewAdapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int mechanicUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mechanic_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);
        mechanicUserId = getIntent().getIntExtra("mechanicUserId", -1);
        if (mechanicUserId == -1) {
            Toast.makeText(this, R.string.error_something_went_wrong, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialTextView tvName = findViewById(R.id.tvName);
        MaterialTextView tvRating = findViewById(R.id.tvRating);
        MaterialTextView tvExperience = findViewById(R.id.tvExperience);
        MaterialTextView tvContact = findViewById(R.id.tvContact);
        MaterialTextView tvNoReviews = findViewById(R.id.tvNoReviews);
        Chip chipSpecialization = findViewById(R.id.chipSpecialization);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        MaterialButton btnBook = findViewById(R.id.btnBookAppointment);

        RecyclerView rvReviews = findViewById(R.id.rvReviews);
        reviewAdapter = new ReviewAdapter();
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);

        executor.execute(() -> {
            var user = db.userDao().getByIdSync(mechanicUserId);
            var profile = db.mechanicProfileDao().getByUserIdSync(mechanicUserId);

            if (user != null && profile != null) {
                runOnUiThread(() -> {
                    tvName.setText(user.getFullName());
                    chipSpecialization.setText(profile.getSpecialization());
                    ratingBar.setRating(profile.getRating());
                    tvRating.setText(String.format("%.1f (%d reviews)",
                            profile.getRating(), profile.getReviewCount()));
                    tvExperience.setText(profile.getExperienceYears() + " years experience");
                    tvContact.setText(user.getEmail() + " | " + user.getPhone());
                });
            }
        });

        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookAppointmentActivity.class);
            intent.putExtra("mechanicUserId", mechanicUserId);
            startActivity(intent);
        });

        db.reviewDao().getByMechanicWithCustomer(mechanicUserId).observe(this, reviews -> {
            if (reviews == null || reviews.isEmpty()) {
                tvNoReviews.setVisibility(View.VISIBLE);
            } else {
                tvNoReviews.setVisibility(View.GONE);
                reviewAdapter.setReviews(reviews);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
