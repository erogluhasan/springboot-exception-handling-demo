package com.example.exception.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity implements Serializable {
    @Column(name = "creator_id")
    Long creator;
    @Column(name = "created_date")
    Date createdDate;
    @Column(name = "updater_id")
    Long updater;
    @Column(name = "updated_date")
    Date updatedDate;
    @Column(name = "deleted")
    Boolean deleted;

}