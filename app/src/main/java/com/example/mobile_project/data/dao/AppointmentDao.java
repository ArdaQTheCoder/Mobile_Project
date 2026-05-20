package com.example.mobile_project.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_project.data.entity.Appointment;
import com.example.mobile_project.data.model.AppointmentWithDetails;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert
    long insert(Appointment appointment);

    @Update
    void update(Appointment appointment);

    @Query("SELECT * FROM appointments WHERE id = :id")
    LiveData<Appointment> getById(int id);

    @Query("SELECT * FROM appointments WHERE id = :id")
    Appointment getByIdSync(int id);

    @Query("SELECT * FROM appointments WHERE customerId = :customerId ORDER BY createdAt DESC")
    LiveData<List<Appointment>> getByCustomerId(int customerId);

    @Query("SELECT * FROM appointments WHERE mechanicId = :mechanicId ORDER BY createdAt DESC")
    LiveData<List<Appointment>> getByMechanicId(int mechanicId);

    @Query("SELECT * FROM appointments WHERE mechanicId = :mechanicId AND date = :date AND status IN ('APPROVED', 'IN_PROGRESS')")
    LiveData<List<Appointment>> getMechanicDailyAppointments(int mechanicId, String date);

    @Query("SELECT * FROM appointments WHERE mechanicId = :mechanicId AND status = 'PENDING'")
    LiveData<List<Appointment>> getPendingForMechanic(int mechanicId);

    @Query("SELECT * FROM appointments WHERE vehicleId = :vehicleId AND status = 'COMPLETED' ORDER BY updatedAt DESC")
    LiveData<List<Appointment>> getCompletedByVehicle(int vehicleId);

    @Query("SELECT category, COUNT(*) as count FROM appointments WHERE vehicleId = :vehicleId AND status = 'COMPLETED' GROUP BY category")
    LiveData<List<CategoryCount>> getCategoryCountsByVehicle(int vehicleId);

    @Query("UPDATE appointments SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    void updateStatus(int id, String status, long updatedAt);

    @Query("UPDATE appointments SET status = :status, notes = :notes, updatedAt = :updatedAt WHERE id = :id")
    void updateStatusWithNotes(int id, String status, String notes, long updatedAt);

    // --- Customer join queries ---

    @Query("SELECT a.*, m.fullName AS mechanicName, c.fullName AS customerName, " +
            "c.phone AS customerPhone, v.make AS vehicleMake, v.model AS vehicleModel, v.year AS vehicleYear " +
            "FROM appointments a " +
            "INNER JOIN users m ON a.mechanicId = m.id " +
            "INNER JOIN users c ON a.customerId = c.id " +
            "INNER JOIN vehicles v ON a.vehicleId = v.id " +
            "WHERE a.customerId = :customerId ORDER BY a.createdAt DESC")
    LiveData<List<AppointmentWithDetails>> getByCustomerWithDetails(int customerId);

    @Query("SELECT a.*, m.fullName AS mechanicName, c.fullName AS customerName, " +
            "c.phone AS customerPhone, v.make AS vehicleMake, v.model AS vehicleModel, v.year AS vehicleYear " +
            "FROM appointments a " +
            "INNER JOIN users m ON a.mechanicId = m.id " +
            "INNER JOIN users c ON a.customerId = c.id " +
            "INNER JOIN vehicles v ON a.vehicleId = v.id " +
            "WHERE a.vehicleId = :vehicleId AND a.status = 'COMPLETED' ORDER BY a.updatedAt DESC")
    LiveData<List<AppointmentWithDetails>> getServiceRecordsByVehicle(int vehicleId);

    // --- Mechanic join queries ---

    @Query("SELECT a.*, m.fullName AS mechanicName, c.fullName AS customerName, " +
            "c.phone AS customerPhone, v.make AS vehicleMake, v.model AS vehicleModel, v.year AS vehicleYear " +
            "FROM appointments a " +
            "INNER JOIN users m ON a.mechanicId = m.id " +
            "INNER JOIN users c ON a.customerId = c.id " +
            "INNER JOIN vehicles v ON a.vehicleId = v.id " +
            "WHERE a.mechanicId = :mechanicId ORDER BY a.createdAt DESC")
    LiveData<List<AppointmentWithDetails>> getByMechanicWithDetails(int mechanicId);

    @Query("SELECT a.*, m.fullName AS mechanicName, c.fullName AS customerName, " +
            "c.phone AS customerPhone, v.make AS vehicleMake, v.model AS vehicleModel, v.year AS vehicleYear " +
            "FROM appointments a " +
            "INNER JOIN users m ON a.mechanicId = m.id " +
            "INNER JOIN users c ON a.customerId = c.id " +
            "INNER JOIN vehicles v ON a.vehicleId = v.id " +
            "WHERE a.mechanicId = :mechanicId AND a.status = 'PENDING' ORDER BY a.createdAt DESC")
    LiveData<List<AppointmentWithDetails>> getPendingForMechanicWithDetails(int mechanicId);

    @Query("SELECT a.*, m.fullName AS mechanicName, c.fullName AS customerName, " +
            "c.phone AS customerPhone, v.make AS vehicleMake, v.model AS vehicleModel, v.year AS vehicleYear " +
            "FROM appointments a " +
            "INNER JOIN users m ON a.mechanicId = m.id " +
            "INNER JOIN users c ON a.customerId = c.id " +
            "INNER JOIN vehicles v ON a.vehicleId = v.id " +
            "WHERE a.mechanicId = :mechanicId AND a.date = :date " +
            "AND a.status IN ('APPROVED', 'IN_PROGRESS') ORDER BY a.time ASC")
    LiveData<List<AppointmentWithDetails>> getMechanicDailyWithDetails(int mechanicId, String date);

    @Query("SELECT a.*, m.fullName AS mechanicName, c.fullName AS customerName, " +
            "c.phone AS customerPhone, v.make AS vehicleMake, v.model AS vehicleModel, v.year AS vehicleYear " +
            "FROM appointments a " +
            "INNER JOIN users m ON a.mechanicId = m.id " +
            "INNER JOIN users c ON a.customerId = c.id " +
            "INNER JOIN vehicles v ON a.vehicleId = v.id " +
            "WHERE a.id = :id")
    LiveData<AppointmentWithDetails> getByIdWithDetails(int id);

    @Query("SELECT * FROM appointments WHERE vehicleId = :vehicleId AND category = 'OIL_CHANGE' AND status = 'COMPLETED' ORDER BY updatedAt DESC LIMIT 1")
    Appointment getLastOilChange(int vehicleId);

    @Query("SELECT COUNT(*) FROM appointments WHERE vehicleId = :vehicleId AND status = 'COMPLETED'")
    int getCompletedCountByVehicle(int vehicleId);

    @Query("SELECT COUNT(*) FROM appointments WHERE mechanicId = :mechanicId AND status = 'PENDING'")
    LiveData<Integer> getPendingCountForMechanic(int mechanicId);

    @Query("SELECT COUNT(*) FROM appointments WHERE mechanicId = :mechanicId AND date = :date AND status IN ('APPROVED', 'IN_PROGRESS')")
    LiveData<Integer> getTodayCountForMechanic(int mechanicId, String date);

    class CategoryCount {
        public String category;
        public int count;
    }
}
