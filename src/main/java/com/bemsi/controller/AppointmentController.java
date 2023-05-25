package com.bemsi.controller;

import com.bemsi.DTOs.mapper.AppointmentMapper;
import com.bemsi.DTOs.mapper.UserMapper;
import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.model.Appointment;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.AppointmentRepository;
import com.bemsi.security.AccessService;
import com.bemsi.security.JwtService;
import com.bemsi.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin (origins={"https://localhost:3000"}, allowCredentials = "true")
//(origins={"https://localhost:3000"}, allowedHeaders = "*",exposedHeaders = {"Access-Control-Allow-Origin","Access-Control-Allow-Credentials"},)
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final JwtService jwtService;
    private final AccessService accessService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentRepository appointmentRepository,
                                 JwtService jwtService,
                                 AccessService accessService){
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
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


    @PatchMapping("/{appointment_id}/schedule")
    private AppointmentDto patchAppointmentWithPatient(@PathVariable long appointment_id, @RequestBody UserDetails patient,
                                                       @NotNull HttpServletRequest request){
        UserDetails user = jwtService.authorization(request);
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_UPDATE,
                user,
                appointment_id);
        Optional<Appointment> appointment = appointmentRepository.findById(appointment_id);
        if(appointment.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        if(user.getId() != patient.getId() && (user.getRole() & 4) == 0)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        appointment.get().setPatient(patient);
        appointmentRepository.save(appointment.get());
        return AppointmentMapper.toAppointmentDto(appointment.get());
    }


    @GetMapping
    public List<AppointmentDto> getAllAppointments() {
        return appointmentService.findAll();
    }


}
