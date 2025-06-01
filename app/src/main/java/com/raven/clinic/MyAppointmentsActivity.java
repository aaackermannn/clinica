package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAppointments;
    private AppointmentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        // Находим RecyclerView по новому id
        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));

        List<Appointment> appointments = getSampleAppointments();
        adapter = new AppointmentsAdapter(appointments);
        recyclerViewAppointments.setAdapter(adapter);

        // BottomNav
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.nav_add).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class)); // или DoctorsActivity
            finish();
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private List<Appointment> getSampleAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment("Доктор Шон Мерфи", "Хирург", System.currentTimeMillis() + 86400000));
        appointments.add(new Appointment("Антонио Бандерас", "Кардиолог", System.currentTimeMillis() + 172800000));
        return appointments;
    }

    private static class Appointment {
        String doctorName;
        String specialty;
        long dateTime;

        public Appointment(String doctorName, String specialty, long dateTime) {
            this.doctorName = doctorName;
            this.specialty = specialty;
            this.dateTime = dateTime;
        }
    }

    private class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

        private final List<Appointment> appointments;

        public AppointmentsAdapter(List<Appointment> appointments) {
            this.appointments = appointments;
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
            Appointment appointment = appointments.get(position);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
            String dateTime = sdf.format(new Date(appointment.dateTime));

            holder.tvDoctorName.setText(appointment.doctorName);
            holder.tvSpecialty.setText(appointment.specialty);
            holder.tvDateTime.setText(dateTime);

            holder.btnCancel.setOnClickListener(v -> {
                appointments.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(MyAppointmentsActivity.this, "Запись отменена", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        public class AppointmentViewHolder extends RecyclerView.ViewHolder {
            TextView tvDoctorName, tvSpecialty, tvDateTime;
            Button btnCancel;

            public AppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}


