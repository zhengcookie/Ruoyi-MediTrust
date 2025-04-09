package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Appointment;

import java.util.List;

public interface AppointmentsMapper {
    public Appointment selectAppointment(Long id);
    public List<Appointment> selectAppointmentList(Long id);
    public Appointment selectAppointmentById(Long id);
    public int insertAppointment(Appointment appointment);
    public int  updateAppointment(Appointment appointment);
    public int  deleteAppointmentById(Long id);
    public int  deleteAppointmentByIds(Long[] ids);
}
