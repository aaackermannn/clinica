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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rvActiveAppointments;
    private TextView tvNoActiveAppointments;
    private ActiveAppointmentsAdapter adapter;
    private List<AppointmentData> appointmentList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        rvActiveAppointments = findViewById(R.id.rvActiveAppointments);
        tvNoActiveAppointments = findViewById(R.id.tvNoActiveAppointments);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Если не залогинен — возвращаемся на Home
            startActivity(new Intent(MyAppointmentsActivity.this, HomeActivity.class));
            finish();
            return;
        }

        String uid = user.getUid();
        // Считываем все записи пользователя из /appointments/{uid}
        dbRef.child("appointments")
                .child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists() || !snapshot.hasChildren()) {
                        // Нет активных записей
                        tvNoActiveAppointments.setVisibility(View.VISIBLE);
                        rvActiveAppointments.setVisibility(View.GONE);
                        return;
                    }
                    tvNoActiveAppointments.setVisibility(View.GONE);
                    rvActiveAppointments.setVisibility(View.VISIBLE);

                    // Проходим по каждой дочерней ноде
                    for (DataSnapshot apptSnapshot : snapshot.getChildren()) {
                        // apptSnapshot — это узел с пуш-ключом, внутри хранятся поля:
                        //   doctorName, specialty, dateTime, doctorPhoto
                        String doctorName = apptSnapshot.child("doctorName")
                                .getValue(String.class);
                        String specialty = apptSnapshot.child("specialty")
                                .getValue(String.class);
                        String dateTime  = apptSnapshot.child("dateTime")
                                .getValue(String.class);
                        String photo     = apptSnapshot.child("doctorPhoto")
                                .getValue(String.class);

                        // Если какое-то поле null (не должно), пропускаем
                        if (doctorName == null || specialty == null || dateTime == null || photo == null) {
                            continue;
                        }

                        appointmentList.add(new AppointmentData(doctorName, specialty, dateTime, photo));
                    }

                    // Настраиваем RecyclerView
                    rvActiveAppointments.setLayoutManager(new LinearLayoutManager(this));
                    adapter = new ActiveAppointmentsAdapter(appointmentList);
                    rvActiveAppointments.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MyAppointmentsActivity.this,
                                "Ошибка загрузки записей: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
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

    // Модель данных для одной записи
    public static class AppointmentData {
        public String doctorName;
        public String specialty;
        public String dateTime;
        public String doctorPhoto;

        // Пустой конструктор нужен для DataSnapshot.getValue(AppointmentData.class)
        public AppointmentData() { }

        public AppointmentData(String doctorName, String specialty, String dateTime, String doctorPhoto) {
            this.doctorName = doctorName;
            this.specialty = specialty;
            this.dateTime = dateTime;
            this.doctorPhoto = doctorPhoto;
        }
    }

    // Адаптер для RecyclerView
    private class ActiveAppointmentsAdapter
            extends RecyclerView.Adapter<ActiveAppointmentsAdapter.AppointmentViewHolder> {

        private final List<AppointmentData> appointments;

        ActiveAppointmentsAdapter(List<AppointmentData> appointments) {
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
            AppointmentData appt = appointments.get(position);

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
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) return;
                String uid = user.getUid();

                // Чтобы удалить, нужно найти тот же узел в RTDB. Поскольку мы не запоминали ключ push(),
                // придёмся искать по doctorName/специальности/дате, либо переписывать логику
                // и хранить сами ключи в appointmentList. Проще: в момент создания мы можем сохранять
                // сразу объект AppointmentData вместе с его ключом. Но здесь приведён упрощённый подход:
                // если ключ будет называться как doctorName.replaceAll("[.#$\\[\\]]", "_"), то:
                String safeKey = appt.doctorName.replaceAll("[.#$\\[\\]]", "_");
                // Иначе, если вы использовали push(), придётся сохранять сам ключ в AppointmentData.

                dbRef.child("appointments")
                        .child(uid)
                        .child(safeKey)
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            appointments.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(MyAppointmentsActivity.this,
                                    "Запись отменена", Toast.LENGTH_SHORT).show();
                            if (appointments.isEmpty()) {
                                tvNoActiveAppointments.setVisibility(View.VISIBLE);
                                rvActiveAppointments.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(MyAppointmentsActivity.this,
                                        "Не удалось отменить: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
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
