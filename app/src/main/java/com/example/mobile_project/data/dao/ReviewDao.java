package com.example.mobile_project.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobile_project.data.entity.Review;
import com.example.mobile_project.data.model.ReviewWithCustomer;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    long insert(Review review);

    @Query("SELECT * FROM reviews WHERE mechanicId = :mechanicId ORDER BY createdAt DESC")
    LiveData<List<Review>> getByMechanicId(int mechanicId);

    @Query("SELECT AVG(rating) FROM reviews WHERE mechanicId = :mechanicId")
    LiveData<Float> getAverageRating(int mechanicId);

    @Query("SELECT * FROM reviews WHERE appointmentId = :appointmentId LIMIT 1")
    LiveData<Review> getByAppointmentId(int appointmentId);

    @Query("SELECT * FROM reviews WHERE appointmentId = :appointmentId LIMIT 1")
    Review getByAppointmentIdSync(int appointmentId);

    @Query("SELECT r.*, u.fullName AS customerName FROM reviews r " +
            "INNER JOIN users u ON r.customerId = u.id " +
            "WHERE r.mechanicId = :mechanicId ORDER BY r.createdAt DESC")
    LiveData<List<ReviewWithCustomer>> getByMechanicWithCustomer(int mechanicId);
}
