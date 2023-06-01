package com.bemsi;

import com.bemsi.model.Specialization;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.SpecializationRepository;
import com.bemsi.repository.UserDetailsRepository;
import com.bemsi.security.InvalidatedTokensRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {


    UserDetailsRepository userDetailsRepository;
    AppointmentGenerator appointmentGenerator;
    SpecializationRepository specializationRepository;

    InvalidatedTokensRepository invalidatedTokensRepository;

    @Autowired
    public DatabaseInitializer(UserDetailsRepository userDetailsRepository,
                               AppointmentGenerator appointmentGenerator,
                               SpecializationRepository specializationRepository,
                                InvalidatedTokensRepository invalidatedTokensRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.appointmentGenerator = appointmentGenerator;
        this.specializationRepository = specializationRepository;
        this.invalidatedTokensRepository = invalidatedTokensRepository;
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

        invalidatedTokensRepository.removeAllByExpirationTimeBefore(new Date());

    }

}
