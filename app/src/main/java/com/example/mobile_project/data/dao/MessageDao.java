package com.example.mobile_project.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobile_project.data.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    long insert(Message message);

    @Query("SELECT * FROM messages WHERE appointmentId = :appointmentId ORDER BY timestamp ASC")
    LiveData<List<Message>> getByAppointmentId(int appointmentId);

    @Query("UPDATE messages SET isRead = 1 WHERE appointmentId = :appointmentId AND senderId != :currentUserId")
    void markAsRead(int appointmentId, int currentUserId);

    @Query("SELECT COUNT(*) FROM messages WHERE appointmentId = :appointmentId AND senderId != :currentUserId AND isRead = 0")
    LiveData<Integer> getUnreadCount(int appointmentId, int currentUserId);
}
