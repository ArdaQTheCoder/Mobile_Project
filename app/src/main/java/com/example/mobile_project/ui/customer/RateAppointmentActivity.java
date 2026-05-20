package com.example.mobile_project.ui.customer;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_project.R;
import com.example.mobile_project.data.database.AppDatabase;
import com.example.mobile_project.data.entity.MechanicProfile;
import com.example.mobile_project.data.entity.Review;
import com.example.mobile_project.data.preferences.PreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RateAppointmentActivity extends AppCompatActivity {

    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        PreferencesManager prefs = new PreferencesManager(this);

        int appointmentId = getIntent().getIntExtra("appointmentId", -1);
        int mechanicId = getIntent().getIntExtra("mechanicId", -1);
        String mechanicName = getIntent().getStringExtra("mechanicName");

        MaterialTextView tvMechanicName = findViewById(R.id.tvMechanicName);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextInputEditText etComment = findViewById(R.id.etComment);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);

        tvMechanicName.setText(mechanicName);

        // Check if already rated
        executor.execute(() -> {
            Review existing = db.reviewDao().getByAppointmentIdSync(appointmentId);
            if (existing != null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.already_rated, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });

        btnSubmit.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            if (rating == 0) {
                Toast.makeText(this, R.string.select_rating, Toast.LENGTH_SHORT).show();
                return;
            }

            String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";
            btnSubmit.setEnabled(false);

            Review review = new Review();
            review.setAppointmentId(appointmentId);
            review.setCustomerId(prefs.getUserId());
            review.setMechanicId(mechanicId);
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedAt(System.currentTimeMillis());

            executor.execute(() -> {
                db.reviewDao().insert(review);

                // Update mechanic's average rating
                MechanicProfile profile = db.mechanicProfileDao().getByUserIdSync(mechanicId);
                if (profile != null) {
                    int newCount = profile.getReviewCount() + 1;
                    float newRating = ((profile.getRating() * profile.getReviewCount()) + rating) / newCount;
                    profile.setRating(newRating);
                    profile.setReviewCount(newCount);
                    db.mechanicProfileDao().update(profile);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.review_submitted, Toast.LENGTH_SHORT).show();
                    finish();
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
