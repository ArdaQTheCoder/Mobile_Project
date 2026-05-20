package com.example.mobile_project.ui.customer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.model.AppointmentWithDetails;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class ServiceRecordAdapter extends RecyclerView.Adapter<ServiceRecordAdapter.ServiceRecordViewHolder> {

    private List<AppointmentWithDetails> records = new ArrayList<>();

    public void setRecords(List<AppointmentWithDetails> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_record, parent, false);
        return new ServiceRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRecordViewHolder holder, int position) {
        AppointmentWithDetails item = records.get(position);
        holder.tvDate.setText(item.appointment.getDate());
        holder.tvCategory.setText(item.appointment.getCategory());
        holder.tvMechanic.setText(item.mechanicName);

        String desc = item.appointment.getDescription();
        String notes = item.appointment.getNotes();
        if (notes != null && !notes.isEmpty()) {
            holder.tvDescription.setText(notes);
        } else if (desc != null && !desc.isEmpty()) {
            holder.tvDescription.setText(desc);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ServiceRecordViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvDate, tvCategory, tvMechanic, tvDescription;

        ServiceRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvMechanic = itemView.findViewById(R.id.tvMechanic);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
