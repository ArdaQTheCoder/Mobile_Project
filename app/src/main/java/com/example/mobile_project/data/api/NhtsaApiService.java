package com.example.mobile_project.data.api;

import com.example.mobile_project.data.api.model.NhtsaMakeResponse;
import com.example.mobile_project.data.api.model.NhtsaModelResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NhtsaApiService {

    @GET("vehicles/GetMakesForVehicleType/car?format=json")
    Call<NhtsaMakeResponse> getCarMakes();

    @GET("vehicles/GetModelsForMakeId/{makeId}?format=json")
    Call<NhtsaModelResponse> getModelsForMake(@Path("makeId") int makeId);
}
