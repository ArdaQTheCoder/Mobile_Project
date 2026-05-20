package com.example.mobile_project.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mobile_project.data.dao.AppointmentDao;
import com.example.mobile_project.data.dao.MechanicProfileDao;
import com.example.mobile_project.data.dao.MessageDao;
import com.example.mobile_project.data.dao.ReviewDao;
import com.example.mobile_project.data.dao.UserDao;
import com.example.mobile_project.data.dao.VehicleDao;
import com.example.mobile_project.data.entity.Appointment;
import com.example.mobile_project.data.entity.MechanicProfile;
import com.example.mobile_project.data.entity.Message;
import com.example.mobile_project.data.entity.Review;
import com.example.mobile_project.data.entity.User;
import com.example.mobile_project.data.entity.Vehicle;

@Database(
        entities = {User.class, Vehicle.class, MechanicProfile.class,
                Appointment.class, Message.class, Review.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract VehicleDao vehicleDao();
    public abstract MechanicProfileDao mechanicProfileDao();
    public abstract AppointmentDao appointmentDao();
    public abstract MessageDao messageDao();
    public abstract ReviewDao reviewDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "mobile_project_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
