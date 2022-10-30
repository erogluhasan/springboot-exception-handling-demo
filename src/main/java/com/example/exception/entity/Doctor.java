package com.example.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Doctor extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String  phone;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name="doctor_professions",
            joinColumns = @JoinColumn( name="doctor_id"),
            inverseJoinColumns = @JoinColumn( name="profession_id")
    )
    private List<Profession> professionList;
}
