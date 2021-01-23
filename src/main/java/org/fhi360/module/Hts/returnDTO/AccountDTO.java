package org.fhi360.module.Hts.returnDTO;

import lombok.Data;

@Data
public class AccountDTO {
    private int communitypharm_id;
    private String pharmacy;
    private String address;
    private String phone;
    private String email;
    private String pin;
}
