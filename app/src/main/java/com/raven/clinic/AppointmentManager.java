package com.raven.clinic;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {

    // Singleton
    private static AppointmentManager instance;

    public static AppointmentManager getInstance() {
        if (instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    // Внутренний класс «Запись»
    public static class Appointment {
        public String doctorName;
        public String specialty;
        public String dateTime;
        public String doctorPhoto; // имя файла с расширением

        public Appointment(String name, String specialty, String dateTime, String photo) {
            this.doctorName   = name;
            this.specialty    = specialty;
            this.dateTime     = dateTime;
            this.doctorPhoto  = photo;
        }
    }

    // Список активных (неназначенных) записей
    private final List<Appointment> appointments = new ArrayList<>();

    private AppointmentManager() { }

    // Возвращает список всех активных записей
    public List<Appointment> getAllAppointments() {
        // Возвращаем копию, чтобы внешний код не мог изменить напрямую
        return new ArrayList<>(appointments);
    }

    // Возвращает запись по врачу, или null, если нет
    public Appointment getAppointmentForDoctor(String doctorName) {
        for (Appointment a : appointments) {
            if (a.doctorName.equals(doctorName)) {
                return a;
            }
        }
        return null;
    }

    // Добавляет или заменяет запись (если уже есть запись для этого врача)
    public void addOrUpdateAppointment(Appointment appt) {
        // Если уже есть, удаляем старую
        Appointment existing = getAppointmentForDoctor(appt.doctorName);
        if (existing != null) {
            appointments.remove(existing);
        }
        appointments.add(appt);
    }

    // Удаляет запись для данного врача
    public void removeAppointment(String doctorName) {
        Appointment existing = getAppointmentForDoctor(doctorName);
        if (existing != null) {
            appointments.remove(existing);
        }
    }
}


