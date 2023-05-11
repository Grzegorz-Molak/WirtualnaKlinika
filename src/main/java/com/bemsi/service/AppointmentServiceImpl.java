package com.bemsi.service;

import com.bemsi.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentRepository appointmentRepository;
    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }

}
