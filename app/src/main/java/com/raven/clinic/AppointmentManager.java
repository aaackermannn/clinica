package com.raven.clinic;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {
    public static class Appointment {
        public String doctorName;
        public String specialty;
        public String dateTime;
        public String doctorPhoto;

        public Appointment(String name, String specialty, String dateTime, String photo) {
            this.doctorName   = name;
            this.specialty    = specialty;
            this.dateTime     = dateTime;
            this.doctorPhoto  = photo;
        }
    }

    private static AppointmentManager instance;
    private final List<Appointment> appointments = new ArrayList<>();

    private AppointmentManager() { }

    public static AppointmentManager getInstance() {
        if (instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public Appointment getAppointmentForDoctor(String doctorName) {
        for (Appointment a : appointments) {
            if (a.doctorName.equals(doctorName)) {
                return a;
            }
        }
        return null;
    }

    public void addOrUpdateAppointment(Appointment appt) {
        Appointment existing = getAppointmentForDoctor(appt.doctorName);
        if (existing != null) {
            appointments.remove(existing);
        }
        appointments.add(appt);
    }

    public void removeAppointment(String doctorName) {
        Appointment existing = getAppointmentForDoctor(doctorName);
        if (existing != null) {
            appointments.remove(existing);
        }
    }
}
