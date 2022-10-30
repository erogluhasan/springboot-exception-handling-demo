package com.example.exception.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProfessionResponseDto implements Serializable {
	private Long id;
	private Long parentId;
    private String name;
    private String description;
    private String  parentName;
}
	 
