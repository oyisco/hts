package org.fhi360.module.Hts.returnDTO;

import lombok.Data;

import java.util.List;

@Data
public class ReturnPatientDTO {
    List<PatientDTO> patients;
    List<AccountDTO> accounts;
    List<DevolveDTO> devolves;
    List<FacilityDTO> facilities;
}
