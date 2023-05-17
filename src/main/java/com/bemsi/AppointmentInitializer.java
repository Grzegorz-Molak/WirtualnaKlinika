package com.bemsi;

import com.bemsi.model.Appointment;
import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.AppointmentRepository;
import com.bemsi.repository.UserDetailsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().plusDays(1); // Start from tomorrow
        while (dates.size() < 3) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
        List<UserDetails> doctors =userDetailsRepository.findAllDoctors();
        for (UserDetails doctor : doctors) {
            for (LocalDate date : dates) {
                LocalDateTime startTime = date.atTime(9, 0);
                LocalDateTime endTime = date.atTime(12, 0);
                while (startTime.isBefore(endTime)) {
                    if(!appointmentRepository.existsByDoctorAndStartTime(doctor, startTime)){
                        appointmentRepository.save(new Appointment(doctor, startTime));
                    }
                    startTime = startTime.plusMinutes(30); // Increment by half an hour
                }
            }
        }
    }

}
