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

        boolean isTrainer = false;
        List<String> roles = perfil.getRoles();
        if (roles != null) {
            for (String roleName : roles) {
                if (roleName != null && (roleName.toUpperCase().contains("ENTRENADOR") || 
                    roleName.toUpperCase().contains("COACH") ||
                    roleName.toUpperCase().contains("TRAINER"))) {
                    isTrainer = true;
                    break;
                }
            }
        }

        if (isTrainer) {
            holder.tvRol.setVisibility(View.VISIBLE);
        } else {
            holder.tvRol.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUsername, tvRol;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvStaffNombre);
            tvUsername = itemView.findViewById(R.id.tvStaffUsername);
            tvRol = itemView.findViewById(R.id.tvStaffRol);
        }
    }
}
