package com.example.mobile_project.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages",
        foreignKeys = {
                @ForeignKey(entity = Appointment.class, parentColumns = "id",
                        childColumns = "appointmentId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id",
                        childColumns = "senderId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("appointmentId"),
                @Index("senderId")
        })
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int appointmentId;
    private int senderId;
    private String content;
    private long timestamp;
    private boolean isRead;

    public Message() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
