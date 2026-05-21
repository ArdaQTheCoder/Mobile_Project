package com.example.mobile_project.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project.R;
import com.example.mobile_project.data.model.AppointmentWithDetails;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(AppointmentWithDetails appointment);
    }

    private List<AppointmentWithDetails> appointments = new ArrayList<>();
    private final OnChatClickListener listener;
    private final boolean isCustomer;
    private Map<Integer, String> lastMessages = new HashMap<>();
    private Map<Integer, Long> lastTimestamps = new HashMap<>();
    private Map<Integer, Integer> unreadCounts = new HashMap<>();

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatListAdapter(OnChatClickListener listener, boolean isCustomer) {
        this.listener = listener;
        this.isCustomer = isCustomer;
    }

    public void setAppointments(List<AppointmentWithDetails> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    public void setChatMeta(Map<Integer, String> lastMessages,
                            Map<Integer, Long> lastTimestamps,
                            Map<Integer, Integer> unreadCounts) {
        this.lastMessages = lastMessages;
        this.lastTimestamps = lastTimestamps;
        this.unreadCounts = unreadCounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        AppointmentWithDetails item = appointments.get(position);
        int appointmentId = item.appointment.getId();

        holder.tvName.setText(isCustomer ? item.mechanicName : item.customerName);
        holder.tvAppointmentInfo.setText(
                item.appointment.getCategory() + " - " + item.vehicleMake + " " + item.vehicleModel);

        String lastMsg = lastMessages.get(appointmentId);
        holder.tvLastMessage.setText(lastMsg != null ? lastMsg : "");

        Long lastTs = lastTimestamps.get(appointmentId);
        holder.tvTime.setText(lastTs != null ? TIME_FORMAT.format(new Date(lastTs)) : "");

        Integer unread = unreadCounts.get(appointmentId);
        if (unread != null && unread > 0) {
            holder.tvUnreadBadge.setVisibility(View.VISIBLE);
            holder.tvUnreadBadge.setText(String.valueOf(unread));
        } else {
            holder.tvUnreadBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onChatClick(item));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvName, tvAppointmentInfo, tvLastMessage, tvTime, tvUnreadBadge;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAppointmentInfo = itemView.findViewById(R.id.tvAppointmentInfo);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUnreadBadge = itemView.findViewById(R.id.tvUnreadBadge);
        }
    }
}
