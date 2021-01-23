package org.fhi360.module.Hts.dto;

import lombok.Data;

@Data
public class FacilityDTO {
    private long facilityId;
    private String name;
    private long stateId;
    private long lgaId;
    private long deviceconfigId;
}
