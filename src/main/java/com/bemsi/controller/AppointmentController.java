package com.bemsi.controller;

import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{appointment_id}/{user_login}")
    public String  makeAppointment(@PathVariable long appointment_id,
                                   @PathVariable String user_login){

        return appointmentService.makeAppointment(appointment_id,user_login);
    }


    @GetMapping
    public List<AppointmentDto> getAllAppointments() {
        return appointmentService.findAll();
    }


}
