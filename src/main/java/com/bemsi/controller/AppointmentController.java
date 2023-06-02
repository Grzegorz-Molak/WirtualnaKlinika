package com.bemsi.controller;

import com.bemsi.DTOs.mapper.AppointmentMapper;
import com.bemsi.DTOs.mapper.UserMapper;
import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.model.Appointment;
import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.AppointmentRepository;
import com.bemsi.repository.UserDetailsRepository;
import com.bemsi.repository.UserRepository;
import com.bemsi.security.AccessService;
import com.bemsi.security.JwtService;
import com.bemsi.service.AppointmentService;
import com.bemsi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin (origins={"https://localhost:3000"}, allowCredentials = "true")
//(origins={"https://localhost:3000"}, allowedHeaders = "*",exposedHeaders = {"Access-Control-Allow-Origin","Access-Control-Allow-Credentials"},)
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;
    private final JwtService jwtService;
    private final AccessService accessService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService,
                                 AppointmentRepository appointmentRepository,
                                 UserService userService,
                                 UserDetailsRepository userDetailsRepository,
                                 JwtService jwtService,
                                 AccessService accessService){
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
        this.userDetailsRepository = userDetailsRepository;
        this.jwtService = jwtService;
        this.accessService = accessService;
    }

    //TODO Chyba usunąć
    @PostMapping("/{appointment_id}/{user_login}")
    public String  makeAppointment(@PathVariable long appointment_id,
                                   @PathVariable String user_login){

        return appointmentService.makeAppointment(appointment_id,user_login);
    }

    @GetMapping("{user_login}/history")
    private List<AppointmentDto> appointmentHistory(@PathVariable String user_login,
            @CookieValue(name="token") String token){
        UserDetails user = jwtService.authorizationCookie(token);
        Optional<UserDetails> searchedUser= userDetailsRepository.findById(Long.parseLong(user_login));
        if(searchedUser.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        List<Appointment> appointments = new ArrayList<>();
        // ***********************************************************
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_HISTORY, user, Long.parseLong(user_login));

        if((user.getRole() & 1) == 1) appointments.addAll( //wiadomo że chcemy swoje wizyty wyszuka
            appointmentRepository.findAllByPatientAndStartTimeBefore(searchedUser.get(), LocalDateTime.now())
        );
        if((user.getRole() & 2) == 2){
            //1. Wyszukujemy swoje wizyty jako lekarz
            if(user.getRole() == searchedUser.get().getId())
                appointments.addAll( //Zbieramy wszystkie swoje wizyty przeszłe
                    appointmentRepository.findAllByDoctorAndPatientNotNullAndStartTimeBefore(user, LocalDateTime.now())
            );
            //2. Wyszukujemy wspólne wizyty z jakimś pacjentem
            // Jeżeli takich nie ma to no problem, bo nic z bazy nie wybierze
            appointments.addAll(
                    appointmentRepository.findAllByPatientAndDoctorAndStartTimeBefore(searchedUser.get(), user, LocalDateTime.now())
            );
        }
        return appointments
                .stream()
                .map(AppointmentMapper::toAppointmentDto)
                .distinct().toList();
    }

    @GetMapping("{user_login}/calendar")
    private List<AppointmentDto> appointmentCalendar(@PathVariable String user_login,
                                                    @CookieValue(name="token") String token){
        UserDetails user = jwtService.authorizationCookie(token);
        Optional<UserDetails> searchedUser= userDetailsRepository.findById(Long.parseLong(user_login));
        if(searchedUser.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        List<Appointment> appointments = new ArrayList<>();
        // ***********************************************************
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_CALENDAR, user, Long.parseLong(user_login));
        if((searchedUser.get().getRole() & 1) == 1){
            appointments.addAll( //wiadomo że chcemy swoje wizyty wyszukać
                    appointmentRepository.findAllByPatientAndStartTimeAfter(searchedUser.get(), LocalDateTime.now()));
        }
        if((searchedUser.get().getRole() & 2) == 2){
            appointments.addAll(
                    appointmentRepository.findAllByDoctorAndPatientNotNullAndStartTimeAfter(searchedUser.get(), LocalDateTime.now())
            );
        }
        return appointments
                .stream()
                .map(AppointmentMapper::toAppointmentDto)
                .distinct().toList();
    }

    @GetMapping("/{id}")
    private AppointmentDto AppointmentById(@PathVariable Long id, @CookieValue(name="token") String token) {
        UserDetails user = jwtService.authorizationCookie(token);
        if(appointmentService.findAppointment(id).patient() == null)
            return appointmentService.findAppointment(id);
        accessService.validateAccess(AccessService.Resource.APPOINTMENT,
                user,
                id);
        return appointmentService.findAppointment(id);
    }


    @PatchMapping("/{appointment_id}/{patient_login}")
    private String patchAppointmentWithPatient(@PathVariable long appointment_id, @PathVariable String patient_login,
                                                       @CookieValue(name="token") String token){
        UserDetails user = jwtService.authorizationCookie(token);
        Optional<UserDetails> patient = userDetailsRepository.findById(Long.parseLong(patient_login));
        if(patient.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        Optional<Appointment> appointment = appointmentRepository.findById(appointment_id);
        if(appointment.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");

        //********************************************************8
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_UPDATE, user, appointment_id);

        if(user.getId() != patient.get().getId() && (user.getRole() & 4) == 0) //Zapis nie siebie i nie jako personel
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        //**** ZAPIS ***********************
        appointment.get().setPatient(patient.get());
        appointmentRepository.save(appointment.get());
        return "Pomyślnie zapisano na wizytę #"+appointment.get().getId()+" "+appointment.get().getStartTime();
    }
    @PatchMapping("/{appointment_id}/resign")
    private String resignAppointment(@PathVariable long appointment_id, @CookieValue(name="token") String token){
        UserDetails user = jwtService.authorizationCookie(token);
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_RESIGN,
                user,
                appointment_id);
        Optional<Appointment> appointment = appointmentRepository.findById(appointment_id);
        if(appointment.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        appointment.get().setPatient(null);
        appointmentRepository.save(appointment.get());
        return "Pomyślnie zrezygnowano z wizyty";
    }


    @GetMapping
    public List<AppointmentDto> getAllAppointments() {
        return appointmentService.findAll();
    }


}
