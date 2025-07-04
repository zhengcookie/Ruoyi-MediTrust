package com.ruoyi.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Appointments implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String patientName;
    private String doctorName;
    private String hospitalName;
    private String department;
    private String remark;
    private Timestamp createdAt;
}
