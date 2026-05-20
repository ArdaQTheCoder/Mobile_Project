package com.example.mobile_project.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.example.mobile_project.data.entity.MechanicProfile;

public class MechanicWithUser {
    @Embedded
    public MechanicProfile profile;

    @ColumnInfo(name = "fullName")
    public String fullName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "phone")
    public String phone;
}
