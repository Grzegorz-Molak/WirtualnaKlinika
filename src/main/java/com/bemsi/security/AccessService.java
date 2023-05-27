package com.bemsi.security;

import com.bemsi.model.Appointment;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.AppointmentRepository;
import com.bemsi.repository.UserDetailsRepository;
import com.bemsi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccessService {

    AppointmentRepository appointmentRepository;
    UserDetailsRepository userDetailsRepository;

    @Autowired
    public AccessService(AppointmentRepository appointmentRepository,
                         UserDetailsRepository userDetailsRepository){
        this.appointmentRepository = appointmentRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    public enum Resource{
        PROFILE, APPOINTMENT, APPOINTMENT_UPDATE, APPOINTMENT_RESIGN, APPOINTMENT_LIST;
    }

    public void validateAccess(Resource resource, UserDetails user, Long resource_id){
        boolean validated;
        switch(resource){
            case PROFILE -> validated = (user.getId() == resource_id || (user.getRole() & 4) == 4);
            case APPOINTMENT ->  {
                Optional<Appointment> appointment = appointmentRepository.findById(resource_id);
                if(appointment.isEmpty()){
                    validated = false;
                    break;
                }
                if(appointment.get().getStartTime().isAfter(LocalDateTime.now())){
                    validated =
                            user.getId() == appointment.get().getPatient().getId() ||
                                    user.getId() == appointment.get().getDoctor().getId() ||
                                    (user.getRole() & 4) == 4;
                }
                else {
                    validated =
                            user.getId() == appointment.get().getPatient().getId() ||
                                    user.getId() == appointment.get().getDoctor().getId();
                }
            }
            case APPOINTMENT_UPDATE -> {
                Optional<Appointment> appointment = appointmentRepository.findById(resource_id);
                if(appointment.isEmpty() || (user.getRole() & 2) == 2){ //Lekarz nie ma nic do gadania i nie ma takiej wizyty
                    validated = false;
                    break;
                }
                if(appointment.get().getPatient() != null){
                    validated = false;
                    break;
                }
                validated = true;
            }
            case APPOINTMENT_RESIGN -> {
                Optional<Appointment> appointment = appointmentRepository.findById(resource_id);
                if(appointment.isEmpty() || (user.getRole() & 2) == 2){ //Lekarz nie ma nic do gadania i nie ma takiej wizyty
                    validated = false;
                    break;
                }
                if(appointment.get().getPatient() == null)//Jeżeli na wizytę nikt nie był umówiony
                    {
                    validated = false;
                    break;
                }
                if((user.getRole() & 4) == 4){ //Obsługa może
                    validated = true;
                    break;
                }
                if(appointment.get().getPatient().getId() != user.getId())
                {  //Jeżeli próbujesz odwołać nie swoją wizytę
                    validated = false;
                    break;
                }
                validated = true;
            }
            case APPOINTMENT_LIST -> { // user to zalogowany użytkownik resource to wizyty pewnego uzytkownika
                var resource_user =  userDetailsRepository.findById(resource_id);
                if(resource_user.isEmpty()){
                    validated = false;
                    break;
                }
                if(user.getId() == resource_id){
                    validated = true;
                    break;
                }
                if((user.getRole() & 4) == 4){
                    validated = true;
                    break;
                }
                if((user.getRole() & 2) == 2){
                    if((resource_user.get().getRole() & 2) == 2){
                        validated = false;
                        break;
                    }
                }
                if((user.getRole() & 1) == 1){
                    if(user.getId() != resource_id) {
                        validated = false;
                        break;
                    }
                }
                validated = true;
            }
            default -> validated = false;
        };
        if(!validated) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
    }
}
