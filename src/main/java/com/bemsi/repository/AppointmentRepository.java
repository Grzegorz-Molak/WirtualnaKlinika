package com.bemsi.repository;

import com.bemsi.model.Appointment;
import com.bemsi.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    boolean existsByDoctorAndStartTime(UserDetails doctor, LocalDateTime startTime);
    Optional<Appointment> findById(long appointment_id);
    List<Appointment> findAllByPatientAndStartTimeBefore(UserDetails patient, LocalDateTime time);
    List<Appointment> findAllByPatientAndStartTimeAfter(UserDetails patient, LocalDateTime time);
    List<Appointment> findAllByDoctorAndPatientNotNullAndStartTimeBefore(UserDetails doctor, LocalDateTime time);
    List<Appointment> findAllByDoctorAndPatientNotNullAndStartTimeAfter(UserDetails doctor, LocalDateTime time);
    List<Appointment> findAllByPatientAndDoctorAndStartTimeBefore(UserDetails patient, UserDetails doctor, LocalDateTime time);
    List<Appointment> findAllByPatientAndDoctorAndStartTimeAfter(UserDetails patient, UserDetails doctor, LocalDateTime time);

    List<Appointment> findAllByPatientNullAndStartTimeAfter(LocalDateTime time);
}
