package com.example.mobile_project.ui.mechanic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.model.AppointmentWithDetails;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class PendingAppointmentAdapter extends RecyclerView.Adapter<PendingAppointmentAdapter.ViewHolder> {

    public interface OnActionListener {
        void onApprove(AppointmentWithDetails item);
        void onReject(AppointmentWithDetails item);
    }

    private List<AppointmentWithDetails> appointments = new ArrayList<>();
    private final OnActionListener listener;

    public PendingAppointmentAdapter(OnActionListener listener) {
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
                .inflate(R.layout.item_pending_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentWithDetails item = appointments.get(position);

        holder.tvCustomerName.setText(item.customerName);
        holder.tvDateTime.setText(item.appointment.getDate() + " " + item.appointment.getTime());
        holder.tvVehicle.setText(item.vehicleYear + " " + item.vehicleMake + " " + item.vehicleModel);
        holder.tvCategory.setText(item.appointment.getCategory());
        holder.tvDescription.setText(item.appointment.getDescription());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(item));
        holder.btnReject.setOnClickListener(v -> listener.onReject(item));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvCustomerName, tvDateTime, tvVehicle, tvCategory, tvDescription;
        MaterialButton btnApprove, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
