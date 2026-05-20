package com.example.mobile_project.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_project.data.entity.Vehicle;

import java.util.List;

@Dao
public interface VehicleDao {
    @Insert
    long insert(Vehicle vehicle);

    @Update
    void update(Vehicle vehicle);

    @Delete
    void delete(Vehicle vehicle);

    @Query("SELECT * FROM vehicles WHERE userId = :userId")
    LiveData<List<Vehicle>> getByUserId(int userId);

    @Query("SELECT * FROM vehicles WHERE id = :id")
    LiveData<Vehicle> getById(int id);

    @Query("SELECT * FROM vehicles WHERE id = :id")
    Vehicle getByIdSync(int id);
}
