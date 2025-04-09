package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Data
public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String patientName;
    private String patient;
    private String doctorName;
    private String doctor;
    private String hospitalName;
    private String department;
    private String remark;
    private Timestamp createdAt;
}
