package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    private CardView cardAppointment;
    private TextView tvAppointmentDoctor, tvAppointmentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupBottomNavigation();

        cardAppointment = findViewById(R.id.cardAppointment);
        tvAppointmentDoctor = findViewById(R.id.tvAppointmentDoctor);
        tvAppointmentDate = findViewById(R.id.tvAppointmentDate);
        Button btnMyAppointments = findViewById(R.id.btnMyAppointments);
        Button btnAllDoctors = findViewById(R.id.btnAllDoctors);

        // Проверка наличия записей
        if (hasAppointments()) {
            cardAppointment.setVisibility(View.VISIBLE);
            tvAppointmentDoctor.setText("Доктор Шон Мерфи");
            tvAppointmentDate.setText("23 Марта, 16:00");
        } else {
            cardAppointment.setVisibility(View.GONE);
        }

        btnMyAppointments.setOnClickListener(v -> {
            startActivity(new Intent(this, MyAppointmentsActivity.class));
        });

        btnAllDoctors.setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorsActivity.class));
        });

        cardAppointment.setOnClickListener(v -> {
            startActivity(new Intent(this, MyAppointmentsActivity.class));
        });
    }

    private boolean hasAppointments() {
        // Здесь должна быть логика проверки наличия записей
        return true; // Для примера всегда true
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            // Уже на главном экране
        });

        findViewById(R.id.nav_add).setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorsActivity.class));
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }
}