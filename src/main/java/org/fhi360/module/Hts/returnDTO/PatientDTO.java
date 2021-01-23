package org.fhi360.module.Hts.returnDTO;

import lombok.Data;

import java.util.Date;

@Data
public class PatientDTO {
    private int patient_id;
    private int facility_id;
    private String hospital_num;
    private String unique_id;
    private String surname;
    private String other_names;
    private String gender;
    private Date date_birth;
    private String address;
    private String phone;
    private Date date_started;
    private String regimen;
    private String regimentype;
    private String last_clinic_stage;
    private Date date_last_viral_load;
    private Double last_viral_load;
    private Date viral_load_due_date;
    private String viral_load_type;
    private Date date_last_cd4;
    private Double last_cd4;
    private Date date_last_clinic;
    private Date date_last_refill;
    private Date date_next_clinic;
    private Date date_next_refill;
    private String last_refill_setting;
}
