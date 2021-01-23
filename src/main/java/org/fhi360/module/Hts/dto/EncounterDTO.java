package org.fhi360.module.Hts.dto;

import lombok.Data;

@Data
public class EncounterDTO {
    private Long facility_id;
    private Long patient_id;
    private String date_visit;
    private String regimen1;
    private String regimen2;
    private String regimen3;
    private String regimen4;
    private int duration1;
    private int duration2;
    private int duration3;
    private int duration4;
    private Long communitypharm_id;
    private String next_refill;
}
