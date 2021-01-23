package org.fhi360.module.Hts.dto;

import lombok.Data;

@Data
public class CommunityPharmDto {
    private Long communitypharm_id;
    private String pharmacy;
    private String  address;
   private String phone;
   private String email;
   private String pin;

}
