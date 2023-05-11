package com.bemsi.DTOs.model;

import java.time.LocalDateTime;

public record AppointmentDto(long id, UserDetailsDto doctor, UserDetailsDto patient, LocalDateTime datetime, String note) {
}
