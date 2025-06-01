package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorsAdapter adapter;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etSearch = findViewById(R.id.etSearch);
        // TODO: добавьте TextWatcher для фильтрации, если нужно

        rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));

        List<Doctor> doctors = getSampleDoctors();
        adapter = new DoctorsAdapter(doctors);
        rvDoctors.setAdapter(adapter);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            // Уже здесь, ничего не делаем
        });

        findViewById(R.id.nav_add).setOnClickListener(v -> {
            // Переход к выбору врача или записи:
            // В данной версии Home содержит список врачей,
            // поэтому можно просто скроллить вниз, либо в будущем сделать другой экран
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private List<Doctor> getSampleDoctors() {
        // TODO: в будущем брать из БД/сервера
        return java.util.Arrays.asList(
                new Doctor("Доктор Шон Мерфи", "Хирург", 4.9, 123),
                new Doctor("Антонио Бандерас", "Кардиолог", 4.8, 95),
                new Doctor("Георгий Рубинский", "Стилист", 4.9, 87),
                new Doctor("Юрий Каплан", "Кардиолог", 5.0, 64),
                new Doctor("Мирон Федоров", "Логопед", 5.0, 42),
                new Doctor("Скала", "Уролог", 5.0, 101)
        );
    }

    // Внутренний класс Doctor и DoctorsAdapter полностью идентичны тому, что ранее давали:
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
            // Рейтинг и отзывы можно отобразить, если раскомментировать и добавить в layout
            // holder.tvRating.setText(String.valueOf(doctor.rating));
            // holder.tvReviews.setText("(" + doctor.reviews + " отзывов)");

            holder.btnBook.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
                intent.putExtra("doctor_name", doctor.name);
                intent.putExtra("doctor_specialty", doctor.specialty);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }

        class DoctorViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView tvDoctorName, tvSpecialty;
            android.widget.Button btnBook;

            public DoctorViewHolder(android.view.View itemView) {
                super(itemView);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                btnBook = itemView.findViewById(R.id.btnBook);
            }
        }
    }
}
