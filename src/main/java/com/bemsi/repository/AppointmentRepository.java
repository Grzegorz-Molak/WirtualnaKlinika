package com.bemsi.repository;

import com.bemsi.model.Appointment;
import com.bemsi.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    boolean existsByDoctorAndStartTime(UserDetails doctor, LocalDateTime startTime);
    Optional<Appointment> findById(long appointment_id);
}
