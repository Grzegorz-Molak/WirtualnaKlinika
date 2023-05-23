package com.bemsi.service;

import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.model.Appointment;
import com.bemsi.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    List<AppointmentDto> findAll();

    AppointmentDto findAppointment(long id);

    String makeAppointment(long appointment_id, String  user_login);
}
