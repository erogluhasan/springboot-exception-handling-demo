package com.example.exception.mapper;


import com.example.exception.dto.ProfessionRequestDto;
import com.example.exception.dto.ProfessionResponseDto;
import com.example.exception.entity.Profession;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfessionMapper {
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    ProfessionResponseDto toDto(Profession profession);

    List<ProfessionResponseDto> toDto(List<Profession> profession);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(target = "deleted",constant = "false")
    @Mapping(target = "id",ignore = true)
    Profession toEntity(ProfessionRequestDto professionRequestDTO);

    @Mapping(source = "id", target = "id")
    Profession toEntity(Long id);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(target = "deleted",ignore = true)
    void updateEntity(@MappingTarget Profession profession, ProfessionRequestDto professionRequestDTO);

    @BeforeMapping
    default void doBeforeMapping(@MappingTarget Profession entity, ProfessionRequestDto dto) {
        entity.setParent(null);
    }
    @AfterMapping
    default void doAfterMapping(@MappingTarget Profession entity, ProfessionRequestDto dto) {
         if (dto.getParentId() == null) {
            entity.setParent(null);
        }
    }

}
