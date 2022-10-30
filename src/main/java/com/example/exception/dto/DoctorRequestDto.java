package com.example.exception.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class DoctorRequestDto implements Serializable {
    private Long id;
    @NotBlank(message = "Doctor adı boş olamaz")
    private String doctorName;
    @NotBlank(message = "Doctor soyadı boş olamaz")
    private String doctorSurName;
    @NotBlank(message = "Doctor telefon numarası boş olamaz")
    private String phone;
    @NotEmpty( message = "En az bir uzmanlık alanı seçilmeli")
    //@Size(min=1,)
    private List<Long> professionIdList;

}
