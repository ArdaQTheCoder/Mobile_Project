package com.example.mobile_project.ui.customer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.entity.Vehicle;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicles = new ArrayList<>();

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        holder.tvMakeModel.setText(vehicle.getMake() + " " + vehicle.getModel());
        holder.tvYear.setText(String.valueOf(vehicle.getYear()));
        holder.tvMileage.setText(vehicle.getMileage() + " km");

        if (vehicle.getPlateNumber() != null && !vehicle.getPlateNumber().isEmpty()) {
            holder.tvPlate.setVisibility(View.VISIBLE);
            holder.tvPlate.setText(vehicle.getPlateNumber());
        } else {
            holder.tvPlate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvMakeModel, tvYear, tvMileage, tvPlate;

        VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMakeModel = itemView.findViewById(R.id.tvMakeModel);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvMileage = itemView.findViewById(R.id.tvMileage);
            tvPlate = itemView.findViewById(R.id.tvPlate);
        }
    }
}
