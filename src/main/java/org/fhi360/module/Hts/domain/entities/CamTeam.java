package org.fhi360.module.Hts.domain.entities;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "camteam")
public class CamTeam implements Serializable , Persistable<Long> {
    @Id
    @Basic(optional = false)
    private Long id;
    @Column(name = "facility_id")
    private Long facilityId;
    @Column(name = "camteam")
    private String camteam;
    @Column(name = "cam_code")
    private String camCode;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
