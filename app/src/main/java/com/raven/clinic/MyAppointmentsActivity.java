package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rvAppointments;
    private AppointmentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        rvAppointments = findViewById(R.id.rvAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));

        List<Appointment> appointments = getSampleAppointments();
        adapter = new AppointmentsAdapter(appointments);
        rvAppointments.setAdapter(adapter);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.nav_add).setOnClickListener(v -> {
            // Перейдём на список врачей
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private List<Appointment> getSampleAppointments() {
        List<Appointment> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        list.add(new Appointment("Доктор Шон Мерфи", "Хирург", now + 86400000));    // +1 день
        list.add(new Appointment("Антонио Бандерас", "Кардиолог", now + 172800000)); // +2 дня
        return list;
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

        @Override
        public AppointmentViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_appointment, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AppointmentViewHolder holder, int position) {
            Appointment appt = appointments.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
            String dateTimeStr = sdf.format(new Date(appt.dateTime));

            holder.tvDoctorName.setText(appt.doctorName);
            holder.tvSpecialty.setText(appt.specialty);
            holder.tvDateTime.setText(dateTimeStr);

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

        class AppointmentViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView tvDoctorName, tvSpecialty, tvDateTime;
            android.widget.Button btnCancel;

            public AppointmentViewHolder(android.view.View itemView) {
                super(itemView);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}

