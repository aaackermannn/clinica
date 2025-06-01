package com.raven.clinic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvNoPreviousAppointments;
    private RecyclerView rvPreviousAppointments;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvNoPreviousAppointments = findViewById(R.id.tvNoPreviousAppointments);
        rvPreviousAppointments = findViewById(R.id.rvPreviousAppointments);
        btnLogout = findViewById(R.id.btnLogout);

        // Предположим, что AppointmentManager хранит все отменённые записи
        List<AppointmentManager.Appointment> cancelled =
                AppointmentManager.getInstance().getCancelledAppointments();

        if (cancelled == null || cancelled.isEmpty()) {
            tvNoPreviousAppointments.setVisibility(View.VISIBLE);
            rvPreviousAppointments.setVisibility(View.GONE);
        } else {
            tvNoPreviousAppointments.setVisibility(View.GONE);
            rvPreviousAppointments.setVisibility(View.VISIBLE);
            rvPreviousAppointments.setLayoutManager(new LinearLayoutManager(this));
            rvPreviousAppointments.setAdapter(new PreviousAppointmentsAdapter(cancelled));
        }

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true; // Уже здесь
            }
            return false;
        });
    }
}



