/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.module.Hts.domain.entities;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "DEVICECONFIG")
@Data
public class DeviceConfig implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DEVICECONFIG_ID")
    private Long id;


    @Column(name = "FACILITY_ID")
    private Long facilityId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "DEVICE_ID")
    private String deviceId;

    private String username;

    private String password;


    @Override
    public boolean isNew() {
        return id == null;
    }
}
