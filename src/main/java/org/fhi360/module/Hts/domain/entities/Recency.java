/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.module.Hts.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.lamisplus.modules.lamis.legacy.domain.entities.Hts;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Data
public class Recency implements Serializable, Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "HTS_ID")
    @ManyToOne
    private Hts hts;
    private String hospitalNum;
    private Long facilityId;
    private String testName;
    private LocalDateTime testDate;
    private String participantId;
    private Long controlLine;
    private Long verificationLine;
    private Long longTermLine;
    private String recencyInterpretation;
    private LocalDateTime timeStamp;
    private String sampleReferenceNumber;
    private LocalDateTime dateSampleCollected;
    private String typeSample;
    private LocalDateTime dateSampleTest;
    private LocalDateTime dateTestDone;
    private String viralLoadResult;

    @Column(name = "UPLOADED")
    @JsonIgnore
    private Integer uploaded;

    @Column(name = "TIME_UPLOADED")
    @JsonIgnore
    private LocalDateTime timeUploaded;


    @PrePersist
    public void preSave() {
        timeStamp = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        timeStamp = LocalDateTime.now();
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
