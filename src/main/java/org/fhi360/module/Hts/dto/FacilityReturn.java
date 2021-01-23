package org.fhi360.module.Hts.dto;

import lombok.Data;

@Data
public class FacilityReturn {
    private long facility_id;
    private String name;
    private String state;
    private String lga;
}
