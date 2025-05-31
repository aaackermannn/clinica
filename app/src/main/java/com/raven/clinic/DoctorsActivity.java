package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DoctorsActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);
        setupBottomNavigation();

        rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));

        List<Doctor> doctors = getSampleDoctors();
        adapter = new DoctorsAdapter(doctors);
        rvDoctors.setAdapter(adapter);
    }

    private List<Doctor> getSampleDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Доктор Шон Мерфи", "Хирург", 4.9, 123));
        doctors.add(new Doctor("Антонио Бандерас", "Кардиолог", 4.8, 95));
        doctors.add(new Doctor("Георгий Рубинский", "Стилист", 4.9, 87));
        doctors.add(new Doctor("Юрий Каплан", "Кардиолог", 5.0, 64));
        doctors.add(new Doctor("Мирон Федоров", "Логопед", 5.0, 42));
        doctors.add(new Doctor("Скала", "Уролог", 5.0, 101));
        return doctors;
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.nav_add).setOnClickListener(v -> {
            // Уже здесь
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private static class Doctor {
        String name;
        String specialty;
        double rating;
        int reviews;

        public Doctor(String name, String specialty, double rating, int reviews) {
            this.name = name;
            this.specialty = specialty;
            this.rating = rating;
            this.reviews = reviews;
        }
    }

    private class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

        private final List<Doctor> doctors;

        public DoctorsAdapter(List<Doctor> doctors) {
            this.doctors = doctors;
        }

        @NonNull
        @Override
        public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_doctor, parent, false);
            return new DoctorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            Doctor doctor = doctors.get(position);
            holder.tvDoctorName.setText(doctor.name);
            holder.tvSpecialty.setText(doctor.specialty);
            holder.tvRating.setText(String.valueOf(doctor.rating));
            holder.tvReviews.setText("(" + doctor.reviews + " отзывов)");

            holder.btnBook.setOnClickListener(v -> {
                Intent intent = new Intent(DoctorsActivity.this, AppointmentActivity.class);
                intent.putExtra("doctor_name", doctor.name);
                intent.putExtra("doctor_specialty", doctor.specialty);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }

        public class DoctorViewHolder extends RecyclerView.ViewHolder {
            TextView tvDoctorName, tvSpecialty, tvRating, tvReviews;
            Button btnBook;

            public DoctorViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                tvRating = itemView.findViewById(R.id.tvRating);
                tvReviews = itemView.findViewById(R.id.tvReviews);
                btnBook = itemView.findViewById(R.id.btnBook);
            }
        }
    }
}
