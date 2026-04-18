package com.example.gymcrush.ui.staff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.PerfilResponseDto;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private final List<PerfilResponseDto> staffList;

    public StaffAdapter(List<PerfilResponseDto> staffList) {
        this.staffList = staffList;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        PerfilResponseDto perfil = staffList.get(position);
        holder.tvNombre.setText(perfil.getNombre() + " " + (perfil.getApellidos() != null ? perfil.getApellidos() : ""));
        holder.tvUsername.setText("@perfil_" + perfil.getId()); // O usar el username si estuviera en PerfilResponseDto
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUsername;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvStaffNombre);
            tvUsername = itemView.findViewById(R.id.tvStaffUsername);
        }
    }
}
