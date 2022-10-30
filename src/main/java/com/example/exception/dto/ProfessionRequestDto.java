package com.example.exception.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class ProfessionRequestDto implements Serializable {
    private Long id;
    private Long parentId;
    private String name;
    private String description;
}
