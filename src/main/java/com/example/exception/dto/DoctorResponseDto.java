package com.example.exception.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class DoctorResponseDto implements Serializable {
    private Long id;
    private String externalId;
    private String doctorName;
    private String doctorSurName;
    private String phone;
    private int numProfessions;
    private List<ProfessionResponseDto> professionResponseDtos;
}
	 
