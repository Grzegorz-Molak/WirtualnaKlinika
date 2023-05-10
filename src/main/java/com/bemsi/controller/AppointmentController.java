package com.bemsi.controller;

import com.bemsi.model.Appointment;
import com.bemsi.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    //@GetMapping
    //public List<Appointment> getAllAppointments(){
      //  return appointmentRepository.findAll();
    //}
}
