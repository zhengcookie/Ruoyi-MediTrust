package com.ruoyi.system.mapper;

<<<<<<< HEAD
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
=======
import java.util.List;
import com.ruoyi.system.domain.Appointments;

/**
 * 预约信息 数据层
 *
 * @author ruoyi
 */
public interface AppointmentsMapper {

    public Appointments selectAppointmentById(Long id);
    public List<Appointments> selectAppointmentList(Appointments Appointments);
    public int insertAppointment(Appointments Appointments);
    public int updateAppointment(Appointments Appointments);
    public int deleteAppointmentById(Long id);
    public int deleteAppointmentByIds(Long[] ids);
    public int checkAppointmentUnique(Appointments Appointments);
}
>>>>>>> ceece8c (实现多选删除功能)
