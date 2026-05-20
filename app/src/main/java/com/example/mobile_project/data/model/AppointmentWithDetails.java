package com.example.mobile_project.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.example.mobile_project.data.entity.Appointment;

public class AppointmentWithDetails {
    @Embedded
    public Appointment appointment;

    @ColumnInfo(name = "mechanicName")
    public String mechanicName;

    @ColumnInfo(name = "customerName")
    public String customerName;

    @ColumnInfo(name = "vehicleMake")
    public String vehicleMake;

    @ColumnInfo(name = "vehicleModel")
    public String vehicleModel;

    @ColumnInfo(name = "vehicleYear")
    public int vehicleYear;

    @ColumnInfo(name = "customerPhone")
    public String customerPhone;
}
