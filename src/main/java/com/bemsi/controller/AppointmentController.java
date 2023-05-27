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
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_LIST, user, Long.parseLong(user_login));

        if((user.getRole() & 4) == 4){ // Zakładamy że nie ma możliwości żeby ktoś był personelem i lekarzem jednoczesnie
            if(user.getId() == searchedUser.get().getId()) {
                appointments = appointmentRepository.findAllByPatientAndStartTimeBefore(searchedUser.get(),
                        LocalDateTime.now());
            }
           /* else{
                appointments = appointmentRepository.findAllByPatientAndStartTimeBefore(searchedUser.get(),
                        LocalDateTime.now()); // Nie wiem czy to tu ma być?
            }*/
        }
        else if((user.getRole() & 2) == 2) {// Zakładamy że nie ma możliwości żeby ktoś był personelem i lekarzem jednoczesnie
            if(user.getId() == searchedUser.get().getId()){ //szukamy siebie
                appointments = appointmentRepository
                        .findAllByDoctorAndPatientNotNullAndStartTimeBefore(searchedUser.get(), LocalDateTime.now());
            }
            else if ((searchedUser.get().getRole() & 1) == 1) {//Szukamy pacjenta
                appointments = appointmentRepository.findAllByPatientAndDoctorAndStartTimeBefore(searchedUser.get(),
                        user, LocalDateTime.now()); //Szukamy wspólnych wizyt
            }
        }
        if((user.getRole() & 1) == 1)
            appointments.addAll(appointmentRepository
                    .findAllByPatientAndStartTimeBefore(searchedUser.get(), LocalDateTime.now()));

        return appointments
                .stream()
                .map(AppointmentMapper::toAppointmentDto)
                .toList();
    }

    @GetMapping("{user_login}/calendar")
    private List<AppointmentDto> appointmentCalendar(@PathVariable String user_login,
                                                    @CookieValue(name="token") String token){
        UserDetails user = jwtService.authorizationCookie(token);
        Optional<UserDetails> searchedUser= userDetailsRepository.findById(Long.parseLong(user_login));
        if(searchedUser.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        List<Appointment> appointments = new ArrayList<>();
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_LIST, user, Long.parseLong(user_login));

        if((user.getRole() & 4) == 4){ // Zakładamy że nie ma możliwości żeby ktoś był personelem i lekarzem jednoczesnie
            if((searchedUser.get().getRole() & 2) == 2)
                appointments = appointmentRepository.findAllByDoctorAndPatientNotNullAndStartTimeAfter(searchedUser.get(),
                        LocalDateTime.now());
            if((searchedUser.get().getRole() & 1) == 1){
                appointments.addAll(appointmentRepository.findAllByPatientAndStartTimeAfter(searchedUser.get(),
                        LocalDateTime.now()));
            }
        }
        else if((user.getRole() & 2) == 2) {// Zakładamy że nie ma możliwości żeby ktoś był personelem i lekarzem jednoczesnie
            if(user.getId() == searchedUser.get().getId()){ //szukamy siebie
                appointments = appointmentRepository
                        .findAllByDoctorAndPatientNotNullAndStartTimeAfter(searchedUser.get(), LocalDateTime.now());
            }
            else if ((searchedUser.get().getRole() & 1) == 1) {//Szukamy pacjenta
                appointments = appointmentRepository.findAllByPatientAndDoctorAndStartTimeAfter(searchedUser.get(),
                        user, LocalDateTime.now()); //Szukamy wspólnych wizyt
            }
        }
        if((user.getRole() & 1) == 1)
            appointments.addAll(appointmentRepository
                    .findAllByPatientAndStartTimeAfter(searchedUser.get(), LocalDateTime.now()));

        return appointments
                .stream()
                .map(AppointmentMapper::toAppointmentDto)
                .toList();
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


    @PatchMapping("/{appointment_id}")
    private AppointmentDto patchAppointmentWithPatient(@PathVariable long appointment_id, @RequestBody UserDetails patient,
                                                       @CookieValue(name="token") String token){
        UserDetails user = jwtService.authorizationCookie(token);
        accessService.validateAccess(AccessService.Resource.APPOINTMENT_UPDATE,
                user,
                appointment_id);
        Optional<Appointment> appointment = appointmentRepository.findById(appointment_id);
        if(appointment.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        if(user.getId() != patient.getId() && (user.getRole() & 4) == 0) //Zapis nie siebie i nie jako personel
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        appointment.get().setPatient(patient);
        appointmentRepository.save(appointment.get());
        return AppointmentMapper.toAppointmentDto(appointment.get());
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
