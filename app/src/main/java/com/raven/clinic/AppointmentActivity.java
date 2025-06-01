package com.raven.clinic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    private TextView tvDoctorName;
    private TextView tvSpecialty;
    private TextView tvSelectedDateTime;
    private Calendar selectedDateTime;
    private boolean isDateTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        Button btnSelectDateTime = findViewById(R.id.btnSelectDateTime);
        Button btnBook = findViewById(R.id.btnBook);

        // Получаем данные о враче из Intent
        String doctorName = getIntent().getStringExtra("doctor_name");
        String doctorSpecialty = getIntent().getStringExtra("doctor_specialty");
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);

        selectedDateTime = Calendar.getInstance();

        btnSelectDateTime.setOnClickListener(v -> showDateTimePicker());
        btnBook.setOnClickListener(v -> confirmAppointment());
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();

        // Сразу создаём и инициализируем datePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // При выборе даты открываем выбор времени
                    selectedDateTime.set(year, month, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                updateSelectedDateTime();
                                isDateTimeSet = true;
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // Ограничиваем выбором только текущую/будущие даты
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateSelectedDateTime() {
        String dateTimeStr = android.text.format.DateFormat
                .format("dd MMMM yyyy, HH:mm", selectedDateTime)
                .toString();
        tvSelectedDateTime.setText(dateTimeStr);
    }

    private void confirmAppointment() {
        if (!isDateTimeSet) {
            tvSelectedDateTime.setError("Выберите дату и время");
            return;
        }
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("doctor_name", tvDoctorName.getText().toString());
        intent.putExtra("doctor_specialty", tvSpecialty.getText().toString());
        intent.putExtra("date_time", selectedDateTime.getTimeInMillis());
        startActivity(intent);
    }
}


