package com.example.mobile_project.ui.mechanic.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.model.AppointmentWithDetails;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class MechanicTodayAdapter extends RecyclerView.Adapter<MechanicTodayAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(AppointmentWithDetails item);
    }

    private List<AppointmentWithDetails> appointments = new ArrayList<>();
    private final OnItemClickListener listener;

    public MechanicTodayAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setAppointments(List<AppointmentWithDetails> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mechanic_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentWithDetails item = appointments.get(position);

        holder.tvTime.setText(item.appointment.getTime());
        holder.tvCustomerName.setText(item.customerName);
        holder.tvVehicle.setText(item.vehicleYear + " " + item.vehicleMake + " " + item.vehicleModel);
        holder.tvCategory.setText(item.appointment.getCategory());

        String status = item.appointment.getStatus();
        holder.tvStatus.setText(status);

        int colorRes = "IN_PROGRESS".equals(status) ? R.color.status_in_progress : R.color.status_approved;
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorRes);
        holder.tvStatus.setTextColor(color);
        holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(color & 0x33FFFFFF));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvTime, tvCustomerName, tvVehicle, tvCategory, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
