package com.bemsi.service;

import com.bemsi.DTOs.mapper.AppointmentMapper;
import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.model.Appointment;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.AppointmentRepository;
import com.bemsi.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentRepository appointmentRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,UserDetailsRepository userDetailsRepository){
        this.appointmentRepository = appointmentRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public List<AppointmentDto> findAll() {
        return (appointmentRepository.findAll()).stream().map(AppointmentMapper::toAppointmentDto).toList();
    }

    @Override
    public String makeAppointment(long appointment_id, long user_login) {
        Appointment appointment = appointmentRepository.findById(appointment_id);
        if (appointment == null){
            return "Nie ma wizyty z tym id";
        }

        Optional<UserDetails> userDetails = userDetailsRepository.findById(user_login);

        if(!userDetails.isPresent()){
            return "nie ma takiego pacjenta";
        }
        if ((userDetails.get().getRole() & 1) != 1){
            return "ten użytkownik to nie pacjent";
        }

        if (appointment.getPatient() == null) {
            appointment.setPatient(userDetails.get());
            appointmentRepository.save(appointment);
            return "success";
        }
        else{
            return "wizyta już jest zajęta";
        }
    }
}
