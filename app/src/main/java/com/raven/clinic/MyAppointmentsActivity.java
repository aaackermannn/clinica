package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAppointments;
    private TextView tvEmptyState;
    private AppointmentsAdapter adapter;
    private List<AppointmentManager.Appointment> appointmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);
        setupBottomNavigation();

        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Получаем список из AppointmentManager
        appointmentsList = AppointmentManager.getInstance().getAppointments();

        if (appointmentsList.isEmpty()) {
            // Если нет активных записей
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewAppointments.setVisibility(View.GONE);
        } else {
            // Показываем список
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewAppointments.setVisibility(View.VISIBLE);

            recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AppointmentsAdapter(appointmentsList);
            recyclerViewAppointments.setAdapter(adapter);
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    // Адаптер для списка записей
    private class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

        private final List<AppointmentManager.Appointment> appointments;

        public AppointmentsAdapter(List<AppointmentManager.Appointment> appointments) {
            this.appointments = appointments;
        }

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_appointment, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
            AppointmentManager.Appointment appt = appointments.get(position);

            // Загружаем фото врача
            int resId = getResources().getIdentifier(
                    appt.photoName.replace(".png", ""),
                    "drawable",
                    getPackageName()
            );
            if (resId != 0) {
                holder.imgDoctorPhoto.setImageResource(resId);
            } else {
                holder.imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
            }

            holder.tvDoctorName.setText(appt.doctorName);
            holder.tvSpecialty.setText(appt.specialty);
            holder.tvDateTime.setText(appt.dateTime);

            holder.btnCancel.setOnClickListener(v -> {
                // Удаляем запись из менеджера и из списка
                AppointmentManager.getInstance().getAppointments().remove(position);
                appointments.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(MyAppointmentsActivity.this, "Запись отменена", Toast.LENGTH_SHORT).show();

                if (appointments.isEmpty()) {
                    // Если после удаления список пуст, показываем сообщение
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerViewAppointments.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        class AppointmentViewHolder extends RecyclerView.ViewHolder {
            android.widget.ImageView imgDoctorPhoto;
            TextView tvDoctorName, tvSpecialty, tvDateTime;
            Button btnCancel;

            public AppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                imgDoctorPhoto = itemView.findViewById(R.id.imgDoctorPhoto);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}




