package com.example.mobile_project.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.example.mobile_project.data.entity.Review;

public class ReviewWithCustomer {
    @Embedded
    public Review review;

    @ColumnInfo(name = "customerName")
    public String customerName;
}
