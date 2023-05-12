package com.bemsi.DTOs.mapper;

import com.bemsi.DTOs.model.AppointmentDto;
import com.bemsi.model.Appointment;

public class AppointmentMapper {
    public static AppointmentDto toAppointmentDto(Appointment appointment){
        return new AppointmentDto(appointment.getId(),
                UserMapper.toUserDetailsDto(appointment.getDoctor()),
                UserMapper.toUserDetailsDto(appointment.getPatient()),
                appointment.getStartTime(),
                appointment.getNote());
    }
}
