package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView rvDoctors;
    private DoctorsAdapter adapter;
    private List<Doctor> allDoctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1) Поле поиска
        etSearch = findViewById(R.id.etSearch);

        // 2) Кнопка "Мои записи"
        LinearLayout buttonMyAppointments = findViewById(R.id.buttonMyAppointments);
        buttonMyAppointments.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MyAppointmentsActivity.class));
        });

        // 3) RecyclerView со списком врачей
        rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));

        allDoctors = getSampleDoctors(); // теперь четыре новых врача
        adapter = new DoctorsAdapter(new ArrayList<>(allDoctors));
        rvDoctors.setAdapter(adapter);

        // Фильтрация при вводе текста
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 4) BottomNav (в меню уже нет nav_add)
        findViewById(R.id.nav_home).setOnClickListener(v -> { /* Уже здесь */ });
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        });
    }

    private void filterDoctors(String query) {
        List<Doctor> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        for (Doctor doc : allDoctors) {
            if (doc.name.toLowerCase().contains(lowerQuery) ||
                    doc.specialty.toLowerCase().contains(lowerQuery)) {
                filtered.add(doc);
            }
        }
        adapter.updateList(filtered);
    }

    private List<Doctor> getSampleDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Billi Ailish", "Педиатр", 4.9, 123, "billie"));
        doctors.add(new Doctor("Anthony", "Спортивный врач", 4.8, 95, "anthony"));
        doctors.add(new Doctor("Мирон Федоров", "Детский логопед", 4.9, 87, "miron"));
        doctors.add(new Doctor("Скала", "Уролог", 5.0, 64, "skala"));
        return doctors;
    }

    // Класс «Doctor» с полем photoName (имя файла из drawable без расширения)
    private static class Doctor {
        String name;
        String specialty;
        double rating;
        int reviews;
        String photoName;

        public Doctor(String name, String specialty, double rating, int reviews, String photoName) {
            this.name = name;
            this.specialty = specialty;
            this.rating = rating;
            this.reviews = reviews;
            this.photoName = photoName;
        }
    }

    private class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {
        private List<Doctor> doctors;

        public DoctorsAdapter(List<Doctor> doctors) {
            this.doctors = doctors;
        }

        @Override
        public DoctorViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_doctor, parent, false);
            return new DoctorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DoctorViewHolder holder, int position) {
            Doctor doctor = doctors.get(position);
            holder.tvDoctorName.setText(doctor.name);
            holder.tvSpecialty.setText(doctor.specialty);

            // Загружаем фото врача из ресурсов по имени
            int resId = getResources().getIdentifier(
                    doctor.photoName,
                    "drawable",
                    getPackageName()
            );
            if (resId != 0) {
                holder.imgDoctorPhoto.setImageResource(resId);
            } else {
                holder.imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
            }

            holder.btnBook.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
                intent.putExtra("doctor_name", doctor.name);
                intent.putExtra("doctor_specialty", doctor.specialty);
                intent.putExtra("doctor_photo", doctor.photoName + ".png");
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }

        public void updateList(List<Doctor> filteredList) {
            this.doctors = filteredList;
            notifyDataSetChanged();
        }

        class DoctorViewHolder extends RecyclerView.ViewHolder {
            android.widget.ImageView imgDoctorPhoto;
            android.widget.TextView tvDoctorName, tvSpecialty;
            android.widget.Button btnBook;

            public DoctorViewHolder(android.view.View itemView) {
                super(itemView);
                imgDoctorPhoto = itemView.findViewById(R.id.imgDoctorPhoto);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                btnBook = itemView.findViewById(R.id.btnBook);
            }
        }
    }
}



