package org.fhi360.module.Hts.returnDTO;
import lombok.Data;
import java.util.Date;

@Data
public class DevolveDTO {
    int facility_id;
    int patient_id ;
    Date date_devolved ;
    String viral_load_assessed;
    Double last_viral_load ;
    Date date_last_viral_load ;
    String cd4_assessed ;
    Double last_cd4 ;
    Date date_last_cd4 ;
    String last_clinic_stage;
    Date date_last_clinic_stage;
    String arv_dispensed;
    String regimentype;
    String regimen;
    Date date_last_refill;
    Date date_next_refill;
    Date date_last_clinic;
    Date date_next_clinic;
    String notes;
    Date date_discontinued;
    String reason_discontinued;
}
