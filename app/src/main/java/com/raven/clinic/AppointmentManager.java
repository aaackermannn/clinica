package com.raven.clinic;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton-класс для хранения списка активных записей пользователя.
 */
public class AppointmentManager {
    private static AppointmentManager instance;
    private final List<Appointment> appointments = new ArrayList<>();

    private AppointmentManager() { }

    public static synchronized AppointmentManager getInstance() {
        if (instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    public void addAppointment(Appointment appt) {
        appointments.add(appt);
    }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public void clearAppointments() {
        appointments.clear();
    }

    public static class Appointment {
        public final String doctorName;
        public final String specialty;
        public final String dateTime;
        public final String photoName;

        public Appointment(String doctorName, String specialty, String dateTime, String photoName) {
            this.doctorName = doctorName;
            this.specialty = specialty;
            this.dateTime = dateTime;
            this.photoName = photoName;
        }
    }
}

