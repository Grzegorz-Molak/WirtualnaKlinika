package com.bemsi.controller;

import com.bemsi.DTOs.mapper.AppointmentMapper;
import com.bemsi.DTOs.mapper.UserMapper;
import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.model.UserDetails;
import com.bemsi.security.AccessService;
import com.bemsi.security.JwtService;
import com.bemsi.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtService jwtService;
    private final AccessService accessService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService,
                                 JwtService jwtService,
                                 AccessService accessService){
        this.appointmentService = appointmentService;
        this.jwtService = jwtService;
        this.accessService = accessService;
    }

    @PostMapping("/{appointment_id}/{user_login}")
    public String  makeAppointment(@PathVariable long appointment_id,
                                   @PathVariable String user_login){

        return appointmentService.makeAppointment(appointment_id,user_login);
    }

    @GetMapping("/{id}")
    private AppointmentDto AppointmentById(@PathVariable Long id, @NotNull HttpServletRequest request) {

        UserDetails user = jwtService.authorization(request);
        accessService.validateAccess(AccessService.Resource.APPOINTMENT,
                user,
                id);
        return appointmentService.findAppointment(id);
    }


    @GetMapping
    public List<AppointmentDto> getAllAppointments() {
        return appointmentService.findAll();
    }


}
