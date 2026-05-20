package com.example.mobile_project.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id",
                        childColumns = "customerId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id",
                        childColumns = "mechanicId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Vehicle.class, parentColumns = "id",
                        childColumns = "vehicleId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("customerId"),
                @Index("mechanicId"),
                @Index("vehicleId")
        })
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int customerId;
    private int mechanicId;
    private int vehicleId;
    private String date;
    private String time;
    private String status; // PENDING, APPROVED, IN_PROGRESS, COMPLETED, REJECTED
    private String category; // ENGINE, ELECTRICAL, TRANSMISSION, BRAKES, OIL_CHANGE, GENERAL
    private String description;
    private String notes;
    private long createdAt;
    private long updatedAt;

    public Appointment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getMechanicId() { return mechanicId; }
    public void setMechanicId(int mechanicId) { this.mechanicId = mechanicId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
