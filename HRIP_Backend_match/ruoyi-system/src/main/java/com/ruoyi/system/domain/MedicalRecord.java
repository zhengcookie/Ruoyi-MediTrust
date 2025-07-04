package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Data
public class MedicalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String patientName;
    private String doctorName;
    private String hospitalName;
    private String department;
    private String registrationInfo;
    private String pastMedicalHistory;
    private String currentMedicalHistory;
    private Timestamp createdAt;
}
