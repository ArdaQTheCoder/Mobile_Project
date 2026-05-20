package com.example.mobile_project.data.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NhtsaModelResponse {
    @SerializedName("Count")
    public int count;

    @SerializedName("Results")
    public List<Model> results;

    public static class Model {
        @SerializedName("Make_ID")
        public int makeId;

        @SerializedName("Model_ID")
        public int modelId;

        @SerializedName("Model_Name")
        public String modelName;
    }
}
