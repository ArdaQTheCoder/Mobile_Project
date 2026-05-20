package com.example.mobile_project.ui.customer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.model.MechanicWithUser;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class MechanicAdapter extends RecyclerView.Adapter<MechanicAdapter.MechanicViewHolder> {

    public interface OnMechanicClickListener {
        void onMechanicClick(MechanicWithUser mechanic);
    }

    private List<MechanicWithUser> mechanics = new ArrayList<>();
    private final OnMechanicClickListener listener;

    public MechanicAdapter(OnMechanicClickListener listener) {
        this.listener = listener;
    }

    public void setMechanics(List<MechanicWithUser> mechanics) {
        this.mechanics = mechanics;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MechanicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mechanic, parent, false);
        return new MechanicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MechanicViewHolder holder, int position) {
        MechanicWithUser mechanic = mechanics.get(position);
        holder.tvName.setText(mechanic.fullName);
        holder.chipSpecialization.setText(mechanic.profile.getSpecialization());
        holder.ratingBar.setRating(mechanic.profile.getRating());
        holder.tvRating.setText(String.format("%.1f (%d reviews)",
                mechanic.profile.getRating(), mechanic.profile.getReviewCount()));
        holder.tvExperience.setText(mechanic.profile.getExperienceYears() + " years experience");

        holder.itemView.setOnClickListener(v -> listener.onMechanicClick(mechanic));
    }

    @Override
    public int getItemCount() {
        return mechanics.size();
    }

    static class MechanicViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvName, tvRating, tvExperience;
        Chip chipSpecialization;
        RatingBar ratingBar;

        MechanicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            chipSpecialization = itemView.findViewById(R.id.chipSpecialization);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
