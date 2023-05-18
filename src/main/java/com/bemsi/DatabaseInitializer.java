package com.bemsi;

import com.bemsi.model.Specialization;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.SpecializationRepository;
import com.bemsi.repository.UserDetailsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {


    UserDetailsRepository userDetailsRepository;
    AppointmentGenerator appointmentGenerator;
    SpecializationRepository specializationRepository;

    @Autowired
    public DatabaseInitializer(UserDetailsRepository userDetailsRepository, AppointmentGenerator appointmentGenerator, SpecializationRepository specializationRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.appointmentGenerator = appointmentGenerator;
        this.specializationRepository = specializationRepository;
    }
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<String> specializations = new ArrayList<>();
        specializations.add("kardiolog");
        specializations.add("pediatra");
        specializations.add("internista");
        specializations.add("endokrynolog");
        specializations.add("ginekolog");
        specializations.add("ortopeda");

        for (String name : specializations) {
            Specialization specialization = specializationRepository.findByName(name);
            if (specialization == null) {
                Specialization newSpecialization = new Specialization(name);
                specializationRepository.save(newSpecialization);
            }
        }
        List<UserDetails> doctors =userDetailsRepository.findAllDoctors();
        appointmentGenerator.generateAppointments(doctors);

    }

}
