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
        PROFILE, APPOINTMENT, APPOINTMENT_UPDATE, APPOINTMENT_RESIGN, APPOINTMENT_HISTORY, APPOINTMENT_CALENDAR, SIGN_UP;
    }

    public void validateAccess(Resource resource, UserDetails user, long resource_id){
        boolean validated;
        switch(resource){
            case PROFILE -> validated = (user.getId() == resource_id);
            case APPOINTMENT ->  {
                validated = false;
                Optional<Appointment> appointment = appointmentRepository.findById(resource_id);
                if(appointment.isEmpty()) break;
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
                validated = false;
                Optional<Appointment> appointment = appointmentRepository.findById(resource_id);
                if(appointment.isEmpty()) break;
                if(appointment.get().getPatient() != null) break;
                if(appointment.get().getStartTime().isBefore(LocalDateTime.now())) break;
                if((user.getRole() & 1) == 1 || (user.getRole() & 4) == 4) validated = true;
                //Sprawdzić czy pacjent chce zapisać siebie czy kogoś
            }
            case APPOINTMENT_RESIGN -> {
                validated = false;
                Optional<Appointment> appointment = appointmentRepository.findById(resource_id);
                if(appointment.isEmpty()) break;
                if(appointment.get().getPatient() == null) break;
                if((user.getRole() & 4) == 4) validated = true;
                if(appointment.get().getPatient().getId() == user.getId()) validated = true;
            }
            case APPOINTMENT_HISTORY -> { // user to zalogowany użytkownik resource to wizyty pewnego uzytkownika
                validated = false;
                var resource_user =  userDetailsRepository.findById(resource_id);
                if(resource_user.isEmpty()) break;
                if(user.getId() == resource_id) validated = true;
                if((user.getRole() & 2) == 2) validated = true;
                // Zakładamy że lekarz może się dostać do historii wizyt pacjenta
                // ale dostanie później tylko swoje wizyty
            }
            case APPOINTMENT_CALENDAR -> { // user to zalogowany użytkownik resource to wizyty pewnego uzytkownika
                validated = false;
                var resource_user =  userDetailsRepository.findById(resource_id);
                if(resource_user.isEmpty()) break;
                if(user.getId() == resource_id) validated = true;
                if((user.getRole() & 4) == 4) validated = true;
                // Lekarz nie musi widzieć przyszłych wizyt pacjenta, ale moze swoje, ale to załatwia warunek id
            }
            case SIGN_UP -> {  //resource_id to rola tworzonego użytkownika
                validated = false;
                if(((resource_id & 4) == 4 || (resource_id & 2) == 2) && //chcemy zrobić lekarza lub personel
                ((user.getRole() & 8) == 8)) validated = true; // tylko jako admin

                if((resource_id & 1) == 1 && (user.getRole() & 4) == 4) validated = true;
                //chcemy zrobić użytkownika jako personel pomocniczy
            }
            default -> validated = false;
        };
        if(!validated) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
    }
}
