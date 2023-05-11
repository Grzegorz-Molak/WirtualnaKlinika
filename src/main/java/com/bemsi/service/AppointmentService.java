package com.bemsi.service;

import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.model.Appointment;
import com.bemsi.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface AppointmentService {

    List<AppointmentDto> findAll();
}
