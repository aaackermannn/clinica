package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorsActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorsAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));

        List<Doctor> doctors = getSampleDoctors();
        adapter = new DoctorsAdapter(doctors);
        rvDoctors.setAdapter(adapter);

        setupBottomNavigation();
    }

    private List<Doctor> getSampleDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Доктор Шон Мерфи", "Хирург", "merphy", ""));
        doctors.add(new Doctor("Антонио Бандерас", "Кардиолог", "anthony", ""));
        doctors.add(new Doctor("Георгий Рубинский", "Стилист", "rubinskiy", ""));
        doctors.add(new Doctor("Юрий Каплан", "Кардиолог", "kaplan", ""));
        doctors.add(new Doctor("Мирон Федоров", "Логопед", "miron", ""));
        doctors.add(new Doctor("Скала", "Уролог", "skala", ""));
        return doctors;
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private static class Doctor {
        String name;
        String specialty;
        String photoResName;
        String about;

        public Doctor(String name, String specialty, String photoResName, String about) {
            this.name = name;
            this.specialty = specialty;
            this.photoResName = photoResName;
            this.about = about;
        }
    }

    private class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

        private final List<Doctor> doctors;

        DoctorsAdapter(List<Doctor> doctors) {
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

            int resId = getResources().getIdentifier(
                    doctor.photoResName,
                    "drawable",
                    getPackageName()
            );
            if (resId != 0) {
                holder.imgDoctorPhoto.setImageResource(resId);
            } else {
                holder.imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
            }

            holder.btnBook.setOnClickListener(v -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(DoctorsActivity.this,
                            "Сначала войдите в учётную запись", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = user.getUid();
                String safeKey = doctor.name.replaceAll("[.#$\\[\\]]", "_");

                database.child("appointments")
                        .child(uid)
                        .child(safeKey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String existingDateTime = snapshot.child("dateTime")
                                            .getValue(String.class);
                                    Intent intent = new Intent(DoctorsActivity.this, ManageAppointmentActivity.class);
                                    intent.putExtra("doctor_name", doctor.name);
                                    intent.putExtra("doctor_specialty", doctor.specialty);
                                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                                    intent.putExtra("current_date_time", existingDateTime);
                                    intent.putExtra("doctor_about", doctor.about);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(DoctorsActivity.this, AppointmentActivity.class);
                                    intent.putExtra("doctor_name", doctor.name);
                                    intent.putExtra("doctor_specialty", doctor.specialty);
                                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                                    intent.putExtra("doctor_about", doctor.about);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(DoctorsActivity.this,
                                        "Ошибка проверки записи: " + error.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }

        class DoctorViewHolder extends RecyclerView.ViewHolder {
            TextView tvDoctorName, tvSpecialty;
            ImageView imgDoctorPhoto;
            Button btnBook;

            DoctorViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                imgDoctorPhoto = itemView.findViewById(R.id.imgDoctorPhoto);
                btnBook = itemView.findViewById(R.id.btnBook);
            }
        }
    }
}
