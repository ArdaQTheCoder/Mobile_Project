package com.example.mobile_project.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_project.data.entity.MechanicProfile;
import com.example.mobile_project.data.model.MechanicWithUser;

import java.util.List;

@Dao
public interface MechanicProfileDao {
    @Insert
    long insert(MechanicProfile profile);

    @Update
    void update(MechanicProfile profile);

    @Query("SELECT * FROM mechanic_profiles WHERE userId = :userId")
    LiveData<MechanicProfile> getByUserId(int userId);

    @Query("SELECT * FROM mechanic_profiles WHERE userId = :userId")
    MechanicProfile getByUserIdSync(int userId);

    @Query("SELECT * FROM mechanic_profiles WHERE specialization = :specialization")
    LiveData<List<MechanicProfile>> getBySpecialization(String specialization);

    @Query("SELECT * FROM mechanic_profiles ORDER BY rating DESC")
    LiveData<List<MechanicProfile>> getAllSortedByRating();

    @Query("SELECT mp.*, u.fullName, u.email, u.phone FROM mechanic_profiles mp " +
            "INNER JOIN users u ON mp.userId = u.id ORDER BY mp.rating DESC")
    LiveData<List<MechanicWithUser>> getAllWithUser();

    @Query("SELECT mp.*, u.fullName, u.email, u.phone FROM mechanic_profiles mp " +
            "INNER JOIN users u ON mp.userId = u.id " +
            "WHERE mp.specialization = :specialization ORDER BY mp.rating DESC")
    LiveData<List<MechanicWithUser>> getBySpecializationWithUser(String specialization);
}
