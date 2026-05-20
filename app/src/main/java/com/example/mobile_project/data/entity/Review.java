package com.example.mobile_project.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews",
        foreignKeys = {
                @ForeignKey(entity = Appointment.class, parentColumns = "id",
                        childColumns = "appointmentId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id",
                        childColumns = "customerId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id",
                        childColumns = "mechanicId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = "appointmentId", unique = true),
                @Index("customerId"),
                @Index("mechanicId")
        })
public class Review {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int appointmentId;
    private int customerId;
    private int mechanicId;
    private int rating; // 1-5
    private String comment;
    private long createdAt;

    public Review() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getMechanicId() { return mechanicId; }
    public void setMechanicId(int mechanicId) { this.mechanicId = mechanicId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
