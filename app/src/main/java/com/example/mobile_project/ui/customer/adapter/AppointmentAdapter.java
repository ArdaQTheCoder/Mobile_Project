package com.example.mobile_project.ui.customer.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.model.AppointmentWithDetails;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnRateClickListener {
        void onRateClick(AppointmentWithDetails appointment);
    }

    private List<AppointmentWithDetails> appointments = new ArrayList<>();
    private final OnRateClickListener rateListener;

    public AppointmentAdapter(OnRateClickListener rateListener) {
        this.rateListener = rateListener;
    }

    public void setAppointments(List<AppointmentWithDetails> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentWithDetails item = appointments.get(position);

        holder.tvDateTime.setText(item.appointment.getDate() + " " + item.appointment.getTime());
        holder.tvMechanic.setText(item.mechanicName);
        holder.tvVehicle.setText(item.vehicleMake + " " + item.vehicleModel);
        holder.tvCategory.setText(item.appointment.getCategory());
        holder.tvDescription.setText(item.appointment.getDescription());

        String status = item.appointment.getStatus();
        holder.tvStatus.setText(status);

        int colorRes;
        switch (status) {
            case "APPROVED": colorRes = R.color.status_approved; break;
            case "IN_PROGRESS": colorRes = R.color.status_in_progress; break;
            case "COMPLETED": colorRes = R.color.status_completed; break;
            case "REJECTED": colorRes = R.color.status_rejected; break;
            default: colorRes = R.color.status_pending; break;
        }
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorRes);
        holder.tvStatus.setTextColor(color);
        holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(color & 0x33FFFFFF));

        if ("COMPLETED".equals(status) && rateListener != null) {
            holder.btnRate.setVisibility(View.VISIBLE);
            holder.btnRate.setOnClickListener(v -> rateListener.onRateClick(item));
        } else {
            holder.btnRate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvDateTime, tvMechanic, tvVehicle, tvCategory, tvDescription, tvStatus;
        MaterialButton btnRate;

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvMechanic = itemView.findViewById(R.id.tvMechanic);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}
