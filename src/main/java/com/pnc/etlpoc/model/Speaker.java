package com.pnc.etlpoc.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
public class Speaker implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String subject;
    private LocalDate date;
    private int words;

}
