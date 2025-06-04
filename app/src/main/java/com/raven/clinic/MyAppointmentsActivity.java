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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rvActiveAppointments;
    private TextView tvNoActiveAppointments;
    private ActiveAppointmentsAdapter adapter;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<AppointmentManager.Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        // Инициализируем Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rvActiveAppointments   = findViewById(R.id.rvActiveAppointments);
        tvNoActiveAppointments = findViewById(R.id.tvNoActiveAppointments);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Если не залогинен — возвращаемся на Home
            startActivity(new Intent(MyAppointmentsActivity.this, HomeActivity.class));
            finish();
            return;
        }

        String uid = user.getUid();
        // Загружаем активные записи из Firestore
        db.collection("appointments")
                .document(uid)
                .collection("user_appointments")
                .get()
                .addOnSuccessListener((QuerySnapshot snapshot) -> {
                    if (snapshot.isEmpty()) {
                        tvNoActiveAppointments.setVisibility(View.VISIBLE);
                        rvActiveAppointments.setVisibility(View.GONE);
                    } else {
                        tvNoActiveAppointments.setVisibility(View.GONE);
                        rvActiveAppointments.setVisibility(View.VISIBLE);
                        for (QueryDocumentSnapshot doc : snapshot) {
                            String docName    = doc.getString("doctorName");
                            String specialty  = doc.getString("specialty");
                            String dateTime   = doc.getString("dateTime");
                            String photo      = doc.getString("doctorPhoto");
                            AppointmentManager.Appointment appt =
                                    new AppointmentManager.Appointment(docName, specialty, dateTime, photo);
                            appointmentList.add(appt);
                        }
                        rvActiveAppointments.setLayoutManager(new LinearLayoutManager(this));
                        adapter = new ActiveAppointmentsAdapter(appointmentList);
                        rvActiveAppointments.setAdapter(adapter);
                    }
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
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) return;
                String uid = user.getUid();
                // Удаляем запись из Firestore
                db.collection("appointments")
                        .document(uid)
                        .collection("user_appointments")
                        .document(appt.doctorName)
                        .delete()
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
