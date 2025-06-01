package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rvActiveAppointments;
    private TextView tvNoActiveAppointments;
    private ActiveAppointmentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        rvActiveAppointments   = findViewById(R.id.rvActiveAppointments);
        tvNoActiveAppointments = findViewById(R.id.tvNoActiveAppointments);

        List<AppointmentManager.Appointment> active =
                AppointmentManager.getInstance().getAllAppointments();

        if (active == null || active.isEmpty()) {
            tvNoActiveAppointments.setVisibility(View.VISIBLE);
            rvActiveAppointments.setVisibility(View.GONE);
        } else {
            tvNoActiveAppointments.setVisibility(View.GONE);
            rvActiveAppointments.setVisibility(View.VISIBLE);
            rvActiveAppointments.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ActiveAppointmentsAdapter(active);
            rvActiveAppointments.setAdapter(adapter);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(MyAppointmentsActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MyAppointmentsActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    // Адаптер для активных записей
    private class ActiveAppointmentsAdapter
            extends RecyclerView.Adapter<ActiveAppointmentsAdapter.AppointmentViewHolder> {

        private final List<AppointmentManager.Appointment> appointments;

        ActiveAppointmentsAdapter(List<AppointmentManager.Appointment> appointments) {
            this.appointments = appointments;
        }

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(
                @NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_appointment, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull AppointmentViewHolder holder, int position) {
            AppointmentManager.Appointment appt = appointments.get(position);

            holder.tvDoctorName.setText(appt.doctorName);
            holder.tvSpecialty.setText(appt.specialty);
            holder.tvDateTime.setText(appt.dateTime);

            int resId = getResources().getIdentifier(
                    appt.doctorPhoto.replace(".png", ""),
                    "drawable",
                    getPackageName()
            );
            if (resId != 0) {
                holder.imgDoctorPhoto.setImageResource(resId);
            } else {
                holder.imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
            }

            holder.btnMyCancel.setOnClickListener(v -> {
                AppointmentManager.getInstance().removeAppointment(appt.doctorName);
                appointments.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(MyAppointmentsActivity.this,
                        "Запись отменена", Toast.LENGTH_SHORT).show();

                // Если больше нет записей, переключаемся на пустой экран
                if (appointments.isEmpty()) {
                    tvNoActiveAppointments.setVisibility(View.VISIBLE);
                    rvActiveAppointments.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        class AppointmentViewHolder extends RecyclerView.ViewHolder {
            android.widget.ImageView imgDoctorPhoto;
            android.widget.TextView tvDoctorName, tvSpecialty, tvDateTime;
            android.widget.Button btnMyCancel;

            AppointmentViewHolder(@NonNull android.view.View itemView) {
                super(itemView);
                imgDoctorPhoto     = itemView.findViewById(R.id.imgMyDoctorPhoto);
                tvDoctorName       = itemView.findViewById(R.id.tvMyDoctorName);
                tvSpecialty        = itemView.findViewById(R.id.tvMySpecialty);
                tvDateTime         = itemView.findViewById(R.id.tvMyDateTime);
                btnMyCancel        = itemView.findViewById(R.id.btnMyCancel);
            }
        }
    }
}