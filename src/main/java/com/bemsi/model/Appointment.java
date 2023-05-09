package com.bemsi.model;

import java.time.LocalDateTime;

public class Appointment {
    private long id;
    private User doctor;
    private User patient;
    private LocalDateTime datetime;
    private String note;
}
