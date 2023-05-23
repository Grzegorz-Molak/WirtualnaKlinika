package com.bemsi.security;

import com.bemsi.model.Appointment;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AccessService {

    AppointmentRepository appointmentRepository;

    @Autowired
    public AccessService(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }

    public enum Resource{
        PROFILE, APPOINTMENT
    }

    public void validateAccess(Resource resource, UserDetails user, Long resource_id){
        boolean validated;
        switch(resource){
            case PROFILE -> validated = (user.getId() == resource_id);
            case APPOINTMENT ->  {
                Optional<Appointment> appointment = appointmentRepository.findById(resource_id);
                if(appointment.isEmpty()){
                    validated = false;
                    break;
                }
                validated =
                        user.getId() == appointment.get().getPatient().getId() ||
                        user.getId() == appointment.get().getDoctor().getId();
            }
            default -> validated = false;
        };
        if(!validated) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
    }
}
