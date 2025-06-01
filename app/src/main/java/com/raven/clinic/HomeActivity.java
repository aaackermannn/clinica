package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

        allDoctors = getSampleDoctors();
        adapter = new DoctorsAdapter(new ArrayList<>(allDoctors));
        rvDoctors.setAdapter(adapter);

        // Фильтрация при вводе текста
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // не используем
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // не используем
            }
        });

        // 4) BottomNav
        setupBottomNavigation();
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

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            // Уже здесь
        });
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        });
    }

    private List<Doctor> getSampleDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Доктор Шон Мерфи", "Хирург", 4.9, 123, "shon.png"));
        doctors.add(new Doctor("Антонио Бандерас", "Кардиолог", 4.8, 95, "banderas.png"));
        doctors.add(new Doctor("Георгий Рубинский", "Стилист", 4.9, 87, "rubinskiy.png"));
        doctors.add(new Doctor("Юрий Каплан", "Кардиолог", 5.0, 64, "kaplan.png"));
        doctors.add(new Doctor("Мирон Федоров", "Логопед", 5.0, 42, "fedorov.png"));
        doctors.add(new Doctor("Скала", "Уролог", 5.0, 101, "skala.png"));
        return doctors;
    }

    // Класс «Doctor» с полем photoName
    private static class Doctor {
        String name;
        String specialty;
        double rating;
        int reviews;
        String photoName; // имя файла в drawable, без расширения

        public Doctor(String name, String specialty, double rating, int reviews, String photoName) {
            this.name = name;
            this.specialty = specialty;
            this.rating = rating;
            this.reviews = reviews;
            this.photoName = photoName;
        }
    }

    // Адаптер со стопроцентной поддержкой фото и клика «Записаться»
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

            // Загрузим фото врача из ресурсов по имени
            int resId = getResources().getIdentifier(
                    doctor.photoName.replace(".png",""),
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
                intent.putExtra("doctor_photo", doctor.photoName);
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


