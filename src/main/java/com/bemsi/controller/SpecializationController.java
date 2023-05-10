package com.bemsi.controller;

import com.bemsi.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpecializationController {

    @Autowired
    private SpecializationRepository specializationRepository;
}
