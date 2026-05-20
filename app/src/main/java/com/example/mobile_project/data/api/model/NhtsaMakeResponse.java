package com.example.mobile_project.data.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NhtsaMakeResponse {
    @SerializedName("Count")
    public int count;

    @SerializedName("Results")
    public List<Make> results;

    public static class Make {
        @SerializedName("MakeId")
        public int makeId;

        @SerializedName("MakeName")
        public String makeName;
    }
}
