package com.raven.clinic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    private TextView tvSelectedDate;
    private Calendar selectedDateTime;
    private String doctorName, doctorSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // Получаем данные о враче
        doctorName = getIntent().getStringExtra("doctor_name");
        doctorSpecialty = getIntent().getStringExtra("doctor_specialty");

        TextView tvDoctorName = findViewById(R.id.tvDoctorName);
        TextView tvSpecialty = findViewById(R.id.tvSpecialty);
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        selectedDateTime = Calendar.getInstance();

        Button btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectDate.setOnClickListener(v -> showDateTimePicker());

        Button btnBook = findViewById(R.id.btnBook);
        btnBook.setOnClickListener(v -> confirmAppointment());
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    selectedDateTime.set(year, month, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (TimePicker view, int hourOfDay, int minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                updateSelectedDateTime();
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateSelectedDateTime() {
        String dateTime = android.text.format.DateFormat.format("dd MMMM yyyy, HH:mm", selectedDateTime).toString();
        tvSelectedDate.setText(dateTime);
    }

    private void confirmAppointment() {
        if (tvSelectedDate.getText().toString().isEmpty()) {
            tvSelectedDate.setError("Выберите дату и время");
            return;
        }

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("doctor_name", doctorName);
        intent.putExtra("doctor_specialty", doctorSpecialty);
        intent.putExtra("date_time", selectedDateTime.getTimeInMillis());
        startActivity(intent);
    }
}
