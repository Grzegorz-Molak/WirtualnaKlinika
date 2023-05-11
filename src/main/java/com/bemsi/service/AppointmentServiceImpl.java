package com.bemsi.service;

import com.bemsi.DTOs.mapper.AppointmentMapper;
import com.bemsi.DTOs.mapper.UserMapper;
import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.model.Appointment;
import com.bemsi.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentRepository appointmentRepository;
    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<AppointmentDto> findAll() {
        return (appointmentRepository.findAll()).stream().map(AppointmentMapper::toAppointmentDto).toList();
    }
}
