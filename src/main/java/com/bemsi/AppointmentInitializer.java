package com.bemsi;

import com.bemsi.model.Appointment;
import com.bemsi.model.UserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().plusDays(1); // Start from tomorrow
        while (dates.size() < 30) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        Query query = entityManager.createNativeQuery("SELECT * FROM user_details u WHERE (u.role & 2) = 2", UserDetails.class);
        List<UserDetails> doctors = query.getResultList();

        for (UserDetails doctor : doctors) {
            for (LocalDate date : dates) {
                LocalDateTime startTime = date.atTime(9, 0);
                LocalDateTime endTime = date.atTime(15, 0);
                while (startTime.isBefore(endTime)) {
                    Query query2 = entityManager.createNativeQuery(
                            "SELECT * FROM appointments WHERE doctor_id = :doctor_id AND start_time = :startTime", Appointment.class);
                    query2.setParameter("doctor_id", doctor.getId());
                    query2.setParameter("startTime", startTime);
                    List<Appointment> appointments = query2.getResultList();
                    if(appointments.isEmpty()) {
                        Appointment appointment = new Appointment(doctor, startTime);
                        entityManager.persist(appointment);
                    }
                    startTime = startTime.plusMinutes(30); // Increment by half an hour
                }
            }
        }
    }

}
