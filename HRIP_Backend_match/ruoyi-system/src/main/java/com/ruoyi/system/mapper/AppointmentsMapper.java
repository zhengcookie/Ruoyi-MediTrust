package com.ruoyi.system.mapper;

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