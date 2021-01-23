package org.fhi360.module.Hts.domain.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "account")
public class Account implements Serializable, Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;
    @Column(name = "pharmacy")
    private String pharmacy;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "pin_code")
    private String pinCode;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "date_registration")
    private String dateRegistration;
    @Column(name = "time_stamp")
    private LocalDateTime timestamp;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
